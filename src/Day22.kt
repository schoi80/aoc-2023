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

    fun isKeyBrick(): Boolean {
        return supports.isNotEmpty() && supports.count { it.supportedBy.size == 1 } > 0
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
    val pq = PriorityQueue(compareBy<Brick> { min(it.z1, it.z2) })
    pq.addAll(bricks)
    val bricksByZ = PriorityQueue(compareBy<Brick> { it.z2 * -1 })
    while (pq.isNotEmpty()) {
        val b1 = pq.poll()
        val tempStack = Stack<Brick>()
        while (bricksByZ.isNotEmpty()) {
            if (bricksByZ.peek().intersects(b1)) break
            tempStack.push(bricksByZ.poll())
        }
        if (bricksByZ.isEmpty())
            b1.settle(1)
        else
            b1.settle(bricksByZ.peek().z2 + 1)

        bricksByZ.add(b1)
        while (tempStack.isNotEmpty()) {
            bricksByZ.add(tempStack.pop())
        }
    }

    val bricksByLevel = bricksByZ.groupBy { it.z1 }
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
        return input.settleBricks().count { !it.isKeyBrick() }
    }

    fun part2(input: List<String>): Int {
        val bricks = input.settleBricks()
        val keyBricks = bricks.filter { it.isKeyBrick() }
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