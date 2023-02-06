package halma

import kotlin.test.*

class OperatorFunGetTest {
    @Test
    fun test_max() {
        val rating = Rating(ULong.MAX_VALUE)
        for (i in 1..6) {
            assertEquals(1023, rating[i])
        }
    }

    @Test
    fun test_min() {
        val rating = Rating(ULong.MIN_VALUE)
        for (i in 1..6) {
            assertEquals(0, rating[i])
        }
    }

    @Test
    fun text_Exception() {
        assertFailsWith(IndexOutOfBoundsException::class) {
            Rating(4UL)[7]
        }
        assertFailsWith(IndexOutOfBoundsException::class) {
            Rating(4UL)[0]
        }
    }
}

class FunWithNewScoreTest {
    @Test
    fun test_max() {
        var rating = Rating(ULong.MIN_VALUE)
        for (i in 1..6) {
            rating = rating.withNewScore(i, 1023)
        }
        assertEquals(
            Rating(0b0000_1111111111_1111111111_1111111111_1111111111_1111111111_1111111111UL),
            rating
        )
    }

    @Test
    fun test_min() {
        var rating = Rating(0b0000_1111111111_1111111111_1111111111_1111111111_1111111111_1111111111UL)
        for (i in 1..6) {
            rating = rating.withNewScore(i, 0)
        }
        assertEquals(Rating(0UL), rating
        )
    }

    @Test
    fun text_Exception() {
        assertFailsWith(IndexOutOfBoundsException::class) {
            Rating(4UL).withNewScore(7, 1)
        }
        assertFailsWith(IndexOutOfBoundsException::class) {
            Rating(4UL).withNewScore(0, 1)
        }
    }

    @Test
    fun test_overflow_do_not_affect_other_score () {
        val r =
            Rating(0UL).withNewScore(1, 0b1111_11111_1111_1111)
        assertEquals(r[1], 1023)
        assertEquals(r[2], 0)
    }

    @Test
    fun test_max_min_Score() {
        assertEquals(0, Rating.MIN_SCORE)
        assertEquals(1023, Rating.MAX_SCORE)
    }
}

class FunRatingOfTest {
    @Test
    fun test1() {
        assertEquals(Rating(), ratingOf())
        assertEquals(
            Rating(0b1111111111_1111111111_1111111111_1111111111_1111111111_1111111111UL),
            ratingOf(1023, 1023, 1023, 1023, 1023, 1023)
        )
        assertEquals(
            Rating(0b0000011111_0000011111_0000011111_0000011111_0000011111_0000011111UL),
            ratingOf(31, 31, 31, 31, 31, 31)
        )
    }
}

class FunWorstRatingForTest {
    @Test
    fun test1() {
        for (playerId in 1..6) {
            val rating = worstRatingFor(playerId)
            for (i in 1..6) {
                if (playerId == i) assertEquals(0, rating[i])
                else assertEquals(1023, rating[i])
            }
        }
    }
}

class FunWithDecrementTest {
    @Test
    fun test1() {
        var rating = ratingOf(1022)
        rating = rating.decrement(1)
        assertEquals(1021, rating[1])

        rating = ratingOf(0)
        rating = rating.decrement(1)
        assertEquals(0, rating[1])
        assertEquals(0, rating[2])
    }

    @Test
    fun testAll() {
        val scores = IntArray(6) { Rating.MAX_SCORE }
        var rating = ratingOf(*scores)
        for (playerId in 1..6) {
            for (s in Rating.MAX_SCORE downTo 0) {
                assertEquals(s, rating[playerId])
                rating = rating.decrement(playerId)
            }
            assertEquals(0, rating[playerId])
        }
    }
}
