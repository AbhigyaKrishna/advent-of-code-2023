import kotlinx.coroutines.*
import java.util.EnumSet

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    fun part1(input: List<String>): Int {
        val visited = mutableMapOf<Point2D, EnumSet<RefDirection>>()
        followPath(Point2D(0, 0), input, RefDirection.RIGHT, visited)
        return visited.size
    }

    fun part2(input: List<String>): Int {
        fun createTask(x: Int, y: Int, direction: RefDirection): Deferred<Int> {
            return GlobalScope.async {
                val visited = mutableMapOf<Point2D, EnumSet<RefDirection>>()
                followPath(Point2D(x, y), input, direction, visited)
                visited.size
            }
        }
        return runBlocking {
            sequence {
                val xLength = input[0].length
                for (y in input.indices) {
                    yield(createTask(0, y, RefDirection.RIGHT))
                    yield(createTask(xLength - 1, y, RefDirection.LEFT))
                }

                for (x in 0..<xLength) {
                    yield(createTask(x, 0, RefDirection.DOWN))
                    yield(createTask(x, input.size - 1, RefDirection.UP))
                }
            }.toList()
                .awaitAll()
                .max()
        }
    }

    val testInput = readInput("Day16_test")
    check(part2(testInput)== 51)

    val input = readInput("Day16")
    part1(input).println()
    part2(input).println()
}

private data class Point2D(val x: Int, val y: Int) {
    fun translate(direction: RefDirection): Point2D {
        return when (direction) {
            RefDirection.UP -> Point2D(x, y - 1)
            RefDirection.DOWN -> Point2D(x, y + 1)
            RefDirection.LEFT -> Point2D(x - 1, y)
            RefDirection.RIGHT -> Point2D(x + 1, y)
        }
    }
}

private enum class RefDirection {
    UP, DOWN, LEFT, RIGHT
}

private operator fun List<String>.get(point: Point2D): Char = this[point.y][point.x]

private fun followPath(point: Point2D, mesh: List<String>, direction: RefDirection, visited: MutableMap<Point2D, EnumSet<RefDirection>>) {
    var curr = point
    var c: Char
    if (curr.x < 0 || curr.y < 0 || curr.y >= mesh.size || curr.x >= mesh[curr.y].length) return
    while (isPassable(mesh[curr].also { c = it }, direction)) {

//        val map = visited.keys.groupBy({ it.x }) { it.y }
//        mesh.mapIndexed { index, s ->
//            map[index]?.run {
//                val arr = s.toCharArray()
//                forEach { arr[it] = 'X' }
//                String(arr)
//            } ?: s
//        }.joinToString("\n").println()
//        println()

        if (curr in visited && direction in visited[curr]!!) return

        visited.getOrPut(curr) { EnumSet.noneOf(RefDirection::class.java) }.add(direction)
        curr = curr.translate(direction)
        if (curr.x < 0 || curr.y < 0 || curr.y >= mesh.size || curr.x >= mesh[curr.y].length) return
    }

    visited.getOrPut(curr) { EnumSet.noneOf(RefDirection::class.java) }.add(direction)
    when (c) {
        '\\' -> {
            when (direction) {
                RefDirection.UP -> followPath(curr.translate(RefDirection.LEFT), mesh, RefDirection.LEFT, visited)
                RefDirection.DOWN -> followPath(curr.translate(RefDirection.RIGHT), mesh, RefDirection.RIGHT, visited)
                RefDirection.LEFT -> followPath(curr.translate(RefDirection.UP), mesh, RefDirection.UP, visited)
                RefDirection.RIGHT -> followPath(curr.translate(RefDirection.DOWN), mesh, RefDirection.DOWN, visited)
            }
        }
        '/' -> {
            when (direction) {
                RefDirection.UP -> followPath(curr.translate(RefDirection.RIGHT), mesh, RefDirection.RIGHT, visited)
                RefDirection.DOWN -> followPath(curr.translate(RefDirection.LEFT), mesh, RefDirection.LEFT, visited)
                RefDirection.LEFT -> followPath(curr.translate(RefDirection.DOWN), mesh, RefDirection.DOWN, visited)
                RefDirection.RIGHT -> followPath(curr.translate(RefDirection.UP), mesh, RefDirection.UP, visited)
            }
        }
        '|' -> {
            followPath(curr.translate(RefDirection.UP), mesh, RefDirection.UP, visited)
            followPath(curr.translate(RefDirection.DOWN), mesh, RefDirection.DOWN, visited)
        }
        '-' -> {
            followPath(curr.translate(RefDirection.LEFT), mesh, RefDirection.LEFT, visited)
            followPath(curr.translate(RefDirection.RIGHT), mesh, RefDirection.RIGHT, visited)
        }
    }
}

private fun isPassable(c: Char, direction: RefDirection): Boolean {
    return when (c) {
        '|' -> direction == RefDirection.UP || direction == RefDirection.DOWN
        '-' -> direction == RefDirection.LEFT || direction == RefDirection.RIGHT
        '.' -> true
        else -> false
    }
}