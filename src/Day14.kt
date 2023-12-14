fun main() {
    fun part1(input: List<String>): Int {
        val xLength = input[0].length
        val yLength = input.size

        val emptySpaces = Array(xLength) { ArrayDeque<Int>() }
        var sum = 0
        for (y in input.indices) {
            val curr = input[y]
            for (x in 0..<xLength) {
                when (curr[x]) {
                    '.' -> emptySpaces[x].add(y)
                    'O' -> {
                        val empty = emptySpaces[x].removeFirstOrNull()
                        if (empty != null) {
                            emptySpaces[x].add(y)
                        }

                        sum += yLength - (empty ?: y)
                    }
                    '#' -> emptySpaces[x].clear()
                }
            }
        }

        return sum
    }

    fun part2(input: List<String>): Int {
        val num = 1000000000
        val seenMap: MutableMap<List<String>, Int> = mutableMapOf()
        val seen: MutableList<List<String>> = arrayListOf()
        var board = input
        var i = 0
        while (i < num) {
            for (j in 1..4) {
                board = rollNorth(board)
                board = rotateBoard(board)
            }

            seen.add(board)
            val seenAt = seenMap.put(board, i)
            if (seenAt != null) {
                val cycle = i - seenAt
                val idx = (num - i) % cycle
                board = seen[seenAt + idx - 1]
                break
            }
            i++
        }

        return board.mapIndexed { idx, s ->
            s.count { it == 'O' } * (board.size - idx)
        }.sum()
    }

    val testInput = readInput("Day14_test")
    check(part2(testInput) == 64)

    val input = readInput("Day14")
    part1(input).println()
    part2(input).println()
}

private fun rollNorth(input: List<String>): List<String> {
    val xLength = input[0].length
    val yLength = input.size

    val board = Array(yLength) { input[it].toCharArray() }
    val emptySpaces = Array(xLength) { ArrayDeque<Int>() }
    for (y in board.indices) {
        val curr = board[y]
        for (x in 0..<xLength) {
            when (curr[x]) {
                '.' -> emptySpaces[x].add(y)
                'O' -> {
                    val empty = emptySpaces[x].removeFirstOrNull()
                    if (empty != null) {
                        board[empty][x] = 'O'
                        board[y][x] = '.'
                        emptySpaces[x].add(y)
                    }
                }
                '#' -> emptySpaces[x].clear()
            }
        }
    }

    return board.map { it.concatToString() }
}

private fun rotateBoard(board: List<String>): List<String> {
    val xLength = board[0].length
    val yLength = board.size

    val newBoard = Array(xLength) { CharArray(yLength) }
    for (y in board.indices) {
        val curr = board[y]
        for (x in 0 until xLength) {
            newBoard[x][yLength - y - 1] = curr[x]
        }
    }

    return newBoard.map { it.concatToString() }
}