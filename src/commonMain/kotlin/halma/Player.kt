package halma

interface Player<T: Board> {
    val id: Int
    val home: List<Int>
    var game: Game<T>

    suspend fun makeMove(): Move

    fun hasWon(): Boolean {
        home.forEach {
            if (game.board.fields[it] != id) return false
        }
        return true
    }
}


