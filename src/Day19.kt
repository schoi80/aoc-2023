import java.util.LinkedList
import kotlin.math.max
import kotlin.math.min

typealias Workflow = Map<String, List<String>>
typealias PartRange = Array<IntRange>

fun initPartRange(i: Int) = arrayOf(1..i, 1..i, 1..i, 1..i)

fun initWorkflow(input: Input): Workflow {
    return input.takeWhile { it.isNotEmpty() }.associate {
        val key = it.takeWhile { it != '{' }
        val value = it.takeLast(it.length - key.length).drop(1).dropLast(1).split(",")
        key to value
    }
}

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

fun nextState(s: List<String>, input: List<Int>): String {
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

fun PartRange.apply(partKey: Char, v: Int, eq: Char): PartRange {
    val input = this.clone()
    val partIndex = partKey.getIndex()
    when (eq) {
        '<' -> input[partIndex] = input[partIndex].first..<v
        '>' -> input[partIndex] = v + 1..input[partIndex].last
        else -> error("cannot happen")
    }
    return input
}

fun PartRange.negate(partKey: Char, v: Int, eq: Char): PartRange {
    val input = this.clone()
    val partIndex = partKey.getIndex()
    when (eq) {
        '>' -> input[partIndex] = input[partIndex].first..min(v, input[partIndex].last)
        '<' -> input[partIndex] = max(v, input[partIndex].first)..input[partIndex].last
        else -> error("cannot happen")
    }
    return input
}

fun main() {

    fun part1(input: List<String>): Long {
        val wf = initWorkflow(input)
        val parts = input.takeLastWhile { it.isNotEmpty() }
            .map { it.drop(1).dropLast(1).split(",", limit = 4).map { it.drop(2).toInt() } }

        return parts.sumOf { s ->
            val r = wf.result("in", s)
            if (r == "A") s.sum().toLong()
            else 0L
        }
    }

    fun part2(input: List<String>): Long {
        val wf = initWorkflow(input)

        val result = mutableSetOf<PartRange>()
        val queue = LinkedList<Pair<String, PartRange>>()
        queue.add("in" to initPartRange(4000))

        while (queue.isNotEmpty()) {
            val (currState, currRange) = queue.pop()
            when (currState) {
                "A" -> result.add(currRange)
                "R" -> continue
                else -> {
                    var tempRange = currRange
                    wf[currState]!!.forEach { rule ->
                        val (nextState, condition) = rule.split(":").let {t ->
                            if (t.size == 1) t[0] to null else t[1] to t[0]
                        }
                        if (condition != null) {
                            val partKey = condition[0]
                            val v = condition.drop(2).toInt()
                            val eq = condition[1]
                            queue.add(nextState to tempRange.apply(partKey,v,eq))
                            tempRange = tempRange.negate(partKey,v,eq)
                        } else {
                            queue.add(nextState to tempRange)
                        }
                    }
                }
            }
        }

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
}