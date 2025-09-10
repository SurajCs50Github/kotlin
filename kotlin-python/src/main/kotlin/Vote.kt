// File: src/main/kotlin/com/example/voting/Vote.kt
package com.example.voting

/**
this function is for the most votes, and the candiate is tallied
 *
 * @param candidates The list of candidates still in the election.
 * @param rankedVotes A list where each inner list represents a voter's ranked preferences.
 * @return An immutable map of candidates to their current vote counts.
 */
fun tallyVotes(candidates: List<String>, rankedVotes: List<List<String>>): Map<String, Int> {
    val voteCounts = mutableMapOf<String, Int>()
    // Initialize all active candidates with zero votes.
    for (candidate in candidates) {
        voteCounts[candidate] = 0
    }

    // Tally the top choice for each vote.
    for (vote in rankedVotes) {
        for (candidate in vote) {
            // Find the first candidate in the voter's preference list who is still in the race.
            if (candidate in candidates) {
                voteCounts[candidate] = voteCounts.getOrDefault(candidate, 0) + 1
                break // Move to the next vote once the top choice is found.
            }
        }
    }
    return voteCounts
}

/**
least votes
 *
 * @param voteCounts A map of candidates to their vote counts.
 * @return The name of the candidate with the minimum number of votes.
 */
fun getMinimumCandidate(voteCounts: Map<String, Int>): String {
    // Use minByOrNull for a concise and safe way to find the entry with the minimum value.
    // The `!!` is safe here because the election logic ensures voteCounts is never empty.
    return voteCounts.minByOrNull { it.value }!!.key
}

/**
if somone has over 50% they win or the last one is kicked out
 *
 * @param candidates The initial list of all candidates in the election.
 * @param rankedVotes A list of ranked-choice ballots.
 * @return A Pair containing the winner's name and their final vote count.
 */
fun holdAlternativeVote(candidates: List<String>, rankedVotes: List<List<String>>): Pair<String, Int> {
    var remainingCandidates = candidates.toList() // Use a mutable copy

    while (remainingCandidates.size > 1) {
        val voteCounts = tallyVotes(remainingCandidates, rankedVotes)
        val totalVotes = voteCounts.values.sum()

        // Check for a majority winner
        val majorityWinner = voteCounts.entries.find { it.value > totalVotes / 2 }
        if (majorityWinner != null) {
            return Pair(majorityWinner.key, majorityWinner.value)
        }

        // If no majority winner, eliminate the candidate with the fewest votes
        val candidateToEliminate = getMinimumCandidate(voteCounts)
        remainingCandidates = remainingCandidates.filter { it != candidateToEliminate }
    }

    // If only one candidate remains, they are the winner
    val winner = remainingCandidates.first()
    val finalTally = tallyVotes(remainingCandidates, rankedVotes)
    return Pair(winner, finalTally.getOrDefault(winner, 0))
}