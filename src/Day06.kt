fun main() {
    fun part1(input: List<String>): Int {
        val times = input[0].substring(11).split(" ").filter { it.isNotBlank() }.map { it.toLong() }
        val distances = input[1].substring(11).split(" ").filter { it.isNotBlank() }.map { it.toLong() }

        var mul = 1
        for (i in times.indices) {
            val time = times[i]
            val distance = distances[i]

            val total = calculateWays(time, distance)

            mul *= total
        }

        return mul
    }

    fun part2(input: List<String>): Int {
        val time = input[0].substring(11).split(" ").filter { it.isNotBlank() }.joinToString("").toLong()
        val distance = input[1].substring(11).split(" ").filter { it.isNotBlank() }.joinToString("").toLong()

        return calculateWays(time, distance)
    }

    val testInput = readInput("Day06_test")
    check(part1(testInput) == 288)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}

private fun calculateWays(time: Long, distance: Long): Int {
    var total = 0

    var curr = time / 2
    var neg = false
    while (true) {
        val covered = curr * (time - curr)
        if (covered > distance) {
            total++
            if (neg) {
                curr--
            } else {
                curr++
            }
        } else {
            if (neg)
                break

            neg = true
            curr = time / 2 - 1
        }
    }
    return total
}