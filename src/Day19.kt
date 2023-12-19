import kotlinx.coroutines.*

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    fun part1(input: List<String>): Int {
        val workflows = readWorkflows(input) {
            val last = it.last()
            val lst = mutableListOf<Pair<(WorkflowData) -> Boolean, String>>()
            for (i in 0 until it.size - 1) {
                val (condition, goto) = it[i].split(":")
                lst.add(readCondition(condition) to goto)
            }

            object : Workflow() {
                override fun apply(data: WorkflowData): String {
                    for ((condition, goto) in lst) {
                        if (condition(data)) {
                            return goto
                        }
                    }
                    return last
                }
            }
        }
        val data = readData(input)

        return runBlocking {
            data.map {
                GlobalScope.async(Dispatchers.IO) {
                    var current = "in"
                    while (current != "A" && current != "R") {
                        current = workflows[current]!!.apply(it)
                    }
                    when (current) {
                        "A" -> it.sum()
                        "R" -> 0
                        else -> error("Unknown workflow: $current")
                    }
                }
            }
                .awaitAll()
                .sum()
        }
    }

    fun part2(input: List<String>): Long {
        val workflows = readWorkflows(input) {
            val last = it.last()
            val lst = mutableListOf<Triple<(WorkflowRange) -> WorkflowRange, (WorkflowRange) -> WorkflowRange, String>>()
            for (i in 0 until it.size - 1) {
                val (condition, goto) = it[i].split(":")
                val num = condition.substring(2).toInt()
                val con: Pair<(IntRange) -> IntRange, (IntRange) -> IntRange> = when (condition[1]) {
                    '>' -> Pair({ x -> x.first.coerceAtLeast(num + 1)..x.last }, { x -> x.first..x.last.coerceAtMost(num) })
                    '<' -> Pair({ x -> x.first..x.last.coerceAtMost(num - 1) }, { x -> x.first.coerceAtLeast(num)..x.last })
                    else -> error("Unknown condition: $condition")
                }

                lst.add(when (condition[0]) {
                    'x' -> Triple({ data: WorkflowRange -> data.copy(x = con.first(data.x)) }, { data: WorkflowRange -> data.copy(x = con.second(data.x)) }, goto)
                    'm' -> Triple({ data: WorkflowRange -> data.copy(m = con.first(data.m)) }, { data: WorkflowRange -> data.copy(m = con.second(data.m)) }, goto)
                    'a' -> Triple({ data: WorkflowRange -> data.copy(a = con.first(data.a)) }, { data: WorkflowRange -> data.copy(a = con.second(data.a)) }, goto)
                    's' -> Triple({ data: WorkflowRange -> data.copy(s = con.first(data.s)) }, { data: WorkflowRange -> data.copy(s = con.second(data.s)) }, goto)
                    else -> error("Unknown condition: $condition")
                })
            }

            object : RangeWorkflow() {
                override fun apply(data: WorkflowRange): List<Pair<WorkflowRange, String>> {
                    return buildList {
                        var original = data
                        for ((correct, wrong, goto) in lst) {
                            add(correct(original) to goto)
                            original = wrong(original)
                        }
                        add(original to last)
                    }
                }
            }
        }

        fun runWorkflow(range: WorkflowRange, current: String): Long {
            return workflows[current]!!.apply(range).sumOf { (range, goto) ->
                when (goto) {
                    "A" -> range.combination()
                    "R" -> 0
                    else -> runWorkflow(range, goto)
                }
            }
        }

        return runWorkflow(WorkflowRange(), "in")
    }


    val testInput = readInput("Day19_test")
    check(part2(testInput) == 167409079868000L)

    val input = readInput("Day19")
    part1(input).println()
    part2(input).println()
}

private data class WorkflowData(val x: Int, val m: Int, val a: Int, val s: Int) {

    fun sum(): Int = x + m + a + s

}

private data class WorkflowRange(val x: IntRange = 1..4000, val m: IntRange = 1..4000, val a: IntRange = 1..4000, val s: IntRange = 1..4000) {

    fun combination(): Long {
        return x.length.coerceAtLeast(1).toLong() * m.length.coerceAtLeast(1).toLong() *
                a.length.coerceAtLeast(1).toLong() * s.length.coerceAtLeast(1).toLong()
    }

    private val IntRange.length: Int get() = if (isEmpty()) 0 else endInclusive - start + 1

}

private abstract class Workflow {

    abstract fun apply(data: WorkflowData): String

}

private abstract class RangeWorkflow {

    abstract fun apply(data: WorkflowRange): List<Pair<WorkflowRange, String>>

}

private fun <T> readWorkflows(input: List<String>, mapper: (List<String>) -> T): Map<String, T> {
    val workflows = mutableMapOf<String, T>()
    input.takeWhile { it.isNotBlank() }.forEach {
        val name = it.takeWhile { c -> c != '{' }
        val conditions = it.dropWhile { c -> c != '{' }.drop(1).dropLast(1).split(",")
        workflows[name] = mapper(conditions)
    }
    return workflows.toMap()
}

private fun readData(input: List<String>): List<WorkflowData> {
    return input.takeLastWhile { it.isNotBlank() }.map {
        val (x, m, a, s) = it.drop(1).dropLast(1).split(",").map { s -> s.substring(2).toInt() }
        WorkflowData(x, m, a, s)
    }
}

private fun readCondition(condition: String): (WorkflowData) -> Boolean {
    val num = condition.substring(2).toInt()
    val con: (Int) -> Boolean = when (condition[1]) {
        '>' -> { x -> x > num }
        '<' -> { x -> x < num }
        else -> error("Unknown condition: $condition")
    }

    when (condition[0]) {
        'a' -> return { (_, _, a, _) -> con(a) }
        's' -> return { (_, _, _, s) -> con(s) }
        'm' -> return { (_, m, _, _) -> con(m) }
        'x' -> return { (x, _, _, _) -> con(x) }
        else -> error("Unknown condition: $condition")
    }
}