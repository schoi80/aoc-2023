data class Spring(
    val data: String,
    val broken: List<Int>
)

fun String.initSpring(factor: Int = 1): Spring {
    return this.split(" ").let {
        val data = it[0]
        val broken = it[1]
        Spring(
            data = (0..<factor).joinToString("?") { data },
            broken = (0..<factor).joinToString(",") { broken }
                .split(",")
                .map { it.toInt() }
        )
    }
}

fun Spring.countHash() = data.countHash()
fun String.countHash() = this.count { it == '#' }
fun Spring.startsWith(c: Char) = data.startsWith(c)
fun Spring.isImpossibleState(lastEndedWithHash: Boolean): Boolean {
    // If broken pipes remain, this is not possible path
    if (broken.isEmpty() && countHash() > 0)
        return true

    // There must be enough chars remaining to satisfy the remaining match
    if (broken.sum() + (broken.size - 1) > data.length)
        return true

    // If previously ended with #
    if (lastEndedWithHash && startsWith('#'))
        return true

    return false
}

// Advance "data" by 1 char
fun Spring.advanceByData() = this.copy(data = this.data.drop(1))

// Advance by first broken size
fun Spring.advanceByBroken() = Spring(data = data.drop(broken.first()), broken = broken.drop(1))

// Memoize this recursion.
val cache = mutableMapOf<Pair<Spring, Boolean>, Long>()
fun Spring.countPossible(lastEndedWithHash: Boolean = false): Long = cache.getOrPut(this to lastEndedWithHash) {
    if (this.isImpossibleState(lastEndedWithHash))
        return@getOrPut 0

    if (this.broken.isEmpty() && (this.data.isEmpty() || this.countHash() == 0))
        return@getOrPut 1

    if (!lastEndedWithHash && !this.startsWith('.')) {
        // Take a peek at head of the data
        val head = this.data.take(this.broken.first())
        // If head is all #####, then we must take this
        if (head.countHash() == head.length) {
            return@getOrPut this.advanceByBroken().countPossible(true)
        } else {
            // If head is combination of ? and #
            val a = if (head.count { it != '.' } == head.length)
                this.advanceByBroken().countPossible(true)
            else 0

            // If head doesn't start with a definitive broken,
            // then assume it is not broken then move forward
            val b = if (!head.startsWith('#'))
                this.advanceByData().countPossible(false)
            else 0

            return@getOrPut a + b
        }
    }
    // Advance by 1 char and move on
    return@getOrPut this.advanceByData().countPossible(false)
}

fun main() {

    fun part1(input: List<String>): Long {
        return input.sumOf {
            it.initSpring()
                .also { println("Processing $it") }
                .countPossible()
                .also { println("Possible: $it") }
        }
    }

    fun part2(input: List<String>): Long {
        return input.sumOf {
            it.initSpring(5)
                .also { println("Processing $it") }
                .countPossible()
                .also { println("Possible: $it") }
        }
    }

    val input = readInput("Day12")
    part1(input).println()
    part2(input).println()
}
