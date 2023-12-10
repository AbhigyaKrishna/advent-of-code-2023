fun main() {
    fun part1(input: List<String>): Int {
        val numbers = input.map { it.splitToSequence(" ").map { n -> n.toInt() } }

        return numbers.sumOf { findNext(it) }
    }

    fun part2(input: List<String>): Int {
        val numbers = input.map { it.splitToSequence(" ").map { n -> n.toInt() } }

        return numbers.sumOf { findPrev(it) }
    }

    val testInput = readInput("Day09_test")
    check(part2(testInput) == 2)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}

private fun findNext(numbers: Sequence<Int>): Int {
    if (numbers.all { it == 0 }) {
        return 0
    }

    val next = findNext(sequence {
        val iter = numbers.iterator()
        var prev = iter.next()
        while (iter.hasNext()) {
            val next = iter.next()
            yield(next - prev)
            prev = next
        }
    })

    return numbers.last() + next
}

private fun findPrev(numbers: Sequence<Int>): Int {
    if (numbers.all { it == 0 }) {
        return 0
    }

    val prev = findPrev(sequence {
        val iter = numbers.iterator()
        var prev = iter.next()
        while (iter.hasNext()) {
            val next = iter.next()
            yield(next - prev)
            prev = next
        }
    })

    return numbers.first() - prev
}