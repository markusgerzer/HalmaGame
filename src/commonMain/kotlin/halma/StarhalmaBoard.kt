package halma

fun starhalmaBoard(numberOfPlayers: Int) = StarhalmaBoard(numberOfPlayers)

class StarhalmaBoard(
    override val numberOfPlayers: Int,
    override val fields: IntArray = IntArray(StarhalmaStaticBoardMappings.fieldsSize)
) : Board,
    StaticBoardMappings by StarhalmaStaticBoardMappings
{
    override fun copyOf() = StarhalmaBoard(numberOfPlayers, fields.copyOf())

    private fun possibleWalks(startIdx: Int) = sequence {
        for (destIdx in fieldNeighbors[startIdx]) {
            if (destIdx >= 0 && fields[destIdx] == 0)
                yield(Move.Walk(startIdx, destIdx))
        }
    }

    private fun possibleJumps(startIdx: Int, visitedIdx: List<Int> = listOf(startIdx)): Sequence<Move.Jump> = sequence {
        for (direction in directions) {
            val overJumpedIdx = fieldNeighbors[startIdx][direction]
            if (overJumpedIdx >= 0 && fields[overJumpedIdx] > 0) {
                val destIdx = fieldNeighbors[overJumpedIdx][direction]
                if (destIdx >= 0 && fields[destIdx] == 0) {
                    if (destIdx !in visitedIdx) {
                        yield(Move.Jump(startIdx, listOf(destIdx), destIdx))

                        val furtherJumps = possibleJumps(destIdx, visitedIdx + destIdx)
                        val furtherMoves = furtherJumps.map {
                            Move.Jump(startIdx, listOf(destIdx) + it.destFieldIdxList, it.destFieldIdx)
                        }
                        yieldAll(furtherMoves)
                    }
                }
            }
        }
    }

    override fun possibleMoves(startIdx: Int): Sequence<Move> {
        require(fields[startIdx] > 0)
        return possibleJumps(startIdx) + possibleWalks(startIdx)
    }

    override fun possibleMovesOfPlayerNr(id: Int) =  sequence<Move> {
        val startIdxList = fields.indices.filter { fields[it] == id }
        for (startIdx in startIdxList) {
            yieldAll(possibleMoves(startIdx))
        }
    }

    private fun isValidWalk(walk: Move.Walk) =
        walk.destFieldIdx in fieldNeighbors[walk.startFieldIdx]
                && fields[walk.destFieldIdx] == 0

    private fun isValidOneStepJump(startIdx: Int, destIdx: Int): Boolean {
        if (fields[destIdx] != 0) return false
        for (direction in directions) {
            val overJumpedIdx = fieldNeighbors[startIdx][direction]
            if (overJumpedIdx >=0 &&
                fields[overJumpedIdx] > 0 &&
                fieldNeighbors[overJumpedIdx][direction] == destIdx
            ) return true
        }
        return false
    }

    private fun isValidJump(jump: Move.Jump): Boolean {
        var idx1 = jump.startFieldIdx
        for (idx2 in jump.destFieldIdxList) {
            if (!isValidOneStepJump(idx1, idx2)) return false
            idx1 = idx2
        }
        return true
    }

    override fun isValidMove(move: Move): Boolean {
        require(fields[move.startFieldIdx] > 0) { "No pawn at index ${move.startFieldIdx}" }
        return when (move) {
            is Move.Walk -> isValidWalk(move)
            is Move.Jump -> isValidJump(move)
        }
    }

    override suspend fun move(move: Move) {
        if (isValidMove(move)) {
            fields[move.destFieldIdx] = fields[move.startFieldIdx]
            fields[move.startFieldIdx] = 0
        } else throw IllegalArgumentException("" +
                "halma.Move is not valid!\n" +
                "fields: ${fields.toList()}\n" +
                "move: $move")
    }
}

