package org.example

import kotlin.math.absoluteValue

/**
 * Implements an AssociativeArray using a hash table with separate chaining.
 * Handles dynamic rehashing when the load factor is exceeded.
 */
class HashMap<K, V> : AssociativeArray<K, V> {

    // Internal class to store key-value pairs in the chains
    private data class Entry<K, V>(val key: K, var value: V)

    private var numElements: Int = 0
    private var capacity: Int = 11 // Start with a prime number
    private var buckets: Array<MutableList<Entry<K, V>>> = Array(capacity) { mutableListOf() }

    private val loadFactorThreshold = 0.75

    /**
     * Calculates the bucket index for a given key using the division method.
     */
    private fun getBucketIndex(k: K): Int {
        return k.hashCode().absoluteValue % capacity
    }

    /**
     * Finds the next prime number roughly double the current capacity for rehashing.
     */
    private fun findNextPrime(n: Int): Int {
        var num = n
        while (true) {
            if (isPrime(num)) {
                return num
            }
            num++
        }
    }

    /**
     * Helper to check if a number is prime.
     */
    private fun isPrime(n: Int): Boolean {
        if (n <= 1) return false
        if (n <= 3) return true
        if (n % 2 == 0 || n % 3 == 0) return false
        var i = 5
        while (i * i <= n) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false
            }
            i += 6
        }
        return true
    }

    /**
     * Doubles the size of the hash table and re-inserts all elements.
     */
    private fun rehash() {
        val oldPairs = keyValuePairs()
        capacity = findNextPrime(capacity * 2)
        buckets = Array(capacity) { mutableListOf() }
        numElements = 0 // Reset size, as 'set' will increment it

        for ((key, value) in oldPairs) {
            this[key] = value // Use the 'set' operator to re-insert
        }
    }

    /**
     * Inserts or updates a key-value pair.
     * Triggers a rehash if the load factor is exceeded *before* inserting.
     */
    override operator fun set(k: K, v: V) {
        // Check for rehashing before adding the new element
        if ((numElements + 1).toDouble() / capacity > loadFactorThreshold) {
            rehash()
        }

        val index = getBucketIndex(k)
        val chain = buckets[index]

        // Check if key already exists in the chain
        val existingEntry = chain.find { it.key == k }

        if (existingEntry != null) {
            // Key found, update the value
            existingEntry.value = v
        } else {
            // Key not found, add a new entry to the chain
            chain.add(Entry(k, v))
            numElements++
        }
    }

    /**
     * Checks if the key is present in the map.
     */
    override operator fun contains(k: K): Boolean {
        return get(k) != null
    }

    /**
     * Retrieves the value associated with the key, or null if not found.
     */
    override operator fun get(k: K): V? {
        val index = getBucketIndex(k)
        val chain = buckets[index]
        return chain.find { it.key == k }?.value
    }

    /**
     * Removes a key-value pair from the map.
     * @return true if successful, false if the key was not found.
     */
    override fun remove(k: K): Boolean {
        val index = getBucketIndex(k)
        val chain = buckets[index]
        val entry = chain.find { it.key == k }

        return if (entry != null) {
            val removed = chain.remove(entry)
            if (removed) {
                numElements--
            }
            removed
        } else {
            false
        }
    }

    /**
     * Returns the total number of key-value pairs stored.
     */
    override fun size(): Int {
        return numElements
    }

    /**
     * Returns a list of all key-value pairs.
     */
    override fun keyValuePairs(): List<Pair<K, V>> {
        val pairs = mutableListOf<Pair<K, V>>()
        for (chain in buckets) {
            for (entry in chain) {
                pairs.add(Pair(entry.key, entry.value))
            }
        }
        return pairs
    }
}