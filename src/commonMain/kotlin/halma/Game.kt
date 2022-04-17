package halma

class Game<T: Board>(
    val board: T,
    val players: List<Player<T>>,
    val block: suspend Game<T>.()->Unit = { }
) {
    var round = 1

    suspend fun start() {
        while (true) {
            for (player in players) {
                val move = player.makeMove()
                board.move(move)
                block()
                if (player.hasWon()) return
            }
            round++
        }
    }
}

