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

    fun Map<Char, Int>.score(): Int {
        val h = this.values.sorted() //.also { it.println() }
        return when (h) {
            listOf(5) -> 1
            listOf(1, 4) -> 2
            listOf(2, 3) -> 3
            listOf(1, 1, 3) -> 4
            listOf(1, 2, 2) -> 5
            listOf(1, 1, 1, 2) -> 6
            else -> 7
        }
    }


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

    fun compare(c1: Char, c2: Char) = c1.score() - c2.score()


    val scoreComparator = Comparator<Triple<Map<Char, Int>, Long, String>> { h1, h2 ->
        if (h1.first.score() != h2.first.score())
            return@Comparator h1.first.score() - h2.first.score()

        val p1 = h1.third
        val p2 = h2.third
        if (p1[0] != p2[0]) {
            return@Comparator compare(p1[0], p2[0])
        } else if (p1[1] != p2[1]) {
            return@Comparator compare(p1[1], p2[1])
        } else if (p1[2] != p2[2]) {
            return@Comparator compare(p1[2], p2[2])
        } else if (p1[3] != p2[3]) {
            return@Comparator compare(p1[3], p2[3])
        }
        return@Comparator return@Comparator compare(p1[4], p2[4])
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

        return when (h) {
            listOf(5) -> 1
            listOf(1, 4) -> 2
            listOf(2, 3) -> 3
            listOf(1, 1, 3) -> 4
            listOf(1, 2, 2) -> 5
            listOf(1, 1, 1, 2) -> 6
            else -> 7
        }
    }

    fun Char.score2(): Int {
        return when (this) {
            'A' -> 1
            'K' -> 2
            'Q' -> 3
            'J' -> 15
            'T' -> 5
            else -> 10 - this.digitToInt() + 5
        }
    }

    fun compare2(c1: Char, c2: Char) = c1.score2() - c2.score2()

    val scoreComparator2 = Comparator<Triple<Map<Char, Int>, Long, String>> { h1, h2 ->
        if (h1.first.score2() != h2.first.score2())
            return@Comparator h1.first.score2() - h2.first.score2()

        val p1 = h1.third
        val p2 = h2.third
        if (p1[0] != p2[0]) {
            return@Comparator compare2(p1[0], p2[0])
        } else if (p1[1] != p2[1]) {
            return@Comparator compare2(p1[1], p2[1])
        } else if (p1[2] != p2[2]) {
            return@Comparator compare2(p1[2], p2[2])
        } else if (p1[3] != p2[3]) {
            return@Comparator compare2(p1[3], p2[3])
        }
        return@Comparator return@Comparator compare2(p1[4], p2[4])
    }

    fun part1(input: List<String>): Long {
        return input.map { it.parse() }
            .sortedWith(scoreComparator)
            .reversed()
            .mapIndexed { i, v -> (i + 1) * v.second }
            .sum()
    }

    fun part2(input: List<String>): Long {
        return input.map { it.parse() }
            .sortedWith(scoreComparator2)
            .reversed()
            .mapIndexed { i, v -> (i + 1) * v.second }
            .sum()
    }

    part1(input).println()
    part2(input).println()
}
