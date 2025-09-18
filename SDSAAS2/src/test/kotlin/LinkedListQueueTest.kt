import org.junit.jupiter.api.Test;

class LinkedListQueueTest {
    @Test
    fun `queue follows FIFO order`() {
        val queue = LinkedListQueue<String>()
        assertTrue(queue.isEmpty())

        queue.enqueue("First")
        queue.enqueue("Second")
        queue.enqueue("Third")

        assertFalse(queue.isEmpty())
        assertEquals("First", queue.peek())
        assertEquals("First", queue.dequeue())
        assertEquals("Second", queue.peek())
        assertEquals("Second", queue.dequeue())
        assertEquals("Third", queue.dequeue())
        assertTrue(queue.isEmpty())
        assertNull(queue.dequeue())
    }
