package gui

import com.soywiz.korge.input.onClick
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import halma.Player
import halma.PlayerAI
import halma.PlayerHashedAI
import halma.PlayerStupidAI


fun Container.menuGui(onStart: (List<(Int, List<Int>) -> Player<StarhalmaBoardGui>>) -> Unit) =
    MenuGui(onStart).addTo(this)

class MenuGui(val onStart: (List<(Int, List<Int>) -> Player<StarhalmaBoardGui>>) -> Unit): Container() {
    private var menuDropDown = false

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
        uiComboBox(200.0, items = supportedTypes.keys.toList())
    }
    init {
        playerTypes.forEach { it.alignLeftToLeftOf(playerTypeLabel) }
        playerTypes[0].alignTopToBottomOf(playerTypeLabel, 20)
        playerTypes.zipWithNext { a: UIComboBox<String>, b: UIComboBox<String> ->
            b.alignTopToBottomOf(a, 10)
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
        for (id in 0 until 6) {
            uiText("${id + 1}") {
                textColor = Colors.BLACK
                alignLeftToLeftOf(playerNrLabel)
                alignTopToTopOf(playerTypes[id])
            }
        }
    }

    val startButton = uiButton(text = "S T A R T") {
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