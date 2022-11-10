package gui

import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import halma.*
import ui.*


fun Container.menuGui(onStart: suspend (GameParameter<StarhalmaBoardGui, StarhalmaBoard>) -> Unit) =
    MenuGui(onStart).addTo(this)

class MenuGui(val onStart: suspend (GameParameter<StarhalmaBoardGui, StarhalmaBoard>) -> Unit): Container() {

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

    private val playerColors = uiComboBoxArray2(
        boxPadding = 5.0,
        items = supportedColors.keys.toList(),
        numberOfComboBoxes = 6
    ) {
        alignLeftToLeftOf(playerColorLabel)
        alignTopToBottomOf(playerColorLabel, 20)

        for (i in 1 until numberOfComboBoxes) deactivateComboBox(i)
    }

    private val playerTypes = uiComboBoxArray1(
        boxWidth = 200.0,
        boxPadding = 5.0,
        items = supportedTypes.keys.toList(),
        deactivationSymbol = "-",
        numberOfComboBoxes = 6
    ) {
        alignLeftToLeftOf(playerTypeLabel)
        alignTopToBottomOf(playerTypeLabel, 20)

        onSelectionUpdate { idx ->
            if (selectedItems[idx] == null) playerColors.deactivateComboBox(idx)
            else playerColors.activateComboBox(idx)
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
        alignTopToBottomOf(playerTypes[5], 20)
        onClick { startGame() }
    }

    init {
        centerOnStage()
    }

    private suspend fun startGame() {
        val playerTypeName = playerTypes.selectedItems
        val player = playerTypeName.mapNotNull { supportedTypes[it] }

        val playerColorNames = playerColors.selectedItems.filterNotNull()
        val playerColors = playerColorNames.mapNotNull { supportedColors[it] }

        val gameParameter = GameParameter(
            Container::starhalmaBoardGui,
            ::StarhalmaBoard,
            player,
            playerColors,
            playerColorNames
        )
        onStart(gameParameter)
    }

    companion object {
        val supportedColors = StarhalmaBoardGuiConfig.defaultPlayerNames
            .zip(StarhalmaBoardGuiConfig.defaultPlayerColors)
            .toMap()

        val supportedTypes: Map<String, ((Int, List<Int>) -> Player<StarhalmaBoardGui>)?> = mapOf(
            "Human Player" to ::PlayerGui,
            "AI" to ::PlayerAI,
            "Hashed AI" to ::PlayerHashedAI,
            "Stupid AI" to ::PlayerStupidAI
        )
    }
}
