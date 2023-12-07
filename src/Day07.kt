fun main() {

    val input = readInput("Day07")

    fun String.parse(): Triple<Map<Char, Int>, Long, String> {
        val (hand, bid) = split(" ").let {
            it[0] to it[1].toLong()
        }

        val h = mutableMapOf<Char, Int>()
        for (c in hand) {
            h[c] = 1 + (h[c] ?: 0)
        }

        return Triple(h, bid, this)
    }

    fun List<Int>.rank(): Int {
        return when (this) {
            listOf(5) -> 1
            listOf(1, 4) -> 2
            listOf(2, 3) -> 3
            listOf(1, 1, 3) -> 4
            listOf(1, 2, 2) -> 5
            listOf(1, 1, 1, 2) -> 6
            else -> 7
        }
    }

    fun Map<Char, Int>.score() = this.values.sorted().rank()

    fun Char.score(): Int {
        return when (this) {
            'A' -> 1
            'K' -> 2
            'Q' -> 3
            'J' -> 4
            'T' -> 5
            else -> 10 - this.digitToInt() + 5
        }
    }

    val compareHands = Comparator<Triple<Map<Char, Int>, Long, String>> { h1, h2 ->
        if (h1.first.score() != h2.first.score())
            return@Comparator h1.first.score() - h2.first.score()
        (0..4).forEach {
            val p1 = h1.third[it]
            val p2 = h2.third[it]
            if (p1 != p2)
                return@Comparator p1.score() - p2.score()
        }
        error("this should never happen")
    }


    fun Map<Char, Int>.score2(): Int {
        val h = this.values.sorted().toMutableList() //.also { it.println() }
        val countJ = this['J'] ?: 0
        val i = h.lastIndexOf(countJ)
        if (i >= 0)
            h.removeAt(i)

        if (h.size == 0)
            h.add(5)
        else
            h[h.size - 1] += countJ

        return h.rank()
    }

    fun Char.score2(): Int {
        return when (this) {
            'A' -> 1
            'K' -> 2
            'Q' -> 3
            'J' -> 15 // J gets super-power
            'T' -> 5
            else -> 10 - this.digitToInt() + 5
        }
    }

    val compareHands2 = Comparator<Triple<Map<Char, Int>, Long, String>> { h1, h2 ->
        if (h1.first.score2() != h2.first.score2())
            return@Comparator h1.first.score2() - h2.first.score2()

        (0..4).forEach {
            val p1 = h1.third[it]
            val p2 = h2.third[it]
            if (p1 != p2)
                return@Comparator p1.score2() - p2.score2()
        }
        error("this should never happen")
    }

    fun part1(input: List<String>): Long {
        return input.map { it.parse() }
            .sortedWith(compareHands)
            .reversed()
            .mapIndexed { i, v -> (i + 1) * v.second }
            .sum()
    }

    fun part2(input: List<String>): Long {
        return input.map { it.parse() }
            .sortedWith(compareHands2)
            .reversed()
            .mapIndexed { i, v -> (i + 1) * v.second }
            .sum()
    }

    part1(input).println()
    part2(input).println()
}
