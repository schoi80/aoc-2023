import kotlin.math.min
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

typealias VMap = Pair<LongRange, LongRange>
typealias VList = List<LongRange>

fun main() {

    val input = readInput("Day05")

    fun String.splitRange(): VMap {
        val p = split("\\s+".toRegex())

        val r = p[2].toLong()
        val dr = p[0].toLong().let { it until it + r }
        val sr = p[1].toLong().let { it until it + r }
        return sr to dr
    }

    fun String.splitRange2(): VMap {
        val p = split("\\s+".toRegex())

        val r = p[2].toLong()
        val dr = p[0].toLong().let { it until it + r }
        val sr = p[1].toLong().let { it until it + r }
        return dr to sr
    }

    fun List<VMap>.get(source: Long): Long = measureTimedValue {
        this.sortedBy { it.first.first }.firstOrNull { it.first.contains(source) }?.let { vmap ->
            val (sr, dr) = vmap
            dr.min() + (source - sr.min())
        } ?: source
    }.let {
        println("List<VMap>.get took ${it.duration}")
        it.value
    }

    fun VMap.get(source: Long): Long = measureTimedValue {
        val (sr, dr) = this
        if (sr.contains(source))
            return@measureTimedValue dr.min() + (source - sr.min())
        return@measureTimedValue source
    }.let {
        println("VMap.get took ${it.duration}")
        it.value
    }

    fun VMap.contains(source: Long): Boolean = measureTimedValue {
        val (sr, dr) = this
        sr.contains(source)
    }.let {
        println("VMap.contains took ${it.duration}")
        it.value
    }

    fun List<VMap>.contains(source: Long): Pair<Boolean, VMap?> {
        forEach { vmap ->
            if (vmap.contains(source))
                return true to vmap
        }
        return false to null
    }

    fun VMap.mapRange(range: LongRange): List<LongRange> {
        val (sr, _) = this
        if (contains(range.first) && contains(range.last)) {
            return listOf(get(range.first)..get(range.last))
        } else if (contains(range.first)) {
            return listOf(
                get(range.first)..get(sr.last),
                (sr.last + 1)..range.last
            )
        } else if (contains(range.last)) {
            return listOf(
                (range.first)..(sr.first - 1),
                get(sr.first)..get(range.last),
            )
        } else return listOf(range)
    }

    fun List<VMap>.mapRange2(range: LongRange): List<LongRange> = measureTimedValue {
        val nr = this.map{it.first}.sortedBy { it.first }
        if (range.last < nr.first().first || range.first > nr.last().last)
            return listOf(range)
        val result = mutableListOf<LongRange>()
        var head = range.first
        nr.forEachIndexed { index, longRange ->
            if (longRange.first > head) {
                result.add(head..<longRange.first)
                val end = min(range.last, longRange.last)
                result.add(get(longRange.first)..get(end))
                head = end + 1
            } else if (longRange.first <= head && head <= longRange.last) {
                val start = head
                val end = min(longRange.last, range.last)
                result.add(get(start)..get(end))
                head = end + 1
            }

            if (head > range.last)
                return@measureTimedValue result
        }
        return@measureTimedValue result
    }.let {
        println("List<VMap>.mapRange2 took ${it.duration}")
        it.value
    }

    fun List<VMap>.mapRange(range: LongRange): List<LongRange> {
        val nr = this.flatMap { listOf(it.first.first, it.first.last) }.sorted().toMutableList()
        if (range.last < nr.first() || range.first > nr.last())
            return listOf(range)

        var i = 0
        var start = range.first
        var end = 0L
        val result = mutableListOf<LongRange>()
        var curr = 0L
        while (i<nr.size) {
            curr = nr[i]
            if (curr >= start) {
                if (i%2 > 0) {
                    end = min(curr, range.last)
                    result.add(get(range.first)..get(end))
                    if (end == curr)
                        start = end + 1
                    else break
                } else {
                    end = min(nr[i+1], range.last)
                    result.add(get(start)..<get(end))
                    start = end + 1
                }
            }

            if (start > range.last)
                break
            i++
        }

        if (curr < range.last)
            result.add(start..range.last)

        return result

//        nr.add(range.first)
//        nr.add(range.last)
//        nr.sort()
//        return nr.chunked(2)
//            .map { it[0] .. it[1] }
//            .filter { it.contains(range.first) || it.contains(range.last) }
//            .map {
//                it.println()
//                if (it.last < range.first)
//
//                get(it.first)..get(it.last)
//            }
//
        return listOf()

//        val result = mutableSetOf<LongRange>()
//        val (headContains, headVmap) = contains(range.first)
//        if (headContains) {
//            val start = get(range.first)
//            if (headVmap!!.first.last >= range.last)
//                result.add(start..get(range.last))
//            else {
//                result.add(start..get(headVmap!!.first.last))
//                val moreRanges = mapRange(headVmap!!.first.last + 1..range.last)
//                result.addAll(moreRanges)
//            }
//        }
//
//        val (tailContains, tailVmap) = contains(range.last)
//        if (tailContains) {
//            if (tailVmap!!.first.first <= range.first)
//                result.add(get(range.first)..get(range.last))
//            else {
//                result.add(get(tailVmap!!.first.first)..get(range.last))
//                val moreRanges = mapRange(range.first..<tailVmap!!.first.first)
//                result.addAll(moreRanges)
//            }
//        }
//
//        if (!headContains && !tailContains)
//            result.add(range)
//
//        return result
    }

    fun part1(input: List<String>): Long {
        val input = input.filter { it.isNotBlank() }
        val seeds = input[0].split(":")[1].trim().split("\\s+".toRegex()).map { it.trim().toLong() }

        val graph = mutableMapOf<String, MutableList<VMap>>()
        var i = 1
        while (i < input.size) {
            if (input[i].contains("map")) {
                val mapName = input[i].split(" ")[0]
                graph[mapName] = mutableListOf<VMap>()
                i++
                while (i < input.size && !input[i].contains("map")) {
                    graph[mapName]!!.add(input[i].splitRange())
                    i++
                }
                graph[mapName]!!.sortBy { it.first.first }
            }
        }

        graph.println()

        return seeds.minOf { seed ->
            val soil = graph["seed-to-soil"]!!.get(seed)
            val fertilizer = graph["soil-to-fertilizer"]!!.get(soil)
            val water = graph["fertilizer-to-water"]!!.get(fertilizer)
            val light = graph["water-to-light"]!!.get(water)
            val temperature = graph["light-to-temperature"]!!.get(light)
            val humidity = graph["temperature-to-humidity"]!!.get(temperature)
            val location = graph["humidity-to-location"]!!.get(humidity)
            println("$seed = $location")
            location
        }

    }

    fun part2(input: List<String>): Long {
        val input = input.filter { it.isNotBlank() }
        val seeds: VList =
            input[0].split(":")[1].trim().split("\\s+".toRegex()).map { it.trim().toLong() }.chunked(2).map {
                it[0] until it[0] + it[1]
            }.sortedBy { it.first }
        println(seeds)

        val graph = mutableMapOf<String, MutableList<VMap>>()
        var i = 1
        while (i < input.size) {
            if (input[i].contains("map")) {
                val mapName = input[i].split(" ")[0]
                graph[mapName] = mutableListOf<VMap>()
                i++
                while (i < input.size && !input[i].contains("map")) {
                    graph[mapName]!!.add(input[i].splitRange())
                    i++
                }
                graph[mapName]!!.sortBy { it.first.first }
            }
        }

        graph.println()


        val soils = seeds.flatMap { graph["seed-to-soil"]!!.mapRange(it) }
            .also { println(it) }
        val fertilizers = soils.flatMap { graph["soil-to-fertilizer"]!!.mapRange2(it) }
            .also { println(it) }
        val waters = fertilizers.flatMap { graph["fertilizer-to-water"]!!.mapRange2(it) }
            .also { println(it) }
        val lights = waters.flatMap { graph["water-to-light"]!!.mapRange2(it) }
            .also { println(it) }
        val temperatures = lights.flatMap { graph["light-to-temperature"]!!.mapRange2(it) }
            .also { println(it) }
        val humidity = temperatures.flatMap { graph["temperature-to-humidity"]!!.mapRange2(it) }
            .also { println(it) }
        val location = humidity.flatMap { graph["humidity-to-location"]!!.mapRange2(it) }
            .also { println(it) }

        return location.minOf { it.first }
    }


//    part1(input).println()
    part2(input).println()
}
