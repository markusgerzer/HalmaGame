import com.soywiz.korge.*
import com.soywiz.korge.scene.*
import com.soywiz.korgw.*
import com.soywiz.korim.color.*
import com.soywiz.korinject.*
import com.soywiz.korma.geom.*
import gui.*
import kotlin.reflect.*


suspend fun main() = Korge(Korge.Config(module = ConfigModule))

object ConfigModule : Module() {
    override val quality = GameWindow.Quality.QUALITY
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
