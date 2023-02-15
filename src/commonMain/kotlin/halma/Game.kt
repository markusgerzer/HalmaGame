package halma

class Game<T: Board>(
    val board: T,
    @Deprecated("Should be private.", ReplaceWith("forEachPlayer, playerById"))
    val players: List<Player<T>>,
    private val block: suspend Game<T>.()->Unit = { }
) {
    var round = 1
        private set

    val forEachPlayer = players::forEach
    fun playerById(id: Int) = players[id - 1]

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

