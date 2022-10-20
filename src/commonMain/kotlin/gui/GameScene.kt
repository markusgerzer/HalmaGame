package gui

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import halma.StarhalmaBoard
import halma.makeGame
import playerClasses

class GameScene : Scene() {
    override suspend fun SContainer.sceneInit() {
    }

    override suspend fun SContainer.sceneMain() {
        StarhalmaBoardGui.initialize()

        val game = makeGame(
            ::starhalmaBoardGui,
            ::StarhalmaBoard,
            playerClasses
        ) { }

        game.start()
        /*
        fixedSizeContainer(512, 512) {
            //scale = .2
            val game = makeGame(
                ::starhalmaBoardGui,
                ::StarhalmaBoard,
                playerClasses
            ) { }

            game.start()
        }*/
    }
}
