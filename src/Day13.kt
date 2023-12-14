fun main() {
    fun part1(input: List<String>): Int {
        return input.chunked { it.isBlank() }
            .map { it to findHorizontalMirror(it, false) + 1 }
            .sumOf { (lst, num) ->
                if (num == 0)
                    findVerticalMirror(lst,  false) + 1
                else
                    num * 100
            }
    }

    fun part2(input: List<String>): Int {
        return input.chunked { it.isBlank() }
            .map { it to findHorizontalMirror(it, true) + 1 }
            .sumOf { (lst, num) ->
                if (num == 0)
                    findVerticalMirror(lst,  true) + 1
                else
                    num * 100
            }
    }

    val testInput = readInput("Day13_test")
    check(part2(testInput) == 400)

    val input = readInput("Day13")
    part1(input).println()
    part2(input).println()
}

private fun findVerticalMirror(input: List<String>, checkSmudge: Boolean): Int {
    var smudgeCheck: Boolean
    val xLength = input[0].length
    val yLength = input.size
    outer@ for (x in 0 until xLength - 1) {
        smudgeCheck = checkSmudge
        var i = 0
        while (x - i >= 0 && x + i + 1 < xLength) {
            for (y in 0..<yLength) {
                if (input[y][x - i] != input[y][x + i + 1]) {
                    if (smudgeCheck) {
                        smudgeCheck = false
                    } else {
                        continue@outer
                    }
                }
            }

            i++
        }

        if (smudgeCheck) continue@outer

        return x
    }

    return -1
}

private fun findHorizontalMirror(input: List<String>, checkSmudge: Boolean): Int {
    var smudgeCheck: Boolean
    val xLength = input[0].length
    val yLength = input.size
    outer@ for (y in 0 until yLength - 1) {
        smudgeCheck = checkSmudge
        var i = 0
        while (y - i >= 0 && y + i + 1 < yLength) {
            for (x in 0..<xLength) {
                if (input[y - i][x] != input[y + i + 1][x]) {
                    if (smudgeCheck) {
                        smudgeCheck = false
                    } else {
                        continue@outer
                    }
                }
            }

            i++
        }

        if (smudgeCheck) continue@outer

        return y
    }

    return -1
}