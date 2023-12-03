typealias RowCol = Pair<Int, Int>

fun main() {

    val input = readInput("Day03")

    fun Char.isSymbol() = !isDigit() && this != '.'

    fun isPartNumber(row: Int, col: Int): Boolean {
        println("evaluating input[$row][$col]")
        if (input[row][col].isDigit()) {
            val rows = ((row - 1)..(row + 1)).filter {
                it >= 0 && it < input.size
            }
            val cols = ((col - 1)..(col + 1)).filter {
                it >= 0 && it < input[row].length
            }
            rows.forEach { r ->
                cols.forEach { c ->
                    if (input[r][c].isSymbol())
                        return true
                }
            }
        }
        return false
    }

    fun findNumber(row: Int): List<Int> {
        val line = input[row]
        val regex = "\\d+".toRegex()
        val result = regex.findAll(line).filter {
            it.range.forEach { col ->
                if (isPartNumber(row, col)) {
                    println("${it.value} is part")
                    return@filter true
                }
            }
            return@filter false
        }.map { it.value.toInt() }.toList()
        return result
    }

    fun isPartNumber2(rc: RowCol): RowCol? {
        val row = rc.first
        val col = rc.second
        println("evaluating input[$row][$col]")
        if (input[row][col].isDigit()) {
            val rows = ((row - 1)..(row + 1)).filter {
                it >= 0 && it < input.size
            }
            val cols = ((col - 1)..(col + 1)).filter {
                it >= 0 && it < input[row].length
            }
            rows.forEach { r ->
                cols.forEach { c ->
                    if (input[r][c].isSymbol())
                        return r to c
                }
            }
        }
        return null
    }

    fun findNumber2(row: Int): List<Pair<Int, RowCol>> {
        val line = input[row]
        val regex = "\\d+".toRegex()
        val result = regex.findAll(line).mapNotNull {
            it.range.forEach { col ->
                val symbolCoordinate = isPartNumber2(row to col)
                if (symbolCoordinate != null) {
                    return@mapNotNull it.value.toInt() to symbolCoordinate
                }
            }
            null
        }.toList()
        return result
    }

    fun part1(input: List<String>): Int {
        return input.indices.flatMap { row ->
            findNumber(row)
        }.sum()
    }

    fun part2(input: List<String>): Int {
        return input.indices.flatMap { row ->
            findNumber2(row)
        }.filter { (_, rc) ->
            input[rc.first][rc.second] == '*'
        }.groupBy {
            it.second
        }.filterValues {
            it.size == 2
        }.values.sumOf {
            it.map { it.first }.reduce { acc, it -> acc * it }
        }
    }

    part1(input).println()
    part2(input).println()
}
