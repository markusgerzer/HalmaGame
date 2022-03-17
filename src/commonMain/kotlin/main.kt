import com.soywiz.korge.Korge
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import gui.PlayerGui
import gui.StarhalmaBoardGui
import gui.starhalmaBoardGui
import halma.PlayerStupidAI
import halma.StarhalmaBoard
import halma.makeGame


suspend fun main() = Korge(virtualWidth = 2600, virtualHeight = 2600, bgcolor = Colors.BEIGE) {
    StarhalmaBoardGui.goButtonIcon = resourcesVfs["check_mark.png"].readBitmap()

    val game = makeGame<StarhalmaBoardGui, StarhalmaBoard>(
        ::starhalmaBoardGui,
        ::StarhalmaBoard,
        //List(6) { ::PlayerStupidAI }
        //List(6) { ::PlayerGui }
        listOf(::PlayerStupidAI, ::PlayerGui)
    )
    game.start()
}