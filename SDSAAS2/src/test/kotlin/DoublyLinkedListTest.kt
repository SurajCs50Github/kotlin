import kotlin.test.*

class DoublyLinkedListTest {

    @Test
    fun `list is initially empty`() {
        val list = DoublyLinkedList<Int>()
        assertTrue(list.isEmpty())
        assertNull(list.peekFront())
        assertNull(list.peekBack())
    }

    @Test
    fun `pushFront correctly adds elements`() {
        val list = DoublyLinkedList<String>()
        list.pushFront("A")
        assertFalse(list.isEmpty())
        assertEquals("A", list.peekFront())
        assertEquals("A", list.peekBack())

        list.pushFront("B") // List: B, A
        assertEquals("B", list.peekFront())
        assertEquals("A", list.peekBack())
    }

    @Test
    fun `pushBack correctly adds elements`() {
        val list = DoublyLinkedList<Char>()
        list.pushBack('X')
        assertFalse(list.isEmpty())
        assertEquals('X', list.peekFront())
        assertEquals('X', list.peekBack())

        list.pushBack('Y') // List: X, Y
        assertEquals('X', list.peekFront())
        assertEquals('Y', list.peekBack())
    }

    @Test
    fun `popFront removes elements correctly`() {
        val list = DoublyLinkedList<Int>()
        list.pushBack(10)
        list.pushBack(20) // List: 10, 20

        assertEquals(10, list.popFront())
        assertEquals(20, list.peekFront())
        assertEquals(20, list.popFront())
        assertTrue(list.isEmpty())
        assertNull(list.popFront())
    }

    @Test
    fun `popBack removes elements correctly`() {
        val list = DoublyLinkedList<Int>()
        list.pushBack(10)
        list.pushBack(20) // List: 10, 20

        assertEquals(20, list.popBack())
        assertEquals(10, list.peekBack())
        assertEquals(10, list.popBack())
        assertTrue(list.isEmpty())
        assertNull(list.popBack())
    }

    @Test
    fun `mixed operations work as expected`() {
        val list = DoublyLinkedList<Int>() // []
        list.pushFront(2) // [2]
        list.pushBack(3)  // [2, 3]
        list.pushFront(1) // [1, 2, 3]
        list.pushBack(4)  // [1, 2, 3, 4]

        assertEquals(1, list.peekFront())
        assertEquals(4, list.peekBack())

        assertEquals(1, list.popFront()) // [2, 3, 4]
        assertEquals(4, list.popBack())   // [2, 3]

        assertEquals(2, list.peekFront())
        assertEquals(3, list.peekBack())
    }
}