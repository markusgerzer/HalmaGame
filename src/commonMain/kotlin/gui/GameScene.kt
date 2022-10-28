package gui

import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import halma.*
import playerClasses

class GameScene : Scene() {
    override suspend fun SContainer.sceneInit() {
    }

    override suspend fun SContainer.sceneMain() {
        val game = makeGame(
            ::starhalmaBoardGui,
            ::StarhalmaBoard,
            playerClasses
        ) {}

        game.board.onExit { sceneContainer.back() }
        game.start()
        sceneContainer.back()
    }
}
