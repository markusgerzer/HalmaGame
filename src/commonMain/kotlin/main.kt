import com.soywiz.korge.Korge
import com.soywiz.korge.scene.Module
import com.soywiz.korge.scene.Scene
import com.soywiz.korim.color.Colors
import com.soywiz.korinject.AsyncInjector
import com.soywiz.korma.geom.SizeInt
import gui.*
import halma.*
import kotlin.reflect.KClass

var playerClasses: List<(Int, List<Int>) -> Player<StarhalmaBoardGui>> = emptyList()
//var playerClasses: List<(Int, List<Int>) -> Player<StarhalmaBoardGui>> = listOf(::PlayerAI, ::PlayerGui)

suspend fun main() = Korge(Korge.Config(module = ConfigModule))

object ConfigModule : Module() {
    override val size = SizeInt(512, 512)
    override val bgcolor = Colors.BEIGE
    override val clipBorders = false
    override val mainScene : KClass<out Scene> = MenuScene::class
    //override val mainScene : KClass<out Scene> = GameScene::class

    override suspend fun AsyncInjector.configure() {
        mapPrototype { GameScene() }
        mapPrototype { MenuScene() }
    }
}
