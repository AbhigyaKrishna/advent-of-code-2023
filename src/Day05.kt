import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicLong

private class FunctionMap(val from: String, val to: String, private val range: LongRange, private val mapper: (Long) -> Long) {
    operator fun contains(value: Long) = value in range
    operator fun invoke(value: Long) = mapper(value)
}

private val converts: MutableMap<String, String> = mutableMapOf()

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    fun part1(input: List<String>): Long {
        val values = input[0].substring(7).splitToSequence(" ").map { it.toLong() }

        val maps = readMaps(input.iterator())
        return seedToLocation(maps, values).min()
    }

    fun part2(input: List<String>): Long {
        val values = input[0].substring(7).split(" ").map { it.toLong() }

        var total = 0L
        val ranges = buildList {
            for (i in values.indices step 2) {
                val range = values[i] until values[i] + values[i + 1]
                addAll(range.split(50).filter { it.last >= it.first }.also { lst ->
                    total += lst.sumOf { it.last - it.first + 1 }
                })
            }
        }

        val progress = AtomicLong(0)
        val loop = GlobalScope.launch {
            while (true) {
                delay(1000)
                println("${progress.get()} / $total")
            }
        }

        val maps = readMaps(input.iterator())
        return ranges.map {
            GlobalScope.async(Dispatchers.IO) {
                seedToLocation(maps, it.asSequence()).minOf {
                    progress.getAndIncrement()
                    it
                }
            }
        }.minOf {
            runBlocking { it.await() }
        }.also {
            progress.set(total)
            runBlocking {
                loop.cancelAndJoin()
            }
        }
    }

    val testInput = readInput("Day05_test")
    check(part2(testInput) == 46L)

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}

private fun extractMap(input: Iterator<String>): Pair<String, Map<LongRange, (Long) -> Long>> {
    val firstLine = input.next()
    val name = firstLine.slice(0 until firstLine.length - 5)

    val ranges = buildMap<LongRange, (Long) -> Long> {
        while (input.hasNext()) {
            val line = input.next()
            if (line.isBlank()) break

            val (nextStart, prevStart, range) = line.split(" ").map { it.toLong() }
            val diff = nextStart - prevStart
            this[prevStart until (prevStart + range)] = { it + diff }
        }
    }

    return name to ranges
}

private fun seedToLocation(maps:  Map<String, List<FunctionMap>>, seeds: Sequence<Long>): Sequence<Long> {
    var values = seeds

    var from = "seed"
    while (from != "location") {
        val to = converts[from] ?: error("No conversion found for $from")
        val map = maps[to] ?: error("No map found for $to")

        values = values.map {
            for (mapper in map) {
                if (it in mapper) return@map mapper(it)
            }
            it
        }
        from = to
    }

    return values
}

private fun readMaps(iter: Iterator<String>): Map<String, List<FunctionMap>> {
    iter.next()
    iter.next()

    return buildMap {
        while (iter.hasNext()) {
            val (name, ranges) = extractMap(iter)
            val (from, to) = name.split("-to-")
            converts[from] = to

            this[to] = ranges.map { (range, mapper) -> FunctionMap(from, to, range, mapper) }
        }
    }
}

private fun LongRange.split(count: Int): List<LongRange> {
    val c = this.last - this.first + 1
    val amount = count.coerceAtMost(c.toInt())
    val step = c / amount

    return buildList {
        var start = first
        repeat(amount) {
            val end = start + step
            add(start until end)
            start = end
        }
    }
}