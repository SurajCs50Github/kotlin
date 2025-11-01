package org.example

/**
 * Provides an implementation of the Lempel-Ziv-Welch (LZW) encoding algorithm.
 */
object LempelZiv {

    /**
     * Encodes a String into a list of integer codes using the LZW algorithm.
     *
     * @param input The raw string to compress.
     * @return A list of integer codes representing the compressed data.
     */
    fun encode(input: String): List<Int> {
        if (input.isEmpty()) {
            return emptyList()
        }

        // 1. Initialize the dictionary (our HashMap) with single characters (ASCII 0-255)
        val dictionary = HashMap<String, Int>()
        for (i in 0..255) {
            dictionary[i.toChar().toString()] = i
        }
        var nextCode = 256 // The next code to assign to a new string

        var w = "" // Current working string
        val compressedOutput = mutableListOf<Int>()

        // 2. Read input stream character by character
        for (c in input) {
            val wc = w + c // New string (working string + new character)

            // 3. Check if wc is in the dictionary
            if (wc in dictionary) {
                // If YES: wc is now the new working string
                w = wc
            } else {
                // If NO:
                // a. Output the code for w
                compressedOutput.add(dictionary[w]!!)

                // b. Add wc to the dictionary with a new code
                dictionary[wc] = nextCode
                nextCode++

                // c. w is reset to the new character c
                w = c.toString()
            }
        }

        // 4. After the loop, output the code for the last working string w
        if (w.isNotEmpty()) {
            compressedOutput.add(dictionary[w]!!)
        }

        return compressedOutput
    }
}