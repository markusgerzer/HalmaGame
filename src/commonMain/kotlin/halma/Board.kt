package halma

interface Board : StaticBoardMappings {
    val numberOfPlayers: Int
    val fields: IntArray

    fun copyOf(): Board
    fun possibleMoves(startIdx: Int): Sequence<Move>
    fun possibleMovesOfPlayerNr(id: Int): Sequence<Move>
    fun isValidMove(move: Move): Boolean
    suspend fun move(move: Move)

    fun validMoveOfOrNull(idxList: List<Int>) : Move? {
        if (idxList.size < 2) return null
        val moves = buildList {
            for (move in possibleMoves(idxList.first())) {
                if (move.destFieldIdx == idxList.last()) {
                    when (move) {
                        is Move.Walk -> return move
                        is Move.Jump -> add(move)
                    }
                }
            }
        }
        return moves.minByOrNull { it.destFieldIdxList.size }
    }

    fun hookBeforeMove(player: Player<out Board>) {}
    suspend fun hookGameEnd(winner: Player<out Board>) { println(winner) }
}

interface StaticBoardMappings {
    val fieldsSize: Int
    val directionSize: Int
    val directions: IntRange
    val home: List<List<Int>>
    val extendedHome: List<List<Int>>
    val fieldVarieties: List<Int>
    val fieldNeighbors: List<List<Int>>
    val fieldDistances: List<List<Int>>
    val maxNumberOfPlayers: List<Int>
    val idToHomeMaps: List<Map<Int, List<Int>>>
    val idToStartMaps: List<Map<Int, List<Int>>>
}

