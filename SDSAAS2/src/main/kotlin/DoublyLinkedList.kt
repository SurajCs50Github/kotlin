
interface LinkedList<T> {
    fun pushFront(data: T)
    fun pushBack(data: T)
    fun popFront(): T?
    fun popBack(): T?
    fun peekFront(): T?
    fun peekBack(): T?
    fun isEmpty(): Boolean
}


class DoublyLinkedList<T> : LinkedList<T> {


    private data class Node<T>(var data: T, var prev: Node<T>? = null, var next: Node<T>? = null)

    private var head: Node<T>? = null
    private var tail: Node<T>? = null
    private var size = 0


    override fun isEmpty(): Boolean {
        return size == 0
    }


    override fun pushFront(data: T) {
        val newNode = Node(data, prev = null, next = head)
        if (head != null) {
            head?.prev = newNode
        }
        head = newNode
        if (tail == null) { // If list was empty, new node is both head and tail
            tail = head
        }
        size++
    }


    override fun pushBack(data: T) {
        if (isEmpty()) {
            pushFront(data) // If empty, pushing to back is same as front
            return
        }
        val newNode = Node(data, prev = tail, next = null)
        tail?.next = newNode
        tail = newNode
        size++
    }

    override fun peekFront(): T? {
        return head?.data
    }

    override fun peekBack(): T? {
        return tail?.data
    }


    override fun popFront(): T? {
        if (isEmpty()) return null
        val data = head?.data
        head = head?.next
        head?.prev = null
        size--
        if (isEmpty()) { // If list becomes empty, tail must also be null
            tail = null
        }
        return data
    }


    override fun popBack(): T? {
        if (isEmpty()) return null
        val data = tail?.data
        tail = tail?.prev
        tail?.next = null
        size--
        if (isEmpty()) { // If list becomes empty, head must also be null
            head = null
        }
        return data
    }
}