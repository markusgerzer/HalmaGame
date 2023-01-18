package halma

import com.soywiz.klock.*
import com.soywiz.klogger.Console
import com.soywiz.korio.async.delay
import com.soywiz.korio.async.launch
import kotlinx.coroutines.*


open class PlayerAI<T: Board>(
    override val id: Int,
    override val home: List<Int>
) : Player<T> {
    override lateinit var game: Game<T>

    val thinkingTime = 1_000.milliseconds

    protected lateinit var boardCopy: Board

    protected var calculated = 0
    protected var cutoffs = 0
    protected var filtered = 0

    override suspend fun makeMove(): Move {
        var depth = 1
        var move: Move = evaluate(depth)
        //Console.log(move)
        depth++

        val job = launch(Dispatchers.Default) {
            while (true) {
                move = evaluate(depth)
                //println(move)
                depth++
            }
        }
        delay(thinkingTime)
        job.cancelAndJoin()

        //println()
        return move
    }

    protected open fun logMove(depth: Int, bestResult: Int) {
        Console.log("round: ${game.round}   id: $id   calculated: $calculated   cutoffs: $cutoffs   filtered: $filtered   depth: $depth    result: $bestResult")
    }

    protected open suspend fun evaluate(depth: Int): Move {
        require(depth > 0)

        boardCopy = game.board.copyOf()

        calculated = 0
        filtered = 0
        cutoffs = 0

        var bestResult = Int.MIN_VALUE
        val bestMoves = mutableListOf<Move>()

        forAllPossibleMove(id) { move ->
            val result = evaluate(depth - 1, id % game.board.numberOfPlayers + 1, bestResult, Int.MAX_VALUE)
            if (result > bestResult) {
                bestResult = result
                bestMoves.clear()
                bestMoves.add(move)
            } else if (result == bestResult) {
                bestMoves.add(move)
            }
        }

        //logMove(depth, bestResult)
        if (bestMoves.isEmpty()) throw IllegalStateException("Can not move!")
        return bestMoves.random()
    }

    protected suspend fun evaluate(depth: Int, playerId: Int, alpha: Int, beta: Int): Int {
        if (willWin(playerId)) {
            return if (playerId == id) MAX_RATING else -MAX_RATING
        }
        if (depth == 0) return rateBoard()

        var a = alpha
        var b = beta

        return if (playerId == id) {
            var bestResult = Int.MIN_VALUE
            forAllPossibleMove(id) {
                val result = evaluate(depth - 1, playerId % game.board.numberOfPlayers + 1, a, b)
                bestResult = maxOf(result - 1, bestResult)
                a = maxOf(a, bestResult)
                if (b <= a) {
                    cutoffs++
                    return@forAllPossibleMove
                }
            }
            bestResult
        } else {
            var bestResult = Int.MAX_VALUE
            forAllPossibleMove(playerId) {
                val result = evaluate(depth - 1, playerId % game.board.numberOfPlayers + 1, a, b)
                bestResult = minOf(result + 1, bestResult)
                b = minOf(b, bestResult)
                if (b <= a) {
                    cutoffs++
                    return@forAllPossibleMove
                }
            }
            bestResult
        }
    }

    protected open fun hook(playerId: Int, vararg fieldIdx: Int) {}

    private suspend inline fun forAllPossibleMove(playerId: Int, block: (Move) -> Unit) {
        val rate0 = rateBoard()
        val possibleMoves = boardCopy.possibleMovesOfPlayerNr(playerId)

        for (move in possibleMoves) {
            yield()
            boardCopy.fields[move.startFieldIdx] = 0
            boardCopy.fields[move.destFieldIdx] = playerId
            hook(playerId, move.startFieldIdx, move.destFieldIdx)
            val rate1 = rateBoard()
            if (rate1 >= rate0) block(move)
            else filtered++
            boardCopy.fields[move.destFieldIdx] = 0
            boardCopy.fields[move.startFieldIdx] = playerId
            hook(playerId, move.startFieldIdx, move.destFieldIdx)
        }
    }

    protected open fun rateBoard(): Int {
        calculated++
        val pansNotAtHome = Array(game.players.size) { mutableListOf<Int>() }
        for ((idx, playerId) in boardCopy.fields.withIndex()) {
            if (playerId > 0 && idx !in game.players[playerId - 1].home) {
                pansNotAtHome[playerId - 1].add(idx)
            }
        }
        val freeHomeFields = game.players.map { player ->
            player.home.filterNot { boardCopy.fields[it] == player.id }
        }

        val distanceSum = fun(playerId: Int): Int {
            var distance = 0
            for (i in pansNotAtHome[playerId - 1].indices) {
                distance += boardCopy.fieldDistances[pansNotAtHome[playerId - 1][i]][freeHomeFields[playerId - 1][i]]
            }
            return MAX_RATING - distance
        }

        val maxPlayerRate = distanceSum(id)
        var minPlayerRate = 0
        for (i in 1..game.players.size) {
            if (i == id) continue
            minPlayerRate = maxOf(minPlayerRate, distanceSum(i))
        }

        return maxPlayerRate - minPlayerRate
    }

    private fun willWin(playerId: Int): Boolean {
        game.players[playerId - 1].home.forEach {
            if (boardCopy.fields[it] != playerId) return false
        }
        return true
    }

    companion object {
        const val MAX_RATING = 1000
    }
}
