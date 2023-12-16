import kotlin.math.max

enum class Direction {
    LEFT, RIGHT, UP, DOWN
}

fun MutableInput<Char>.getDirection(i: Int, j: Int, d: Direction): List<Direction> {
    return when (d) {
        Direction.LEFT -> when (this[i][j]) {
            '.', '-' -> listOf(d)
            '\\' -> listOf(Direction.UP)
            '/' -> listOf(Direction.DOWN)
            '|' -> listOf(Direction.UP, Direction.DOWN)
            else -> error("Asd")
        }

        Direction.RIGHT -> when (this[i][j]) {
            '.', '-' -> listOf(d)
            '\\' -> listOf(Direction.DOWN)
            '/' -> listOf(Direction.UP)
            '|' -> listOf(Direction.UP, Direction.DOWN)
            else -> error("Asd")
        }

        Direction.UP -> when (this[i][j]) {
            '.', '|' -> listOf(d)
            '\\' -> listOf(Direction.LEFT)
            '/' -> listOf(Direction.RIGHT)
            '-' -> listOf(Direction.LEFT, Direction.RIGHT)
            else -> error("Asd")
        }

        Direction.DOWN -> when (this[i][j]) {
            '.', '|' -> listOf(d)
            '\\' -> listOf(Direction.RIGHT)
            '/' -> listOf(Direction.LEFT)
            '-' -> listOf(Direction.LEFT, Direction.RIGHT)
            else -> error("Asd")
        }
    }
}

fun MutableInput<Char>.activate(
    i: Int = 0,
    j: Int = 0,
    d: Direction = Direction.RIGHT,
    visited: MutableSet<Pair<RowCol, Direction>>
) {
    if (i < 0 || j < 0 || i >= this.size || j >= this[0].size || visited.contains((i to j) to d))
        return

    val next = getDirection(i, j, d)
    visited.add((i to j) to d)
    next.forEach {
        when (it) {
            Direction.LEFT -> activate(i, j - 1, it, visited)
            Direction.RIGHT -> activate(i, j + 1, it, visited)
            Direction.UP -> activate(i - 1, j, it, visited)
            Direction.DOWN -> activate(i + 1, j, it, visited)
        }
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        val newInput = input.toMutableInput { it }
        val visited = mutableSetOf<Pair<RowCol, Direction>>()
        newInput.activate(visited = visited)
        return visited.distinctBy { it.first }.size
    }

    fun part2(input: List<String>): Int {
        val newInput = input.toMutableInput { it }
        var maxCount = 0
        for (i in newInput.indices) {
            val visited = mutableSetOf<Pair<RowCol, Direction>>()
            newInput.activate(i, j = 0, d = Direction.RIGHT, visited = visited)
            val visited2 = mutableSetOf<Pair<RowCol, Direction>>()
            newInput.activate(i, j = newInput[0].size - 1, d = Direction.LEFT, visited = visited2)
            maxCount = max(maxCount, max(visited.distinctBy { it.first }.size, visited2.distinctBy { it.first }.size))
        }

        for (j in newInput[0].indices) {
            val visited = mutableSetOf<Pair<RowCol, Direction>>()
            newInput.activate(i = 0, j = j, d = Direction.DOWN, visited = visited)
            val visited2 = mutableSetOf<Pair<RowCol, Direction>>()
            newInput.activate(i = newInput.size - 1, j = j, d = Direction.UP, visited = visited2)
            maxCount = max(maxCount, max(visited.distinctBy { it.first }.size, visited2.distinctBy { it.first }.size))
        }

        return maxCount
    }

    val input = readInput("Day16")
    part1(input).println()
    part2(input).println()
}
