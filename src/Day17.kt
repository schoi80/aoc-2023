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

fun MutableInput<Long>.minDistance(): Long {
    val maxInDirection = 3
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
            .filter { (_, dir) ->
                val isOpposite = curr.lastMoves.lastOrNull()?.isOpposite(dir) ?: false
                val isNotRepeated = curr.lastMoves.count { it == dir } < maxInDirection
                !isOpposite && isNotRepeated
            }
            .forEach { (next, dir) ->
                val nextPos = Position(rc = next, lastMoves = (curr.lastMoves + dir).takeLast(maxInDirection))
                val cost = visited[curr]!! + this.get(nextPos.rc)
                if (cost < (visited[nextPos] ?: Long.MAX_VALUE)) {
                    visited[nextPos] = cost
                    q.add(nextPos)
                }
            }
    }

    return visited.filterKeys { this.isEnd(it) }.values.min()
}

fun MutableInput<Long>.minDistance2(): Long {
    val maxInDirection = 10
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
            .filter { (_, dir) ->
                val isOpposite = curr.lastMoves.lastOrNull()?.isOpposite(dir) ?: false
                val isNotRepeated = curr.lastMoves.count { it == dir } < maxInDirection
                val canTurn = if (curr.lastMoves.isNotEmpty()) {
                    val lastMove = curr.lastMoves.last()
                    if (dir != lastMove) {
                        curr.lastMoves.takeLast(4).count { it == lastMove } == 4
                    } else true
                } else true
                !isOpposite && isNotRepeated && canTurn
            }
            .forEach { (next, dir) ->
                val nextPos = Position(rc = next, lastMoves = (curr.lastMoves + dir).takeLast(maxInDirection))
                val cost = visited[curr]!! + this.get(nextPos.rc)
                if (cost < (visited[nextPos] ?: Long.MAX_VALUE)) {
                    visited[nextPos] = cost
                    q.add(nextPos)
                }
            }
    }

    return visited.filterKeys {
        this.isEnd(it) && it.lastMoves.takeLast(4).distinct().count() == 1
    }.values.min()
}


fun main() {

    fun part1(input: List<String>): Long {
        return input.toMutableInput { it.digitToInt().toLong() }.minDistance()
    }

    fun part2(input: List<String>): Long {
        return input.toMutableInput { it.digitToInt().toLong() }.minDistance2()
    }

    val input = readInput("Day17")
    part1(input).println()
    part2(input).println()
}
