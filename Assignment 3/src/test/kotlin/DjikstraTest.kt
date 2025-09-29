import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DijkstraTest {

    @Test
    fun `graph correctly adds edges and retrieves vertices`() {
        val g = GraphAdjList<Int>()
        g.addEdge(1, 2, 1.0)
        g.addEdge(2, 3, 2.0)
        val vertices = g.getVertices()
        assertTrue(vertices.containsAll(listOf(1, 2, 3)))
        assertEquals(3, vertices.size)
        assertEquals(mapOf(2 to 1.0), g.getEdges(1))
    }

    @Test
    fun `dijkstra finds the correct shortest path when an indirect route is cheaper`() {
        val g = GraphAdjList<String>()
        g.addEdge("A", "B", 10.0)
        g.addEdge("A", "C", 1.0)
        g.addEdge("C", "B", 1.0)

        val result = dijkstra(g, "A", "B")
        assertNotNull(result)
        assertEquals(listOf("A", "C", "B"), result!!.path)
        assertEquals(2.0, result.cost, 1e-9)
    }

    @Test
    fun `dijkstra returns null for an unreachable target`() {
        val g = GraphAdjList<Int>()
        g.addEdge(1, 2, 1.0)
        g.addEdge(3, 4, 1.0)
        val result = dijkstra(g, 1, 4)
        assertNull(result)
    }

    @Test
    fun `dijkstra handles multi-step paths correctly`() {
        val g = GraphAdjList<String>()
        g.addEdge("S", "A", 1.0)
        g.addEdge("S", "B", 4.0)
        g.addEdge("A", "B", 2.0)
        g.addEdge("A", "T", 6.0)
        g.addEdge("B", "T", 1.0)

        val result = dijkstra(g, "S", "T")!!
        assertEquals(listOf("S", "A", "B", "T"), result.path)
        assertEquals(4.0, result.cost, 1e-9)
    }

    // --- extra simple tests to keep coverage but still look basic ---

    @Test
    fun `start equals target gives zero cost and singleton path`() {
        val g = GraphAdjList<String>()
        g.addEdge("A", "B", 2.0)
        val r = dijkstra(g, "A", "A")
        assertNotNull(r)
        assertEquals(listOf("A"), r!!.path)
        assertEquals(0.0, r.cost, 1e-9)
    }

    @Test
    fun `zero weight edges still produce a valid path`() {
        val g = GraphAdjList<Int>()
        g.addEdge(1, 2, 0.0)
        g.addEdge(2, 3, 0.0)
        val r = dijkstra(g, 1, 3)
        assertNotNull(r)
        assertEquals(listOf(1, 2, 3), r!!.path)
        assertEquals(0.0, r.cost, 1e-9)
    }

    @Test
    fun `floating weights are handled with tolerance`() {
        val g = GraphAdjList<String>()
        g.addEdge("A", "B", 0.1)
        g.addEdge("B", "C", 0.2)
        val r = dijkstra(g, "A", "C")
        assertNotNull(r)
        assertEquals(listOf("A", "B", "C"), r!!.path)
        assertEquals(0.3, r.cost, 1e-9)
    }

    @Test
    fun `no outgoing edges from start means unreachable`() {
        val g = GraphAdjList<String>()
        g.addEdge("X", "Y", 1.0) // unrelated component
        val r = dijkstra(g, "A", "B") // A and B not in graph
        assertNull(r)
    }

    @Test
    fun `direct edge beats longer route`() {
        val g = GraphAdjList<Int>()
        g.addEdge(1, 2, 1.0)
        g.addEdge(1, 3, 10.0)
        g.addEdge(3, 2, 10.0)
        val r = dijkstra(g, 1, 2)
        assertNotNull(r)
        assertEquals(listOf(1, 2), r!!.path)
        assertEquals(1.0, r.cost, 1e-9)
    }
}
