import com.soywiz.klock.seconds
import com.soywiz.korge.Korge
import com.soywiz.korge.tween.get
import com.soywiz.korge.tween.tween
import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.degrees
import gui.PlayerGui
import gui.StarhalmaBoardGui
import gui.starhalmaBoardGui
import halma.PlayerStupidAI
import halma.StarhalmaBoard
import halma.makeGame


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
        listOf(::PlayerGui, ::PlayerStupidAI)
    ) {
        /*board as StarhalmaBoardGui
        board.tween(
            board::spin[d.degrees],
            time = 2.seconds
        )
        d = (d + 60) % 360*/
    }

    game.board as StarhalmaBoardGui
    game.board.tween(
        game.board::spin[d.degrees],
        time = 3.seconds
    )

    game.start()


}