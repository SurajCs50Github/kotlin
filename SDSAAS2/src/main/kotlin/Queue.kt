interface Queue<T> {
    fun enqueue(data: T)
    fun dequeue(): T?
    fun peek(): T?
    fun isEmpty(): Boolean
}


class LinkedListQueue<T> : Queue<T> {
    private val list = DoublyLinkedList<T>()

    override fun enqueue(data: T) = list.pushBack(data)

    override fun dequeue(): T? = list.popFront()

    override fun peek(): T? = list.peekFront()

    override fun isEmpty(): Boolean = list.isEmpty()
}