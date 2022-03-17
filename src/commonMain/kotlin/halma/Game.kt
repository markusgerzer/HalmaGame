package halma

class Game<T: Board>(
    val board: Board,
    val players: List<Player<T>>,
    val block: Game<T>.()->Unit = { }
) {
    var round = 1

    fun hasWon(player: Player<T>): Boolean {
        player.home.forEach {
            if (board.fields[it] != player.id) return false
        }
        return true
    }

    suspend fun start() {
        while (true) {
            for (player in players) {
                val move = player.makeMove()
                board.move(move)
                block()
                if (hasWon(player)) return
            }
            round++
        }
    }
}

