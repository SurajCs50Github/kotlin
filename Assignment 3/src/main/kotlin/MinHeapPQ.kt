/**

 * It uses an index map for efficient O(log n) priority adjustments (decrease-key).
 */
class MinHeapPQ<T> : MinPriorityQueue<T> {
    private data class Node<T>(val elem: T, var priority: Double)

    private val heap = mutableListOf<Node<T>>()
    private val index: MutableMap<T, Int> = mutableMapOf()

    override fun isEmpty(): Boolean = heap.isEmpty()

    override fun addWithPriority(elem: T, priority: Double) {
        require(priority.isFinite()) { "Priority must be a finite number." }
        if (elem in index) {
            adjustPriority(elem, priority)
        } else {
            heap.add(Node(elem, priority))
            val newIndex = heap.lastIndex
            index[elem] = newIndex
            heapifyUp(newIndex)
        }
    }

    override fun next(): T? {
        if (heap.isEmpty()) return null
        val minElem = heap[0].elem
        swap(0, heap.lastIndex)
        index.remove(minElem)
        heap.removeAt(heap.lastIndex)
        if (heap.isNotEmpty()) heapifyDown(0)
        return minElem
    }

    override fun adjustPriority(elem: T, newPriority: Double) {
        val i = index[elem] ?: return
        val oldPriority = heap[i].priority
        heap[i].priority = newPriority
        if (newPriority < oldPriority) heapifyUp(i) else heapifyDown(i)
    }

    private fun parent(i: Int) = (i - 1) / 2
    private fun left(i: Int) = 2 * i + 1
    private fun right(i: Int) = 2 * i + 2

    private fun heapifyUp(start: Int) {
        var i = start
        while (i > 0 && heap[i].priority < heap[parent(i)].priority) {
            swap(i, parent(i))
            i = parent(i)
        }
    }

    private fun heapifyDown(start: Int) {
        var i = start
        while (true) {
            val l = left(i)
            val r = right(i)
            var smallest = i
            if (l < heap.size && heap[l].priority < heap[smallest].priority) smallest = l
            if (r < heap.size && heap[r].priority < heap[smallest].priority) smallest = r
            if (smallest != i) {
                swap(i, smallest)
                i = smallest
            } else break
        }
    }

    private fun swap(i: Int, j: Int) {
        if (i == j) return
        val nodeI = heap[i]
        val nodeJ = heap[j]
        heap[i] = nodeJ
        heap[j] = nodeI
        index[nodeI.elem] = j
        index[nodeJ.elem] = i
    }
}