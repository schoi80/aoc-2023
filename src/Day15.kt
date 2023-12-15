fun Char.convert(curr: Long = 0): Long {
    return (curr + this.toInt()) * 17 % 256
}

fun String.convert(): Long {
    return fold(0L) { acc, c -> c.convert(acc) }
}

data class Box(
    val set: MutableSet<String>,
    val list: MutableList<String>,
    val label: MutableMap<String, Int>
) {
    fun remove(lens: String) {
        if (set.contains(lens)) {
            set.remove(lens)
            list.remove(lens)
            label.remove(lens)
        }
    }

    fun add(lens: String, fl: Int) {
        label[lens] = fl
        if (!set.contains(lens)) {
            set.add(lens)
            list.add(lens)
        }
    }

    fun calc(): Long {
        return list.mapIndexed { index, s ->
            (index + 1L) * label[s]!!
        }.sum()
    }
}

fun main() {

    fun part1(input: List<String>): Long {
        return input.sumOf {
            it.split(",").sumOf {
                it.fold(0L) { acc, c -> c.convert(acc) }
            }
        }
    }

    fun part2(input: List<String>): Long {
        val boxes = Array<Box>(256) { Box(mutableSetOf(), mutableListOf(), mutableMapOf()) }
        input[0].split(",").forEach {
            val tokens = it.split("=")
            if (tokens.size == 2) {
                val lens = tokens[0]
                val fl = tokens[1].toInt()
                val boxIndex = lens.convert().toInt()
                boxes[boxIndex].add(lens, fl)
            } else {
                val lens = it.take(it.length - 1)
                val boxIndex = lens.convert().toInt()
                boxes[boxIndex].remove(lens)
            }
        }
        return boxes.mapIndexed { index, box ->
            (index + 1) * box.calc()
        }.sum()
    }

    val input = readInput("Day15")
    part1(input).println()
    part2(input).println()
}
