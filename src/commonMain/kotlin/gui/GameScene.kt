package gui

import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korio.async.*
import halma.*

class GameScene : Scene() {
    override suspend fun SContainer.sceneInit() {
    }

    override suspend fun SContainer.sceneMain() {
        val game = makeGame(injector.get<GameParameter<StarhalmaBoardGui, StarhalmaBoard>>())
        game.board.onExit { launchImmediately { sceneContainer.back() } }
        game.start()

        sceneContainer.back()
    }
}
