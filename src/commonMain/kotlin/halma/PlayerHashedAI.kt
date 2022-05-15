package halma

import com.soywiz.kds.CacheMap
import com.soywiz.klogger.Console
import kotlin.random.Random


class PlayerHashedAI<T: Board>(
    id: Int,
    home: List<Int>
) : PlayerAI<T>(id, home) {
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

    override fun hook(playerId: Int, vararg fieldIdx: Int) {
        fieldIdx.forEach { updateZobristHash(playerId, it) }
    }

    override suspend fun makeMove(): Move {
        initZobristHash()
        return super.makeMove()
    }

    override fun logMove(depth: Int, bestResult: Int) {
        Console.log("round: ${game.round}   id: $id   calculated: $calculated   cutoffs: $cutoffs   filtered: $filtered   hashed: $hashed   cached: ${cachedRates.size}   depth: $depth    result: $bestResult")
    }

    override suspend fun evaluate(depth: Int): Move {
        hashed = 0
        return super.evaluate(depth)
    }

    override fun rateBoard(): Int {
        return cachedRates[zobristHash].also { hashed++ } ?: super.rateBoard().also { cachedRates[zobristHash] = it }
    }
}