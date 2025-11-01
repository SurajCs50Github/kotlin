package org.example

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class LempelZivTest {

    @Test
    fun `test encode empty string`() {
        val result = LempelZiv.encode("")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `test encode single character`() {
        val result = LempelZiv.encode("A")
        assertEquals(listOf(65), result) // ASCII 'A'
    }

    @Test
    fun `test encode repeated characters`() {
        // "AAAAA"
        // w="", c=A -> w=A
        // w=A, c=A -> wc=AA. Not in dict. out: 65 (A). dict[256]=AA. w=A
        // w=A, c=A -> wc=AA. In dict. w=AA
        // w=AA, c=A -> wc=AAA. Not in dict. out: 256 (AA). dict[257]=AAA. w=A
        // w=A, c=A -> wc=AA. In dict. w=AA
        // End. out: 256 (AA)
        val result = LempelZiv.encode("AAAAA")
        assertEquals(listOf(65, 256, 256), result)
    }

    @Test
    fun `test encode simple string`() {
        // "BABA"
        // w="", c=B -> w=B
        // w=B, c=A -> wc=BA. Not in dict. out: 66 (B). dict[256]=BA. w=A
        // w=A, c=B -> wc=AB. Not in dict. out: 65 (A). dict[257]=AB. w=B
        // w=B, c=A -> wc=BA. In dict. w=BA
        // End. out: 256 (BA)
        val result = LempelZiv.encode("BABA")
        assertEquals(listOf(66, 65, 256), result)
    }

    @Test
    fun `test encode classic LZW example`() {
        // This test uses the well-known example "TOBEORNOTTOBEORTOBEORNOT"
        val input = "TOBEORNOTTOBEORTOBEORNOT"

        // The expected sequence is derived from the correct algorithm trace:
        // 1. T, O, B, E, O, R, N, O, T (9 chars, 9 codes)
        // 2. dict[256]=TO, dict[257]=OB, dict[258]=BE, ... dict[263]=OT, dict[264]=TT
        // 3. TO (in dict) -> out: TO(256), dict[265]=TOB
        // 4. BE (in dict) -> out: BE(258), dict[266]=BEO
        // 5. OR (in dict) -> out: OR(260), dict[267]=ORT
        // 6. TOB (in dict) -> out: TOB(265), dict[268]=TOBE
        // 7. EO (in dict) -> out: EO(259), dict[269]=EOR
        // 8. RN (in dict) -> out: RN(261), dict[270]=RNO
        // 9. OT (in dict) -> out: OT(263) (end of string)
        val expected = listOf(
            84, 79, 66, 69, 79, 82, 78, 79, 84, // "TOBEORNOT"
            256, // "TO"
            258, // "BE"
            260, // "OR"
            265, // "TOB"
            259, // "EO"
            261, // "RN"
            263  // "OT"
        )

        val result = LempelZiv.encode(input)
        assertEquals(expected, result)
    }
}