import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    fun part1(input: List<String>): Long {
        return input.asSequence()
            .map { it.split(" ") }
            .map { it.first() to it.last().split(",").map { num -> num.toInt() } }
            .sumOf { (pattern, nums) -> findNumberOfArrangements(pattern, nums) }
    }

    fun part2(input: List<String>): Long {
        val counter = AtomicInteger(0)
        val job = GlobalScope.launch {
            while (true) {
                delay(1000)
                println("Progress: ${counter.get()} / 1000")
            }
        }
        return runBlocking {
            input.asSequence()
                .map { it.split(" ") }
                .map { it[0].repeat(5, "?") to it.last().split(",").map { num -> num.toInt() }.repeat(5) }
                .map { (pattern, nums) -> GlobalScope.async(Dispatchers.IO) {
                    val v = findNumberOfArrangements(pattern, nums)
                    counter.incrementAndGet()
                    v
                } }
                .toList()
                .awaitAll()
                .sum()
                .also {
                    job.cancelAndJoin()
                }
        }
    }

    val testInput = readInput("Day12_test")
    check(part2(testInput) == 525152L)

    val input = readInput("Day12")
    part1(input).println()
    part2(input).println()
}

private val DP: MutableMap<Pair<String, List<Int>>, Long> = mutableMapOf()

private fun findNumberOfArrangements(pattern: String, nums: List<Int>): Long {
//    println("Starting with $pattern and $nums")
    if ((pattern to nums) in DP) {
//        println("Found in DP")
        runCatching {
            return DP[pattern to nums]!!
        }
    }
    if (nums.isEmpty()) return 1
    val current = nums[0]
    if (pattern.length < current) return 0
    val hashCount = pattern.count { it == '#' }
    var i = 0
    var total = 0L
    while (i < pattern.length && i + current <= pattern.length) {
//        println("Step $pattern -> $i")
        if (pattern[i] != '.') {
            val sub = pattern.slice(i until i + current)
//            println("Sub: $sub")
            if (nums.size == 1 && sub.count { it == '#' } != hashCount) {
//                println("Invalid sub due to hash count")
                i++
                continue
            }
            if (sub.indexOf('.') == -1) {
                val prev = if (i == 0) '.' else pattern[i - 1]
                val next = if (i + current >= pattern.length) '.' else pattern[i + current]
                if (prev != '#' && next != '#') {
//                    println("Valid sub recursing others")
                    total += findNumberOfArrangements(pattern.slice(i + current + 1 until pattern.length), nums.drop(1))
                }
            }

            if (pattern[i] == '#') break
        }

        i++
    }

    DP[pattern to nums] = total
    return total
}