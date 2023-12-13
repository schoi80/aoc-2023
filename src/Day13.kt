import kotlin.math.min

fun Input.split(): List<Input> {
    val r = mutableListOf<Input>()
    var curr = mutableListOf<String>()
    for (l in this) {
        if (l == "") {
            r.add(curr)
            curr = mutableListOf()
        } else
            curr.add(l)
    }
    r.add(curr)
    return r
}

fun String.isReflection(start: Int): Boolean {
    var a = substring(0, start)
    var b = substring(start)
    val len = min(a.length, b.length)
    if (a.length == len)
        b = b.take(len)
    else a = a.takeLast(len)
    return a.reversed() == b
}

fun String.diff(start: Int): Long {
    var a = substring(0, start)
    var b = substring(start)
    val len = min(a.length, b.length)
    if (a.length == len)
        b = b.take(len)
    else a = a.takeLast(len)
    return a.reversed().zip(b).sumOf { if (it.first == it.second) 0L else 1L }
}

fun Input.testHorizontal(tolerance: Long = 0L): Long {
    for (i in 1..<this[0].length) {
        if (this.sumOf { it.diff(i) } == tolerance)
            return i.toLong()
    }
    return 0
}

fun Input.testVertical(tolerance: Long = 0L): Long {
    return (this[0].length - 1 downTo 0).map { i ->
        this.map { it[i] }.joinToString("")
    }.testHorizontal(tolerance)
}

fun main() {

    fun part1(input: List<String>): Long {
        return input.split().sumOf {
            it.testHorizontal() + it.testVertical() * 100
        }
    }

    fun part2(input: List<String>): Long {
        return input.split().sumOf {
            it.testHorizontal(1) + it.testVertical(1) * 100
        }
    }

    val input = readInput("Day13")
    part1(input).println()
    part2(input).println()
}
