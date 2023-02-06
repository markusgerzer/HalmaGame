package gui

import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import halma.*


typealias BoardGuiBuilder<D, B> = suspend Container.(Int, B, List<RGBA>, List<String>) -> D
typealias BoardBuilder<B> = (Int) -> B
typealias PlayerBuilder<D> = (Int, List<Int>) -> Player<D>

data class GameParameter<D: BoardGui, B: Board> (
    val boardGuiBuilder: BoardGuiBuilder<D, B>,
    val boardBuilder: BoardBuilder<B>,
    val playerBuilderList: List<PlayerBuilder<D>>,
    val playerColors: List<RGBA>,
    val playerNames: List<String>,
    val block: suspend Game<D>.() -> Unit = {}
)
