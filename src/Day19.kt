import java.util.LinkedList
import kotlin.math.max
import kotlin.math.min

typealias Workflow = Map<String, List<String>>

fun List<Int>.getValue(c: Char) = this[c.getIndex()]

fun Char.getIndex() = when (this) {
    'x' -> 0
    'm' -> 1
    'a' -> 2
    's' -> 3
    else -> error("asdasd")
}


fun Workflow.result(state: String, input: List<Int>): String {
    if (state == "A" || state == "R")
        return state

    val conditions = this[state]!!
    return result(nextState(conditions, input), input)
}

fun Workflow.buildPath(path: List<String>): List<List<String>> {
    val state = path.last()
    if (state == "A" || state == "R")
        return listOf(path)

    val conditions = this[state]!!
    return conditions.flatMap {
        buildPath(path + it.split(":").last())
    }.distinct()
}


fun Workflow.nextState(s: List<String>, input: List<Int>): String {
    (0..<s.size - 1).forEach { i ->
        val (condition, next) = s[i].split(":", limit = 2)
        val n1 = input.getValue(condition[0])
        val n2 = condition.drop(2).toInt()
        val c = condition[1]
        val conditionMet = when (c) {
            '<' -> n1 < n2
            '>' -> n1 > n2
            else -> error("asdad")
        }
        if (conditionMet)
            return next
    }

    return s.last()
}

fun Workflow.possibleInput(s1: String, s2: String, input: Array<IntRange>): Array<IntRange> {
    val conditions = this[s1]!!
    val terminalCondition = conditions.last()
    val conditionToNext = conditions.find { it.endsWith(s2) }!!

//    if (terminalCondition == conditionToNext)
//        return input

    var negate = false
    if (terminalCondition == s2)
        negate = true

    if (negate) {
        conditions.dropLast(1).forEach { c ->
            val (condition, _) = c.split(":", limit = 2)
            val n1 = condition[0]
            val n2 = condition.drop(2).toInt()
            val eq = condition[1]
            when (eq) {
                '>' -> input[n1.getIndex()] = input[n1.getIndex()].first..min(n2, input[n1.getIndex()].last)
                '<' -> input[n1.getIndex()] = max(n2, input[n1.getIndex()].first)..input[n1.getIndex()].last
                else -> error("asdad")
            }
        }
    } else {
        val (condition, _) = conditionToNext.split(":", limit = 2)
        val n1 = condition[0]
        val n2 = condition.drop(2).toInt()
        val eq = condition[1]
        when (eq) {
            '<' -> input[n1.getIndex()] = input[n1.getIndex()].first..<n2
            '>' -> input[n1.getIndex()] = n2 + 1..input[n1.getIndex()].last
            else -> error("asdad")
        }
    }
    return input
}

//
//fun Workflow.applyCondition(s1: String, s2: String, input: Array<IntRange>) {
//    val conditions = this[s1]!!
//    val terminalCondition = conditions.last()
//    val conditionToNext = conditions.find {
//        it.split(":", limit = 2).last() == s2
//    }!!
//
//    var negate = false
//    if (terminalCondition == s2)
//        negate = true
//
//    if (negate) {
//        conditions.dropLast(1).forEach { c ->
//            val (condition, _) = c.split(":", limit = 2)
//            val n1 = condition[0]
//            val n2 = condition.drop(2).toInt()
//            val eq = condition[1]
//            when (eq) {
//                '>' -> input[n1.getIndex()] = input[n1.getIndex()].first..min(n2, input[n1.getIndex()].last)
//                '<' -> input[n1.getIndex()] = max(n2, input[n1.getIndex()].first)..input[n1.getIndex()].last
//                else -> error("asdad")
//            }
//        }
//    } else {
//        val (condition, _) = conditionToNext.split(":", limit = 2)
//        val n1 = condition[0]
//        val n2 = condition.drop(2).toInt()
//        val eq = condition[1]
//        when (eq) {
//            '<' -> input[n1.getIndex()] = input[n1.getIndex()].first..<n2
//            '>' -> input[n1.getIndex()] = n2 + 1..input[n1.getIndex()].last
//            else -> error("asdad")
//        }
//    }
//}

fun Workflow.applyCondition(s1: String, s2: String, input: Array<IntRange>) {
    var conditions = this[s1]!!

    fun negateCondition(i:Int, v:Int, eq:Char) {
        when (eq) {
            '>' -> input[i] = input[i].first..min(v, input[i].last)
            '<' -> input[i] = max(v, input[i].first)..input[i].last
            else -> error("asdad")
        }
    }

    fun applyCondition(i:Int, v:Int, eq:Char) {
        when (eq) {
            '<' -> input[i] = input[i].first..<v
            '>' -> input[i] = v + 1..input[i].last
            else -> error("asdad")
        }
    }

//    if (conditions.size == 2 && conditions.last() == "A" && conditions.first().endsWith("A"))
//        return
    if (s2 == "A" && conditions.last() == "A")
        conditions = conditions.filterNot {
            it.endsWith("A")
        }

    for (c in conditions) {
        if (!c.contains(":"))
            continue

        val (condition, next) = c.split(":")
        val i = condition[0].getIndex()
        val v = condition.drop(2).toInt()
        val eq = condition[1]
        if (next != s2)
            negateCondition(i, v, eq)
        else {
            applyCondition(i, v, eq)
            return
        }
    }
}

