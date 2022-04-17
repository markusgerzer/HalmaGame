package halma

class PlayerStupidAI<T: Board>(
    override val id: Int,
    override val home: List<Int>
) : Player<T> {
    override lateinit var game: Game<T>

    override suspend fun makeMove(): Move {
        val fieldsCopy = game.board.fields.copyOf()
        val possibleMoves = game.board.possibleMovesOfPlayerNr(id)
        var minDistance = Int.MAX_VALUE
        var minDistanceI = -1

        for ((i, move) in possibleMoves.withIndex()) {
            fieldsCopy[move.startFieldIdx] = 0
            fieldsCopy[move.destFieldIdx] = id
            val distance = rate(fieldsCopy)
            if (distance < minDistance) {
                minDistance = distance
                minDistanceI = i
            }
            fieldsCopy[move.destFieldIdx] = 0
            fieldsCopy[move.startFieldIdx] = id
        }
        if (minDistanceI < 0) throw IllegalStateException("Can not move!")
        return possibleMoves[minDistanceI]
    }

    fun rate(fields: IntArray): Int {
        val pansNotAtHome = ownPans(fields).filterNot { it in home }
        val freeHomeFields = home.filterNot { fields[it] == id }
        var distance = 0
        for (i in pansNotAtHome.indices) {
            distance += game.board.fieldDistances[pansNotAtHome[i]][freeHomeFields[i]]
        }
        return distance
    }

    fun ownPans(fields: IntArray) = fields.indices.filter { fields[it] == id }
}