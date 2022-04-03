package halma

import gui.BoardGui


fun <T: Board>makeBoard(Board: () -> T, numberOfPlayers: Int) = Board().apply {
    require(numberOfPlayers in possibleNumberOfPlayers)
    for ((value, idxList) in idToStartMaps[numberOfPlayers - 1]) {
        for (idx in idxList) fields[idx] = value
    }
}

fun <B: Board>makeGame(
    boardClass: () -> B,
    playerClasses: List<(Int, B, List<Int>) -> Player<B>>,
    block: suspend Game<B>.() -> Unit
): Game<B> {
    makeBoard(boardClass, playerClasses.size).apply {
        val players = playerClasses.mapIndexed { i, Player ->
            Player(i + 1, this, idToHomeMaps[playerClasses.size - 1][i + 1]!!)
        }
        return Game(this, players, block)
    }
}

fun <D: BoardGui, B: Board>makeGame(
    decorator: (B) -> D,
    boardClass: () -> B,
    playerClasses: List<(Int, D, List<Int>) -> Player<D>>,
    block: suspend Game<D>.()->Unit = { }
): Game<D> {
    makeBoard(boardClass, playerClasses.size).apply {
        val boardGui = decorator(this)
        val players = playerClasses.mapIndexed { i, Player ->
            Player(i + 1, boardGui, idToHomeMaps[playerClasses.size - 1][i + 1]!!)
        }
        return Game(boardGui, players, block)
    }
}