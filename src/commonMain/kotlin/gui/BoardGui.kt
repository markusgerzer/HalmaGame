package gui

import com.soywiz.korge.ui.*
import com.soywiz.korio.async.*
import halma.*

interface BoardGui: Board {
    val onEmptyFieldClicked: AsyncSignal<Int>
    val goButton: UIButton
    val onExit: Signal<Unit>

    fun panAt(fieldIdx: Int): Pan
    fun mark(fieldIdx: Int)
    fun unMark(fieldIdx: Int)
}
