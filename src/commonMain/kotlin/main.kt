import com.soywiz.korge.Korge
import com.soywiz.korge.scene.Module
import com.soywiz.korge.scene.Scene
import com.soywiz.korim.color.Colors
import com.soywiz.korinject.AsyncInjector
import com.soywiz.korma.geom.SizeInt
import gui.GameScene
import gui.MenuScene
import gui.StarhalmaBoardGui
import halma.Player
import kotlin.reflect.KClass

var playerClasses: List<(Int, List<Int>) -> Player<StarhalmaBoardGui>> = emptyList()

suspend fun main() = Korge(Korge.Config(module = ConfigModule))

object ConfigModule : Module() {
    //override val size = SizeInt(2600, 2600)
    override val size = SizeInt(512, 512)
    //override val windowSize = SizeInt(512, 512)
    override val bgcolor = Colors.BEIGE
    override val clipBorders = false
    override val mainScene : KClass<out Scene> = MenuScene::class

    override suspend fun AsyncInjector.configure() {
        mapPrototype { GameScene() }
        mapPrototype { MenuScene() }
    }
}

/*
suspend fun main() = Korge(
    virtualWidth = 2600,
    virtualHeight = 2600,
    bgcolor = Colors.BEIGE,
    clipBorders = false
) {
    StarhalmaBoardGui.initialize()

    //var d = 180
    val game = makeGame(
        ::starhalmaBoardGui,
        ::StarhalmaBoard,
        //List(6) { ::PlayerAI }
        //List(6) { ::PlayerStupidAI }
        //List(6) { ::PlayerGui }
        //listOf(::PlayerStupidAI, ::PlayerGui)
        //listOf(::PlayerStupidAI)
        //listOf(::PlayerGui)
        //listOf(::PlayerAI)
        //listOf(::PlayerStupidAI, ::PlayerAI)
        //listOf(::PlayerGui, ::PlayerStupidAI)
        listOf(::PlayerAI, ::PlayerHashedAI)
        //listOf(::PlayerAI, :: PlayerGui)
        //listOf(::PlayerHashedAI, ::PlayerGui)
        //listOf(::PlayerGui, ::PlayerAI)
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

    /*game.board as StarhalmaBoardGui
    game.board.tween(
        game.board::spin[d.degrees],
        time = 3.seconds
    )*/

    game.start()
}


 */
