package org.example

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HashMapTest {

    private lateinit var map: HashMap<String, Int>

    @BeforeEach
    fun setUp() {
        map = HashMap()
    }

    @Test
    fun `test set and get new element`() {
        map["key1"] = 100
        assertEquals(100, map["key1"])
        assertEquals(1, map.size())
    }

    @Test
    fun `test get non-existent element`() {
        assertNull(map["nonexistent"])
    }

    @Test
    fun `test set to overwrite existing element`() {
        map["key1"] = 100
        assertEquals(100, map["key1"])
        assertEquals(1, map.size())

        map["key1"] = 200
        assertEquals(200, map["key1"])
        assertEquals(1, map.size()) // Size should not change
    }

    @Test
    fun `test contains`() {
        map["key1"] = 1
        assertTrue("key1" in map)
        assertFalse("key2" in map)
    }

    @Test
    fun `test remove element`() {
        map["key1"] = 1
        map["key2"] = 2
        assertEquals(2, map.size())

        val removed = map.remove("key1")
        assertTrue(removed)
        assertEquals(1, map.size())
        assertNull(map["key1"])
        assertFalse("key1" in map)
        assertEquals(2, map["key2"])
    }

    @Test
    fun `test remove non-existent element`() {
        map["key1"] = 1
        val removed = map.remove("key2")
        assertFalse(removed)
        assertEquals(1, map.size())
    }

    @Test
    fun `test keyValuePairs`() {
        map["a"] = 1
        map["b"] = 2
        map["c"] = 3

        val pairs = map.keyValuePairs().sortedBy { it.first }
        val expected = listOf(Pair("a", 1), Pair("b", 2), Pair("c", 3))

        assertEquals(3, pairs.size)
        assertEquals(expected, pairs)
    }

    @Test
    fun `test rehashing`() {
        // Initial capacity is 11, load factor 0.75. Rehash triggered at 9 elements.
        val entries = (0..20).map { "key$it" to it }

        for ((key, value) in entries) {
            map[key] = value
        }

        assertEquals(21, map.size())

        // Verify all elements are still present after rehashing
        for ((key, value) in entries) {
            assertEquals(value, map[key], "Failed to find $key after rehashing")
        }
    }

    @Test
    fun `test collision handling`() {
        // These two keys are designed to collide with capacity 11
        // "a".hashCode() % 11 = 97 % 11 = 9
        // "l".hashCode() % 11 = 108 % 11 = 9
        map["a"] = 1
        map["l"] = 12

        assertEquals(2, map.size())
        assertEquals(1, map["a"])
        assertEquals(12, map["l"])

        map.remove("a")
        assertEquals(1, map.size())
        assertNull(map["a"])
        assertEquals(12, map["l"])
    }
}