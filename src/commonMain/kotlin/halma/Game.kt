package halma

class Game<T: Board>(
    val board: T,
    val players: List<Player<T>>,
    val block: suspend Game<T>.()->Unit = { }
) {
    var round = 1
        private set

    suspend fun start() {
        val winner: Player<T>
        round@while (true) {
            for (player in players) {
                board.hookBeforeMove(player)
                val move = player.makeMove()
                board.move(move)
                block()
                if (player.hasWon()) {
                    winner = player
                    break@round
                }
            }
            round++
        }
        board.hookGameEnd(winner)
    }
}

