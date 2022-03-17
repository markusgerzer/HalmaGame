package gui

import com.soywiz.korge.input.onClick
import halma.Move
import halma.Player
import kotlinx.coroutines.channels.Channel

class PlayerGui<T: BoardGui>(
    override val id: Int,
    override val board: T,
    override val home: List<Int>
) : Player<T> {
    private val playerPans: List<Pan>
    init {
        val panList = mutableListOf<Pan>()
        for (i in board.fields.indices) {
            if (board.fields[i] == id) panList.add(board.panAt(i))
        }
        playerPans = panList
    }

    private val channel = Channel<Move>(0)
    private var idxList = listOf<Int>()
    init {
        board.goButton.let{
            it.onClick {
                board.validMoveOfOrNull(idxList)?.let { move -> channel.send(move) }
            }
        }
    }

    fun goButtonEnable() {
        board.goButton.let {
            it.visible = true
            it.enabled = true

        }
    }

    fun goButtonDisable() {
        board.goButton.run {
            visible = false
            enabled = false
        }
    }

    suspend fun fieldSelect(fieldGui: FieldGui) {
        if (idxList.isEmpty()) return

        if (idxList.last() == fieldGui.idx) {
            if (idxList.size == 1) panSelect(board.panAt(idxList[0]))
            else {
                board.guiFields[fieldGui.idx].unMark()
                idxList = idxList.dropLast(1)
                val move = board.validMoveOfOrNull(idxList)
                if (move == null) goButtonDisable()
                else goButtonEnable()
            }
        }
        else {
            val move = board.validMoveOfOrNull(idxList + fieldGui.idx)
            if (move != null) {
                idxList += fieldGui.idx
                fieldGui.mark()
                goButtonEnable()
            }
        }
    }

    suspend fun panSelect(pan: Pan) {
        if (board.possibleMoves(pan.fieldIdx).isEmpty()) return

        if (idxList.isNotEmpty()) {
            for (idx in idxList) board.guiFields[idx].unMark()
            val selectedPan = board.panAt(idxList[0])
            if (selectedPan !== pan) selectedPan.unTip()
        }
        goButtonDisable()
        idxList = listOf(pan.fieldIdx)
        pan.tip()
        board.guiFields[pan.fieldIdx].mark()
    }

    fun setPansOnClick(block: (suspend(Pan) -> Unit)?) {
        for (pan in playerPans) {
            pan.onClickCallback = block
        }
    }

    fun setGuiFieldsOnClick(block: (suspend (FieldGui) -> Unit)?) {
        for (guiField in board.guiFields) {
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
            board.guiFields[idx].unMark()
        }
        idxList = emptyList()

        return move
    }
}