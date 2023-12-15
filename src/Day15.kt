fun main() {
    fun part1(input: List<String>): Int {
        return input[0].splitToSequence(",")
            .map(::hash)
            .sumOf { it.toInt() }
    }

    fun part2(input: List<String>): Int {
        val lens: Array<ArrayList<LabeledLens>> = Array(256) { ArrayList<LabeledLens>() }
        input[0].splitToSequence(",")
            .forEach { str ->
                if (str.last() != '-') {
                    val l = LabeledLens(str.slice(0 until str.indexOf('=')), str.last().digitToInt())
                    val hash = l.hashCode()
                    lens[hash].let {
                        val lensIdx = it.indexOf(l)
                        if (lensIdx == -1) {
                            it.add(l)
                        } else {
                            it[lensIdx] = l
                        }
                    }
                } else {
                    val l = LabeledLens(str.slice(0 until str.length - 1), -1)
                    val hash = l.hashCode()
                    lens[hash].remove(l)
                }
            }

        return lens.mapIndexed { index, lenses ->
            lenses.mapIndexed { idx, labeledLens ->
                labeledLens.calculateValue(index, idx)
            }.sum()
        }.sum()
    }

    val testInput = readInput("Day15_test")
    check(part2(testInput) == 145)

    val input = readInput("Day15")
    part1(input).println()
    part2(input).println()
}

private fun hash(input: String): UByte {
    return input.fold(0U.toUByte()) { acc, c ->
        ((acc.toInt() + c.code) * 17).toUByte()
    }
}

private class LabeledLens(val label: String, val focus: Int) {
    fun calculateValue(boxIdx: Int, lensIdx: Int): Int {
        return (boxIdx + 1) * (lensIdx + 1) * focus
    }

    override fun hashCode(): Int {
        return hash(label).toInt()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LabeledLens) return false

        if (label != other.label) return false

        return true
    }

    override fun toString(): String {
        return "$label:$focus"
    }
}