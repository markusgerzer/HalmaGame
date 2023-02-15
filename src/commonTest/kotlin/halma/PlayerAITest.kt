package halma

import com.soywiz.klock.TimeSpan
import com.soywiz.klock.measureTime
import com.soywiz.korio.async.suspendTest
import com.soywiz.korio.lang.*
import gui.*
import kotlin.test.*


class PlayerAITest {
    @Test
    fun solveTest1() {
        val boardBuilder: BoardBuilder<StarhalmaBoard> = ::starhalmaBoard
        val playerBuilder: PlayerBuilder<StarhalmaBoard> = ::PlayerAI2
        for (n in 1..6) {
            runTest(
                playerBuilder::class.portableSimpleName,
                List(n) { playerBuilder },
                boardBuilder
            )
        }
    }

    @Test
    fun distanceSum() {
        val game = makeGame(
            ::starhalmaBoard,
            listOf(::PlayerAI2)
        ) {}

        with(game.playerById(1)) {
            val board = game.board
            val fields = board.fields
            val ownPans = fields.indices.filter { fields[it] == id }
            val pansNotAtHome = ownPans.filterNot { it in home }
            val freeHomeFields = home.filterNot { fields[it] == id }
            var distance = 0
            for (i in pansNotAtHome.indices) {
                distance += board.fieldDistances[pansNotAtHome[i]][freeHomeFields[i]]
                println(distance)
            }
        }
    }

    class NotSolvedException() : Exception()
    private fun <B: Board>runTest(
        name: String,
        playerBuilder: List<PlayerBuilder<B>>,
        boardBuilder: BoardBuilder<B>,
        block: Player<B>.()->Unit = { }
    ) = suspendTest(TimeSpan.NIL) {
        val game = makeGame(
            boardBuilder,
            playerBuilder,
        ) {
            //println("Round: $round")
            if (round >= 100) {
                //println(board.fields.toList())
                //displayStarhalmaFields(board.fields.toList())
                throw NotSolvedException()
            }
        }
        game.forEachPlayer { it.apply(block) }
        try {
            val time = measureTime { game.start() }
            print("$name: ")
            println("Needed ${game.round} rounds and ${time.seconds} seconds to solve.")
        } catch (e: NotSolvedException) {
            displayStarhalmaFields(game.board.fields.toList())
            throw e
        }
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
            game.forEachPlayer { it.game = game }
            displayStarhalmaFields(fields.toList())
            game.start()
        }
    }

    @Test
    fun lastMoveTest2() {
        repeat(20) {
            suspendTest {
                val fields = intArrayOf(
                    2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1
                )
                val player1 = PlayerAI<StarhalmaBoard>(1, StarhalmaStaticBoardMappings.idToHomeMaps[1][1]!!)
                val player2 = PlayerAI<StarhalmaBoard>(2, StarhalmaStaticBoardMappings.idToHomeMaps[1][2]!!)
                val board = StarhalmaBoard(2, fields)
                val game = Game(board, listOf(player1, player2)) {
                    displayStarhalmaFields(fields.toList())
                    assertEquals(1, round)
                }
                game.forEachPlayer { it.game = game }
                displayStarhalmaFields(fields.toList())
                game.start()

            }
        }
    }


    @Test
    fun zobristHashTest() = suspendTest {
        val game = makeGame(::StarhalmaBoard, listOf(::PlayerHashedAI)) { }
        val player = game.playerById(1) as PlayerHashedAI
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
