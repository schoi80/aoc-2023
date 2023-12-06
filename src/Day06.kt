fun main() {

    val input = readInput("Day06")

    fun numWaysToWin(time: Long, distance: Long): Long {
        return (1..time).count { speed ->
            (time - speed) * speed > distance
        }.toLong()
    }

    fun part1(input: List<String>): Long {
        val time = input[0].split(":")[1].trim().split("\\s+".toRegex()).map { it.toLong() }
        val distance = input[1].split(":")[1].trim().split("\\s+".toRegex()).map { it.toLong() }
        return time.zip(distance).fold(1) { acc, pair ->
            numWaysToWin(pair.first, pair.second) * acc
        }
    }

    fun part2(input: List<String>): Long {
        val time = input[0].split(":")[1].trim().replace("\\s+".toRegex(), "").toLong()
        val distance = input[1].split(":")[1].trim().replace("\\s+".toRegex(), "").toLong()
        return numWaysToWin(time, distance)
    }

    part1(input).println()
    part2(input).println()
}
