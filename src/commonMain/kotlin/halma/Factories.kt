package halma

import gui.*


fun <T: Board>makeBoard(
    numberOfPlayers: Int,
    boardCreator: BoardCreator<T>,
) = boardCreator(numberOfPlayers).apply {
    require(numberOfPlayers in maxNumberOfPlayers)
    for ((value, idxList) in idToStartMaps[numberOfPlayers - 1]) {
        for (idx in idxList) fields[idx] = value
    }
}

fun <T: Board>makeGame(
    boardCreator: BoardCreator<T>,
    playerCreators: List<PlayerCreator<T>>,
    block: suspend Game<T>.() -> Unit
): Game<T> {
    makeBoard(playerCreators.size, boardCreator).apply {
        val players = playerCreators.mapIndexed { i, Player ->
            Player(i + 1, idToHomeMaps[playerCreators.size - 1][i + 1]!!)
        }
        return Game(this, players, block)
            .also { game ->
                players.forEach { it.game = game }
            }
    }
}
