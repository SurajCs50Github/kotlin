fun main() {
    val g = GraphAdjList<String>()

    g.addEdge("Boston", "New York", 3.5)
    g.addEdge("Boston", "Albany", 2.5)
    g.addEdge("Albany", "New York", 2.0)
    g.addEdge("Albany", "Philadelphia", 4.0)
    g.addEdge("New York", "Philadelphia", 1.5)
    g.addEdge("Philadelphia", "Washington", 2.5)
    g.addEdge("New York", "Washington", 4.0)

    println("Example 1: Boston to Washington")
    val r1 = dijkstra(g, "Boston", "Washington")
    if (r1 == null) {
        println("No path")
    } else {
        println("Path: " + r1.path)
        println("Cost: " + r1.cost)
    }

    // Add an isolated edge to show an unreachable target from Boston
    g.addEdge("Seattle", "Portland", 3.0)

    println()
    println("Example 2: Boston to Seattle")
    val r2 = dijkstra(g, "Boston", "Seattle")
    if (r2 == null) {
        println("No path")
    } else {
        println("Path: " + r2.path)
        println("Cost: " + r2.cost)
    }
}
