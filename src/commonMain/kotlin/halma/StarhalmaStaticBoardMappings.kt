package halma

import kotlin.math.abs

object StarhalmaStaticBoardMappings: StaticBoardMappings {
    override val fieldsSize = 121
    override val directionSize = 6
    override val directions = 0 until directionSize

    val matrix: List<List<Int>>
    init{
        val m = List(17) { IntArray(17) { -1 } }
        var idx = 0
        var y = 0

        repeat(4) {
            for (x in 4 .. 4 + it) { m[y][x] = idx++ }
            y++
        }
        repeat(4) {
            for (x in it..12) { m[y][x] = idx++ }
            y++
        }
        repeat(5) {
            for (x in 4..(12 + it)) { m[y][x] = idx++ }
            y++
        }
        repeat(4) {
            for (x in (9 + it)..12) { m[y][x] = idx++ }
            y++
        }

        matrix = m.map { it.toList() }
    }

    val matrixCoordinates: List<Coordinate>
    init {
        val coordinates = Array(fieldsSize) { Coordinate(-1, -1) }
        for (y in matrix.indices) {
            for (x in matrix[y].indices) {
                val idx = matrix[y][x]
                if (idx >= 0) {
                    coordinates[idx] = Coordinate(x, y)
                }
            }
        }
        matrixCoordinates = coordinates.toList()
    }

    override val fieldNeighbors: List<List<Int>>
    init {
        val neighbors =
            List(fieldsSize) { MutableList(directionSize) { -2 } }

        for (y in matrix.indices) {
            for (x in matrix[y].indices) {
                val idx =  matrix[y][x]
                if (idx >= 0) {
                    if (y > 0) neighbors[idx][0] = matrix[y-1][x]
                    if (x < matrix[y].lastIndex) neighbors[idx][1] = matrix[y][x+1]
                    if (y < matrix.lastIndex && x < matrix[y+1].lastIndex) neighbors[idx][2] = matrix[y+1][x+1]
                    if (y < matrix.lastIndex) neighbors[idx][3] = matrix[y+1][x]
                    if (x > 0) neighbors[idx][4] = matrix[y][x-1]
                    if (x > 0 && y > 0) neighbors[idx][5] = matrix[y-1][x-1]
                }
            }
        }

        fieldNeighbors = neighbors
    }

    override val fieldVarieties: List<Int>
    init {
        val varieties = IntArray(fieldsSize) { -1 }

        for (v in 0 until 4) {
            for (y in (v and 1) until matrix.size step 2) {
                for (x in (v shr 1) until matrix[y].size step 2) {
                    val idx = matrix[y][x]
                    if (idx >= 0) varieties[idx] = v
                }
            }
        }

        fieldVarieties = varieties.toList()
    }

    override val home = listOf(
        listOf(  0,   1,   2,   3,   4,   5,   6,   7,   8,   9),   // home 1
        listOf( 19,  20,  21,  22,  32,  33,  34,  44,  45,  55),   // home 2
        listOf( 74,  84,  85,  95,  96,  97, 107, 108, 109, 110),   // home 3
        listOf(111, 112, 113, 114, 115, 116, 117, 118, 119, 120),   // home 4
        listOf( 65,  75,  76,  86,  87,  88,  98,  99, 100, 101),   // home 5
        listOf( 10,  11,  12,  13,  23,  24,  25,  35,  36,  46)    // home 6
    )

    override val extendedHome = listOf(
        listOf(  0,   1,   2,   3,   4,   5,   6,   7,   8,   9,  14,  15,  16,  17,  18),   // home 1
        listOf( 18,  19,  20,  21,  22,  31,  32,  33,  34,  43,  44,  45,  54,  55,  64),   // home 2
        listOf( 64,  73,  74,  83,  84,  85,  94,  95,  96,  97, 106, 107, 108, 109, 110),   // home 3
        listOf(102, 103, 104, 105, 106, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120),   // home 4
        listOf( 56,  65,  66,  75,  76,  77,  86,  87,  88,  89,  98,  99, 100, 101, 102),   // home 5
        listOf( 10,  11,  12,  13,  14,  23,  24,  25,  26,  35,  36,  37,  46,  47,  56)    // home 6
    )

    override val maxNumberOfPlayers = (1..6).toList()

    override val fieldDistances =
        List(fieldsSize) { idx1 ->
            List(fieldsSize) { idx2 ->
                distance(idx1, idx2)
            }
        }

    override val idToHomeMaps = listOf(
        mapOf(1 to extendedHome[3]),
        mapOf(1 to extendedHome[3], 2 to extendedHome[0]),
        mapOf(1 to extendedHome[3], 2 to extendedHome[5], 3 to extendedHome[1]),
        mapOf(1 to home[3], 2 to home[4], 3 to home[0], 4 to home[1]),
        mapOf(1 to home[3], 2 to home[4], 3 to home[5], 4 to home[0], 5 to home[1]),
        mapOf(1 to home[3], 2 to home[4], 3 to home[5], 4 to home[0], 5 to home[1], 6 to home[2])
    )

    override val idToStartMaps = listOf(
        mapOf(1 to extendedHome[0]),
        mapOf(1 to extendedHome[0], 2 to extendedHome[3]),
        mapOf(1 to extendedHome[0], 2 to extendedHome[2], 3 to extendedHome[4]),
        mapOf(1 to home[0], 2 to home[1], 3 to home[3], 4 to home[4]),
        mapOf(1 to home[0], 2 to home[1], 3 to home[2], 4 to home[3], 5 to home[4]),
        mapOf(1 to home[0], 2 to home[1], 3 to home[2], 4 to home[3], 5 to home[4], 6 to home[5])
    )

    fun distance(idx1: Int, idx2: Int): Int {
        val (x1, y1) = matrixCoordinates[idx1]
        val (x2, y2) = matrixCoordinates[idx2]
        return if ((x1 < x2 && y1 < y2) || (x1 > x2 && y1 > y2))
            maxOf(abs(x1 - x2), abs(y1 - y2))
        else abs(x1 - x2) + abs(y1 - y2)
    }
}