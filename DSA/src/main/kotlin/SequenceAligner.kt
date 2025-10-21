import kotlin.math.max

/**
 * Performs local sequence alignment using the Smith-Waterman algorithm.
 *
 * @param seq1 The first sequence.
 * @param seq2 The second sequence.
 * @param matchScore The score for a character match.
 * @param mismatchPenalty The penalty for a character mismatch (a negative value).
 * @param gapPenalty The penalty for a gap (a negative value).
 */
fun smithWaterman(
    seq1: String,
    seq2: String,
    matchScore: Int = 2,
    mismatchPenalty: Int = -1,
    gapPenalty: Int = -1
) {
    val n = seq1.length
    val m = seq2.length
    val scoreMatrix = Array(n + 1) { IntArray(m + 1) }
    var maxScore = 0
    var maxI = 0
    var maxJ = 0

    // 1. Fill the scoring matrix
    for (i in 1..n) {
        for (j in 1..m) {
            val match = scoreMatrix[i - 1][j - 1] + if (seq1[i - 1] == seq2[j - 1]) matchScore else mismatchPenalty
            val delete = scoreMatrix[i - 1][j] + gapPenalty
            val insert = scoreMatrix[i][j - 1] + gapPenalty
            scoreMatrix[i][j] = maxOf(0, match, delete, insert)

            if (scoreMatrix[i][j] > maxScore) {
                maxScore = scoreMatrix[i][j]
                maxI = i
                maxJ = j
            }
        }
    }

    // 2. Traceback to find the alignment
    var align1 = ""
    var align2 = ""
    var i = maxI
    var j = maxJ

    while (i > 0 && j > 0 && scoreMatrix[i][j] != 0) {
        val currentScore = scoreMatrix[i][j]
        val diagScore = scoreMatrix[i - 1][j - 1]
        val upScore = scoreMatrix[i - 1][j]
        val leftScore = scoreMatrix[i][j - 1]

        val matchValue = if (seq1[i - 1] == seq2[j - 1]) matchScore else mismatchPenalty

        when (currentScore) {
            diagScore + matchValue -> {
                align1 = seq1[i - 1] + align1
                align2 = seq2[j - 1] + align2
                i--
                j--
            }
            upScore + gapPenalty -> {
                align1 = seq1[i - 1] + align1
                align2 = "-" + align2
                i--
            }
            else -> { // leftScore + gapPenalty
                align1 = "-" + align1
                align2 = seq2[j - 1] + align2
                j--
            }
        }
    }

    // 3. Print the results
    println("### Smith-Waterman Local Alignment ###")
    println("Best Alignment Score: $maxScore")
    println("\nAlignment:")
    println("Sequence 1: $align1")
    println("Sequence 2: $align2")
}


fun main() {
    // Provided DNA sequences from the assignment
    val genomeSnippet = "ACGTACGATCGAGCATCGATCGATCAGCTACGATGC"
    val targetGenome = "GATTACAGATTACAGATTACAGATTACAACGTACGATCGAGCATCGATCGATCAGCTACGATGCAGATTACAGATTACAGATTACAGATTACA"
    val testAgainst = "AGTACTGATCGAGCATCGATCGATCAGCTACGATGC"

    // Run Smith-Waterman to find how `testAgainst` aligns within `targetGenome`
    println("--- Aligning 'testAgainst' with 'targetGenome' ---")
    smithWaterman(testAgainst, targetGenome)

    println("\n\n--- Aligning 'genomeSnippet' with 'testAgainst' ---")
    smithWaterman(genomeSnippet, testAgainst)
}