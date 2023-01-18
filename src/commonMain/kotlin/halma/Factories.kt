package halma

import gui.*


fun <T: Board>makeBoard(
    numberOfPlayers: Int,
    boardBuilder: BoardBuilder<T>,
) = boardBuilder(numberOfPlayers).apply {
    require(numberOfPlayers in maxNumberOfPlayers)
    for ((value, idxList) in idToStartMaps[numberOfPlayers - 1]) {
        for (idx in idxList) fields[idx] = value
    }
}

fun <T: Board>makeGame(
    boardBuilder: BoardBuilder<T>,
    playerCreators: List<PlayerBuilder<T>>,
    block: suspend Game<T>.() -> Unit
): Game<T> {
    makeBoard(playerCreators.size, boardBuilder).apply {
        val players = playerCreators.mapIndexed { i, Player ->
            Player(i + 1, idToHomeMaps[playerCreators.size - 1][i + 1]!!)
        }
        return Game(this, players, block)
            .also { game ->
                players.forEach { it.game = game }
            }
    }
}
