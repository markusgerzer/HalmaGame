package gui

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.fixedSizeContainer
import halma.StarhalmaBoard
import halma.makeGame
import playerClasses

class GameScene : Scene() {
    override suspend fun Container.sceneInit() {
    }

    override suspend fun Container.sceneMain() {
        StarhalmaBoardGui.initialize()

        fixedSizeContainer(512, 512) {
            scale = .2
            val game = makeGame(
                ::starhalmaBoardGui,
                ::StarhalmaBoard,
                playerClasses
            ) { }

            game.start()
        }
    }
}