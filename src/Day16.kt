import kotlinx.coroutines.*
import java.util.EnumSet

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    fun part1(input: List<String>): Int {
        val visited = mutableMapOf<Point2D, EnumSet<Direction>>()
        followPath(Point2D(0, 0), input, Direction.RIGHT, visited)
        return visited.size
    }

    fun part2(input: List<String>): Int {
        fun createTask(x: Int, y: Int, direction: Direction): Deferred<Int> {
            return GlobalScope.async {
                val visited = mutableMapOf<Point2D, EnumSet<Direction>>()
                followPath(Point2D(x, y), input, direction, visited)
                visited.size
            }
        }
        return runBlocking {
            sequence {
                val xLength = input[0].length
                for (y in input.indices) {
                    yield(createTask(0, y, Direction.RIGHT))
                    yield(createTask(xLength - 1, y, Direction.LEFT))
                }

                for (x in 0..<xLength) {
                    yield(createTask(x, 0, Direction.DOWN))
                    yield(createTask(x, input.size - 1, Direction.UP))
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

private operator fun List<String>.get(point: Point2D): Char = this[point.y][point.x]

private fun followPath(point: Point2D, mesh: List<String>, direction: Direction, visited: MutableMap<Point2D, EnumSet<Direction>>) {
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

        visited.getOrPut(curr) { EnumSet.noneOf(Direction::class.java) }.add(direction)
        curr = curr.translate(direction)
        if (curr.x < 0 || curr.y < 0 || curr.y >= mesh.size || curr.x >= mesh[curr.y].length) return
    }

    visited.getOrPut(curr) { EnumSet.noneOf(Direction::class.java) }.add(direction)
    when (c) {
        '\\' -> {
            when (direction) {
                Direction.UP -> followPath(curr.translate(Direction.LEFT), mesh, Direction.LEFT, visited)
                Direction.DOWN -> followPath(curr.translate(Direction.RIGHT), mesh, Direction.RIGHT, visited)
                Direction.LEFT -> followPath(curr.translate(Direction.UP), mesh, Direction.UP, visited)
                Direction.RIGHT -> followPath(curr.translate(Direction.DOWN), mesh, Direction.DOWN, visited)
            }
        }
        '/' -> {
            when (direction) {
                Direction.UP -> followPath(curr.translate(Direction.RIGHT), mesh, Direction.RIGHT, visited)
                Direction.DOWN -> followPath(curr.translate(Direction.LEFT), mesh, Direction.LEFT, visited)
                Direction.LEFT -> followPath(curr.translate(Direction.DOWN), mesh, Direction.DOWN, visited)
                Direction.RIGHT -> followPath(curr.translate(Direction.UP), mesh, Direction.UP, visited)
            }
        }
        '|' -> {
            followPath(curr.translate(Direction.UP), mesh, Direction.UP, visited)
            followPath(curr.translate(Direction.DOWN), mesh, Direction.DOWN, visited)
        }
        '-' -> {
            followPath(curr.translate(Direction.LEFT), mesh, Direction.LEFT, visited)
            followPath(curr.translate(Direction.RIGHT), mesh, Direction.RIGHT, visited)
        }
    }
}

private fun isPassable(c: Char, direction: Direction): Boolean {
    return when (c) {
        '|' -> direction == Direction.UP || direction == Direction.DOWN
        '-' -> direction == Direction.LEFT || direction == Direction.RIGHT
        '.' -> true
        else -> false
    }
}