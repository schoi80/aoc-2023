import kotlin.math.pow

fun main() {

    val input = readInput("Day04")

    fun String.splitNumbers(): Pair<Set<String>, Set<String>> {
        return split(":")[1].split("|").map {
            it.trim().split("\\s+".toRegex()).toSet()
        }.let { it[0] to it[1] }
    }

    fun String.matchCount(): Int {
        val (winningNumbers, myNumbers) = splitNumbers()
        return myNumbers.count { winningNumbers.contains(it) }
    }

    fun String.score(): Int {
        return matchCount().let {
            if (it >= 1)
                2.0.pow(it - 1).toInt()
            else 0
        }
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { it.score() }
    }

    fun part2(input: List<String>): Int {
        val copies = mutableMapOf<Int, Int>()
        input.forEachIndexed { i, line ->
            copies[i] = copies[i] ?: 1
            val count = line.matchCount()
            if (count > 0) {
                (1..count).forEach {
                    val gameCard = it + i
                    copies[gameCard] = copies[i]!! + (copies[gameCard] ?: 1)
                }
            }
        }
        return copies.values.sum()
    }
    input[0].splitNumbers().println()
    part1(input).println()
    part2(input).println()
}
