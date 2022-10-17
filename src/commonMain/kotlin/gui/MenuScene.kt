package gui

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korio.async.launchImmediately
import playerClasses

class MenuScene : Scene() {
    override suspend fun Container.sceneInit() {
    }

    override suspend fun Container.sceneMain() {
        menuGui {
            playerClasses = it
            launchImmediately { sceneContainer.changeTo<GameScene>() }
        }
    }
}