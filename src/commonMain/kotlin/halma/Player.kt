package halma

interface Player<T: Board> {
    val id: Int
    val board: T
    val home: List<Int>

    suspend fun makeMove(): Move
}


