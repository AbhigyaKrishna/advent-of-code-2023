private const val MAX_RED = 12
private const val MAX_GREEN = 13
private const val MAX_BLUE = 14

private data class Hand(val red: Int = 0, val blue: Int = 0, val green: Int = 0) {
    val isValid: Boolean get() = red <= MAX_RED && green <= MAX_GREEN && blue <= MAX_BLUE

    val power: Int get() = red * blue * green
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.fold(0) { acc, line ->
            val (game, hand) = line.split(": ")
            val hands = hand.extractHand()

            if (hands.all { it.isValid })
                return@fold acc + game.substring(5).toInt()

            acc
        }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { line ->
            val (_, hand) = line.split(": ")
            val hands = hand.extractHand()

            hands.maxRequired().power
        }
    }

    val testInput = readInput("Day02_test")
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}

private fun String.extractHand(): List<Hand> {
    return this.split("; ").map {
        val colors = it.split(", ")

        var red = 0
        var green = 0
        var blue = 0
        for (color in colors) {
            val (amount, colorName) = color.split(" ")
            when (colorName) {
                "red" -> red += amount.toInt()
                "green" -> green += amount.toInt()
                "blue" -> blue += amount.toInt()
            }
        }

        Hand(red, blue, green)
    }
}

private fun List<Hand>.maxRequired(): Hand {
    return this.reduce { acc, hand ->
        Hand(
            red = maxOf(acc.red, hand.red),
            blue = maxOf(acc.blue, hand.blue),
            green = maxOf(acc.green, hand.green)
        )
    }
}