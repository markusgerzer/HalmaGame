package gui

import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import halma.*
import ui.*


fun Container.menuGui(onStart: suspend (GameParameter<StarhalmaBoardGui, StarhalmaBoard>) -> Unit) =
    MenuGui(onStart).addTo(this) { centerOnStage() }

class MenuGui(val onStart: suspend (GameParameter<StarhalmaBoardGui, StarhalmaBoard>) -> Unit): Container() {
    private val playerNrLabel = uiText(S.nrLabel, 32.0, 36.0) {
        textColor = Colors.BLACK
        textSize = 32.0
        alignTopToTopOf(this@MenuGui, 50)
        alignLeftToLeftOf(this@MenuGui, 50)
    }

    private val playerTypeLabel = uiText("        " + S.typeLabel, 400.0, 36.0) {
        textColor = Colors.BLACK
        textSize = 32.0
        alignTopToTopOf(playerNrLabel)
        alignLeftToRightOf(playerNrLabel, 32)
    }

    private val playerColorLabel = uiText("    " + S.colorLabel, 256.0, 36.0) {
        textColor = Colors.BLACK
        textSize = 32.0
        alignTopToTopOf(playerTypeLabel)
        alignLeftToRightOf(playerTypeLabel, 32)
    }

    private val playerColors = uiComboBoxArray2(
        boxWidth = 256.0,
        boxHeight = 64.0,
        boxPadding = 8.0,
        items = supportedColors.keys.toList(),
        numberOfComboBoxes = 6
    ) {
        textSize = 32.0
        alignLeftToLeftOf(playerColorLabel)
        alignTopToBottomOf(playerColorLabel, 32)
        for (i in 1 until numberOfComboBoxes) deactivateComboBox(i)
        onSelectionUpdate { updatePan(it) }
    }

    private val playerTypes = uiComboBoxArray1(
        boxWidth = 400.0,
        boxHeight = 64.0,
        boxPadding = 8.0,
        items = supportedTypes.keys.toList(),
        deactivationSymbol = "",
        numberOfComboBoxes = 6
    ) {
        textSize = 32.0
        alignLeftToLeftOf(playerTypeLabel)
        alignTopToBottomOf(playerTypeLabel, 32)

        onSelectionUpdate { idx ->
            if (selectedItems[idx] == null) playerColors.deactivateComboBox(idx)
            else playerColors.activateComboBox(idx)
            updatePan(idx)
        }
    }

    val playerNr = List(6) {
        uiText("${it + 1}") {
            textColor = Colors.BLACK
            textSize = 32.0
            alignLeftToLeftOf(playerNrLabel)
            centerYOn(playerTypes[it])
        }
    }

    private val pans = Array(6) { pan(it) }

    private fun updatePan(idx: Int) {
        pans[idx]?.removeFromParent()
        pans[idx] = pan(idx)
    }

    private fun pan(idx: Int): Pan? {
        val colorName = playerColors.selectedItems[idx]
        val color = supportedColors[colorName]
        return color?.let {
            pan(it).apply {
                alignLeftToRightOf(playerColors, 32)
                centerYOn(playerNr[idx])
            }
        }
    }

    val startButton = uiButton("S T A R T", width = 256.0, height = 64.0) {
        centerXOn(this@MenuGui)
        alignTopToBottomOf(playerNr.last(), 64)
        onClick { startGame() }
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
             S.playerGui to ::PlayerGui,
             S.playerAI to ::PlayerAI2,
             //S.playerHashedAI to ::PlayerHashedAI,
             //S.playerStupidAI to ::PlayerStupidAI
        )
    }
}
