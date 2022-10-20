package gui

import com.soywiz.korge.*
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.soywiz.korio.async.launchImmediately
import playerClasses

class MenuScene : Scene() {
    override suspend fun SContainer.sceneInit() {
    }

    override suspend fun SContainer.sceneMain() {
        menuGui {
            playerClasses = it
            launchImmediately { sceneContainer.changeTo<GameScene>() }
        }
    }
}
