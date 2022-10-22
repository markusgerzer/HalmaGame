package gui

import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korio.async.*
import playerClasses

class MenuScene : Scene() {
    override suspend fun SContainer.sceneInit() {
    }

    override suspend fun SContainer.sceneMain() {
        menuGui {
            playerClasses = it
            launchImmediately { sceneContainer.pushTo<GameScene>() }
        }
    }
}
