import kotlin.math.pow

fun main() {

    val input = readInput("Day04")

    fun winningNumbers(line: String): Set<String> {
        return line.split("|")[0].trim().split(":")[1].trim().split("\\s+".toRegex()).toSet()
    }

    fun score(line: String, winNums: Set<String>): Int {
        val myNums = line.split("|")[1].trim().split(" ").toSet()
        val count = myNums.filter { winNums.contains(it) }.toSet().count()
        if (count == 1)
            return 1
        else if (count > 1)
            return 2.0.pow(count - 1).toInt()
        else return 0
    }

    fun score2(line: String, winNums: Set<String>): Int {
        val myNums = line.split("|")[1].trim().split(" ").toSet()
        return myNums.filter { winNums.contains(it) }.toSet().count()
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            winningNumbers(line).let { winningNumbers ->
                score(line, winningNumbers).also { println("score: $it") }
            }
        }
    }

    fun part2(input: List<String>): Int {
        val copies = mutableMapOf<Int, Int>()
        input.forEachIndexed { i, line ->
            copies[i] = copies[i] ?: 1
            winningNumbers(line).let { winningNumbers ->
                val count = score2(line, winningNumbers)
                if (count > 0) {
                    (1..count).forEach {
                        val gameCard = it + i
                        copies[gameCard] = copies[i]!! + (copies[i + it] ?: 1)
                    }
                }
            }
        }
        copies.println()
        return copies.values.sum()
    }

    part1(input).println()
    part2(input).println()
}
