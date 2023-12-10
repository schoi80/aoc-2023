fun main() {

    val input = readInput("Day03")

    fun RowCol.isOutOfBounds(): Boolean {
        val inRange = first >= 0 && first < input.size && second >= 0 && second < input[first].length
        return !inRange
    }

    fun RowCol.isSymbol(): Boolean {
        if (!isOutOfBounds()) {
            val c = input[first][second]
            return !c.isDigit() && c != '.'
        }
        return false
    }

    fun RowCol.isPartNumber(): Boolean {
        if (input[first][second].isDigit()) {
            ((first - 1)..(first + 1)).forEach { r ->
                ((second - 1)..(second + 1)).forEach { c ->
                    if ((r to c).isSymbol())
                        return true
                }
            }
        }
        return false
    }

    fun findNumber(row: Int): List<Int> {
        val line = input[row]
        val regex = "\\d+".toRegex()
        return regex.findAll(line).filter {
            it.range.forEach { col ->
                if ((row to col).isPartNumber()) {
                    return@filter true
                }
            }
            return@filter false
        }.map { it.value.toInt() }.toList()
    }

    fun RowCol.isPartNumber2(): RowCol? {
        if (input[first][second].isDigit()) {
            ((first - 1)..(first + 1)).forEach { r ->
                ((second - 1)..(second + 1)).forEach { c ->
                    if ((r to c).isSymbol())
                        return r to c
                }
            }
        }
        return null
    }

    fun findNumber2(row: Int): List<Pair<Int, RowCol>> {
        val line = input[row]
        val regex = "\\d+".toRegex()
        return regex.findAll(line).mapNotNull { mr ->
            mr.range.forEach { col ->
                (row to col).isPartNumber2()?.let {
                    return@mapNotNull mr.value.toInt() to it
                }
            }
            null
        }.toList()
    }

    fun part1(input: List<String>): Int {
        return input.indices.flatMap { findNumber(it) }.sum()
    }

    fun part2(input: List<String>): Int {
        return input.indices.flatMap { findNumber2(it) }
            .filter { (_, rc) -> input[rc.first][rc.second] == '*' }
            .groupBy { it.second }
            .filterValues { it.size == 2 }
            .values.sumOf { it.map { it.first }.reduce { acc, it -> acc * it } }
    }

    part1(input).println()
    part2(input).println()
}
