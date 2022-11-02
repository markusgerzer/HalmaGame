package gui

import com.soywiz.korge.input.onClick
import halma.Game
import halma.Move
import halma.Player
import kotlinx.coroutines.channels.Channel

class PlayerGui<T: BoardGui>(
    override val id: Int,
    override val home: List<Int>
) : Player<T> {
    override lateinit var game: Game<T>

    private val playerPans: List<Pan> by lazy {
        game.board.goButton.let{
            it.onClick {
                game.board.validMoveOfOrNull(idxList)?.let { move -> channel.send(move) }
            }
        }

        val panList = mutableListOf<Pan>()
        for (i in game.board.fields.indices) {
            if (game.board.fields[i] == id) panList.add(game.board.panAt(i))
        }
        panList
    }

    private val channel = Channel<Move>(0)
    private var idxList = listOf<Int>()

    private fun goButtonEnable() {
        game.board.goButton.let {
            it.visible = true
            it.enabled = true

        }
    }

    private fun goButtonDisable() {
        game.board.goButton.run {
            visible = false
            enabled = false
        }
    }

    private suspend fun fieldSelect(fieldGui: FieldGui) {
        if (idxList.isEmpty()) return

        if (idxList.last() == fieldGui.idx) {
            if (idxList.size == 1) panSelect(game.board.panAt(idxList[0]))
            else {
                game.board.guiFields[fieldGui.idx].unMark()
                idxList = idxList.dropLast(1)
                val move = game.board.validMoveOfOrNull(idxList)
                if (move == null) goButtonDisable()
                else goButtonEnable()
            }
        }
        else {
            val move = game.board.validMoveOfOrNull(idxList + fieldGui.idx)
            if (move != null) {
                val oldIdxList = idxList
                idxList = when (move) {
                    is Move.Walk -> listOf(move.startFieldIdx, move.destFieldIdx)
                    is Move.Jump -> listOf(move.startFieldIdx) + move.destFieldIdxList
                }
                for (idx in oldIdxList.filterNot { it in idxList })
                    game.board.guiFields[idx].unMark()
                for (idx in idxList) {
                    game.board.guiFields[idx].mark()
                }
                goButtonEnable()
            }
        }
    }

    private suspend fun panSelect(pan: Pan) {
        if (game.board.possibleMoves(pan.fieldIdx).toList().isEmpty()) return

        if (idxList.isNotEmpty()) {
            for (idx in idxList) game.board.guiFields[idx].unMark()
            val selectedPan = game.board.panAt(idxList[0])
            if (selectedPan !== pan) selectedPan.unTip()
        }
        goButtonDisable()
        idxList = listOf(pan.fieldIdx)
        pan.tip()
        game.board.guiFields[pan.fieldIdx].mark()
    }

    private fun setPansOnClick(block: (suspend(Pan) -> Unit)?) {
        for (pan in playerPans) {
            pan.onClickCallback = block
        }
    }

    private fun setGuiFieldsOnClick(block: (suspend (FieldGui) -> Unit)?) {
        for (guiField in game.board.guiFields) {
            guiField.onClickCallback = block
        }
    }

    override suspend fun makeMove(): Move {
        setPansOnClick(::panSelect)
        setGuiFieldsOnClick(::fieldSelect)

        val move = channel.receive()

        setPansOnClick(null)
        setGuiFieldsOnClick(null)
        goButtonDisable()
        for (idx in idxList) {
            game.board.guiFields[idx].unMark()
        }
        idxList = emptyList()

        return move
    }
}
