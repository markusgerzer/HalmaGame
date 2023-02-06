package halma

import com.soywiz.klogger.*

class RatedMove (val move: Move?, val rating: Rating) {
    override fun toString() = "$move, $rating"
}


fun interface CalcScore {
    fun calcScore(player: Player<*>): Int
}

val distanceToHome = CalcScore { player ->
    with(player) {
        val board = game.board
        val fields = board.fields
        val ownPans = fields.indices.filter { fields[it] == id }
        val pansNotAtHome = ownPans.filterNot { it in home }
        val freeHomeFields = home.filterNot { fields[it] == id }
        var distance = 0
        for (i in pansNotAtHome.indices) {
            distance += board.fieldDistances[pansNotAtHome[i]][freeHomeFields[i]]
        }
        Rating.MAX_SCORE - distance
    }
}


open class PlayerAI2<T: Board>(
    override val id: Int,
    override val home: List<Int>
) : Player<T>, CalcScore by distanceToHome {
    override lateinit var game: Game<T>

    override suspend fun makeMove(): Move {
        val depth = 2
        val ratedMove = evaluate(depth, id)
        Console.log(ratedMove)
        return ratedMove.move!!
    }

    private fun compareRatingsForPlayerId(playerId: Int, a: Rating, b: Rating) =
        a[playerId].compareTo(b[playerId])

    private fun calcRating() = ratingOf(*IntArray(game.players.size) { calcScore(game.players[it]) })

    private fun evaluate(depth: Int, playerId: Int): RatedMove {
        if (depth == 0 || game.players[playerId -1 ].hasWon())
            return RatedMove(null, calcRating())

        var bestRating = worstRatingFor(playerId)
        var bestMove: Move? = null

        val possibleMoves = game.board.possibleMovesOfPlayerNr(playerId).toList()
        for (move in possibleMoves) {
            game.board.doMove(move)

            val rating =
                evaluate(depth - 1, game.board.nextPlayer(playerId)).rating

            val compare = compareRatingsForPlayerId(playerId, rating, bestRating)
            if (compare > 0) {
                bestRating = rating
                bestMove = move
            } else if (compare == 0) { /* TODO() */ }

            game.board.undoMove(move)
        }
        return RatedMove(bestMove, bestRating.decrement(playerId))
    }

}
