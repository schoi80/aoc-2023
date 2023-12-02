import kotlin.math.max

fun main() {

    fun isPossible(mm: Map<String, Int>, marbles: String): Boolean {
        marbles.trim().split(";").forEach { mb ->
            mb.trim().split(",").associate {
                it.trim().split(" ").let { it[1] to it[0].toInt() }
            }.forEach { (color, count) ->
                if (count > (mm[color] ?: 0))
                    return false
            }
        }
        return true
    }

    fun part1(input: List<String>): Int {
        val m = mapOf(
            "red" to 12,
            "green" to 13,
            "blue" to 14,
        )
        return input.sumOf { line ->
            val (game, marbles) = line.split(":").let { it[0].split(" ")[1].toInt() to it[1] }
            if (isPossible(m, marbles))
                game
            else 0
        }
    }

    fun minRequired(marbles: String): Int {
        val maxMap = mutableMapOf<String, Int>()
        marbles.trim().split(";").map { mb ->
            mb.trim().split(",").associate {
                it.trim().split(" ").let { it[1] to it[0].toInt() }
            }.forEach { (color, count) ->
                maxMap[color] = max(count, maxMap[color] ?: 0)
            }
        }
        return maxMap.values.fold(1) { acc, i -> acc * i }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { line ->
            val (_, marbles) = line.split(":").let { it[0].split(" ")[1].toInt() to it[1] }
            minRequired(marbles)
        }
    }

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
