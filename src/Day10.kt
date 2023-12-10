import java.util.LinkedList


fun String.upscaleH(): String {
    val newStr = this.map { "$it " }.joinToString("")
    return newStr.indices.map {
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
fun main() {

    val input = readInput("Day10").map { it.upscaleH() }
        .map { listOf(it, CharArray(it.length) { '|' }.joinToString("")) }
        .flatten()
        .onEach { println(it) }


    fun RowCol.isOutOfBounds(): Boolean {
        val inRange = first >= 0 && first < input.size && second >= 0 && second < input[first].length
        return !inRange
    }

    fun RowCol.get(): Char {
        val (i, j) = this
        return input[i][j]
    }


    fun RowCol.isConnectedTo(n: RowCol): Boolean {
        val (i1, j1) = this
        val (i2, j2) = n

        return ((this.get() == 'F') && (n.get() == '-' || n.get() == '7' || n.get() == 'J') && i1 == i2 && j1 + 1 == j2) ||
                ((this.get() == 'F') && (n.get() == '|' || n.get() == 'J' || n.get() == 'L') && i1 + 1 == i2 && j1 == j2) ||
                ((this.get() == 'L') && (n.get() == '|' || n.get() == 'F' || n.get() == '7') && i1 - 1 == i2 && j1 == j2) ||
                ((this.get() == 'L') && (n.get() == '-' || n.get() == 'J' || n.get() == '7') && i1 == i2 && j1 + 1 == j2) ||
                ((this.get() == '7') && (n.get() == '|' || n.get() == 'J' || n.get() == 'L') && i1 + 1 == i2 && j1 == j2) ||
                ((this.get() == '7') && (n.get() == '-' || n.get() == 'L' || n.get() == 'F') && i1 == i2 && j1 - 1 == j2) ||
                ((this.get() == 'J') && (n.get() == '|' || n.get() == '7' || n.get() == 'F') && i1 - 1 == i2 && j1 == j2) ||
                ((this.get() == 'J') && (n.get() == '-' || n.get() == 'L' || n.get() == 'F') && i1 == i2 && j1 - 1 == j2) ||
                ((this.get() == '-') && (n.get() == '-' || n.get() == 'J' || n.get() == '7') && i1 == i2 && j1 + 1 == j2) ||
                ((this.get() == '-') && (n.get() == '-' || n.get() == 'F' || n.get() == 'L') && i1 == i2 && j1 - 1 == j2) ||
                ((this.get() == '|') && (n.get() == '|' || n.get() == 'F' || n.get() == '7') && i1 - 1 == i2 && j1 == j2) ||
                ((this.get() == '|') && (n.get() == '|' || n.get() == 'L' || n.get() == 'J') && i1 + 1 == i2 && j1 == j2) ||
                ((this.get() == 'S') && (n.get() == '|' || n.get() == 'L' || n.get() == 'J') && i1 + 1 == i2 && j1 == j2) ||
                ((this.get() == 'S') && (n.get() == '|' || n.get() == 'F' || n.get() == '7') && i1 - 1 == i2 && j1 == j2) ||
                ((this.get() == 'S') && (n.get() == '-' || n.get() == 'J' || n.get() == '7') && i1 == i2 && j1 + 1 == j2) ||
                ((this.get() == 'S') && (n.get() == '-' || n.get() == 'F' || n.get() == 'L') && i1 == i2 && j1 - 1 == j2)

    }


    fun RowCol.nextMove(): List<RowCol> {
        val (i, j) = this
        if (input[i][j] == '.') return emptyList()
        return listOf(
            (i - 1 to j),
            (i + 1 to j),
            (i to j - 1),
            (i to j + 1)
        ).filter { !it.isOutOfBounds() && isConnectedTo(it) }
    }


    fun findStart(input: List<String>): RowCol {
        for (i in input.indices) {
            for (j in 0..<input[i].length) {
                if ((i to j).get() == 'S') {
                    return i to j
                }
            }
        }
        error("blip")
    }



    fun part1(input: List<String>): Int {
        val map = mutableMapOf<RowCol, Int>()
        val s = findStart(input).also { println(it) }
        val q = LinkedList<RowCol>().apply {
            add(s)
            map[s] = 0
        }

        while (q.isNotEmpty()) {
            q.println()
            val c = q.pop()
            val curr = map[c] ?: 0
            println("Curr position $c = ${c.get()}")

            val next = c.nextMove()
                .filter { !map.containsKey(it) }
                .onEach {
                    map[it] = curr + 1
                    println("Next position $it = ${it.get()}")
                }

            q.addAll(next)
        }


        val outside = mutableSetOf<RowCol>()

        fun RowCol.isOutside(): Boolean {
            if (map.containsKey(this))
                return false

            val (i,j) = this
            if ((i == 0 || i == input.size - 1) || (j == 0 || j == input[0].length - 1))
                return true

            return listOf(
                i-1 to j,
                i+1 to j,
                i to j-1,
                i to j+1
            ).any { !it.isOutOfBounds() && outside.contains(it)}
        }



        val q2 = LinkedList<RowCol>().apply {
            input.first().indices.forEach { j ->
                if ((0 to j).get() == '.')
                    add(0 to j)
            }
            input.last().indices.forEach { j ->
                if ((input.size - 1 to j).get() == '.')
                    add(input.size - 1 to j)
            }
            (1..input.size-2).forEach {i ->
                if ((i to 0).get() == '.')
                    add(i to 0)
                if ((i to input[i].length-1).get() == '.')
                    add(i to input[i].length-1)
            }
        }
        while (q2.isNotEmpty()) {
            val c = q2.pop()
            if (map.containsKey(c))
                continue
            if (outside.contains(c))
                continue

            if (c.isOutside()) {
                outside.add(c)
                val (i,j) = c
                q2.addAll(listOf(
                    i-1 to j,
                    i+1 to j,
                    i to j-1,
                    i to j+1
                ).filter { !it.isOutOfBounds() })
            }
        }

        val newInputs = input.indices.mapNotNull { i ->
            if (i%2 == 1) null
            else input[i].indices.map { j ->
                if (map.containsKey(i to j))
                    'X'
                else if (outside.contains(i to j))
                    'O'
                else '.'
            }.joinToString("").mapIndexed { index, i ->
                if (index%2 == 1) ""
                else i
            }.joinToString("")
        }.onEach { println(it) }

        val area = newInputs.sumOf {
            it.count { it == '.' }
        }
        println("area = $area")
        return map.values.max()
    }


    fun part2(input: List<String>): Long {
        val map = mutableMapOf<RowCol, Int>()
        val s = findStart(input).also { println(it) }
        val q = LinkedList<RowCol>().apply {
            add(s)
            map[s] = 0
        }

        while (q.isNotEmpty()) {
            q.println()
            val c = q.pop()
            val curr = map[c] ?: 0
            println("Curr position $c = ${c.get()}")

            val next = c.nextMove()
                .filter { !map.containsKey(it) }
                .onEach {
                    map[it] = curr + 1
                    println("Next position $it = ${it.get()}")
                }

            q.addAll(next)
        }


        return 0L
    }

    part1(input).println()
//    part2(input).println()
}
