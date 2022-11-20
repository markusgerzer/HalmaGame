package gui

import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.Container


abstract class FieldGui(val idx: Int): Container() {

    var onClickCallback: (suspend (FieldGui) -> Unit) = {}
    init {
        onClick { onClickCallback(this) }
    }

    abstract fun mark()
    abstract fun unMark()
}
