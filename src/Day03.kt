fun main() {
    fun part1(input: List<String>): Int {
        val nonSymbol = arrayOf('1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '.')
        val pos = arrayOf(1, 0, -1)
        return input.foldIndexed(0) { idx, acc, line ->
            var sum = acc
            line.forEachIndexed { index, c ->
                if (c in nonSymbol) return@forEachIndexed

                val extractedPos = arrayListOf<Pair<Int, Int>>()
                for (x in pos) {
                    for (y in pos) {
                        if (x == 0 && y == 0) continue
                        if (input.getOrNull(idx + y)?.getOrNull(index + x)?.digitToIntOrNull() in 0..9) {
                            val (num, indices) = extractNumber(index + x, input[idx + y])
                            val i = indices.map { it to idx + y }
                            if (extractedPos.any { it in i }) continue
                            sum += num
                            extractedPos.addAll(i)
                        }
                    }
                }
            }

            sum
        }
    }

    fun part2(input: List<String>): Int {
        val pos = arrayOf(1, 0, -1)
        return input.foldIndexed(0) { idx, acc, line ->
            var sum = acc
            line.forEachIndexed { index, c ->
                if (c != '*') return@forEachIndexed

                val extractedPos = arrayListOf<Pair<Int, Int>>()
                val nums = arrayListOf<Int>()
                for (x in pos) {
                    for (y in pos) {
                        if (x == 0 && y == 0) continue
                        if (input.getOrNull(idx + y)?.getOrNull(index + x)?.digitToIntOrNull() in 0..9) {
                            if (index + x to idx + y in extractedPos) continue
                            val (num, indices) = extractNumber(index + x, input[idx + y])
                            nums.add(num)
                            extractedPos.addAll(indices.map { it to idx + y })
                        }
                    }
                }

                if (nums.size > 1) sum += nums.reduce { acc, i -> acc * i }
            }

            sum
        }
    }

    val testInput = readInput("Day03_test")
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}

fun extractNumber(pos: Int, line: String): Pair<Int, List<Int>> {
    val indices = arrayListOf<Int>()
    var number = ""
    var index = pos
    while (line.getOrNull(index)?.digitToIntOrNull() in 0..9) {
        number += line[index]
        indices.add(index)
        index++
    }

    index = pos - 1
    while (line.getOrNull(index)?.digitToIntOrNull() in 0..9) {
        number = line[index] + number
        indices.add(index)
        index--
    }

    return number.toInt() to indices
}