import java.util.PriorityQueue

fun Direction.isOpposite(d: Direction) = when (this) {
    Direction.UP -> d == Direction.DOWN
    Direction.DOWN -> d == Direction.UP
    Direction.LEFT -> d == Direction.RIGHT
    Direction.RIGHT -> d == Direction.LEFT
}

fun getDirection(rc1: RowCol, rc2: RowCol): Direction {
    val (i1, j1) = rc1
    val (i2, j2) = rc2
    return when {
        i1 == i2 && j1 < j2 -> Direction.RIGHT
        i1 == i2 && j1 > j2 -> Direction.LEFT
        i1 < i2 && j1 == j2 -> Direction.DOWN
        i1 > i2 && j1 == j2 -> Direction.UP
        else -> error("asdsa")
    }
}

data class Position(
    val rc: RowCol,
    val lastMoves: List<Direction> = listOf()
)

fun MutableInput<Long>.isEnd(curr: Position): Boolean {
    val (i, j) = curr.rc
    return (i == this.size - 1 && j == this[0].size - 1)
}

typealias CruciblePath = MutableMap<Position, Long>

fun canMoveInDirection1(curr: Position, dir: Direction, maxInDirection: Int): Boolean {
    val isOpposite = curr.lastMoves.lastOrNull()?.isOpposite(dir) ?: false
    val maxNotReached = curr.lastMoves.count { it == dir } < maxInDirection
    return !isOpposite && maxNotReached
}

fun canMoveInDirection2(curr: Position, dir: Direction, maxInDirection: Int): Boolean {
    val canTurn = if (curr.lastMoves.isNotEmpty()) {
        val lastMove = curr.lastMoves.last()
        if (dir != lastMove) {
            curr.lastMoves.takeLast(4).count { it == lastMove } == 4
        } else true
    } else true
    return canMoveInDirection1(curr, dir, maxInDirection) && canTurn
}

fun MutableInput<Long>.minHeatLossPath(
    maxInDirection: Int = 10,
    canMoveInDirection: (Position, Direction, Int) -> Boolean
): CruciblePath {
    val start = Position(rc = 0 to 0)
    val visited = mutableMapOf<Position, Long>().apply { put(start, 0L) }
    val q = PriorityQueue(compareBy<Position> { visited[it] ?: Long.MAX_VALUE }) //<CurrPosition>()
    q.add(start)
    while (q.isNotEmpty()) {
        val curr = q.poll()
        if (this.isEnd(curr))
            continue
        this.adjacent(curr.rc)
            .map { it to getDirection(curr.rc, it) }
            .filter { (_, dir) -> canMoveInDirection(curr, dir, maxInDirection) }
            .forEach { (next, dir) ->
                val nextPos = Position(rc = next, lastMoves = (curr.lastMoves + dir).takeLast(maxInDirection))
                val cost = visited[curr]!! + this.get(nextPos.rc)
                if (cost < (visited[nextPos] ?: Long.MAX_VALUE)) {
                    visited[nextPos] = cost
                    q.add(nextPos)
                }
            }
    }

    return visited
}


fun main() {

    fun part1(input: List<String>): Long {
        val grid = input.toMutableInput { it.digitToInt().toLong() }
        return grid.minHeatLossPath(3, ::canMoveInDirection1)
            .filterKeys { grid.isEnd(it) }
            .values.min()
    }

    fun part2(input: List<String>): Long {
        val grid = input.toMutableInput { it.digitToInt().toLong() }
        return grid.minHeatLossPath(10, ::canMoveInDirection2)
            .filterKeys { grid.isEnd(it) && it.lastMoves.takeLast(4).distinct().count() == 1 }
            .values.min()
    }

    val input = readInput("Day17")
    part1(input).println()
    part2(input).println()
}
