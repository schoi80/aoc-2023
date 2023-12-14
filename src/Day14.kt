fun MutableInput<Char>.tiltNorth(): MutableInput<Char> {
    for (j in this[0].indices) {
        var rockPosition = -1
        var rockCount = 0
        for (i in this.indices) {
            when (this[i][j]) {
                '.' -> continue
                'O' -> rockCount++
                else -> {
                    var i2 = rockPosition + 1
                    while (rockCount > 0) {
                        this[i2][j] = 'O'
                        rockCount--
                        i2++
                    }
                    while (i2 < i) {
                        this[i2][j] = '.'
                        i2++
                    }
                    rockPosition = i
                }
            }
        }
        var i2 = rockPosition + 1
        while (rockCount > 0) {
            this[i2][j] = 'O'
            rockCount--
            i2++
        }
        while (i2 < this.size) {
            this[i2][j] = '.'
            i2++
        }
    }
    return this
}

fun MutableInput<Char>.tiltWest(): MutableInput<Char> {
    for (i in this.indices) {
        var rockPosition = -1
        var rockCount = 0
        for (j in this[i].indices) {
            when (this[i][j]) {
                '.' -> continue
                'O' -> rockCount++
                else -> {
                    var j2 = rockPosition + 1
                    while (rockCount > 0) {
                        this[i][j2] = 'O'
                        rockCount--
                        j2++
                    }
                    while (j2 < j) {
                        this[i][j2] = '.'
                        j2++
                    }
                    rockPosition = j
                }
            }
        }
        var j2 = rockPosition + 1
        while (rockCount > 0) {
            this[i][j2] = 'O'
            rockCount--
            j2++
        }
        while (j2 < this[0].size) {
            this[i][j2] = '.'
            j2++
        }
    }
    return this
}

fun MutableInput<Char>.tiltSouth(): MutableInput<Char> {
    for (j in this[0].indices) {
        var rockPosition = this.size
        var rockCount = 0
        for (i in this.size - 1 downTo 0) {
            when (this[i][j]) {
                '.' -> continue
                'O' -> rockCount++
                else -> {
                    var i2 = rockPosition - 1
                    while (rockCount > 0) {
                        this[i2][j] = 'O'
                        rockCount--
                        i2--
                    }
                    while (i2 > i) {
                        this[i2][j] = '.'
                        i2--
                    }
                    rockPosition = i
                }
            }
        }
        var i2 = rockPosition - 1
        while (rockCount > 0) {
            this[i2][j] = 'O'
            rockCount--
            i2--
        }
        while (i2 >= 0) {
            this[i2][j] = '.'
            i2--
        }
    }
    return this
}

fun MutableInput<Char>.tiltEast(): MutableInput<Char> {
    for (i in this.indices) {
        var rockPosition = this[i].size
        var rockCount = 0
        for (j in this[i].size - 1 downTo 0) {
            when (this[i][j]) {
                '.' -> continue
                'O' -> rockCount++
                else -> {
                    var j2 = rockPosition - 1
                    while (rockCount > 0) {
                        this[i][j2] = 'O'
                        rockCount--
                        j2--
                    }
                    while (j2 > j) {
                        this[i][j2] = '.'
                        j2--
                    }
                    rockPosition = j
                }
            }
        }
        var j2 = rockPosition - 1
        while (rockCount > 0) {
            this[i][j2] = 'O'
            rockCount--
            j2--
        }
        while (j2 >= 0) {
            this[i][j2] = '.'
            j2--
        }
    }
    return this
}

fun MutableInput<Char>.calcLoad(): Long {
    return this.mapIndexed { index, s ->
        s.count { it == 'O' } * (this.size - index.toLong())
    }.sum()
}

fun MutableInput<Char>.getHash(): Int {
    return this.joinToString("") { it.joinToString("") }.hashCode()
}

fun MutableInput<Char>.cycle() = this.tiltNorth().tiltWest().tiltSouth().tiltEast()

fun main() {

    fun part1(input: List<String>): Long {
        val grid = input.toMutableInput { it }
        return grid.tiltNorth().calcLoad()
    }

    fun part2(input: List<String>): Long {
        var grid = input.toMutableInput { it }
        val cycleCount = 1000000000
        var curr = 0
        val cache = mutableMapOf<Int, Int>()
        while (cycleCount > curr) {
            grid = grid.cycle()
            curr++
            val seenBefore = cache.getOrPut(grid.getHash()) {curr}
            if (seenBefore != curr) {
                println("i've seen this at $seenBefore! curr: $curr")
                println("cycle at every ${curr - seenBefore}")
                curr = cycleCount - ((cycleCount - curr) % (curr - seenBefore))
                println("advancing to $curr")
            }
        }
        return grid.calcLoad()
    }

    val input = readInput("Day14")
    part1(input).println()
    part2(input).println()
}
