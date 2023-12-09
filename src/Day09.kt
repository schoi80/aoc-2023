fun List<Long>.extrapolate(): List<List<Long>> {
    return (1..<this.size).map { i -> this[i] - this[i - 1] }.let {
        if (it.all { it == 0L }) listOf(this)
        else mutableListOf(this).apply { addAll(it.extrapolate()) }
    }
}

fun List<List<Long>>.lastSeq(): Long {
    return ((this.size - 1) downTo 0).fold(0L) { acc, i ->
        if (acc == 0L) this[i].last()
        else acc + this[i].last()
    }
}

fun List<List<Long>>.firstSeq(): Long {
    return ((this.size - 1) downTo 0).fold(0L) { acc, i ->
        if (acc == 0L) this[i].first()
        else this[i].first() - acc
    }
}

fun main() {

    val input = readInput("Day09")

    fun part1(input: List<String>): Long {
        return input.sumOf { line ->
            line.split("\\s+".toRegex())
                .map { it.trim().toLong() }
                .extrapolate()
//                .onEach { println(it) }
                .lastSeq()
//                .also { println("extrapolated: $it") }
        }
    }

    fun part2(input: List<String>): Long {
        return input.sumOf { line ->
            line.split("\\s+".toRegex())
                .map { it.trim().toLong() }
                .extrapolate()
//                .onEach { println(it) }
                .firstSeq()
//                .also { println("extrapolated: $it") }
        }
    }

    part1(input).println()
    part2(input).println()
}
