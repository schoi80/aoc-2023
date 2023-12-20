fun main() {

    val input = readInput("Day08")

    val nodes = (2..<input.size).associate { i ->
        val line = input[i].split("=")
        val node = line[0].trim()
        val steps = line[1].split(",").map { it.trim() }.let {
            it[0].substring(1) to it[1].dropLast(1)
        }
        node to steps
    }

    fun String.nextNode(i:Char): String {
        return if (i == 'L')
            nodes[this]!!.first
        else nodes[this]!!.second
    }

    fun part1(input: List<String>): Long {
        val instruction = input[0]
        var stepCount = 0
        var currNode = "AAA"
        while (currNode != "ZZZ") {
            val index = stepCount % instruction.length
            currNode = currNode.nextNode(instruction[index])
            stepCount++
        }

        return stepCount.toLong()
    }

    fun part2(input: List<String>): Long {
        val start = nodes.keys.filter { it.last() == 'A' }
        val instruction = input[0]

        fun String.countStepsToZ(): Long {
            var currNode = this
            var stepCount = 0L
            while (currNode.last() != 'Z') {
                val index = stepCount % instruction.length
                currNode = currNode.nextNode(instruction[index.toInt()])
                stepCount++
            }
            return stepCount
        }

        return start.map { it.countStepsToZ() }.reduce { acc, i -> lcm(acc, i) }
    }

    part1(input).println()
    part2(input).println()
}
