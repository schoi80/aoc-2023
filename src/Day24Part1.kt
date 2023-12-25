import Day24Part1.part1
import kotlin.math.max
import kotlin.math.min

object Day24Part1 {
    data class XYZ(
        val x: Double,
        val y: Double,
        val z: Double,
    )

    data class Path(
        val p1: XYZ,
        val p2: XYZ,
    ) {
        fun onPath(x: Double, y: Double): Boolean {
            val xRange = listOf(p1.x, p2.x).sorted()
            val yRange = listOf(p1.y, p2.y).sorted()
            return xRange[0] <= x && x <= xRange[1] && yRange[0] <= y && y <= yRange[1]
        }
    }

    data class Hailstone(
        val pos: XYZ,
        val vel: XYZ,
    ) {
        // y = mx + b
        private val m = vel.y / vel.x
        private val b = pos.y - (m * pos.x)

        companion object {
            fun init(line: String): Hailstone {
                val (pos, vel) = line.split("@", limit = 2)
                return Hailstone(
                    pos = pos.split(",", limit = 3).map { it.trim().toDouble() }.let { (x, y, z) -> XYZ(x, y, z) },
                    vel = vel.split(",", limit = 3).map { it.trim().toDouble() }.let { (x, y, z) -> XYZ(x, y, z) },
                )
            }
        }

        private fun pathAtBoundary(mn: Double, mx: Double): Path {
            val (x0, x1) = timeAtBoundary(pos.x, vel.x, mn, mx)
            val (y0, y1) = timeAtBoundary(pos.y, vel.y, mn, mx)
            val t0 = max(x0, y0)
            val t1 = min(x1, y1)
            return Path(
                p1 = XYZ(pos.x + vel.x * t0, pos.y + vel.y * t0, 0.0),
                p2 = XYZ(pos.x + vel.x * t1, pos.y + vel.y * t1, 0.0),
            )
        }

        fun crosses(h2: Hailstone, mn: Double, mx: Double): Boolean {
            val h1 = this

            // Parallel
            if (h1.m == h2.m) return false

            // Find crossing point
            val x = (h1.b - h2.b) / (h2.m - h1.m)
            val y = h1.m * x + h1.b

            // If crossing point is out of range, return false
            if (x < mn || x > mx || y < mn || y > mx)
                return false

            // If crossing point is on path for both hailstones
            return h1.pathAtBoundary(mn, mx).onPath(x, y) && h2.pathAtBoundary(mn, mx).onPath(x, y)
        }
    }

    fun timeAtBoundary(p: Double, v: Double, mn: Double, mx: Double): List<Double> {
        val t0 = (mn - p) / v
        val t1 = (mx - p) / v
        return listOf(max(0.0, t0), max(0.0, t1)).sorted()
    }

    fun part1(input: List<String>): Int {
        val pvs = input.map { Hailstone.init(it) }
        var count = 0
        val mn = 200000000000000.0
        val mx = 400000000000000.0
        (0..<pvs.size - 1).forEach { i ->
            val h1 = pvs[i]
            (i + 1..<pvs.size).forEach { j ->
                val h2 = pvs[j]
                if (h1.crosses(h2, mn, mx))
                    count += 1
            }
        }
        return count
    }
}

fun main() {
    val input = readInput("Day24")
    part1(input).println()
}