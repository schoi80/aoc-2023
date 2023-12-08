fun main() {

    val input = readInput("Day08")

    fun part1(input: List<String>): Long {

        val steps = mutableMapOf<String, Pair<String, String>>()
        for (i in (2..<input.size)) {
            val line = input[i].split("=")
            val step = line[0].trim()
            steps[step] = line[1].split(",").map { it.trim() }.let {
                val l = it[0].substring(1)
                val r = it[1].dropLast(1)
                l to r
            }
        }
        steps.println()

        val inst = input[0]
        var curr = 0

        var done = false
        var step = "AAA"
        while (!done) {
            val index = curr.mod(inst.length)
            step = when (inst[index]) {
                'L' -> steps[step]!!.first
                else -> steps[step]!!.second
            }
            done = step == "ZZZ"
            curr++
        }

        return curr.toLong()
    }



    fun part2(input: List<String>): Long {
        val steps = mutableMapOf<String, Pair<String, String>>()
        for (i in (2..<input.size)) {
            val line = input[i].split("=")
            val step = line[0].trim()
            steps[step] = line[1].split(",").map { it.trim() }.let {
                val l = it[0].substring(1)
                val r = it[1].dropLast(1)
                l to r
            }
        }
        steps.println()


        val start = steps.keys.filter { it.last() == 'A' }

        val inst = input[0]

        fun String.run() : List<Long>{
            var start = this
            val endsInZ = mutableListOf<Long>()
            for (i in (0..Int.MAX_VALUE)) {
                val index = i.mod(inst.length)
                start = if (inst[index] == 'L')
                    steps[start]!!.first
                else steps[start]!!.second
                if (start.last() == 'Z')
                    endsInZ.add((i + 1).toLong())

                if (endsInZ.size > 0 && i >= inst.length - 1) {
                    return endsInZ
                }

            }
            error("should never happen")
        }
        println("START: $start")
        val r = start.map { it.run().first() }

        fun gcd(a: Long, b: Long): Long {
            return if (b == 0L) a else gcd(b, a % b)
        }

        fun lcm(a: Long, b: Long): Long {
            return (a * b) / gcd(a, b)
        }

        return r.reduce{acc, i -> lcm(acc, i)}
    }
//
//    fun part2(input: List<String>): Long {
//        val steps = mutableMapOf<String, Pair<String, String>>()
//        for (i in (2..<input.size)) {
//            val line = input[i].split("=")
//            val step = line[0].trim()
//            steps[step] = line[1].split(",").map { it.trim() }.let {
//                val l = it[0].substring(1)
//                val r = it[1].dropLast(1)
//                l to r
//            }
//        }
//        steps.println()
//
//        var start = steps.keys.filter { it.last() == 'A' }
//
//        fun List<String>.isDone() = this.count{ it.last() == 'Z' } == start.size
//
//        val inst = input[0]
//        var curr = 0
//
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
