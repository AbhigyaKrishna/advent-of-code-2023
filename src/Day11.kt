import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Long {
        val galaxies = findGalaxyCoords(input, 1)

        return combinationSum(galaxies)
    }

    fun part2(input: List<String>): Long {
        val galaxies = findGalaxyCoords(input, 1000000 - 1)

        return combinationSum(galaxies)
    }

    val testInput = readInput("Day11_test")
    check(part1(testInput) == 374L)

    val input = readInput("Day11")
    part1(input).println()
    part2(input).println()
}

private fun findGalaxyCoords(input: List<String>, freeSpaceIncrement: Long): List<Pair<Long, Long>> {
    var x = 0
    var y = 0

    var dx = 0L
    var dy = 0L
    val galaxies = mutableMapOf<Pair<Int, Int>, Pair<Long, Long>>()
    while (x != input.size) {
        // rows
        var foundGalaxy = false
        for (i in x until input.size) {
            if (input[y][i] == '#') {
                galaxies[i to y] = i + dx to y + dy
                foundGalaxy = true
            }
        }

        if (!foundGalaxy && galaxies.keys.none { it.second == y }) {
            dy += freeSpaceIncrement
            for (entry in galaxies.entries) {
                if (entry.key.second > y) {
                    entry.setValue(entry.value.first to entry.value.second + freeSpaceIncrement)
                }
            }
        }

        // columns
        foundGalaxy = false
        for (i in y + 1 until input.size) {
            if (input[i][x] == '#') {
                galaxies[x to i] = x + dx to i + dy
                foundGalaxy = true
            }
        }

        if (!foundGalaxy && galaxies.keys.none { it.first == x }) {
            dx += freeSpaceIncrement
            for (entry in galaxies.entries) {
                if (entry.key.first > x) {
                    entry.setValue(entry.value.first + freeSpaceIncrement to entry.value.second)
                }
            }
        }

        x++
        y++
    }
    return galaxies.values.toList()
}

private fun combinationSum(candidates: List<Pair<Long, Long>>): Long {
    var sum = 0L
    for (i in candidates.indices) {
        for (j in i + 1 until candidates.size) {
            val (x1, y1) = candidates[i]
            val (x2, y2) = candidates[j]
            sum += abs(x1 - x2) + abs(y1 - y2)
        }
    }

    return sum
}