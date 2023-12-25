import Day24Part2.part2
import com.microsoft.z3.Context
import com.microsoft.z3.Model
import com.microsoft.z3.RatNum
import com.microsoft.z3.RealExpr
import com.microsoft.z3.Status
import java.math.BigDecimal

object Day24Part2 {
    data class XYZ(
        val x: Long,
        val y: Long,
        val z: Long,
    )

    data class Hailstone(
        val pos: XYZ,
        val vel: XYZ,
    ) {

        companion object {
            fun init(line: String): Hailstone {
                val (pos, vel) = line.split("@", limit = 2)
                return Hailstone(
                    pos = pos.split(",", limit = 3).map { it.trim().toLong() }.let { (x, y, z) -> XYZ(x, y, z) },
                    vel = vel.split(",", limit = 3).map { it.trim().toLong() }.let { (x, y, z) -> XYZ(x, y, z) },
                )
            }
        }
    }

    fun Model.getVar(x: RealExpr): BigDecimal {
        val xc = getConstInterp(x) as RatNum
        return xc.bigIntNumerator.toBigDecimal().divide(xc.bigIntDenominator.toBigDecimal())
    }


    fun part2(input: List<String>): Long = with(Context()) {
        val pvs = input.map { Hailstone.init(it) }
        val solver = mkSolver()
        val x = mkRealConst("x")
        val y = mkRealConst("y")
        val z = mkRealConst("z")
        val vx = mkRealConst("vx")
        val vy = mkRealConst("vy")
        val vz = mkRealConst("vz")

        // Create 3 time variables that are greater than 0
        val t = listOf(
            mkRealConst("t1"),
            mkRealConst("t2"),
            mkRealConst("t3")
        ).onEach { solver.add(mkGe(it, mkReal(0))) }

        // 3 time variables must be different from each other
        solver.add(mkNot(mkEq(t[0], t[1])))
        solver.add(mkNot(mkEq(t[1], t[2])))
        solver.add(mkNot(mkEq(t[0], t[2])))

        // Just need 3 points to solve
        pvs.take(3).forEachIndexed { i, h ->
            solver.add(mkGe(t[i], mkReal(0))) // t > 0
            val rockX = mkAdd(x, mkMul(vx, t[i]))
            val rockY = mkAdd(y, mkMul(vy, t[i]))
            val rockZ = mkAdd(z, mkMul(vz, t[i]))
            val hX = mkAdd(mkReal(h.pos.x), mkMul(mkReal(h.vel.x), t[i]))
            val hY = mkAdd(mkReal(h.pos.y), mkMul(mkReal(h.vel.y), t[i]))
            val hZ = mkAdd(mkReal(h.pos.z), mkMul(mkReal(h.vel.z), t[i]))
            solver.add(mkEq(rockX, hX))
            solver.add(mkEq(rockY, hY))
            solver.add(mkEq(rockZ, hZ))
        }

        if (solver.check() != Status.SATISFIABLE)
            error("cannot happen")

        val rx = solver.model.getVar(x).toLong()
        val ry = solver.model.getVar(y).toLong()
        val rz = solver.model.getVar(z).toLong()
        println("Solved rock($rx, $ry, $rz)")

        return rx + ry + rz
    }
}

fun main() {
    val input = readInput("Day24")
    part2(input).println()
}
