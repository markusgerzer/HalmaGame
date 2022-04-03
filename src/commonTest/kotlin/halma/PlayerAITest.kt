package halma

import com.soywiz.korio.async.suspendTest
import kotlin.test.Test


class PlayerAITest {
    @Test
    fun singlePlayerSolveTest() {
        println("===========================================")
        singlePlayerSolve(::PlayerStupidAI, ::StarhalmaBoard)
        println("===========================================")
    }

    private fun <B: Board>singlePlayerSolve(
        playerClass: (Int, B, List<Int>) -> Player<B>,
        boardClass: () -> B
    ) = suspendTest {
        val game = makeGame(
            boardClass,
            listOf(playerClass)
        ) { }
        print("${PlayerStupidAI::class.simpleName}: ")
        game.start()
        println("Needed ${game.round} rounds to solve.")
    }
}