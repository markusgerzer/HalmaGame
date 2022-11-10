package gui

import com.soywiz.korge.ui.UIButton
import com.soywiz.korio.async.*
import halma.Board

interface BoardGui: Board {
    val guiFields: List<FieldGui>
    val goButton: UIButton
    fun panAt(fieldIdx: Int): Pan
    val onExit: Signal<Unit>
}
