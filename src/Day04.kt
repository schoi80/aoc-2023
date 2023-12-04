import kotlin.math.pow

fun main() {

    val input = readInput("Day04")

    fun String.winningNumbers(): Set<String> {
        return split("|")[0].trim()
            .split(":")[1].trim()
            .split("\\s+".toRegex()).toSet()
    }

    fun String.matchCount(winNums: Set<String>): Int {
        return split("|")[1].trim()
            .split(" ").toSet()
            .filter { winNums.contains(it) }.toSet()
            .count()
    }

    fun String.score(winNums: Set<String>): Int {
        return matchCount(winNums).let {
            if (it >= 1)
                2.0.pow(it - 1).toInt()
            else 0
        }
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { line -> line.score(line.winningNumbers()) }
    }

    fun part2(input: List<String>): Int {
        val copies = mutableMapOf<Int, Int>()
        input.forEachIndexed { i, line ->
            copies[i] = copies[i] ?: 1
            val count = line.matchCount(line.winningNumbers())
            if (count > 0) {
                (1..count).forEach {
                    val gameCard = it + i
                    copies[gameCard] = copies[i]!! + (copies[gameCard] ?: 1)
                }
            }
        }
        return copies.values.sum()
    }

    part1(input).println()
    part2(input).println()
}
