package halma

class RatedMove (val move: Move?, val rating: Rating) {
    override fun toString() = "$move, $rating"
}


open class PlayerAI2<T: Board>(
    override val id: Int,
    override val home: List<Int>
) : Player<T>, DistanceToHome {
    override lateinit var game: Game<T>

    override suspend fun makeMove(): Move {
        val depth = 2
        val ratedMove = evaluate(depth, id)
        //Console.log(ratedMove)
        return ratedMove.move!!
    }

    private fun compareRatingsForPlayerId(playerId: Int, a: Rating, b: Rating) =
        a[playerId].value.compareTo(b[playerId].value)

    private fun compareRatingsForPlayerId2(playerId: Int, a: Rating, b: Rating): Int {
        TODO()
    }


    private fun calcRating() = ratingFrom(List(game.board.numberOfPlayers) { calcScore(game.playerById(it + 1)) })

    private fun evaluate(
        depth: Int,
        playerId: Int,
        ratingTillNow: Rating = calcRating()
    ): RatedMove {
        if (depth == 0 || game.playerById(playerId).hasWon())
            return RatedMove(null, ratingTillNow)

        val scoreTillNow = ratingTillNow[playerId]
        var bestRating = ratingTillNow
        val possibleMoves = game.board.possibleMovesOfPlayerNr(playerId)
        var bestMove = possibleMoves.first()
        for (move in possibleMoves) {
            game.board.doMove(move)

            //val score = adjustScore(game.players[playerId - 1], scoreTillNow, move)
            val score = calcScore(game.playerById(playerId))
            //println(score)
            if (score.value > scoreTillNow.value) {
                //print(depth)
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
