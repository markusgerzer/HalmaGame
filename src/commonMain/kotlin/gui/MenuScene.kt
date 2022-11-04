package gui

import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import halma.*

class MenuScene : Scene() {
    override suspend fun SContainer.sceneInit() {
    }

    override suspend fun SContainer.sceneMain() {
        menuGui { playerConstructors ->
            val gameParameter = GameParameter(
                Container::starhalmaBoardGui,
                ::StarhalmaBoard,
                playerConstructors,
                StarhalmaBoardGuiConfig.defaultPlayerColors,
                StarhalmaBoardGuiConfig.defaultPlayerNames
            )
            sceneContainer.pushTo<GameScene>(gameParameter)
        }
    }
}
