package halma

import kotlin.jvm.*

private const val PLAYER_1_MASK = 0b0000_0000000000_0000000000_0000000000_0000000000_0000000000_1111111111UL
private const val PLAYER_2_MASK = 0b0000_0000000000_0000000000_0000000000_0000000000_1111111111_0000000000UL
private const val PLAYER_3_MASK = 0b0000_0000000000_0000000000_0000000000_1111111111_0000000000_0000000000UL
private const val PLAYER_4_MASK = 0b0000_0000000000_0000000000_1111111111_0000000000_0000000000_0000000000UL
private const val PLAYER_5_MASK = 0b0000_0000000000_1111111111_0000000000_0000000000_0000000000_0000000000UL
private const val PLAYER_6_MASK = 0b0000_1111111111_0000000000_0000000000_0000000000_0000000000_0000000000UL

private const val PLAYER_1_U_MASK = 0b0000_1111111111_1111111111_1111111111_1111111111_1111111111_0000000000UL
private const val PLAYER_2_U_MASK = 0b0000_1111111111_1111111111_1111111111_1111111111_0000000000_1111111111UL
private const val PLAYER_3_U_MASK = 0b0000_1111111111_1111111111_1111111111_0000000000_1111111111_1111111111UL
private const val PLAYER_4_U_MASK = 0b0000_1111111111_1111111111_0000000000_1111111111_1111111111_1111111111UL
private const val PLAYER_5_U_MASK = 0b0000_1111111111_0000000000_1111111111_1111111111_1111111111_1111111111UL
private const val PLAYER_6_U_MASK = 0b0000_0000000000_1111111111_1111111111_1111111111_1111111111_1111111111UL

private const val SCORE_MASK = 0b11_1111_1111

@JvmInline
value class Score(val value: Int) {
    init {
        require(value in MIN_VALUE..MAX_VALUE) {
            "$value not in $MIN_VALUE..$MAX_VALUE"
        }
    }

    companion object {
        const val MIN_VALUE = Rating.MIN_SCORE
        const val MAX_VALUE = Rating.MAX_SCORE
    }
}

fun ratingOf() = ratingOf<Score>()
fun <T: Score>ratingOf(vararg scores: T) = ratingFrom(scores.toList())

fun ratingFrom(scores: List<Score>): Rating {
    var rating = Rating()
    for (i in scores.indices)
        rating = rating.withNewScore(i + 1, scores[i])
    return rating
}

fun worstRatingFor(playerId: Int): Rating = when (playerId) {
    1 -> Rating(PLAYER_1_U_MASK)
    2 -> Rating(PLAYER_2_U_MASK)
    3 -> Rating(PLAYER_3_U_MASK)
    4 -> Rating(PLAYER_4_U_MASK)
    5 -> Rating(PLAYER_5_U_MASK)
    6 -> Rating(PLAYER_6_U_MASK)
    else -> throw IndexOutOfBoundsException()
}


@JvmInline
value class Rating(private val data: ULong = 0UL) {
    companion object {
        const val MIN_SCORE = 0
        const val MAX_SCORE = SCORE_MASK
    }

    operator fun get(playerId: Int) = Score(
        when (playerId) {
            1 -> data and PLAYER_1_MASK
            2 -> data and PLAYER_2_MASK shr 10
            3 -> data and PLAYER_3_MASK shr 20
            4 -> data and PLAYER_4_MASK shr 30
            5 -> data and PLAYER_5_MASK shr 40
            6 -> data and PLAYER_6_MASK shr 50
            else -> throw IndexOutOfBoundsException()
        }.toInt()
    )

    fun decrement(playerId: Int): Rating {
        if (this[playerId] == Score(Score.MIN_VALUE)) return this
        var data1 = data
        var mask = when (playerId) {
            1 -> 0b1UL
            2 -> 0b1_0000000000UL
            3 -> 0b1_0000000000_0000000000UL
            4 -> 0b1_0000000000_0000000000_0000000000UL
            5 -> 0b1_0000000000_0000000000_0000000000_0000000000UL
            6 -> 0b1_0000000000_0000000000_0000000000_0000000000_0000000000UL
            else -> throw IndexOutOfBoundsException()
        }
        while (data1 and mask == 0UL) {
            data1 = data1 xor mask
            mask = mask shl 1
        }
        data1 = data1 xor mask
        return Rating(data1)
    }

    fun withNewScore(playerId: Int, score: Score): Rating {
        val mask = (score.value and SCORE_MASK).toULong()
        return when (playerId) {
            1 -> Rating(data and PLAYER_1_U_MASK or mask)
            2 -> Rating(data and PLAYER_2_U_MASK or (mask shl 10))
            3 -> Rating(data and PLAYER_3_U_MASK or (mask shl 20))
            4 -> Rating(data and PLAYER_4_U_MASK or (mask shl 30))
            5 -> Rating(data and PLAYER_5_U_MASK or (mask shl 40))
            6 -> Rating(data and PLAYER_6_U_MASK or (mask shl 50))
            else -> throw IndexOutOfBoundsException()
        }
    }

    override fun toString() = List(6) { this[6 - it] }.toString()
}
