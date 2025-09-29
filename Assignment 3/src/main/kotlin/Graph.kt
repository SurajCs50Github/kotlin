/**
 * `Graph` represents a directed, weighted graph.
 * @param V The type representing a vertex.
 */
interface Graph<V> {
    /** @return All vertices (sources and sinks) present in the graph. */
    fun getVertices(): Set<V>

    /** add with cost */
    fun addEdge(from: V, to: V, cost: Double)

    /**
     * Get all outgoing edges from.
     * @return An immutable map of neighbor -> edge weight.
     */
    fun getEdges(from: V): Map<V, Double>

    /** Remove all vertices and edges from the graph. */
    fun clear()
}

/**
 * An adjacency-list implementation of a directed, weighted graph.
 * Internal storage is a map of: from  (to cost).
 */
class GraphAdjList<V> : Graph<V> {
    private val adj: MutableMap<V, MutableMap<V, Double>> = mutableMapOf()

    override fun getVertices(): Set<V> {
        val vs = mutableSetOf<V>()
        vs.addAll(adj.keys)
        adj.values.forEach { neighbors -> vs.addAll(neighbors.keys) }
        return vs
    }

    override fun addEdge(from: V, to: V, cost: Double) {
        require(cost.isFinite()) { "Edge cost must be a finite number." }
        val edges = adj.getOrPut(from) { mutableMapOf() }
        edges[to] = cost
        adj.putIfAbsent(to, mutableMapOf())
    }

    override fun getEdges(from: V): Map<V, Double> {
        return adj[from]?.toMap() ?: emptyMap()
    }

    override fun clear() = adj.clear()
}