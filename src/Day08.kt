import kotlinx.coroutines.*

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    fun part1(input: List<String>): Int {
        val path = input[0]
        val tracks = input.subList(2, input.size)
            .associate { it.slice(0..2) to (it.slice(7..9) to it.slice(12..14)) }

        var i = 0
        val len = path.length
        var curr = "AAA"
        while (curr != "ZZZ") {
            val (left, right) = tracks[curr]!!
            curr = if (path[i % len] == 'L') {
                left
            } else {
                right
            }
            i++
        }

        return i
    }

    fun part2(input: List<String>): Long {
        val path = input[0]

        val paths = mutableListOf<String>()
        val tracks = input.subList(2, input.size)
            .associate {
                it.slice(0..2).also { from ->
                    if (from[2] == 'A') paths.add(from)
                } to (it.slice(7..9) to it.slice(12..14))
            }

        val count = mutableListOf<Int>()
        val len = path.length

        for (p in paths) {
            var curr = p
            var i = 0
            while (curr[2] != 'Z') {
                val (left, right) = tracks[curr]!!
                curr = if (path[i % len] == 'L') {
                    left
                } else {
                    right
                }
                i++
            }

            count.add(i)
        }

        return count.lcm()
    }

    val testInput = readInput("Day08_test")
    check(part2(testInput) == 6L)

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}

private fun List<Int>.lcm(): Long {
    var result = 1L
    var d = 2
    var n = this
    while (true) {
        val count = n.count { it % d == 0 }
        if (count == 0) {
            if (d * d > n.max()) {
                break
            } else {
                d++
            }
        } else {
            result *= d
            n = n.map { if (it % d == 0) it / d else it }
        }
    }

    return result * n.max()
}