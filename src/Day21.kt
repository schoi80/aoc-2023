typealias Garden = MutableInput<Char>

fun Garden.canStep(curr: RowCol): List<RowCol> {
    return adjacent(curr).filter { this.get(it).let { it == '.' || it == 'S' } }
}

fun Garden.step(curr: Set<RowCol>): MutableSet<RowCol> {
    return curr.fold(mutableSetOf()) { acc, it ->
        acc.addAll(canStep((it)))
        acc
    }
}

fun Garden.findStart(): RowCol {
    for (i in indices) {
        for (j in this[0].indices) {
            if (this.get(i to j) == 'S')
                return i to j
        }
    }
    error("cannot happen")
}

fun Garden.count(): Int {
    var count = 0
    for (i in indices) {
        for (j in this[0].indices) {
            if (this.get(i to j) == '.' || this.get(i to j) == 'S')
                count++
        }
    }
    return count
}

fun Garden.adjacentInf(rc: RowCol): List<RowCol> {
    return listOf(rc.up(), rc.down(), rc.left(), rc.right())
}

fun Garden.getInf(rc: RowCol): Char {
    var (i, j) = rc
    i = (i % this.size)
    j = (j % this[0].size)
    if (i < 0)
        i += this.size
    if (j < 0)
        j += this[0].size
    return this.get(i to j)
}

//val cache2 = mutableMapOf<RowCol, List<RowCol>>()
fun Garden.canStepInf(curr: RowCol): List<RowCol> {
    return adjacentInf(curr).filter { this.getInf(it).let { it == '.' || it == 'S' } }
}

fun Garden.stepInf(curr: Set<RowCol>): MutableSet<RowCol> {
    return curr.fold(mutableSetOf()) { acc, it ->
        acc.addAll(canStepInf((it)))
        acc
    }
}

fun main() {

    fun part1(input: List<String>): Long {
        val g = input.toMutableInput { it }
        val s = g.findStart()
        val r = (1..64).fold(mutableSetOf(s)) { acc, _ ->
            g.step(acc)
        }
        return r.size.toLong()
    }

    fun part2(input: List<String>): Long {
//        val g = input.toMutableInput { it }
//        g.count().println()
//        val s = g.findStart()
//        var d1 = 1
//        var d2 = 0
//        var d3 = 0
//        val r = (1..26501365).fold(mutableSetOf(s)) { acc, i ->
//            g.stepInf(acc).also {
//                val nd1 = it.size - d1
//                val nd2 = nd1 - d1
//                val nd3 = nd2 - d2
//                if (i%131 == 65)
//                println("$i;${it.size};$nd1;$nd2;$nd3")
//                d1 = nd1
//                d2 = nd2
//                d3 = nd3
//            }
//        }
//        return r.size.toLong()

        // I'm too dumb to calculate quad formula.
        // https://www.wolframalpha.com/input?i=quadratic+fit+calculator&assumption=%7B%22F%22%2C+%22QuadraticFitCalculator%22%2C+%22data3x%22%7D+-%3E%22%7B0%2C+1%2C+2%2C+3%7D%22&assumption=%7B%22F%22%2C+%22QuadraticFitCalculator%22%2C+%22data3y%22%7D+-%3E%22%7B3701%2C33108%2C91853%2C179936%7D%22
        val x = 202300L
        return (x * x * 14669) + (14738 * x) + 3701
    }

    val input = readInput("Day21")
//    part1(input).println()
    part2(input).println()
}