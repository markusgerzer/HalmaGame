package halma

class RatedMove (val move: Move?, val rating: Rating) {
    override fun toString() = "$move, $rating"
}


fun interface CalcScore {
    fun calcScore(player: Player<*>): Score
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
        Score(Rating.MAX_SCORE - distance)
    }
}


fun interface AdjustScore {
    fun adjustScore(player: Player<*>, score: Int, move: Move): Score
}



open class PlayerAI2<T: Board>(
    override val id: Int,
    override val home: List<Int>
) : Player<T>, CalcScore by distanceToHome {
    override lateinit var game: Game<T>

    override suspend fun makeMove(): Move {
        val depth = 3
        val ratedMove = evaluate(depth, id)
        //Console.log(ratedMove)
        return ratedMove.move!!
    }

    private fun compareRatingsForPlayerId(playerId: Int, a: Rating, b: Rating) =
        a[playerId].value.compareTo(b[playerId].value)

    private fun compareRatingsForPlayerId2(playerId: Int, a: Rating, b: Rating): Int {
        TODO()
    }


    private fun calcRating() = ratingFrom(List(game.players.size) { calcScore(game.players[it]) })

    private fun evaluate(
        depth: Int,
        playerId: Int,
        ratingTillNow: Rating = calcRating()
    ): RatedMove {
        if (depth == 0 || game.players[playerId -1].hasWon())
            return RatedMove(null, ratingTillNow)

        val scoreTillNow = ratingTillNow[playerId]
        var bestRating = ratingTillNow
        val possibleMoves = game.board.possibleMovesOfPlayerNr(playerId)
        var bestMove = possibleMoves.first()
        for (move in possibleMoves) {
            game.board.doMove(move)

            val score = calcScore(game.players[playerId - 1])
            if (score.value > scoreTillNow.value) {
                val rating =
                    evaluate(
                        depth - 1,
                        game.board.nextPlayer(playerId),
                        bestRating.withNewScore(playerId, score)
                    ).rating

                val compare = compareRatingsForPlayerId(playerId, rating, bestRating)
                if (compare > 0) {
                    bestRating = rating
                    bestMove = move
                } else if (compare == 0) { /* TODO() */
                }
            }

            game.board.undoMove(move)
        }
        return RatedMove(bestMove, bestRating.decrement(playerId))
    }

}
