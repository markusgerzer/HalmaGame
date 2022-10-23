package halma

import gui.BoardGui


fun <T: Board>makeBoard(
    numberOfPlayers: Int,
    boardClass: (Int) -> T
) = boardClass(numberOfPlayers).apply {
    require(numberOfPlayers in maxNumberOfPlayers)
    for ((value, idxList) in idToStartMaps[numberOfPlayers - 1]) {
        for (idx in idxList) fields[idx] = value
    }
}

fun <B: Board>makeGame(
    boardClass: (Int) -> B,
    playerClasses: List<(Int, List<Int>) -> Player<B>>,
    block: suspend Game<B>.() -> Unit
): Game<B> {
    makeBoard(playerClasses.size, boardClass).apply {
        val players = playerClasses.mapIndexed { i, Player ->
            Player(i + 1, idToHomeMaps[playerClasses.size - 1][i + 1]!!)
        }
        return Game(this, players, block)
            .also { game ->
                players.forEach { it.game = game }
            }
    }
}

suspend fun <D: BoardGui, B: Board>makeGame(
    decorator: suspend (Int, B) -> D,
    boardClass: (Int) -> B,
    playerClasses: List<(Int, List<Int>) -> Player<D>>,
    block: suspend Game<D>.()->Unit = { }
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
}

