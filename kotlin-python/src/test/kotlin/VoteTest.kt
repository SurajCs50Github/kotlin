// File: src/test/kotlin/com/example/voting/VoteTest.kt
package com.example.voting

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.random.Random

/**
 * Test suite for the holdAlternativeVote election logic.
 * This class covers simple majorities, ties, and multi-round vote transfers.
 */
class VoteTest {

    /**
     * Helper function to generate vote data for two primary candidates, Alice and Bob.
     * This simplifies the creation of test cases.
     */
    private fun aliceBobVotes(alice: Int = 0, bob: Int = 0, aliceBob: Int = 0, bobAlice: Int = 0): List<List<String>> {
        val votes = mutableListOf<List<String>>()
        repeat(alice) { votes.add(listOf("Alice")) }
        repeat(bob) { votes.add(listOf("Bob")) }
        repeat(aliceBob) { votes.add(listOf("Alice", "Bob")) }
        repeat(bobAlice) { votes.add(listOf("Bob", "Alice")) }
        return votes
    }

    @Test
    fun `test simple majority winner in first round`() {
        val candidates = listOf("Alice", "Bob")
        val votes = aliceBobVotes(alice = 51, bob = 49)
        val expected = Pair("Alice", 51)
        assertEquals(expected, holdAlternativeVote(candidates, votes))
    }

    @Test
    fun `test tie results in one candidate winning after elimination`() {
        val candidates = listOf("Alice", "Bob")
        // In a two-person tie, one is eliminated, and the other wins by default.
        // The total number of votes is 200.
        val votes = aliceBobVotes(alice = 100, bob = 100)
        val (winner, finalVotes) = holdAlternativeVote(candidates, votes)

        assertTrue(winner in candidates, "Winner should be one of the candidates")
        assertEquals(100, finalVotes, "Final vote count for the winner should be 100")
    }

    @Test
    fun `test single vote transfer after elimination`() {
        val candidates = listOf("Alice", "Bob", "Charlie")
        val votes = mutableListOf<List<String>>().apply {
            // Round 1: Bob: 40, Alice: 35, Charlie: 25 -> Charlie is eliminated
            repeat(40) { add(listOf("Bob", "Alice")) }
            repeat(35) { add(listOf("Alice", "Bob")) }
            repeat(25) { add(listOf("Charlie", "Alice")) } // Charlie's votes transfer to Alice
        }
        // Round 2: Bob: 40, Alice: 35 + 25 = 60 -> Alice wins
        val expected = Pair("Alice", 60)
        assertEquals(expected, holdAlternativeVote(candidates, votes.shuffled(Random(42))))
    }

    @Test
    fun `test double vote transfer across two rounds`() {
        val candidates = listOf("Memphis", "Nashville", "Chattanooga", "Knoxville")
        val votes = mutableListOf<List<String>>().apply {
            // Round 1: Memphis: 42, Nashville: 26, Knoxville: 17, Chattanooga: 15
            // --> Chattanooga (15) is eliminated. Her votes transfer to Knoxville.
            repeat(42) { add(listOf("Memphis", "Nashville")) }
            repeat(26) { add(listOf("Nashville", "Knoxville")) }
            repeat(17) { add(listOf("Knoxville", "Nashville")) }
            repeat(15) { add(listOf("Chattanooga", "Knoxville")) }
        }
        // Round 2: Memphis: 42, Nashville: 26, Knoxville: 17 + 15 = 32
        // --> Nashville (26) is eliminated. Her votes transfer to Knoxville.
        // Round 3: Memphis: 42, Knoxville: 32 + 26 = 58
        // --> Knoxville wins.
        val expected = Pair("Knoxville", 58)
        assertEquals(expected, holdAlternativeVote(candidates, votes))
    }
}