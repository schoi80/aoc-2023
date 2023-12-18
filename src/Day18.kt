import java.util.LinkedList
import kotlin.math.abs

data class DigPlan(
    val d: Direction,
    val v: Long,
    val c: String? = null
)

fun String.toDirection(): Direction {
    return when (this) {
        "L", "2" -> Direction.LEFT
        "R", "0" -> Direction.RIGHT
        "U", "3" -> Direction.UP
        "D", "1" -> Direction.DOWN
        else -> error("Asdasd")
    }
}

fun Char.toDirection(): Direction {
    return when (this) {
        '2' -> Direction.LEFT
        '0' -> Direction.RIGHT
        '3' -> Direction.UP
        '1' -> Direction.DOWN
        else -> error("Asdasd")
    }
}

fun String.parse(): DigPlan {
    return split(" ", limit = 3).let {
        DigPlan(
            d = it[0].toDirection(),
            v = it[1].toLong(),
            c = it[2]
        )
    }
}

fun String.parseV2(): DigPlan {
    return split(" ", limit = 3).let {
        val c = it[2].drop(2).dropLast(1)
        DigPlan(
            d = c.last().toDirection(),
            v = c.take(5).toLong(16),
        )
    }
}

fun RowCol.move(d: Direction): RowCol {
    return when (d) {
        Direction.LEFT -> first to second - 1
        Direction.RIGHT -> first to second + 1
        Direction.UP -> first - 1 to second
        Direction.DOWN -> first + 1 to second
    }
}

fun Pair<Long, Long>.move(d: Direction, v: Long): Pair<Long, Long> {
    return when (d) {
        Direction.LEFT -> first to second - v
        Direction.RIGHT -> first to second + v
        Direction.UP -> first - v to second
        Direction.DOWN -> first + v to second
    }
}

fun MutableInput<Char>.mark(start: RowCol, p: DigPlan): RowCol {
    var count = 0
    var curr = start
    while (count < p.v) {
        curr = curr.move(p.d)
        this.set(curr, '#')
        count++
    }
    return curr
}

fun MutableInput<Char>.fill(pos: RowCol) {
    val q = LinkedList<RowCol>()
    q.add(pos)
    while (q.isNotEmpty()) {
        val p = q.pop()
        if (this.get(p) == '#')
            continue
        if (this.get(p) == ' ') {
            this.set(p, '#')
            q.addAll(this.adjacent(p).filter { this.get(it) == ' ' })
        }
    }
}

fun main() {

    // Brute forcing this at first...
    fun part1(input: List<String>): Long {
        var grid = Array(500) { Array<Char>(300) { ' ' } }
        input.map { it.parse() }.fold(150 to 50) { acc, it ->
            grid.mark(acc, it)
        }
        grid = grid.filter { it.joinToString("").trim() != "" }.toTypedArray()
        grid.fill(1 to 192)
        /**
        grid.map { it.joinToString("") }.onEach { println(it) }
        grid.onEachIndexed { index, chars -> println("$index: ${chars.toList()}") }
         **/
        return grid.sumOf { line ->
            line.count { it == '#' }.toLong()
        }
    }

    // Okay.. I got some help from here
    // https://www.reddit.com/r/adventofcode/comments/18l0qtr/comment/kduu02z
    fun part2(input: List<String>): Long {
        val plans = input.map { it.parseV2() }
        val polygon: List<Pair<Long, Long>> = plans.runningFold(0L to 0L) { curr, plan -> curr.move(plan.d, plan.v) }

        // https://en.wikipedia.org/wiki/Shoelace_formula#Trapezoid_formula_2
        val area = polygon.indices.sumOf { i ->
            val curr = polygon[i]
            // If curr is last coordinate, it must wrap back to origin
            val next = if (i + 1 == polygon.size) polygon[0] else polygon[i + 1]
            (curr.first + next.first) * (curr.second - next.second)
        }.let { abs(it / 2) }

        // This is essentially the perimeter,
        // aka, every coordinate that represents the boundary of the polygon
        val boundaryPoints = plans.sumOf { it.v }

        // https://en.wikipedia.org/wiki/Pick%27s_theorem#Formula
        // A = i + (b/2) - 1
        // i = A - (b/2) + 1
        val interiorPoints = area - (boundaryPoints / 2) + 1

        // Cubic meter of lava is just the sum of boundary + interior points
        return boundaryPoints + interiorPoints
    }

    val input = readInput("Day18")
    part1(input).println()
    part2(input).println()
}
