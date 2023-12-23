import java.util.*

fun main() {
    fun part1(input: List<String>): Int {
        val graph = Array(input.size) {
            IntArray(input.size) { idx ->
                input[it][idx].digitToInt()
            }
        }

        val dist = findMinHeatLoss(graph, listOf(State(Point2D(0, 0), Direction.RIGHT, 0)), 0, 3)
        return dist
    }

    fun part2(input: List<String>): Int {
        val graph = Array(input.size) {
            IntArray(input.size) { idx ->
                input[it][idx].digitToInt()
            }
        }

        val dist = findMinHeatLoss(graph, listOf(State(Point2D(0, 0), Direction.RIGHT, 0), State(Point2D(0, 0), Direction.DOWN, 0)), 4, 10)
        return dist
    }

    val testInput = readInput("Day17_test")
    check(part2(testInput) == 94)

    val input = readInput("Day17")
    part1(input).println()
    part2(input).println()
}

private fun findMinHeatLoss(grid: Array<IntArray>, initialStates: List<State>, minBlocks: Int, maxBlocks: Int): Int {
    val end = Point2D(grid.lastIndex, grid.lastIndex)

    val costs = mutableMapOf<State, Int>().withDefault { Int.MAX_VALUE }
    val toVisit = PriorityQueue<StateWithCost>()

    for (state in initialStates) {
        costs[state] = 0
        toVisit.add(StateWithCost(state, 0))
    }

    while (toVisit.isNotEmpty()) {
        val current = toVisit.poll()
        if (current.state.point == end) {
            return current.cost
        }

        current.state.next(minBlocks, maxBlocks)
            .filter { it.point.y in grid.indices && it.point.x in grid.indices }
            .forEach {
                val newCost = current.cost + grid[it.point.y][it.point.x]
                if (newCost < costs.getValue(it)) {
                    costs[it] = newCost
                    toVisit.add(StateWithCost(it, newCost))
                }
            }
    }

    return -1
}

private data class State(val point: Point2D, val dir: Direction, val blocks: Int) {
    fun next(minBlocks: Int, maxBlocks: Int) = buildList {
        when {
            blocks < minBlocks -> add(copy(point = point.translate(dir), dir = dir, blocks = blocks + 1))
            else -> {
                val left = dir.left()
                val right = dir.right()

                add(copy(point = point.translate(left), dir = left, blocks = 1))
                add(copy(point = point.translate(right), dir = right, blocks = 1))

                if (blocks < maxBlocks) {
                    add(copy(point = point.translate(dir), dir = dir, blocks = blocks + 1))
                }
            }
        }
    }
}

private data class StateWithCost(val state: State, val cost: Int) : Comparable<StateWithCost> {
    override fun compareTo(other: StateWithCost): Int {
        return cost compareTo other.cost
    }
}

//private fun dijkstra(graph: Array<IntArray>, src: Pair<Int, Int>): Int {
//    val maxIdx = graph.size
//    val dist = Array(maxIdx) { IntArray(maxIdx) { Int.MAX_VALUE } }
//    val visited = Array(maxIdx) { BooleanArray(maxIdx) }
//    val prev = Array(maxIdx) { Array(maxIdx) { -1 to -1 }  }
//    prev[src.first][src.second] = src
//
//    var (y, x) = src
//    dist[y][x] = 0
//    var finished = false
//    var count = 0
//
//    while (!finished) {
//        if (y < maxIdx - 1) {
//            if (!visited[y + 1][x] && dist[y + 1][x] > dist[y][x] + graph[y + 1][x] && !checkContinuous(prev, y, x, -1, 0)) {
//                dist[y + 1][x] = dist[y][x] + graph[y + 1][x]
//                prev[y + 1][x] = -1 to 0
//            }
//        }
//        if (y > 0) {
//            if (!visited[y - 1][x] && dist[y - 1][x] > dist[y][x] + graph[y - 1][x] && !checkContinuous(prev, y, x, 1, 0)) {
//                dist[y - 1][x] = dist[y][x] + graph[y - 1][x]
//                prev[y - 1][x] = 1 to 0
//            }
//        }
//
//        if (x < maxIdx - 1) {
//            if (!visited[y][x + 1] && dist[y][x + 1] > dist[y][x] + graph[y][x + 1] && !checkContinuous(prev, y, x, 0, -1)) {
//                dist[y][x + 1] = dist[y][x] + graph[y][x + 1]
//                prev[y][x + 1] = 0 to -1
//            }
//        }
//        if (x > 0) {
//            if (!visited[y][x - 1] && dist[y][x - 1] > dist[y][x] + graph[y][x - 1] && !checkContinuous(prev, y, x, 0, 1)) {
//                dist[y][x - 1] = dist[y][x] + graph[y][x - 1]
//                prev[y][x - 1] = 0 to 1
//            }
//        }
//
//        visited[y][x] = true
//
//        println("At step $count: $y, $x")
//        dist.mapIndexed { index, ints ->
//            ints.mapIndexed { idx, i ->
//                if (i == Int.MAX_VALUE) {
//                    "X"
//                } else {
//                    i.toString()
//                } + if (index == y && idx == x) {
//                    "."
//                } else {
//                    ""
//                }
//            }
//        }.joinToString("\n").println()
////        prev.joinToString("\n") { it.contentToString() }.println()
//        println()
//
//        val (newX, newY) = minDistance(dist, visited)
//        if (newX == -1 && newY == -1) {
//            finished = true
//        } else {
//            y = newX
//            x = newY
//        }
//        count += 1
//    }
//
//    val distTemp = graph.map { it.map { it.toString()[0] }.toMutableList() }
//    // Backtrack to find the shortest path and fill the path with X
//    val lst = mutableListOf<Int>()
//    y = maxIdx - 1
//    x = maxIdx - 1
//    while (y != 0 || x != 0) {
//        val (x1, y1) = prev[y][x]
//        lst.add(graph[y][x])
//        when {
//            x1 == 1 -> distTemp[y][x] = '^'
//            x1 == -1 -> distTemp[y][x] = 'v'
//            y1 == 1 -> distTemp[y][x] = '<'
//            y1 == -1 -> distTemp[y][x] = '>'
//        }
//        y += x1
//        x += y1
//    }
//
//    println()
//    distTemp.joinToString("\n").println()
//    println()
//    lst.reversed().println()
//
//    return dist[maxIdx - 1][maxIdx - 1]
//}
//
//// Check for 3 continuous indices to be in same direction
//private fun checkContinuous(prev: Array<Array<Pair<Int, Int>>>, x: Int, y: Int, dx: Int, dy: Int): Boolean {
//    if (x + 2 * dx !in prev.indices || y + 2 * dy !in prev.indices) return false
//
//    return prev[x + dx][y + dy] == prev[x][y] && (prev[x + 2 * dx][y + 2 * dy] == prev[x][y] || prev[x + 2 * dx][y + 2 * dy] == 0 to 0)
//}
//
//private fun minDistance(dist: Array<IntArray>, visited: Array<BooleanArray>): Pair<Int, Int> {
//    var min = Int.MAX_VALUE
//    var minIdx = Pair(-1, -1)
//
//    for (u in dist.indices) {
//        for (v in dist.indices) {
//            if (!visited[v][u] && dist[v][u] <= min) {
//                min = dist[v][u]
//                minIdx = v to u
//            }
//        }
//    }
//
//    return minIdx
//}