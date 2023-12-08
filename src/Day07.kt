private enum class HandType(val configuration: List<Int>) {
    FiveOfAKind(listOf(5)),
    FourOfAKind(listOf(4, 1)),
    FullHouse(listOf(3, 2)),
    ThreeOfAKind(listOf(3, 1, 1)),
    TwoPairs(listOf(2, 2, 1)),
    OnePair(listOf(2, 1, 1, 1)),
    HighCard(listOf(1, 1, 1, 1, 1)),
    ;
}

private val cardIndices = arrayOf('2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A')

private class CardHand(val cards: String, val bid: Int, val jAsWild: Boolean = false): Comparable<CardHand> {
    val handType: HandType by lazy {
        val cardsGroup = cards.groupingBy { it }.eachCount().toMutableMap()
        if (jAsWild && 'J' in cardsGroup && cardsGroup.size > 1) {
            val jCount = cardsGroup.remove('J')!!
            val (k, v) = cardsGroup.maxBy { it.value }
            cardsGroup[k] = v + jCount
        }

        when (cardsGroup.values.sortedDescending()) {
            HandType.FiveOfAKind.configuration -> HandType.FiveOfAKind
            HandType.FourOfAKind.configuration -> HandType.FourOfAKind
            HandType.FullHouse.configuration -> HandType.FullHouse
            HandType.ThreeOfAKind.configuration -> HandType.ThreeOfAKind
            HandType.TwoPairs.configuration -> HandType.TwoPairs
            HandType.OnePair.configuration -> HandType.OnePair
            else -> HandType.HighCard
        }
    }

    override fun compareTo(other: CardHand): Int {
        val handTypeCompare = -handType.compareTo(other.handType)
        if (handTypeCompare != 0) return handTypeCompare

        for (i in 0 until 5) {
            var cardIndex = cardIndices.indexOf(cards[i])
            var otherCardIndex = cardIndices.indexOf(other.cards[i])
            if (jAsWild) {
                if (cardIndex == 9)
                    cardIndex = 0
                else if (cardIndex < 9)
                    cardIndex++

                if (otherCardIndex == 9)
                    otherCardIndex = 0
                else if (otherCardIndex < 9)
                    otherCardIndex++
            }

            if (cardIndex != otherCardIndex)
                return cardIndex.compareTo(otherCardIndex)
        }

        return 0
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.asSequence()
            .map { it.split(" ") }
            .map { CardHand(it[0], it[1].toInt()) }
            .sorted()
            .mapIndexed { index, card -> card.bid * (index + 1) }
            .sum()
    }

    fun part2(input: List<String>): Int {
        return input.asSequence()
            .map { it.split(" ") }
            .map { CardHand(it[0], it[1].toInt(), true) }
            .sorted()
            .mapIndexed { index, card -> card.bid * (index + 1) }
            .sum()
    }

    val testInput = readInput("Day07_test")
    check(part2(testInput) == 5905)

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}