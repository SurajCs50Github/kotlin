import java.io.File
import java.util.PriorityQueue
import java.util.Scanner

// Basic node structure for the huffman tree
// Leaf nodes have a symbol, internal nodes have left/right children
data class Node(
    val symbol: Char? = null,
    val frequency: Int,
    val left: Node? = null,
    val right: Node? = null
) : Comparable<Node> {
    override fun compareTo(other: Node): Int = frequency.compareTo(other.frequency)
    fun isLeaf(): Boolean = left == null && right == null
}

class HuffmanCompressor {

    // count how many times each character appears
    fun analyzeFrequency(text: String): Map<Char, Int> {
        val freqMap = mutableMapOf<Char, Int>()
        for (char in text) {
            freqMap[char] = freqMap.getOrDefault(char, 0) + 1
        }

        // sanity check - frequencies should add up to text length
        val totalFreq = freqMap.values.sum()
        require(totalFreq == text.length) {
            "frequency sum mismatch: $totalFreq vs ${text.length}"
        }
        println("freq analysis done: ${freqMap.size} unique, $totalFreq total")
        return freqMap
    }

    // build the huffman tree using a min-heap
    // keep merging the two smallest frequency nodes until we have one tree
    fun buildHuffmanTree(frequencies: Map<Char, Int>): Node {
        require(frequencies.isNotEmpty()) { "can't build tree from nothing" }

        val pq = PriorityQueue<Node>()
        // start with all symbols as leaf nodes
        frequencies.forEach { (char, freq) ->
            pq.offer(Node(symbol = char, frequency = freq))
        }

        // merge until we have a single root
        while (pq.size > 1) {
            val left = pq.poll()
            val right = pq.poll()
            val merged = Node(
                frequency = left.frequency + right.frequency,
                left = left,
                right = right
            )
            pq.offer(merged)
        }

        val root = pq.poll()
        println("tree built, root freq: ${root.frequency}")
        return root
    }

    // traverse tree to generate binary codes
    // left = 0, right = 1
    fun generateCodes(root: Node): Map<Char, String> {
        val codeTable = mutableMapOf<Char, String>()

        // recursive DFS to build codes
        fun traverse(node: Node, code: String) {
            if (node.isLeaf()) {
                // edge case: single symbol gets code "0"
                codeTable[node.symbol!!] = if (code.isEmpty()) "0" else code
            } else {
                node.left?.let { traverse(it, code + "0") }
                node.right?.let { traverse(it, code + "1") }
            }
        }

        traverse(root, "")

        // verify no code is a prefix of another (should be guaranteed by tree structure)
        val codes = codeTable.values.toList()
        for (i in codes.indices) {
            for (j in codes.indices) {
                if (i != j && codes[j].startsWith(codes[i])) {
                    error("prefix collision: ${codes[i]} and ${codes[j]}")
                }
            }
        }

        println("generated ${codeTable.size} codes")
        return codeTable
    }

    // encode text using the code table and write to file
    fun encode(text: String, codeTable: Map<Char, String>, outputFile: File) {
        // convert text to bit string
        val bitString = StringBuilder()
        for (char in text) {
            bitString.append(codeTable[char] ?: error("no code for '$char'"))
        }

        // build header with code table
        val header = StringBuilder()
        header.append("${codeTable.size}\n")
        codeTable.forEach { (char, code) ->
            // store char as integer to handle special chars safely
            header.append("${char.code}:$code\n")
        }
        header.append("END_HEADER\n")
        header.append("${bitString.length}\n")  // store actual bit length to trim padding later

        // pack bits into bytes (pad last byte with 0s if needed)
        val bytes = mutableListOf<Byte>()
        for (i in bitString.indices step 8) {
            val chunk = bitString.substring(i, minOf(i + 8, bitString.length))
                .padEnd(8, '0')
            bytes.add(chunk.toInt(2).toByte())
        }

        // write header as text, then binary payload
        outputFile.writeText(header.toString(), Charsets.UTF_8)
        outputFile.appendBytes(bytes.toByteArray())

        val ratio = (bytes.size.toDouble() / text.length) * 100.0
        println("encoded ${text.length} chars -> ${bytes.size} bytes (%.2f%%)".format(ratio))
    }

