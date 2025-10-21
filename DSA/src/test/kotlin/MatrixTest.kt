import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MatrixTest {

    private val DELTA = 1e-9 // A small tolerance for comparing double values

    @Test
    fun `test conventional multiplication with a simple 2x2 matrix`() {
        val a = Matrix(2)
        a[0, 0] = 1.0; a[0, 1] = 2.0
        a[1, 0] = 3.0; a[1, 1] = 4.0

        val b = Matrix(2)
        b[0, 0] = 5.0; b[0, 1] = 6.0
        b[1, 0] = 7.0; b[1, 1] = 8.0

        // Expected result of A * B
        val expected = Matrix(2)
        expected[0, 0] = 19.0; expected[0, 1] = 22.0
        expected[1, 0] = 43.0; expected[1, 1] = 50.0

        val result = a.multiply(b)!!

        for (i in 0 until 2) {
            assertArrayEquals(expected.data[i], result.data[i], DELTA)
        }
    }

    @Test
    fun `test Strassen's multiplication with a simple 2x2 matrix`() {
        val a = Matrix(2)
        a[0, 0] = 1.0; a[0, 1] = 2.0
        a[1, 0] = 3.0; a[1, 1] = 4.0

        val b = Matrix(2)
        b[0, 0] = 5.0; b[0, 1] = 6.0
        b[1, 0] = 7.0; b[1, 1] = 8.0

        // Expected result of A * B
        val expected = Matrix(2)
        expected[0, 0] = 19.0; expected[0, 1] = 22.0
        expected[1, 0] = 43.0; expected[1, 1] = 50.0

        val result = a.strassenMultiply(b)!!

        for (i in 0 until 2) {
            assertArrayEquals(expected.data[i], result.data[i], DELTA)
        }
    }

    @Test
    fun `conventional and Strassen's multiplication should produce identical results`() {
        val size = 8 // Must be a power of 2 for this simple test
        val a = Matrix.random(size)
        val b = Matrix.random(size)

        val conventionalResult = a.multiply(b)!!
        val strassenResult = a.strassenMultiply(b)!!

        assertEquals(conventionalResult.size, strassenResult.size)

        for (i in 0 until size) {
            assertArrayEquals(conventionalResult.data[i], strassenResult.data[i], DELTA)
        }
    }

    @Test
    fun `multiplication should return null for incompatible matrix sizes`() {
        val a = Matrix(2)
        val b = Matrix(3)

        val conventionalResult = a.multiply(b)
        val strassenResult = a.strassenMultiply(b)

        assertEquals(null, conventionalResult)
        assertEquals(null, strassenResult)
    }
}