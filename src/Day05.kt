import java.util.SortedSet
import kotlin.math.min
import kotlin.time.measureTimedValue

typealias VMap = Pair<LongRange, LongRange>

fun main() {

    val input = readInput("Day05")

    fun String.splitRange(): VMap {
        val p = split("\\s+".toRegex())
        val r = p[2].toLong()
        val dr = p[0].toLong().let { it until it + r }
        val sr = p[1].toLong().let { it until it + r }
        return sr to dr
    }

    fun VMap.sourceRange() = this.first

    fun VMap.get(source: Long): Long {
        if (this.first.contains(source)) {
            val (sr, dr) = this
            return dr.min() + (source - sr.min())
        }
        return source
    }

    fun SortedSet<VMap>.get(source: Long): Long = measureTimedValue {
        for (r in this) {
            if (r.sourceRange().contains(source)) {
                return@measureTimedValue r.get(source)
            }
            if (r.sourceRange().first > source)
                break
        }
        return@measureTimedValue source
    }.let {
        println("SortedSet<VMap>.get took ${it.duration}")
        it.value
    }

    fun SortedSet<VMap>.mapRange(range: LongRange): List<LongRange> = measureTimedValue {
        // Border checks
        if (range.last < first().sourceRange().first || range.first > this.last().sourceRange().last)
            return listOf(range)
        val result = mutableListOf<LongRange>()
        var head = range.first
        forEach { vmap ->
            val sourceRange = vmap.sourceRange()
            if (sourceRange.first > head) {
                result.add(head..<sourceRange.first)
                val end = min(range.last, sourceRange.last)
                result.add(vmap.get(sourceRange.first)..vmap.get(end))
                head = end + 1
            } else if (sourceRange.first <= head && head <= sourceRange.last) {
                val start = head
                val end = min(sourceRange.last, range.last)
                result.add(vmap.get(start)..vmap.get(end))
                head = end + 1
            }
            if (head > range.last)
                return@measureTimedValue result
        }
        return@measureTimedValue result
    }.let {
        println("SortedSet<VMap>.mapRange took ${it.duration}")
        it.value
    }

    fun part1(input: List<String>): Long {
        val input = input.filter { it.isNotBlank() }
        val seeds = input[0].split(":")[1].trim().split("\\s+".toRegex()).map { it.trim().toLong() }
        val mapList = mutableListOf<SortedSet<VMap>>()
        var i = 1
        while (i < input.size) {
            if (input[i].contains("map")) {
                val dict = sortedSetOf(compareBy<VMap> { it.first.first })
                i++
                while (i < input.size && !input[i].contains("map")) {
                    dict.add(input[i].splitRange())
                    i++
                }
                mapList.add(dict)
            }
        }
        mapList.println()
        return seeds.minOf { seed ->
            mapList.fold(seed) { acc, map -> map.get(acc) }
        }
    }

    fun part2(input: List<String>): Long {
        val input = input.filter { it.isNotBlank() }
        val seeds =
            input[0].split(":")[1].trim().split("\\s+".toRegex()).map { it.trim().toLong() }.chunked(2).map {
                it[0] until it[0] + it[1]
            }.sortedBy { it.first }
        println(seeds)

        val mapList = mutableListOf<SortedSet<VMap>>()
        var i = 1
        while (i < input.size) {
            if (input[i].contains("map")) {
                val dict = sortedSetOf(compareBy<VMap> { it.first.first })
                i++
                while (i < input.size && !input[i].contains("map")) {
                    dict.add(input[i].splitRange())
                    i++
                }
                mapList.add(dict)
            }
        }
        mapList.println()

        return mapList.fold(seeds) { acc, map ->
            acc.flatMap { map.mapRange(it) }
        }.minOf { it.first }
    }


    part1(input).println()
    part2(input).println()
}
