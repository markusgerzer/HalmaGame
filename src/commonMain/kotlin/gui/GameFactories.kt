package gui

import com.soywiz.korge.view.Container
import halma.Board
import halma.Game
import halma.makeBoard

/*
suspend fun <D: BoardGui, B: Board> Container.makeGame(
    decorator: BoardGuiCreator<D, B>,
    boardClass: BoardCreator<B>,
    playerClasses: List<PlayerCreator<D>>,
    block: suspend Game<D>.() -> Unit = { }
): Game<D> {
    val numberOfPlayers = playerClasses.size
    makeBoard(numberOfPlayers, boardClass).apply {
        val boardGui = decorator(numberOfPlayers, this)
        val players = playerClasses.mapIndexed { i, Player ->
            Player(i + 1, idToHomeMaps[numberOfPlayers - 1][i + 1]!!)
        }
        return Game(boardGui, players, block)
            .also { game ->
                players.forEach { it.game = game }
            }
    }
}*/

suspend fun <D: BoardGui, B: Board> Container.makeGame(
    gameParameter: GameParameter<D, B>
): Game<D> {
    val numberOfPlayers = gameParameter.playerCreators.size
    makeBoard(numberOfPlayers, gameParameter.boardCreator).apply {
        val boardGui = gameParameter.boardGuiCreator(this@makeGame, numberOfPlayers, this)
        val players = gameParameter.playerCreators.mapIndexed { i, Player ->
            Player(i + 1, idToHomeMaps[numberOfPlayers - 1][i + 1]!!)
        }
        return Game(boardGui, players, gameParameter.block)
            .also { game ->
                players.forEach { it.game = game }
            }
    }
}
