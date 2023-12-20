import java.util.LinkedList

enum class SignalType {
    LOW, HIGH
}

typealias Signal = Triple<String, SignalType, String>

sealed class Module(val name: String, var next: List<String>) {

    abstract fun handleSignal(input: Signal): List<Signal>

    class Broadcaster(next: List<String>) : Module("broadcaster", next) {
        override fun handleSignal(input: Signal): List<Signal> {
            return next.map { Triple(name, SignalType.LOW, it) }
        }
    }

    class FlipFlop(name: String, next: List<String>) : Module(name, next) {
        private var isOn: Boolean = false
        override fun handleSignal(input: Signal): List<Signal> {
            val (_, st, _) = input
            if (st == SignalType.HIGH)
                return listOf()
            isOn = !isOn
            return next.map {
                val signalType = if (isOn) SignalType.HIGH else SignalType.LOW
                Triple(name, signalType, it)
            }
        }
    }

    class Conjunction(name: String, next: List<String>) : Module(name, next) {
        private val srcMap = mutableMapOf<String, SignalType>()

        fun setSource(source: List<String>) {
            source.forEach { srcMap[it] = SignalType.LOW }
        }

        override fun handleSignal(input: Signal): List<Signal> {
            val (src, st, _) = input
            srcMap[src] = st
            val signalToSend = if (srcMap.values.all { it == SignalType.HIGH }) SignalType.LOW else SignalType.HIGH
            return next.map {
                Triple(name, signalToSend, it)
            }
        }
    }
}

fun Input.buildCircuit(): Map<String, Module> {
    val modules = this.map { line ->
        val (src, tgt) = line.split(" -> ", limit = 2)
        val next = tgt.split(",").map { it.trim() }
        when (src) {
            "broadcaster" -> Module.Broadcaster(next)
            else -> {
                when (src.first()) {
                    '%' -> Module.FlipFlop(src.drop(1), next)
                    '&' -> Module.Conjunction(src.drop(1), next)
                    else -> error("cannot happen")
                }
            }
        }
    }
    val sourceMap = mutableMapOf<String, MutableSet<String>>()
    modules.forEach { m ->
        m.next.forEach { s ->
            sourceMap.getOrPut(s) { mutableSetOf() }.add(m.name)
        }
    }
    modules.filterIsInstance<Module.Conjunction>().forEach {
        it.setSource(sourceMap[it.name]?.toList() ?: listOf())
    }
    return modules.associateBy { it.name }
}

fun main() {

    fun part1(input: List<String>): Long {
        val circuit = input.buildCircuit()
        var highCount = 0L
        var lowCount = 0L
        val buttonCount = 1000
        val queue = LinkedList<Signal>()

        repeat(buttonCount) {
            queue.add(Triple("button", SignalType.LOW, "broadcaster"))
            while (queue.isNotEmpty()) {
                val signal = queue.pop()
                println("${signal.first} - ${signal.second} -> ${signal.third}")
                if (signal.second == SignalType.HIGH)
                    highCount++
                else lowCount++
                val module = circuit[signal.third] ?: continue
                queue.addAll(module.handleSignal(signal))
            }
        }

        return highCount * lowCount
    }

    fun part2(input: List<String>): Long {
        val circuit = input.buildCircuit()
        val queue = LinkedList<Signal>()
        var buttonCount = 0L

        // I pulled them from the input.  These must send HIGH in order for rx to receive LOW
        val modulesOfInterest = setOf("sr", "sn", "rf", "vq")
        val cycleCounts = mutableListOf<Long>()
        while (cycleCounts.size < 4) {
            buttonCount++
            queue.add(Triple("button", SignalType.LOW, "broadcaster"))
            while (queue.isNotEmpty()) {
                val signal = queue.pop()
                if (signal.first in modulesOfInterest && signal.second == SignalType.HIGH) {
                    println("${signal.first} sent ${signal.second} on button click $buttonCount")
                    cycleCounts.add(buttonCount)
                }
                val module = circuit[signal.third] ?: continue
                queue.addAll(module.handleSignal(signal))
            }
        }

        // Find LCM of cycle counts.
        // i.e., 4 modules of our interest cycle to HIGH at each of these intervals
        return cycleCounts.fold(1L) { acc, it -> lcm(acc, it) }
    }

    val input = readInput("Day20")
    part1(input).println()
    part2(input).println()
}