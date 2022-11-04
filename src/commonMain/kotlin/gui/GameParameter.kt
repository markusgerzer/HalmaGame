package gui

import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import halma.*


typealias BoardGuiCreator<D, B> = suspend Container.(Int, B) -> D
typealias BoardCreator<B> = (Int) -> B
typealias PlayerCreator<D> = (Int, List<Int>) -> Player<D>

data class GameParameter<D: BoardGui, B: Board> (
    val boardGuiCreator: BoardGuiCreator<D, B>,
    val boardCreator: BoardCreator<B>,
    val playerCreators: List<PlayerCreator<D>>,
    val playerColors: List<RGBA>,
    val playerNames: List<String>,
    val block: suspend Game<D>.() -> Unit = {}
)
