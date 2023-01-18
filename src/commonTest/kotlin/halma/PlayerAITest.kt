package halma

import com.soywiz.klock.TimeSpan
import com.soywiz.klock.measureTime
import com.soywiz.korio.async.suspendTest
import com.soywiz.korio.lang.portableSimpleName
import gui.*
import kotlin.test.Test


class PlayerAITest {
    @Test
    fun solveTest() {
        println("===========================================")
        runTest(
            "${PlayerStupidAI::class.portableSimpleName} player = 1",
            listOf(::PlayerStupidAI),
            ::StarhalmaBoard)
        println("===========================================")
        runTest(
            "${PlayerAI::class.portableSimpleName} player = 1",
            listOf(::PlayerAI),
            ::StarhalmaBoard)
        println("===========================================")
        runTest(
            "${PlayerHashedAI::class.portableSimpleName} player = 1",
            listOf(::PlayerHashedAI),
            ::StarhalmaBoard)
        println("===========================================")
        runTest(
            "${PlayerStupidAI::class.portableSimpleName} player = 2",
            List(2) { ::PlayerStupidAI },
            ::StarhalmaBoard)
        println("===========================================")
        runTest(
            "${PlayerAI::class.portableSimpleName} player = 2",
            List(2) { ::PlayerAI },
            ::StarhalmaBoard)
        println("===========================================")
        runTest(
            "${PlayerHashedAI::class.portableSimpleName} player = 2",
            List(2) { ::PlayerHashedAI },
            ::StarhalmaBoard)
        println("===========================================")
    }

    @Test
    fun solveTest2() {
        val boardBuilder: BoardBuilder<StarhalmaBoard> = ::starhalmaBoard
        val playerBuilder: PlayerBuilder<StarhalmaBoard> = ::PlayerAI
        runTest(
            playerBuilder::class.portableSimpleName,
            listOf(playerBuilder),
            boardBuilder
        )
    }

    private fun <B: Board>runTest(
        name: String,
        playerBuilder: List<(Int, List<Int>) -> Player<B>>,
        boardBuilder: (Int) -> B,
        block: Player<B>.()->Unit = { }
    ) = suspendTest(TimeSpan.NIL) {
        val game = makeGame(
            boardBuilder,
            playerBuilder,
        ) {
            if (round >= 100) {
                println(board.fields.toList())
                displayStarhalmaFields(board.fields.toList())
                throw Exception()
            }
        }
        game.players.forEach{ it.apply(block) }
        val time = measureTime { game.start() }
        print("$name: ")
        println("Needed ${game.round} rounds and ${time.seconds} seconds to solve.")
    }

    @Test
    fun lastMoveTest() {
        suspendTest {
            val fields = intArrayOf(
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1
            )
            val player = PlayerAI<StarhalmaBoard>(1, StarhalmaStaticBoardMappings.idToHomeMaps[0][1]!!)
            val board = StarhalmaBoard(1, fields)
            val game = Game(board, listOf(player)) { displayStarhalmaFields(fields.toList()) }
            game.players.forEach { it.game = game }
            displayStarhalmaFields(fields.toList())
            game.start()
        }
    }

    @Test
    fun zobristHashTest() = suspendTest {
        val game = makeGame(::StarhalmaBoard, listOf(::PlayerHashedAI)) { }
        val player = game.players[0] as PlayerHashedAI
        println(player.zobristTable.toList())
        println(player.zobristHash)
        player.updateZobristHash(1, 14)
        player.updateZobristHash(1, 27)
        println(player.zobristHash)
        player.updateZobristHash(1, 27)
        player.updateZobristHash(1, 14)
        println(player.zobristHash)
        player.updateZobristHash(1, 14)
        player.updateZobristHash(1, 27)
        println(player.zobristHash)
    }
}
