fun main() {
    fun part1(input: List<String>): Int {
        return input.fold(0) { acc, s ->
            acc + s.first { it.isDigit() }.digitToInt() * 10 + s.last { it.isDigit() }.digitToInt()
        }
    }

    fun part2(input: List<String>): Int {
        val digits = arrayOf("zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
        return input.fold(0) { acc, s ->
            val numbers = arrayListOf<Int>()
            s.forEachIndexed { index, c ->
                if (c.isDigit()) {
                    numbers.add(c.digitToInt())
                }

                digits.forEachIndexed sub@ { n, digit ->
                    if (s.substring(index).startsWith(digit)) {
                        numbers.add(n)
                        return@sub
                    }
                }
            }

            acc + numbers.first() * 10 + numbers.last()
        }
    }

    val testInput = readInput("Day01_test")
    check(part2(testInput) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
