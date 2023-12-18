import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        for (y in input.indices) {
            val x = input[y].indexOf('S')
            if (x == -1) continue
            var pipes = emptyMap<Point2D, Char>()
            if (y != input.lastIndex)
                pipes = checkPipe(input, x, y, Direction.DOWN)
            if (pipes.isEmpty() && y != 0)
                pipes = checkPipe(input, x, y, Direction.UP)
            if (pipes.isEmpty() && x != 0)
                pipes = checkPipe(input, x, y, Direction.LEFT)
            if (pipes.isEmpty() && x != input[y].lastIndex)
                pipes = checkPipe(input, x, y, Direction.RIGHT)

            return pipes.size / 2
        }

        return -1
    }

    fun part2(input: List<String>): Int {
        for (y in input.indices) {
            val x = input[y].indexOf('S')
            if (x == -1) continue
            var pipes = emptyMap<Point2D, Char>()
            if (y != input.lastIndex)
                pipes = checkPipe(input, x, y, Direction.DOWN)
            if (pipes.isEmpty() && y != 0)
                pipes = checkPipe(input, x, y, Direction.UP)
            if (pipes.isEmpty() && x != 0)
                pipes = checkPipe(input, x, y, Direction.LEFT)
            if (pipes.isEmpty() && x != input[y].lastIndex)
                pipes = checkPipe(input, x, y, Direction.RIGHT)

            return calculateArea(pipes.keys)
        }

        return -1
    }

    val testInput = readInput("Day10_test")
    check(part2(testInput) == 4)

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}

private fun checkPipe(
    input: List<String>,
    startX: Int,
    startY: Int,
    direction: Direction
): Map<Point2D, Char> {
    val result = LinkedHashMap<Point2D, Char>()
    var dx = startX
    var dy = startY
    var direction = direction
    when (direction) {
        Direction.UP -> dy--
        Direction.DOWN -> dy++
        Direction.LEFT -> dx--
        Direction.RIGHT -> dx++
    }
    while (true) {
        val c = input[dy][dx]
        if (startX == dx && startY == dy) {
            result[Point2D(dx, dy)] = c
            break
        }
        if (dy !in input.indices || dx !in input[dy].indices) break
        result[Point2D(dx, dy)] = c
        when (c) {
            '|' -> when (direction) {
                Direction.UP -> dy--
                Direction.DOWN -> dy++
                else -> return emptyMap()
            }
            '-' -> when (direction) {
                Direction.LEFT -> dx--
                Direction.RIGHT -> dx++
                else -> return emptyMap()
            }
            'L' -> when (direction) {
                Direction.DOWN -> {
                    dx++
                    direction = Direction.RIGHT
                }
                Direction.LEFT -> {
                    dy--
                    direction = Direction.UP
                }
                else -> return emptyMap()
            }
            'J' -> when (direction) {
                Direction.DOWN -> {
                    dx--
                    direction = Direction.LEFT
                }
                Direction.RIGHT -> {
                    dy--
                    direction = Direction.UP
                }
                else -> return emptyMap()
            }
            '7' -> when (direction) {
                Direction.UP -> {
                    dx--
                    direction = Direction.LEFT
                }
                Direction.RIGHT -> {
                    dy++
                    direction = Direction.DOWN
                }
                else -> return emptyMap()
            }
            'F' -> when (direction) {
                Direction.UP -> {
                    dx++
                    direction = Direction.RIGHT
                }
                Direction.LEFT -> {
                    dy++
                    direction = Direction.DOWN
                }
                else -> return emptyMap()
            }
            else -> return emptyMap()
        }
    }

    return result
}

// Pick's theorem
private fun calculateArea(coords: Set<Point2D>): Int {
    var area = 0
    var boundary = 0
    for (i in coords.indices) {
        val (x1, y1) = coords.elementAt(i)
        val (x2, y2) = coords.elementAt((i + 1) % coords.size)
        area += x1 * y2 - x2 * y1
        boundary += abs(x1 - x2) + abs(y1 - y2)
    }

    return (abs(area) - boundary + 2) / 2
}