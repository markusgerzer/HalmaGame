package halma

import kotlin.test.Test
import kotlin.test.assertEquals

class Board_validMoveOfOrNull_Test {
    private val board = makeBoard(1, ::StarhalmaBoard)

    @Test
    fun simpleTest() {
        assertEquals(Move.Walk(15, 28), board.validMoveOfOrNull(listOf(15, 28)))
        assertEquals(Move.Jump(6, listOf(28)), board.validMoveOfOrNull(listOf(6, 28)))
        assertEquals(null, board.validMoveOfOrNull(listOf(0, 1)))
        assertEquals(null, board.validMoveOfOrNull(listOf(17, 95)))
    }
}