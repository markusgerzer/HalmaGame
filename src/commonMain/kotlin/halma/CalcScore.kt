package halma

interface CalcScore {
    fun calcScore(player: Player<*>): Score
    fun adjustScore(player: Player<*>, score: Score, move: Move): Score
}

interface DistanceToHome : CalcScore {
    override fun calcScore(player: Player<*>): Score {
        return with(player) {
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

    override fun adjustScore(player: Player<*>, score: Score, move: Move) = calcScore(player)
    /*
    override fun adjustScore(player: Player<*>, score: Score, move: Move): Score {
        val board =  player.game.board
        val firstFreeHomeField = player.home.first {
            board.fields[it] != player.id
        }
        val distance0 = board.fieldDistances[move.startFieldIdx][firstFreeHomeField]
        val distance1 = board.fieldDistances[move.destFieldIdx][firstFreeHomeField]
        val distanceDelta = distance0 - distance1
        return Score(score.value + distanceDelta)
    }*/
}

/*
@ThreadLocal
object HomeDistance : CalcScore {
    private const val ARRAY_SIZE = 7

    val rating get() = _rating
    private var _rating = Rating()

    private val pans = Array(ARRAY_SIZE) { mutableListOf<Int>() }
    private val pansNotAtHome = Array(ARRAY_SIZE) { listOf<Int>() }
    private val freeHomeFields = Array(ARRAY_SIZE) { listOf<Int>() }

    override fun calcScore(player: Player<*>) = calcRating(player)[player.id]


    fun calcRating(player: Player<*>): Rating {
        val game = player.game
        val board = game.board
        val fields = board.fields

        for (i in 0 until ARRAY_SIZE) { pans[i].clear() }

        for (idx in fields.indices) {
            if (fields[idx] > 0) pans[fields[idx] - 1].add(idx)
        }

        var newRating = Rating()
        for (playerId in 1 .. game.players.size) {
            pansNotAtHome[playerId - 1] = pans[playerId - 1].filterNot { it in game.players[playerId - 1].home }
            freeHomeFields[playerId - 1] = game.players[playerId - 1].home.filterNot { fields[it] == playerId }

            var distance = 0
            for (i in pansNotAtHome.indices) {
                val src = pansNotAtHome[playerId - 1][i]
                val dest = freeHomeFields[playerId - 1][i]
                distance += board.fieldDistances[src][dest]
            }
            val score = Score(Rating.MAX_SCORE - distance)
            newRating = rating.withNewScore(playerId, score)
        }

        _rating = newRating
        return rating
    }

    override fun adjustScore(player: Player<*>, score: Score, move: Move) = calcScore(player)
}*/
