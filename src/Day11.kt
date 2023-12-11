import kotlin.math.abs

data class VirtualGalaxy(
    val input: Input,
    val exRows: List<Int>,
    val exCols: List<Int>,
    val expandSize: Long,
)

fun String.canExpand() = this.all { it == '.' }

fun Input.canExpand(i: Int) = this.map { it[i] }.joinToString("").canExpand()

fun String.expand(l: List<Int>): String {
    return l.reversed().fold(this) { acc, i ->
        acc.substring(0, i) + "." + acc.substring(i)
    }
}

fun Input.expand(l: List<Int>): Input {
    val row = CharArray(this[0].length) { '.' }.joinToString("")
    return l.reversed().fold(this) { acc, i ->
        acc.toMutableList().apply { add(i + 1, row) }
    }
}

fun Input.expand(): Input {
    val expandableCols = this[0].indices.filter { canExpand(it) }
    val expandableRows = this.indices.filter { this[it].canExpand() }.also { println(it) }
    return this.map { it.expand(expandableCols) }.expand(expandableRows)
}

fun RowCol.distance(rc: RowCol): Int {
    return (abs(first - rc.first) + abs(second - rc.second)).also {
        println("$this -> $rc = $it")
    }
}

fun VirtualGalaxy.calcDistance(rc1: RowCol, rc2: RowCol): Long {
    val rowRange = listOf(rc1.first, rc2.first).sorted().let { it[0]..it[1] }
    val colRange = listOf(rc1.second, rc2.second).sorted().let { it[0]..it[1] }

    val e1 = this.exRows.count { rowRange.contains(it) }
    val d1 = (rowRange.last() - rowRange.first()) - e1 + (e1 * this.expandSize)

    val e2 = this.exCols.count { colRange.contains(it) }
    val d2 = (colRange.last() - colRange.first()) - e2 + (e2 * this.expandSize)

    return d1 + d2
}

fun main() {

    fun part1(input: List<String>): Long {
        val galaxy = input.expand().onEach { println(it) }
        val stars = mutableListOf<RowCol>()
        galaxy.indices.forEach { i ->
            galaxy[0].indices.forEach { j ->
                if (galaxy.get(i to j) == '#')
                    stars.add(i to j)
            }
        }
        return stars.indices.sumOf {
            val origin = stars[it]
            (it..<stars.size).sumOf { origin.distance(stars[it]) }
        }.toLong()
    }

    fun part2(input: List<String>): Long {
        val galaxy = VirtualGalaxy(
            input = input,
            exRows = input.indices.filter { input[it].canExpand() },
            exCols = input[0].indices.filter { input.canExpand(it) },
            expandSize = 1000000
        )
        val stars = mutableListOf<RowCol>()
        input.indices.forEach { i ->
            input[0].indices.forEach { j ->
                if (input.get(i to j) == '#')
                    stars.add(i to j)
            }
        }

        return stars.indices.sumOf {
            val origin = stars[it]
            (it..<stars.size).sumOf { galaxy.calcDistance(origin, stars[it]) }
        }
    }

    val input = readInput("Day11")
    part1(input).println()
    part2(input).println()
}
