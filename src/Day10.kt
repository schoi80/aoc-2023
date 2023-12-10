import java.util.LinkedList

fun Input.upscale(): Input {
    return this.map { line ->
        val newStr = line.map { "$it " }.joinToString("")
        newStr.indices.map {
            if (it == newStr.length - 1) {
                '.'
            } else if (newStr[it] == ' ') {
                val prev = newStr[it - 1]
                val next = newStr[it + 1]
                if (setOf('-', 'F', 'L', 'S').contains(prev) && setOf('-', 'J', '7', 'S').contains(next))
                    '-'
                else '.'
            } else newStr[it]
        }.joinToString("")
    }
        .map { listOf(it, CharArray(it.length) { '|' }.joinToString("")) }
        .flatten()
}

fun Input.isConnected(n1: RowCol, n2: RowCol): Boolean {
    val v1 = this.get(n1)
    val v2 = this.get(n2)
    return (v1 == 'F' && setOf('-', '7', 'J').contains(v2) && n1.right() == n2) ||
            (v1 == 'F' && setOf('|', 'J', 'L').contains(v2) && n1.down() == n2) ||
            (v1 == 'L' && setOf('-', 'J', '7').contains(v2) && n1.right() == n2) ||
            (v1 == 'L' && setOf('|', 'F', '7').contains(v2) && n1.up() == n2) ||
            (v1 == '7' && setOf('-', 'L', 'F').contains(v2) && n1.left() == n2) ||
            (v1 == '7' && setOf('|', 'J', 'L').contains(v2) && n1.down() == n2) ||
            (v1 == 'J' && setOf('-', 'L', 'F').contains(v2) && n1.left() == n2) ||
            (v1 == 'J' && setOf('|', '7', 'F').contains(v2) && n1.up() == n2) ||
            (v1 == '-' && setOf('-', 'F', 'L').contains(v2) && n1.left() == n2) ||
            (v1 == '-' && setOf('-', 'J', '7').contains(v2) && n1.right() == n2) ||
            (v1 == '|' && setOf('|', 'F', '7').contains(v2) && n1.up() == n2) ||
            (v1 == '|' && setOf('|', 'L', 'J').contains(v2) && n1.down() == n2) ||
            (v1 == 'S' && setOf('|', 'F', '7').contains(v2) && n1.up() == n2) ||
            (v1 == 'S' && setOf('|', 'L', 'J').contains(v2) && n1.down() == n2) ||
            (v1 == 'S' && setOf('-', 'F', 'L').contains(v2) && n1.left() == n2) ||
            (v1 == 'S' && setOf('-', 'J', '7').contains(v2) && n1.right() == n2)
}


fun Input.next(rc: RowCol): List<RowCol> {
    if (this.get(rc) == '.') return emptyList()
    return adjacent(rc).filter { isConnected(rc, it) }
}

fun Input.isEdge(rc: RowCol) =
    (rc.first == 0 || rc.first == this.size - 1) || (rc.second == 0 || rc.second == this[0].length - 1)

fun Input.isOutside(rc: RowCol, pathMap: Map<RowCol, Int>, visited: Set<RowCol>): Boolean {
    if (pathMap.containsKey(rc))
        return false

    if (isEdge(rc))
        return true

    return adjacent(rc).any { visited.contains(it) }
}

fun findStart(input: Input): RowCol {
    for (i in input.indices) {
        for (j in 0..<input[i].length) {
            if (input.get(i to j) == 'S') {
                return i to j
            }
        }
    }
    error("this should never happen")
}

fun getPathMap(input: Input): Map<RowCol, Int> {
    val pathMap = mutableMapOf<RowCol, Int>()
    val s = findStart(input)
    val q = LinkedList<RowCol>().apply {
        add(s)
        pathMap[s] = 0
    }

    while (q.isNotEmpty()) {
        val c = q.pop()
        val curr = pathMap[c] ?: 0
        val next = input.next(c)
            .filter { !pathMap.containsKey(it) }
            .onEach { pathMap[it] = curr + 1 }
        q.addAll(next)
    }
    return pathMap
}

fun main() {

    fun part1(input: Input): Int {
        return getPathMap(input).values.max()
    }

    fun part2(input: Input): Int {
        val pathMap = getPathMap(input)
        val outside = mutableSetOf<RowCol>()

        // We're going to "floodfill" for any '.' that sits outside
        // of our path, starting with edges
        val queue = LinkedList<RowCol>().apply {
            input.forEachIndexed { i, line ->
                line.indices.forEach { j ->
                    val rc = i to j
                    if (input.isEdge(rc) && input.get(rc) == '.')
                        add(rc)
                }
            }
        }

        while (queue.isNotEmpty()) {
            val c = queue.pop()
            if (pathMap.containsKey(c))
                continue
            if (outside.contains(c))
                continue
            if (input.isOutside(c, pathMap, outside)) {
                outside.add(c)
                queue.addAll(input.adjacent(c))
            }
        }

        // Downscale the input, then count the number of '.'
        return input.indices.mapNotNull { i ->
            if (i % 2 == 1) null
            else input[i].indices.map { j ->
                if (pathMap.containsKey(i to j))
                    'X'
                else if (outside.contains(i to j))
                    'O'
                else '.'
            }.joinToString("").mapIndexed { index, c ->
                if (index % 2 == 1) ""
                else c
            }.joinToString("")
        }.sumOf { it.count { it == '.' } }
    }

    val input = readInput("Day10")
    part1(input).println()

    val input2 = input.upscale()
    part2(input2).println()
}
