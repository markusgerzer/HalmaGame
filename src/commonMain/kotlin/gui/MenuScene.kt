package gui

import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*

class MenuScene : Scene() {
    override suspend fun SContainer.sceneInit() {
    }

    override suspend fun SContainer.sceneMain() {
        menuGui { sceneContainer.pushTo<GameScene>(it) }
    }
}
