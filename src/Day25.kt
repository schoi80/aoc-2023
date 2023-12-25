import Day25.part1
import Day25.part2
import java.util.LinkedList

typealias Edge = Pair<String, String>

object Day25 {

    class Graph {
        val adjacencyList: MutableMap<String, MutableList<String>> = mutableMapOf()
        fun addEdge(e: Edge) {
            adjacencyList.getOrPut(e.first) { mutableListOf() }.add(e.second)
            adjacencyList.getOrPut(e.second) { mutableListOf() }.add(e.first)
        }

        fun removeEdge(e: Edge) {
            adjacencyList[e.first]!!.remove(e.second)
            adjacencyList[e.second]!!.remove(e.first)
        }

        fun getPath(v1: String, v2: String): MutableList<String> {
            val q = LinkedList<MutableList<String>>()
            val visited = mutableSetOf<String>()
            q.add(mutableListOf(v1))
            while (q.isNotEmpty()) {
                val p = q.poll()
                if (visited.contains(p.last()))
                    continue
                visited.add(p.last())
                if (p.last() == v2)
                    return p
                adjacencyList[p.last()]?.map {
                    q.add((p + it).toMutableList())
                }
            }
            error("cannot happen")
        }

        fun countVerticesInDisjointSets(): List<Long> {
            val disjointSetCounts = mutableListOf<Long>()
            val visited = mutableSetOf<String>()

            for (v in adjacencyList.keys) {
                if (!visited.contains(v)) {
                    val count = dfsCount(v, visited)
                    disjointSetCounts.add(count)
                }
            }

            return disjointSetCounts
        }

        private fun dfsCount(curr: String, visited: MutableSet<String>): Long {
            visited.add(curr)
            var count = 1L
            for (next in adjacencyList[curr]!!) {
                if (!visited.contains(next)) {
                    count += dfsCount(next, visited)
                }
            }
            return count
        }
    }

    // Keep edge sorted by vertex
    private fun Edge.sort(): Edge {
        return if (first < second) this else second to first
    }

    fun part1(input: List<String>): Long {
        val graph = Graph().apply {
            input.flatMap {
                val (a, b) = it.split(": ", limit = 2)
                b.split(" ").map { a to it }
            }.forEach { this.addEdge(it) }
        }

        val vertices = graph.adjacencyList.keys.toList()
        println("vertex count: ${vertices.size}")
        val edgeWeightMap = mutableMapOf<Edge, Int>()

        // Find the path between every 2 nodes
        for (i in 0..<vertices.size - 1) {
            val v1 = vertices[i]
            for (j in i + 1..<vertices.size) {
                println("$i, $j")
                val v2 = vertices[j]
                val p = graph.getPath(v1, v2)

                // For each path we cross, add 1 to its weight
                p.zipWithNext().forEach {
                    val edge = it.sort()
                    edgeWeightMap[edge] = (edgeWeightMap[edge] ?: 0) + 1
                }
            }
        }

        // We're going to cut 3 most heavily travelled edges
        edgeWeightMap.toList()
            .sortedBy { it.second }
            .takeLast(3)
            .forEach { (edge, _) -> graph.removeEdge(edge) }

        // Fingers crossed!!
        return graph.countVerticesInDisjointSets().reduce { acc, it -> acc * it }
    }

    fun part2(input: List<String>): Long {
        return 0L
    }
}

fun main() {
    val input = readInput("Day25")
    part1(input).println()
    part2(input).println()
}
