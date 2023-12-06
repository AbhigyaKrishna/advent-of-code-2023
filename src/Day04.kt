import kotlin.math.pow

fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            val winner = getWinner(line)
            if (winner.isEmpty()) 0 else (2.0).pow(winner.size - 1).toInt().coerceAtLeast(1)
        }
    }

    fun part2(input: List<String>): Int {
        return List(input.size) { index ->
            noOfWinning(index, input)
        }.sum()
    }

    val testInput = readInput("Day04_test")
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}

fun getWinner(line: String): List<String> {
    val (_, table) = line.split(": ")
    val (winning, contain) = table.split(" | ").map { it.split(" ").filter { it.isNotEmpty() } }

    return winning.filter { it in contain }
}

fun noOfWinning(card: Int, input: List<String>): Int {
    val line = input[card]
    val winner = getWinner(line)
    return if (winner.isEmpty())
        1
    else {
        var i = card
        var sum = 1
        while (i - card < winner.size && i < input.size) {
            sum += noOfWinning(++i, input)
        }

        sum
    }
}