package halma

sealed class Move {
    abstract val startFieldIdx: Int
    abstract val destFieldIdx: Int

    data class Walk(
        override val startFieldIdx: Int,
        override val destFieldIdx: Int
        ): Move()

    data class Jump(
        override val startFieldIdx: Int,
        val destFieldIdxList: List<Int>,
        override val destFieldIdx: Int = destFieldIdxList.last()
        ): Move()
}








