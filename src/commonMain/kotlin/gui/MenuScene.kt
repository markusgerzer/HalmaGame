package gui

import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import playerClasses

class MenuScene : Scene() {
    override suspend fun SContainer.sceneInit() {
    }

    override suspend fun SContainer.sceneMain() {
        menuGui {
            playerClasses = it
            sceneContainer.pushTo<GameScene>()
        }
    }
}
