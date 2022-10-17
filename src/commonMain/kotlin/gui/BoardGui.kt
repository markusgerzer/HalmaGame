package gui

import com.soywiz.korge.ui.UIButton
import halma.Board

interface BoardGui: Board {
    val guiFields: List<FieldGui>
    val goButton: UIButton
    fun panAt(fieldIdx: Int): Pan
}