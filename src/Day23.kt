import kotlin.math.max

typealias Grid = MutableInput<Char>

fun main() {

    fun Grid.next(curr: RowCol): List<RowCol> {
        return when (this.get(curr)) {
            '>' -> listOf(curr.right())
            '<' -> listOf(curr.left())
            '^' -> listOf(curr.up())
            'v' -> listOf(curr.down())
            else -> this.adjacent(curr).filter { this.get(it) != '#' }
        }
    }

    fun Grid.next2(curr: RowCol, prev: RowCol? = null): List<RowCol> {
        when (this.get(curr)) {
            '>' -> if (prev != null && prev != curr.right()) return listOf(curr.right())
            '<' -> if (prev != null && prev != curr.left()) return listOf(curr.left())
            '^' -> if (prev != null && prev != curr.up()) return listOf(curr.up())
            'v' -> if (prev != null && prev != curr.down()) return listOf(curr.down())
        }
        return this.adjacent(curr).filter { this.get(it) != '#' }
    }

    // For debugging
    fun Grid.print(visited: MutableSet<RowCol>) {
        for (i in this.indices) {
            this[i].mapIndexed { j, c ->
                if (visited.contains(i to j)) 'O'
                else this.get(i to j)
            }.joinToString("").println()
        }
        println(visited.size)
    }

    fun part1(input: List<String>): Int {
        val grid = input.toMutableInput { it }
        val start = 0 to 1
        val end = grid.size - 1 to grid[0].size - 2
        var maxPath = 0

        fun Grid.hike(curr: RowCol, visited: MutableSet<RowCol>, steps: Int) {
            if (curr == end) {
                maxPath = max(maxPath, steps)
//                this.print(visited)
                return
            }

            val next = this.next(curr).filterNot { visited.contains(it) }
            if (next.isEmpty()) return
            for (n in next) {
                visited.add(n)
                this.hike(n, visited, steps + 1)
                visited.remove(n)
            }
        }

        grid.set(start, 'O')
        grid.hike(start, mutableSetOf(start), 0)

        return maxPath
    }

    fun part2(input: List<String>): Int {
        val grid = input.toMutableInput { it }
        val start = 0 to 1
        val end = grid.size - 1 to grid[0].size - 2
        var maxPath = 0

        fun Grid.hike(curr: RowCol, visited: MutableSet<RowCol>, steps: Int, prev:RowCol?=null) {
            if (curr == end) {
                maxPath = max(maxPath, steps)
                // I'm not going to let this run forever..
                // Just start entering the current observed max every 5(?) mins..
                println("$maxPath : $steps")
                return
            }

            val next = this.next2(curr, prev).filterNot { visited.contains(it) }
            if (next.isEmpty()) return
            for (n in next) {
                visited.add(n)
                this.hike(n, visited, steps + 1, curr)
                visited.remove(n)
            }
        }

        grid.set(start, 'O')
        grid.hike(start, mutableSetOf(start), 0, null)

        return maxPath
    }

    val input = readInput("Day23")
    part1(input).println()
    part2(input).println()
}