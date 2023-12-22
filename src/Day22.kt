import java.util.PriorityQueue
import java.util.Stack
import kotlin.math.min

data class Brick(
    val name: String,
    val xRange: IntRange,
    val yRange: IntRange,
    var zRange: IntRange,
    val supports: MutableList<Brick> = mutableListOf(),
    val supportedBy: MutableList<Brick> = mutableListOf(),
    var isCrumbled: Boolean = false
) {
    val z1 get() = zRange.first
    val z2 get() = zRange.last

    companion object {
        fun init(line: String, name: String = ""): Brick {
            val (c1, c2) = line.split("~", limit = 2)
            val (x1, y1, z1) = c1.split(",", limit = 3).map { it.toInt() }
            val (x2, y2, z2) = c2.split(",", limit = 3).map { it.toInt() }
            return Brick(name, x1..x2, y1..y2, z1..z2)
        }
    }

    fun intersects(b: Brick): Boolean {
        val x = this.xRange.intersects(b.xRange)
        val y = this.yRange.intersects(b.yRange)
        return x && y
    }

    fun supports(b: Brick): Boolean {
        return this.intersects(b) && z2 + 1 == b.z1
    }

    fun settle(h: Int) {
        zRange = h..(h + (z2 - z1))
    }

    fun canDisintegrate(): Boolean {
        return !(supports.isNotEmpty() && supports.count { it.supportedBy.size == 1 } > 0)
    }

    fun countFall(): Int {
        isCrumbled = true
        if (supports.isEmpty())
            return 0

        // Count the supporting bricks where its supports are all crumbled
        val bricks = supports.filter { b ->
            b.supportedBy.count { !it.isCrumbled } == 0
        }

        return bricks.size + bricks.sumOf { it.countFall() }
    }

}


fun Input.settleBricks(): List<Brick> {
    val bricks = this.mapIndexed { i, it -> Brick.init(it, (i + 1).toString()) }

    // Bricks fall in the order of its lower z
    val pq = PriorityQueue(compareBy<Brick> { it.z1 })
    pq.addAll(bricks)

    // Bricks by its upper z after settling
    val settledBricks = PriorityQueue(compareBy<Brick> { it.z2 * -1 })
    while (pq.isNotEmpty()) {
        val b1 = pq.poll()

        // Start popping the settle bricks until we find one that can be a support
        val tempStack = Stack<Brick>()
        while (settledBricks.isNotEmpty()) {
            if (settledBricks.peek().intersects(b1)) break
            tempStack.push(settledBricks.poll())
        }

        // If no more bricks left, this one will settle on the ground level
        // Otherwise, put this one on top of the highest settled brick
        val settleLevel =
            if (settledBricks.isEmpty()) 1
            else settledBricks.peek().z2 + 1
        settledBricks.add(b1.apply { settle(settleLevel) })

        // Move the popped bricks back in
        while (tempStack.isNotEmpty()) {
            settledBricks.add(tempStack.pop())
        }
    }

    // Build the bricks dependency
    val bricksByLevel = settledBricks.groupBy { it.z1 }
    bricksByLevel.forEach { (_, bricks) ->
        for (b1 in bricks) {
            bricksByLevel[b1.z2 + 1]?.forEach { b2 ->
                if (b1.supports(b2)) {
                    b1.supports.add(b2)
                    b2.supportedBy.add(b1)
                }
            }
        }
    }

    return bricks
}

fun main() {

    fun part1(input: List<String>): Int {
        return input.settleBricks().count { it.canDisintegrate() }
    }

    fun part2(input: List<String>): Int {
        val bricks = input.settleBricks()
        val keyBricks = bricks.filter { !it.canDisintegrate() }
        return keyBricks.sumOf {
            // Reset crumbed state
            bricks.forEach { it.isCrumbled = false }
            it.countFall()
        }
    }

    val input = readInput("Day22")
    part1(input).println()
    part2(input).println()
}