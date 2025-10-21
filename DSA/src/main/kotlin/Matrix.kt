import kotlin.system.measureTimeMillis
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.pow

/**
 * A class to represent a square matrix and perform matrix operations.
 *
 * @property size The dimension of the square matrix (n x n).
 * @property data The 2D array storing the matrix values.
 */
class Matrix(val size: Int, val data: Array<DoubleArray>) {

    /**
     * Secondary constructor to create a matrix of a given size initialized to zeros.
     */
    constructor(size: Int) : this(size, Array(size) { DoubleArray(size) })

    /**
     * Allows accessing matrix elements using bracket notation, e.g., matrix[r, c].
     */
    operator fun get(row: Int, col: Int): Double {
        return data[row][col]
    }

    /**
     * Allows setting matrix elements using bracket notation, e.g., matrix[r, c] = value.
     */
    operator fun set(row: Int, col: Int, value: Double) {
        data[row][col] = value
    }

    /**
     * Overloads the '+' operator for matrix addition.
     */
    operator fun plus(other: Matrix): Matrix {
        if (size != other.size) throw IllegalArgumentException("Matrix dimensions must be the same for addition.")
        val result = Matrix(size)
        for (i in 0 until size) {
            for (j in 0 until size) {
                result[i, j] = this[i, j] + other[i, j]
            }
        }
        return result
    }

    /**
     * Overloads the '-' operator for matrix subtraction.
     */
    operator fun minus(other: Matrix): Matrix {
        if (size != other.size) throw IllegalArgumentException("Matrix dimensions must be the same for subtraction.")
        val result = Matrix(size)
        for (i in 0 until size) {
            for (j in 0 until size) {
                result[i, j] = this[i, j] - other[i, j]
            }
        }
        return result
    }

    /**
     * Conventional matrix multiplication with O(n^3) complexity.
     * @return The resulting matrix product, or null if dimensions are incompatible.
     */
    fun multiply(other: Matrix): Matrix? {
        if (this.size != other.size) return null
        val result = Matrix(this.size)
        for (i in 0 until this.size) {
            for (j in 0 until other.size) {
                var sum = 0.0
                for (k in 0 until this.size) {
                    sum += this[i, k] * other[k, j]
                }
                result[i, j] = sum
            }
        }
        return result
    }

    /**
     * Strassen's algorithm for matrix multiplication.
     * Includes a hybrid approach, switching to conventional multiplication for smaller matrices.
     * @param other The matrix to multiply with.
     * @return The resulting matrix product, or null if dimensions are incompatible.
     */
    fun strassenMultiply(other: Matrix): Matrix? {
        if (this.size != other.size) return null

        // Pad matrices to the next power of 2
        val n = this.size
        val m = nextPowerOf2(n)
        val aPadded = this.pad(m)
        val bPadded = other.pad(m)

        val cPadded = strassenRecursive(aPadded, bPadded)

        // Unpad the result to the original size
        return cPadded.unpad(n)
    }

    private fun strassenRecursive(a: Matrix, b: Matrix): Matrix {
        val n = a.size

        // Crossover point for hybrid algorithm. For matrices smaller than this,
        // use standard multiplication. This value is determined by benchmarking.
        val CROSSOVER_POINT = 128
        if (n <= CROSSOVER_POINT) {
            return a.multiply(b)!!
        }

        val half = n / 2
        val (a11, a12, a21, a22) = a.split()
        val (b11, b12, b21, b22) = b.split()

        val p1 = strassenRecursive(a11, b12 - b22)
        val p2 = strassenRecursive(a11 + a12, b22)
        val p3 = strassenRecursive(a21 + a22, b11)
        val p4 = strassenRecursive(a22, b21 - b11)
        val p5 = strassenRecursive(a11 + a22, b11 + b22)
        val p6 = strassenRecursive(a12 - a22, b21 + b22)
        val p7 = strassenRecursive(a11 - a21, b11 + b12)

        val c11 = p5 + p4 - p2 + p6
        val c12 = p1 + p2
        val c21 = p3 + p4
        val c22 = p5 + p1 - p3 - p7

        return combine(c11, c12, c21, c22)
    }

    /**
     * Splits the current matrix into four n/2 x n/2 sub-matrices.
     */
    private fun split(): List<Matrix> {
        val half = size / 2
        val a11 = Matrix(half)
        val a12 = Matrix(half)
        val a21 = Matrix(half)
        val a22 = Matrix(half)

        for (i in 0 until half) {
            for (j in 0 until half) {
                a11[i, j] = this[i, j]
                a12[i, j] = this[i, j + half]
                a21[i, j] = this[i + half, j]
                a22[i, j] = this[i + half, j + half]
            }
        }
        return listOf(a11, a12, a21, a22)
    }

    /**
     * Helper to pad matrix to a larger size, filling with zeros.
     */
    private fun pad(newSize: Int): Matrix {
        if (newSize < size) throw IllegalArgumentException("New size must be >= original size.")
        val newMatrix = Matrix(newSize)
        for (i in 0 until size) {
            for (j in 0 until size) {
                newMatrix[i, j] = this[i, j]
            }
        }
        return newMatrix
    }

    /**
     * Helper to unpad matrix to a smaller original size.
     */
    private fun unpad(originalSize: Int): Matrix {
        if (originalSize > size) throw IllegalArgumentException("Original size must be <= current size.")
        val newMatrix = Matrix(originalSize)
        for (i in 0 until originalSize) {
            for (j in 0 until originalSize) {
                newMatrix[i, j] = this[i, j]
            }
        }
        return newMatrix
    }

    companion object {
        /**
         * Combines four n/2 x n/2 matrices into a single n x n matrix.
         */
        fun combine(c11: Matrix, c12: Matrix, c21: Matrix, c22: Matrix): Matrix {
            val half = c11.size
            val n = half * 2
            val result = Matrix(n)
            for (i in 0 until half) {
                for (j in 0 until half) {
                    result[i, j] = c11[i, j]
                    result[i, j + half] = c12[i, j]
                    result[i + half, j] = c21[i, j]
                    result[i + half, j + half] = c22[i, j]
                }
            }
            return result
        }

        /**
         * Creates a square matrix of a given size with random values.
         */
        fun random(size: Int): Matrix {
            val matrix = Matrix(size)
            for (i in 0 until size) {
                for (j in 0 until size) {
                    matrix[i, j] = Math.random() * 10
                }
            }
            return matrix
        }

        /**
         * Finds the next power of 2 for a given integer.
         */
        fun nextPowerOf2(n: Int): Int {
            return 2.0.pow(ceil(log2(n.toDouble()))).toInt()
        }
    }
}

/**
 * Main function to run benchmarks.
 */
fun main() {
    println("### Matrix Multiplication Benchmarks ###")
    println("----------------------------------------")
    println("| Matrix Size | Conventional Time (ms) | Strassen's Time (ms) |")
    println("|-------------|------------------------|----------------------|")

    listOf(64, 128, 256, 512, 1024, 2048).forEach { size ->
        val a = Matrix.random(size)
        val b = Matrix.random(size)

        val conventionalTime = measureTimeMillis {
            a.multiply(b)
        }

        val strassenTime = measureTimeMillis {
            a.strassenMultiply(b)
        }

        println("| %-11s | %-22s | %-20s |".format(size, conventionalTime, strassenTime))
    }
}