    // decode a compressed file back to original text
    fun decode(inputFile: File): String {
        val bytes = inputFile.readBytes()
        var i = 0

        // helper to read lines from byte array (avoids text/binary mixing issues)
        fun readLine(): String {
            if (i >= bytes.size) error("unexpected end of file")
            val start = i
            while (i < bytes.size && bytes[i] != '\n'.code.toByte()) i++
            val s = String(bytes, start, i - start, Charsets.UTF_8)
            i++  // skip newline
            return s
        }

        // parse header
        val tableSize = readLine().toInt()
        val codeTable = mutableMapOf<String, Char>()
        repeat(tableSize) {
            val line = readLine()
            val parts = line.split(":")
            codeTable[parts[1]] = parts[0].toInt().toChar()
        }
        require(readLine() == "END_HEADER") { "bad header" }
        val bitLength = readLine().toInt()

        if (bitLength == 0) return ""  // empty file case

        // rest of file is compressed payload
        val payload = bytes.copyOfRange(i, bytes.size)

        // convert bytes back to bit string
        val bitString = buildString(payload.size * 8) {
            for (b in payload) {
                append((b.toInt() and 0xFF).toString(2).padStart(8, '0'))
            }
        }.substring(0, bitLength)  // trim to actual bit length (removes padding)

        // decode by matching prefixes in the bit string
        val decoded = StringBuilder()
        var cur = ""
        for (bit in bitString) {
            cur += bit
            codeTable[cur]?.let { ch ->
                decoded.append(ch)
                cur = ""  // reset for next code
            }
        }
        require(cur.isEmpty()) { "incomplete code: $cur" }

        println("decoded ${payload.size} bytes -> ${decoded.length} chars")
        return decoded.toString()
    }

    // full compression pipeline
    fun compress(inputFile: File, outputFile: File) {
        println("\n--- compression ---")
        val text = inputFile.readText(Charsets.UTF_8)

        // handle empty file edge case
        if (text.isEmpty()) {
            outputFile.writeText("0\nEND_HEADER\n0\n", Charsets.UTF_8)
            println("empty file")
            println("--- done ---\n")
            return
        }

        val frequencies = analyzeFrequency(text)
        val tree = buildHuffmanTree(frequencies)
        val codes = generateCodes(tree)
        encode(text, codes, outputFile)
        println("--- done ---\n")
    }

    // full decompression pipeline
    fun decompress(inputFile: File): String {
        println("\n--- decompression ---")
        val result = decode(inputFile)
        println("--- done ---\n")
        return result
    }
}

// visualization tool for understanding the huffman tree
class HuffmanVisualizer(private val compressor: HuffmanCompressor) {

    fun visualizeFromText(text: String) {
        if (text.isEmpty()) {
            println("can't visualize empty text")
            return
        }

        println("\n" + "=".repeat(60))
        println("HUFFMAN TREE VISUALIZATION")
        println("=".repeat(60))

        // show frequency distribution
        val frequencies = compressor.analyzeFrequency(text)
        println("\nFrequency Table:")
        frequencies.entries.sortedByDescending { it.value }.forEach { (char, freq) ->
            val display = when (char) {
                ' ' -> "SPACE"
                '\n' -> "NEWLINE"
                '\t' -> "TAB"
                else -> "'$char'"
            }
            val bar = "█".repeat((freq * 20) / text.length + 1)
            println("  $display -> $freq times $bar")
        }

        val tree = compressor.buildHuffmanTree(frequencies)

        // show generated codes
        val codes = compressor.generateCodes(tree)
        println("\nHuffman Codes:")
        codes.entries.sortedBy { it.value.length }.forEach { (char, code) ->
            val display = when (char) {
                ' ' -> "SPACE"
                '\n' -> "NEWLINE"
                '\t' -> "TAB"
                else -> "'$char'"
            }
            println("  $display -> $code (${code.length} bits)")
        }

        // draw tree structure
        println("\nTree Structure:")
        printTree(tree, "", true)

        // calculate compression stats
        val originalBits = text.length * 8
        val compressedBits = text.sumOf { codes[it]?.length ?: 0 }
        val ratio = (compressedBits.toDouble() / originalBits) * 100

        println("\nCompression Stats:")
        println("  original: $originalBits bits (${text.length} chars x 8)")
        println("  compressed: $compressedBits bits")
        println("  ratio: ${"%.2f".format(ratio)}%")
        println("  saved: ${"%.2f".format(100 - ratio)}%")
        println("=".repeat(60) + "\n")
    }

