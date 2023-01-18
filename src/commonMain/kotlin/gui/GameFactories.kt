package gui

import com.soywiz.korge.view.*
import halma.*

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
) =
    with(gameParameter) {
    val container = this@makeGame
    val numberOfPlayers = gameParameter.playerCreators.size

    makeBoard(numberOfPlayers, boardBuilder)
        .let { board ->
            val boardGui =
                boardGuiBuilder(
                    container,
                    numberOfPlayers,
                    board,
                    playerColors,
                    playerNames
                )

            val players =
                playerCreators
                    .mapIndexed { i, Player ->
                        Player(i + 1, board.idToHomeMaps[numberOfPlayers - 1][i + 1]!!)
                    }

            Game(boardGui, players, block)
                .also { game ->
                    players.forEach { it.game = game }
                }
        }
}
