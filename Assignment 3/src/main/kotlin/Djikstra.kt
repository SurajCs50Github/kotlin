/**
 * A data class to hold the result of a shortest-path query,
 * containing the [path] as a list of vertices and the total [cost].
 */
data class PathResult<V>(val path: List<V>, val cost: Double)

/**
 * Implements Dijkstra's algorithm to find the shortest path in a graph with non-negative weights.
 *
 * @param graph The directed, weighted graph to search.
 * @param start The starting vertex.
 * @param target The target vertex.
 * @return A [PathResult] containing the path and cost, or null if the target is unreachable.
 */
fun <V> dijkstra(graph: Graph<V>, start: V, target: V): PathResult<V>? {
    val dist = mutableMapOf<V, Double>().withDefault { Double.POSITIVE_INFINITY }
    val prev = mutableMapOf<V, V>()
    val pq: MinPriorityQueue<V> = MinHeapPQ()

    dist[start] = 0.0
    pq.addWithPriority(start, 0.0)

    while (!pq.isEmpty()) {
        val u = pq.next() ?: break
        if (u == target) break

        val distToU = dist.getValue(u)
        for ((v, weight) in graph.getEdges(u)) {
            require(weight >= 0.0) { "Dijkstra's algorithm requires non-negative edge weights." }
            val newDistToV = distToU + weight
            if (newDistToV < dist.getValue(v)) {
                dist[v] = newDistToV
                prev[v] = u
                pq.addWithPriority(v, newDistToV)
            }
        }
    }

    val finalCost = dist[target] ?: return null
    if (!finalCost.isFinite()) return null

    val path = generateSequence(seed = target) { current -> prev[current] }
        .toList()
        .reversed()

    return if (path.firstOrNull() == start) {
        PathResult(path = path, cost = finalCost)
    } else {
        null
    }
}