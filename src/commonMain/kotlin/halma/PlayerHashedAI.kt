package halma

import com.soywiz.kds.CacheMap
import com.soywiz.klock.*
import com.soywiz.klogger.Console
import com.soywiz.korio.async.delay
import com.soywiz.korio.async.launch
import kotlinx.coroutines.*
import kotlin.random.Random


class PlayerHashedAI<T: Board>(
    override val id: Int,
    override val home: List<Int>
) : Player<T> {
    override lateinit var game: Game<T>

    val thinkingTime = 500.milliseconds

    private lateinit var boardCopy: Board

    private var calculated = 0
    private var cutoffs = 0
    private var hashed = 0

    val zobristTable by lazy {
        LongArray(game.board.numberOfPlayers * game.board.fields.size) { Random.nextLong() }
    }
    var zobristHash = 0L
    val cachedRates = CacheMap<Long, Int>(100_000)

    fun initZobristHash() {
        zobristHash = 0L
        for ((i, playerId) in game.board.fields.withIndex()) {
            if (playerId > 0) updateZobristHash(playerId, i)
        }
    }

    fun updateZobristHash(playerId: Int, fieldIdx: Int) {
        val idx = (playerId - 1) * game.board.fields.size + fieldIdx
        zobristHash = zobristHash xor zobristTable[idx]
    }

    override suspend fun makeMove(): Move {
        initZobristHash()

        var depth = 1
        var move: Move = evaluate(depth)
        Console.log(move)
        depth++

        val job = launch(Dispatchers.Default) {
            while (true) {
                move = evaluate(depth)
                println(move)
                depth++
            }
        }
        delay(thinkingTime)
        job.cancelAndJoin()

        println()
        return move
    }

    private suspend fun evaluate(depth: Int): Move {
        require(depth > 0)

        boardCopy = game.board.copyOf()

        calculated = 0
        cutoffs = 0
        hashed = 0

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

        Console.log("round: ${game.round}   id: $id   calculated: $calculated   cutoffs: $cutoffs   hashed: $hashed   cached: ${cachedRates.size}   depth: $depth    result: $bestResult")
        //val hashTimeD = hashTime / hashed
        //val rateTimeD = rateTime / calculated
        //Console.log("hashTimeD: $hashTimeD   rateTimeD: $rateTimeD")
        if (bestMoves.isEmpty()) throw IllegalStateException("Can not move!")
        return bestMoves.random()
    }

    private suspend fun evaluate(depth: Int, playerId: Int, alpha: Int, beta: Int): Int {
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

    private suspend inline fun forAllPossibleMove(playerId: Int, block: (Move) -> Unit) {
        val possibleMoves = boardCopy.possibleMovesOfPlayerNr(playerId)

        for (move in possibleMoves) {
            yield()
            boardCopy.fields[move.startFieldIdx] = 0
            boardCopy.fields[move.destFieldIdx] = playerId
            updateZobristHash(playerId, move.startFieldIdx)
            updateZobristHash(playerId, move.destFieldIdx)
            block(move)
            boardCopy.fields[move.destFieldIdx] = 0
            boardCopy.fields[move.startFieldIdx] = playerId
            updateZobristHash(playerId, move.startFieldIdx)
            updateZobristHash(playerId, move.destFieldIdx)
        }
    }

    /*
    var hashTime = TimeSpan.ZERO
    var rateTime = TimeSpan.ZERO
    private fun rateBoard(): Int {
        val (r, t) = measureTimeWithResult { boardRates[zobristHash] }
        return if (r != null) {
            hashTime += t
            hashed++
            r
        } else {
            val (r2, t) = measureTimeWithResult { rate() }
            rateTime += t
            r2
        }
    }
     */

    private fun rateBoard(): Int {
        val rating = cachedRates[zobristHash] ?: return rate()
        hashed++
        return rating
    }

    private fun rate(): Int {
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

        val distanceSum = fun (playerId: Int): Int {
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

        val rate = maxPlayerRate - minPlayerRate
        cachedRates[zobristHash] = rate
        return rate
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