import com.soywiz.klock.seconds
import com.soywiz.korge.Korge
import com.soywiz.korge.tween.get
import com.soywiz.korge.tween.tween
import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.degrees
import gui.PlayerGui
import gui.StarhalmaBoardGui
import gui.starhalmaBoardGui
import halma.*


suspend fun main() = Korge(
    virtualWidth = 2600,
    virtualHeight = 2600,
    bgcolor = Colors.BEIGE,
    clipBorders = false
) {
    StarhalmaBoardGui.initialize()

    var d = 180
    val game = makeGame(
        ::starhalmaBoardGui,
        ::StarhalmaBoard,
        //List(6) { ::PlayerStupidAI }
        //List(6) { ::PlayerGui }
        //listOf(::PlayerStupidAI, ::PlayerGui)
        //listOf(::PlayerStupidAI)
        //listOf(::PlayerGui)
        //listOf(::PlayerAI)
        listOf(::PlayerAI, ::PlayerAI)
        //listOf(::PlayerGui, ::PlayerStupidAI)
        //listOf(::PlayerAI, ::PlayerStupidAI)
        //listOf(::PlayerAI, ::PlayerGui)
    ) {
        /*board as StarhalmaBoardGui
        board.tween(
            board::spin[d.degrees],
            time = 2.seconds
        )
        d = (d + 60) % 360*/
        /*println()
        println("==========")
        println(round)
        println("==========")*/
    }

    /*
    game.board as StarhalmaBoardGui
    game.board.tween(
        game.board::spin[d.degrees],
        time = 3.seconds
    )
     */

    game.start()

    /*
    val fields = intArrayOf(
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1
    )
    val board = StarhalmaBoardGui(1, StarhalmaBoard(1, fields))
    val game = Game(board, listOf(PlayerAI(1, StarhalmaStaticBoardMappings.idToHomeMaps[0][1]!!)))
    game.players.forEach { it.game = game }
    game.start()

     */
}

