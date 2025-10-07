import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SortsTest {

    private fun getUnsortedList() = mutableListOf(5, 1, 4, 2, 8, 9, 3, 6, 7)
    private val sortedList = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)

    @Test
    fun `test insertion sort`() {
        val list = getUnsortedList()
        insertionSort(list)
        assertEquals(sortedList, list)
    }

    @Test
    fun `test selection sort`() {
        val list = getUnsortedList()
        selectionSort(list)
        assertEquals(sortedList, list)
    }

    @Test
    fun `test quick sort`() {
        val list = getUnsortedList()
        quickSort(list)
        assertEquals(sortedList, list)
    }

    @Test
    fun `test merge sort`() {
        val list = getUnsortedList()
        val result = mergeSort(list)
        assertEquals(sortedList, result)
    }

    @Test
    fun `test sorting an empty list`() {
        val empty = mutableListOf<Int>()
        insertionSort(empty) // Test one in-place
        assertTrue(empty.isEmpty())

        val result = mergeSort(empty) // Test one that returns a new list
        assertTrue(result.isEmpty())
    }

    @Test
    fun `test sorting an already sorted list`() {
        val alreadySorted = mutableListOf(1, 2, 3, 4, 5)
        quickSort(alreadySorted)
        assertEquals(listOf(1, 2, 3, 4, 5), alreadySorted)
    }
}