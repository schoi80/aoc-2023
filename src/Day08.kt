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

    fun part1(input: List<String>): Long {
        val instruction = input[0]
        var stepCount = 0
        var isDone = false
        var currNode = "AAA"
        while (!isDone) {
            currNode = if (instruction[stepCount.mod(instruction.length)] == 'L')
                nodes[currNode]!!.first
            else nodes[currNode]!!.second
            isDone = currNode == "ZZZ"
            stepCount++
        }

        return stepCount.toLong()
    }


    fun part2(input: List<String>): Long {
        val start = nodes.keys.filter { it.last() == 'A' }
        val instruction = input[0]

        fun String.isDone(): List<Long> {
            var currNode = this
            val endsInZ = mutableListOf<Long>()
            var stepCount = 0
            while (true) {
                val index = stepCount.mod(instruction.length)
                currNode = if (instruction[index] == 'L')
                    nodes[currNode]!!.first
                else nodes[currNode]!!.second
                if (currNode.last() == 'Z')
                    endsInZ.add((stepCount + 1).toLong())
                if (endsInZ.size > 0 && stepCount >= instruction.length - 1)
                    break
                stepCount++
            }
            return endsInZ
        }

        fun gcd(a: Long, b: Long): Long {
            return if (b == 0L) a else gcd(b, a % b)
        }

        fun lcm(a: Long, b: Long): Long {
            return (a * b) / gcd(a, b)
        }

        return start.map { it.isDone().first() }.reduce { acc, i -> lcm(acc, i) }
    }
//
//    fun part2(input: List<String>): Long {
//        var start = steps.keys.filter { it.last() == 'A' }
//        fun List<String>.isDone() = this.count{ it.last() == 'Z' } == start.size
//        val inst = input[0]
//        var curr = 0
//        var done = false
//
//        while (!done) {
//            val index = curr.mod(inst.length)
//            val dir = inst[index]
//            println("$dir: $start")
//            start = start.map {
//                val next = steps[it]
//                when (dir) {
//                    'L' -> next!!.first
//                    else -> next!!.second
//                }
//            }
//            done = start.isDone()
//            curr++
//        }
//        println("DONE: $start")
//        return curr.toLong()
//    }

    part1(input).println()
    part2(input).println()
}
