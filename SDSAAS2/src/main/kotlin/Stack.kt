// Provided Stack Interface
interface Stack<T> {
    fun push(data: T)
    fun pop(): T?
    fun peek(): T?
    fun isEmpty(): Boolean
}

/**
 * An implementation of the Stack ADT using a DoublyLinkedList.
 * This class delegates its operations to the underlying list, achieving
 * LIFO behavior by mapping stack operations to the front of the list.
 */
class LinkedListStack<T> : Stack<T> {
    private val list = DoublyLinkedList<T>()

    override fun push(data: T) = list.pushFront(data)

    override fun pop(): T? = list.popFront()

    override fun peek(): T? = list.peekFront()

    override fun isEmpty(): Boolean = list.isEmpty()
}