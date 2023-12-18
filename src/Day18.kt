@OptIn(ExperimentalStdlibApi::class)
fun main() {
    fun part1(input: List<String>): Int {
        val points = mutableSetOf<Point2D>()
        var curr = Point2D.ZERO
        points.add(curr)

        var sum = 0
        input.map {
            when (it[0]) {
                'R' -> Direction.RIGHT
                'L' -> Direction.LEFT
                'U' -> Direction.UP
                'D' -> Direction.DOWN
                else -> error("Unknown direction")
            } to it.split(" ")[1].toInt()
        }.forEach { (direction, units) ->
            val next = curr.translate(direction, units)
            sum += units
            points.add(next)
            curr = next
        }

        return sum + calculateAreaPicksTheorem(points.toList()).toInt()
    }

    fun part2(input: List<String>): Long {
        val points = mutableSetOf<Point2D>()
        var curr = Point2D.ZERO
        points.add(curr)

        var sum = 0L
        input.map {
            when(it[it.lastIndex - 1]) {
                '0' -> Direction.RIGHT
                '1' -> Direction.DOWN
                '2' -> Direction.LEFT
                '3' -> Direction.UP
                else -> error("Unknown direction")
            } to it.slice(it.lastIndex - 6 until it.lastIndex - 1).hexToInt()
        }.forEach { (direction, units) ->
            val next = curr.translate(direction, units)
            sum += units
            points.add(next)
            curr = next
        }

        return sum + calculateAreaPicksTheorem(points.toList())
    }

    val testInput = readInput("Day18_test")
    check(part2(testInput) == 952408144115L)

    val input = readInput("Day18")
    part1(input).println()
    part2(input).println()
}