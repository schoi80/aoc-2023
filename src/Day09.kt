fun List<Long>.extrapolate(): List<List<Long>> {
    val r = (1..<this.size).map { i ->
        this[i] - this[i - 1]
    }

    // smh....... wtf!!!!
//    if (r.sum() == 0L)
    if (r.count { it == 0L } == r.size)
        return listOf(this)

    return mutableListOf(this).apply { addAll(r.extrapolate()) }
}

fun List<List<Long>>.lastSeq(): Long {
    var curr = 0L
    for (i in (this.size - 1) downTo 0) {
        if (curr == 0L)
            curr = this[i].last()
        else curr += this[i].last()
    }
    return curr
}

fun List<List<Long>>.firstSeq(): Long {
    var curr = 0L
    for (i in (this.size - 1) downTo 0) {
        if (curr == 0L)
            curr = this[i].first()
        else curr = this[i].first() - curr
    }
    return curr
}

fun main() {

    val input = readInput("Day09")

    fun part1(input: List<String>): Long {
        return input.sumOf { line ->
            line.split("\\s+".toRegex())
                .map { it.trim().toLong() }
                .extrapolate()
                .onEach { println(it) }
                .lastSeq()
                .also { println("extrapolated: $it") }
        }
    }

    fun part2(input: List<String>): Long {
        return input.sumOf { line ->
            line.split("\\s+".toRegex())
                .map { it.trim().toLong() }
                .extrapolate()
                .onEach { println(it) }
                .firstSeq()
                .also { println("extrapolated: $it") }
        }
    }

    part1(input).println()
    part2(input).println()
}
