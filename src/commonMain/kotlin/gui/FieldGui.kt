package gui

import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.Container
import com.soywiz.korui.UiButton


abstract class FieldGui(val idx: Int): Container() {
    var onClickCallback: (suspend (FieldGui) -> Unit)? = null
    init {
        onClick { onClickCallback?.let { block -> block(this) } }
    }

    abstract fun mark()
    abstract fun unMark()
}