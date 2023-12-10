import java.util.SortedSet
import kotlin.math.min
import kotlin.time.measureTimedValue

typealias VMap = Pair<LongRange, Long>

fun main() {

    val input = readInput("Day05")

    fun String.splitRange(): VMap {
        val p = split("\\s+".toRegex())
        val r = p[2].toLong()
        val dr = p[0].toLong() //.let { it until it + r }
        val sr = p[1].toLong().let { it until it + r }
        return sr to dr
    }

    fun VMap.sourceRange() = this.first

    fun VMap.get(source: Long): Long {
        if (this.first.contains(source)) {
            val (sr, dr) = this
            return dr + (source - sr.min())
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
        for (vmap in this) {
            val sourceRange = vmap.sourceRange()
            if (sourceRange.last < range.first) {
                println("skip")
                continue
            }
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
            if (head > range.last) {
                return@measureTimedValue result
            }
        }
        return@measureTimedValue result
    }.let {
        println("SortedSet<VMap>.mapRange took ${it.duration}")
        it.value
    }

    fun SortedSet<VMap>.mapRange(rl: List<LongRange>): List<LongRange> = measureTimedValue {
        var rangeIndex = 0
        var vmapIndex = 0
        val vmapList = this.toList()
        val result = mutableListOf<LongRange>()

        fun getSourceRange(i: Int) = vmapList[i].sourceRange()
        fun getRange(i: Int) = rl[i]
        var head = rl[0].first
        while (vmapIndex < this.size && rangeIndex < rl.size && head < rl.last().last) {
            var sourceRange = getSourceRange(vmapIndex)
            var range = getRange(rangeIndex)


            if (sourceRange.last < range.first) {
                vmapIndex++
                continue
            }

            if (sourceRange.first > range.last) {
                rangeIndex++
                continue
            }

            head = range.first
            while (vmapIndex < this.size && rangeIndex < rl.size) {
                sourceRange = getSourceRange(vmapIndex)
                range = getRange(rangeIndex)
                val vmap = vmapList[vmapIndex]

                if (sourceRange.first > head) {
                    result.add(head..<sourceRange.first)
                    val end = min(range.last, sourceRange.last)
                    val rToAdd = vmap.get(sourceRange.first)..vmap.get(end)
                    result.add(rToAdd)
                    head = end + 1
                } else if (sourceRange.first <= head && head <= sourceRange.last) {
                    val start = head
                    val end = min(sourceRange.last, range.last)
                    val rToAdd = vmap.get(start)..vmap.get(end)
                    result.add(rToAdd)
                    head = end + 1
                }

                if (head > sourceRange.last)
                    vmapIndex++

                if (head > range.last) {
                    rangeIndex++
                    break
                }
            }
        }
        if (head < rl.last().last) {
            result.add(head..getRange(rangeIndex).last)
            result.addAll(rl.subList(rangeIndex + 1, rl.size))
        }
        val queue = mutableListOf<LongRange>()
        result.sortedBy { it.first }.forEach {
            queue.lastOrNull()?.let { lastElem ->
                if (lastElem.last + 1 == it.first) {
                    queue.removeLast()
                    queue.add(lastElem.first()..it.last)
                } else queue.add(it)
            } ?: queue.add(it)
        }
        return@measureTimedValue queue
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
            map.mapRange(acc).also { println(it) }
        }[0].first
    }

//    part1(input).println()
    part2(input).println()
}