fun Workflow.possible(
    states: List<String>,
    input: Array<IntRange>,
): Array<IntRange> {
//    val input = limit
    (0..states.size - 2).forEach {
        val curr = states[it]
        val next = states[it + 1]
//        input = possibleInput(curr, next, input)
        applyCondition(curr, next, input)
    }
    return input
}

fun main() {


    fun part1(input: List<String>): Long {
        val wf = input.takeWhile { it.isNotEmpty() }.associate {
            val key = it.takeWhile { it != '{' }
            val value = it.takeLast(it.length - key.length).drop(1).dropLast(1).split(",")
            key to value
        }

        val states = input.takeLastWhile { it.isNotEmpty() }
            .map { it.drop(1).dropLast(1).split(",", limit = 4).map { it.drop(2).toInt() } }

        return states.sumOf { s ->
            val r = wf.result("in", s)
            if (r == "A") s.sum().toLong()
            else 0L
        }
    }

    fun part2(input: List<String>): Long {
        val wf = input.takeWhile { it.isNotEmpty() }.associate {
            val key = it.takeWhile { it != '{' }
            val value = it.takeLast(it.length - key.length).drop(1).dropLast(1).split(",")
            key to value
        }
        val paths = wf.buildPath(listOf("in")).filter { it.last() == "A" }
        val limit = arrayOf(1..4000, 1..4000, 1..4000, 1..4000)
        val possibleRanges = paths.map { p ->
            wf.possible(p, limit.clone()).also {
                println("path: $p  range: ${it.toList()}")
            }
        }

        possibleRanges.onEach { println(it.toList()) }

        return possibleRanges.sumOf {
            (it[0].last - it[0].first + 1L) *
                    (it[1].last - it[1].first + 1L) *
                    (it[2].last - it[2].first + 1L) *
                    (it[3].last - it[3].first + 1L)
        }
    }


    fun part22(input: List<String>): Long {
        val wf = input.takeWhile { it.isNotEmpty() }.associate {
            val key = it.takeWhile { it != '{' }
            val value = it.takeLast(it.length - key.length).drop(1).dropLast(1).split(",").associate {
                val t = it.split(":")
                if (t.size == 1)
                    t[0] to null
                else
                    t[1] to t[0]
            }
            key to value
        }.also { it.println() }

        val q = LinkedList<Pair<String, Xmas>>()
        q.add("in" to initXmas(4000))
        val result = mutableSetOf<Xmas>()
        while (q.isNotEmpty()) {
            val (c,r) = q.pop()
            when (c) {
                "A" -> result.add(r)
                "R" -> continue
                else -> {
                    wf[c]?.forEach { (n, nc) ->
                        if (nc != null)
                            q.add(n to r.applyCondition(nc))
                        else {
                            q.add(n to wf[c]!!.values.fold(r) {acc, it ->
                                if (it != null)
                                    acc.negateCondition(it)
                                else acc
                            })
                        }
                    }
                }
            }
        }

        result.onEach { println(it.toList()) }

        return result.sumOf {
            (it[0].last - it[0].first + 1L) *
                    (it[1].last - it[1].first + 1L) *
                    (it[2].last - it[2].first + 1L) *
                    (it[3].last - it[3].first + 1L)
        }
    }

    val input = readInput("Day19")
    part1(input).println()
    part2(input).println()
//    part22(input).println()
}

typealias Xmas = Array<IntRange>

fun initXmas(i: Int) = arrayOf(1..i, 1..i, 1..i, 1..i)
fun Xmas.getRange(c: Char) = this[c.getIndex()]

fun Xmas.setRange(c: Char, r: IntRange) {
    this[c.getIndex()] = r
}

fun Xmas.applyCondition(c:String): Xmas {
    val i = c[0]
    val limit = c.drop(2).toInt()
    val eq = c[1]
    val range = this.getRange(i)
    val xmas = this.clone()
    when (eq) {
        '<' -> xmas.setRange(i, range.first..<limit)
        '>' -> xmas.setRange(i, limit + 1..range.last)
        else -> error("asdad")
    }
    return xmas
}

fun Xmas.negateCondition(c:String): Xmas {
    val i = c[0]
    val limit = c.drop(2).toInt()
    val eq = c[1]
    val range = this.getRange(i)
    val xmas = this.clone()
    when (eq) {
        '>' -> xmas.setRange(i, range.first..min(limit, range.last))
        '<' -> xmas.setRange(i, max(limit, range.first)..range.last)
        else -> error("asdad")
    }
    return xmas
}
