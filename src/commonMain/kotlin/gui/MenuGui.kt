package gui

import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import halma.*
import ui.*


fun Container.menuGui(onStart: (List<(Int, List<Int>) -> Player<StarhalmaBoardGui>>) -> Unit) =
    MenuGui(onStart).addTo(this)

class MenuGui(val onStart: (List<(Int, List<Int>) -> Player<StarhalmaBoardGui>>) -> Unit): Container() {

    private val playerNrLabel = uiText("Nr.") {
        textColor = Colors.BLACK
        alignTopToTopOf(this@MenuGui, 50)
        alignLeftToLeftOf(this@MenuGui, 50)
    }

    private val playerColorLabel = uiText("Color") {
        textColor = Colors.BLACK
        alignTopToTopOf(playerNrLabel)
        alignLeftToLeftOf(playerNrLabel, 50)
    }

    private val playerTypeLabel = uiText("Player") {
        textColor = Colors.BLACK
        alignTopToTopOf(playerColorLabel)
        alignLeftToLeftOf(playerColorLabel, 150)
    }

    private val playerTypes = List(6) {
        uiComboBox(200.0, items = supportedTypes.keys.toList()) {
            deactivate()
        }
    }
    init {
        playerTypes.forEach { it.alignLeftToLeftOf(playerTypeLabel) }
        playerTypes[0].alignTopToBottomOf(playerTypeLabel, 20)
        playerTypes.zipWithNext { a, b ->
            b.alignTopToBottomOf(a, 10)
        }

        playerTypes[0].activate()
        for (i in 0 until playerTypes.size - 1) {
            playerTypes[i].onSelectionUpdate {
                if (it.selectedIndex > 0) playerTypes[i + 1].activate()
                else {
                    for (j in i + 1 until playerTypes.size) {
                        playerTypes[j].deactivate()
                        playerTypes[j].selectedIndex = 0
                    }
                }
            }
        }
    }

    private val playerColors = List(6) {
        uiText(StarhalmaBoardGui.playerNames[it]) {
            textColor = Colors.BLACK
            alignLeftToLeftOf(playerColorLabel)
            alignTopToTopOf(playerTypes[it])
        }
    }

    init {
        for (i in 0 until 6) {
            uiText("${i + 1}") {
                textColor = Colors.BLACK
                alignLeftToLeftOf(playerNrLabel)
                alignTopToTopOf(playerTypes[i])
            }
        }
    }

    val startButton = uiButton("S T A R T") {
        centerXOn(this@MenuGui)
        alignTopToBottomOf(playerTypes.last(), 20)
        onClick { startGame() }
    }

    init {
        centerOnStage()
    }

    private fun startGame() {
        val playerTypeName = playerTypes.map { it.selectedItem }
        val player = playerTypeName.mapNotNull { supportedTypes[it] }
        onStart(player)
    }

    companion object {
        val supportedTypes: Map<String, ((Int, List<Int>) -> Player<StarhalmaBoardGui>)?> = mapOf(
            "-" to null,
            "Human Player" to ::PlayerGui,
            "AI" to ::PlayerAI,
            "Hashed AI" to ::PlayerHashedAI,
            "Stupid AI" to ::PlayerStupidAI
        )
    }
}