    // recursive tree printer using box-drawing characters
    private fun printTree(node: Node?, prefix: String = "", isTail: Boolean = true) {
        if (node == null) return
        val connector = if (isTail) "└── " else "├── "
        val extension = if (isTail) "    " else "│   "

        if (node.isLeaf()) {
            val display = when (node.symbol) {
                ' ' -> "SPACE"
                '\n' -> "NEWLINE"
                '\t' -> "TAB"
                else -> "'${node.symbol}'"
            }
            println("$prefix$connector$display (${node.frequency})")
        } else {
            println("$prefix$connector[${node.frequency}]")
            node.left?.let { printTree(it, prefix + extension, false) }
            node.right?.let { printTree(it, prefix + extension, true) }
        }
    }

    // interactive mode for visualizing arbitrary text
    fun interactiveMode() {
        val scanner = Scanner(System.`in`)
        println("\n" + "=".repeat(60))
        println("INTERACTIVE MODE")
        println("=".repeat(60))
        println("type text to visualize, 'quit' to exit, 'file:path' to load")
        println("=".repeat(60))

        while (true) {
            print("\nenter text: ")
            val input = scanner.nextLine()
            when {
                input.equals("quit", ignoreCase = true) -> {
                    println("bye")
                    break
                }
                input.startsWith("file:", ignoreCase = true) -> {
                    val path = input.substring(5).trim()
                    val file = File(path)
                    if (file.exists()) {
                        val text = file.readText(Charsets.UTF_8)
                        visualizeFromText(text)
                    } else {
                        println("file not found: $path")
                    }
                }
                input.isEmpty() -> println("need some text")
                else -> visualizeFromText(input)
            }
        }
    }
}

// test edge case: file with only one unique character
fun testSingleChar() {
    println("\ntesting single char case")
    val compressor = HuffmanCompressor()
    val file = File("single_char.txt")
    val compressed = File("single_char.huff")

    file.writeText("aaaaaaaaaa", Charsets.UTF_8)
    compressor.compress(file, compressed)
    val result = compressor.decompress(compressed)

    println(if (result == "aaaaaaaaaa") "pass" else "FAIL")
}

// main interactive menu
fun interactiveMain() {
    val compressor = HuffmanCompressor()
    val visualizer = HuffmanVisualizer(compressor)
    val scanner = Scanner(System.`in`)

    println("\n" + "-".repeat(50))
    println("HUFFMAN CODING TOOLKIT")
    println("-".repeat(50))

    while (true) {
        println("\n1. visualize tree")
        println("2. compress file")
        println("3. decompress file")
        println("4. run tests")
        println("5. quit")
        print("\nselect (1-5): ")

        when (scanner.nextLine().trim()) {
            "1" -> visualizer.interactiveMode()
            "2" -> {
                print("input file: ")
                val input = scanner.nextLine().trim()
                print("output file: ")
                val output = scanner.nextLine().trim()
                compressor.compress(File(input), File(output))
            }
            "3" -> {
                print("compressed file: ")
                val input = scanner.nextLine().trim()
                val result = compressor.decompress(File(input))
                print("save to (empty for console): ")
                val output = scanner.nextLine().trim()
                if (output.isEmpty()) {
                    println("\n--- output ---")
                    println(result)
                } else {
                    File(output).writeText(result, Charsets.UTF_8)
                    println("saved to $output")
                }
            }
            "4" -> {
                testSingleChar()
                HuffmanVisualizer(compressor).visualizeFromText("hello world")
            }
            "5" -> {
                println("done")
                break
            }
            else -> println("invalid option")
        }
    }
}

fun main() {
    interactiveMain()
}