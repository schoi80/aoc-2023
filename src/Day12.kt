data class Spring(
    val data: String,
    val broken: List<Int>
)

fun String.parse(): Spring {
    return split(" ").let {
        Spring(
            data = it[0].dropWhile { it == '.' }.dropLastWhile { it == '.' },//.compact(),
            broken = it[1].split(",").map { it.toInt() }
        )
    }
}

fun String.parse2(): Spring {
    return split(" ").let {
        val data = it[0]
        val broken = it[1]
        Spring(
            data = (0..<5).joinToString("?") { data },
            broken = (0..<5).joinToString(",") { broken }
                .split(",")
                .map { it.toInt() }
        )
    }
}

val memoize = mutableMapOf<Pair<Spring, Boolean>, Long>()
fun Spring.countPossible(endsWithBroken: Boolean = false): Long = memoize.getOrPut(this to endsWithBroken) {
    if (this.broken.isEmpty() && (this.data.isEmpty() || this.data.all { it == '?' || it == '.' }))
        return@getOrPut 1

    // If broken pipes remain, this is not possible path
    if (this.broken.isEmpty() && this.data.count { it == '#' } > 0)
        return@getOrPut 0

    // There must be enough chars remaining to satisfy the remaining match
    if (this.broken.sum() + (this.broken.size - 1) > this.data.length)
        return@getOrPut 0

    if (endsWithBroken) {
        return@getOrPut if (this.data.startsWith('#')) 0
        else Spring(data = this.data.drop(1), broken = this.broken).countPossible(false)
    } else {
        if (this.data.first() == '?' || this.data.first() == '#') {

            // Scenario 1.  assume it's broken only if the next "brokenCount" is all # and ?
            val brokenCount = this.broken.first()
            val next = this.data.take(brokenCount)

            if (next.count { it == '#' } == next.length) {
                return@getOrPut Spring(data = this.data.drop(brokenCount), broken = this.broken.drop(1)).countPossible(
                    true
                )
            } else {
                val a = if (next.count { it == '#' || it == '?' } == next.length)
                    Spring(data = this.data.drop(brokenCount), broken = this.broken.drop(1)).countPossible(true)
                else 0

                // Scenario 2.  assume it's not broken
                val b = if (!next.startsWith('#'))
                    Spring(data = this.data.drop(1), broken = this.broken).countPossible(false)
                else 0

                return@getOrPut a + b
            }
        } else {
            return@getOrPut Spring(data = this.data.drop(1), broken = this.broken).countPossible(false)
        }
    }
}

fun main() {

    fun part1(input: List<String>): Long {
        return input.sumOf {
            it.parse()
                .also { println("Processing $it") }
                .countPossible()
                .also { println("Possible: $it") }
        }
    }

    fun part2(input: List<String>): Long {
        return input.sumOf {
            it.parse2()
                .also { println("Processing $it") }
                .countPossible()
                .also { println("Possible: $it") }
        }
    }

    val input = readInput("Day12")
    part1(input).println()
    part2(input).println()
}
