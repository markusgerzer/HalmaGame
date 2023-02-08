package halma

import kotlin.test.*

class OperatorFunGetTest {
    @Test
    fun test_max() {
        val rating = Rating(ULong.MAX_VALUE)
        for (i in 1..6) {
            assertEquals(Score(1023), rating[i])
        }
    }

    @Test
    fun test_min() {
        val rating = Rating(ULong.MIN_VALUE)
        for (i in 1..6) {
            assertEquals(Score(0), rating[i])
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
            rating = rating.withNewScore(i, Score(1023))
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
            rating = rating.withNewScore(i, Score(0))
        }
        assertEquals(Rating(0UL), rating
        )
    }

    @Test
    fun text_Exception() {
        assertFailsWith(IndexOutOfBoundsException::class) {
            Rating(4UL).withNewScore(7, Score(1))
        }
        assertFailsWith(IndexOutOfBoundsException::class) {
            Rating(4UL).withNewScore(0, Score(1))
        }
    }

    @Test
    fun test_overflow_do_not_affect_other_score () {
        assertFailsWith(IllegalArgumentException::class) {
            Rating(0UL).withNewScore(1, Score(0b1111_11111_1111_1111))
        }
        //val r = Rating(0UL).withNewScore(1, Score(0b1111_11111_1111_1111))
        //assertEquals(r[1], Score(1023))
        //assertEquals(r[2], Score(0))
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
            ratingOf(
                Score(1023), Score(1023), Score(1023),
                Score(1023), Score(1023), Score(1023)
            )
        )
        assertEquals(
            Rating(0b0000011111_0000011111_0000011111_0000011111_0000011111_0000011111UL),
            ratingOf(
                Score(31), Score(31), Score(31),
                Score(31), Score(31), Score(31)
            )
        )
    }
}

class FunWorstRatingForTest {
    @Test
    fun test1() {
        for (playerId in 1..6) {
            val rating = worstRatingFor(playerId)
            for (i in 1..6) {
                if (playerId == i) assertEquals(Score(0), rating[i])
                else assertEquals(Score(1023), rating[i])
            }
        }
    }
}

class FunWithDecrementTest {
    @Test
    fun test1() {
        var rating = ratingOf(Score(1022))
        rating = rating.decrement(1)
        assertEquals(Score(1021), rating[1])

        rating = ratingOf(Score(0))
        rating = rating.decrement(1)
        assertEquals(Score(0), rating[1])
        assertEquals(Score(0), rating[2])
    }

    @Test
    fun testAll() {
        val scores = Array(6) { Score(Score.MAX_VALUE) }
        var rating = ratingOf(*scores)
        for (playerId in 1..6) {
            for (s in Rating.MAX_SCORE downTo 0) {
                assertEquals(Score(s), rating[playerId])
                rating = rating.decrement(playerId)
            }
            assertEquals(Score(0), rating[playerId])
        }
    }
}

class ScoreTest {
    @Test
    fun initRequirementsTest() {
        assertFailsWith(IllegalArgumentException::class) { Score(-1) }
        assertFailsWith(IllegalArgumentException::class) { Score(1024) }
    }
}
