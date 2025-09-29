
interface MinPriorityQueue<T> {
    /** @return True if the queue is empty, false otherwise. */
    fun isEmpty(): Boolean

    /** Add (element) with a given (priority). If the element already exists, its priority is updated. */
    fun addWithPriority(elem: T, priority: Double)

    /**
     * Get the next element (the one with the lowest priority value) and remove it from the queuee
     */
    fun next(): T?

    /**
     * If the element is not in the queue, this operation does nothing.
     */
    fun adjustPriority(elem: T, newPriority: Double)
}