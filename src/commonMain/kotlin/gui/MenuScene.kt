package gui

import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*

class MenuScene : Scene() {
    override suspend fun SContainer.sceneInit() {
    }

    override suspend fun SContainer.sceneMain() {
        stage?.uiSkin = UISkin {
            textSize = 32.0
        }

        menuGui { sceneContainer.pushTo<GameScene>(it) }
    }
}
