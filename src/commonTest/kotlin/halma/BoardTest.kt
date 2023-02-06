package halma

import com.soywiz.korio.async.*
import kotlin.test.*

class Board_validMoveOfOrNull_Test {
    private val board = makeBoard(1, ::StarhalmaBoard)

    @Test
    fun simpleTest() {
        assertEquals(Move.Walk(15, 28), board.validMoveOfOrNull(listOf(15, 28)))
        assertEquals(Move.Jump(6, listOf(28)), board.validMoveOfOrNull(listOf(6, 28)))
        assertEquals(null, board.validMoveOfOrNull(listOf(0, 1)))
        assertEquals(null, board.validMoveOfOrNull(listOf(17, 95)))
    }

    @Test
    fun test2() = suspendTest {
        var move: Move = Move.Jump(9, listOf(29))
        assertEquals(move, board.validMoveOfOrNull(listOf(9, 29)))
        board.move(move)
        move = Move.Walk(29, 41)
        assertEquals(move, board.validMoveOfOrNull(listOf(29, 41)))
        board.move(move)
        move = Move.Jump(2, listOf(9, 29, 52))
        assertEquals(move, board.validMoveOfOrNull(listOf(2, 52)))
    }
}

class Board_doMove_Test {
    @Test
    fun test1() {
        val board1 = makeBoard(1, ::StarhalmaBoard)
        val board2 = makeBoard(1, ::StarhalmaBoard)
        val move1 = Move.Jump(0, listOf(51, 60, 120))
        val move2 = Move.Walk(14, 118)

        board1.doMove(move1)
        board1.undoMove(move1)
        assertContentEquals(board1.fields, board2.fields)
        board1.doMove(move2)
        board1.undoMove(move2)
        assertContentEquals(board1.fields, board2.fields)
    }
}
