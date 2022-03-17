package halma

import gui.BoardGui
import gui.StarhalmaBoardGui
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction3


fun <T: Board>makeBoard(Board: () -> T, numberOfPlayers: Int) = Board().apply {
    require(numberOfPlayers in possibleNumberOfPlayers)
    for ((value, idxList) in idToStartMaps[numberOfPlayers - 1]) {
        for (idx in idxList) fields[idx] = value
    }
}

/*
fun makeGame(
    Board: () -> Board,
    playerClasses: List<(Int, Board, List<Int>) -> Player>,
    block: Game.()->Unit = { }
): Game {
    makeBoard(Board, playerClasses.size).apply {
        val players = playerClasses.mapIndexed { i, Player ->
            Player(i + 1, this, idToHomeMaps[playerClasses.size - 1][i + 1]!!)
        }
        return Game(this, players, block)
    }
}*/

/*
fun <D: Board, B: Board>makeGame(
    decorator: (B) -> D,
    Board: () -> B,
    playerClasses: List<(Int, Board, List<Int>) -> Player>,
    block: Game.()->Unit = { }
): Game {
    makeBoard(Board, playerClasses.size).apply {
        val boardGui = decorator(this)
        val players = playerClasses.mapIndexed { i, Player ->
            Player(i + 1, this, idToHomeMaps[playerClasses.size - 1][i + 1]!!)
        }
        return Game(boardGui, players, block)
    }
}*/

fun <D: BoardGui, B: Board>makeGame(
    decorator: (B) -> D,
    Board: () -> B,
    playerClasses: List<(Int, D, List<Int>) -> Player<D>>,//List<KFunction3<Int, BoardGui, List<Int>, Player<out Board>>>,
    block: Game<D>.()->Unit = { }
): Game<D> {
    makeBoard(Board, playerClasses.size).apply {
        val boardGui = decorator(this)
        val players = playerClasses.mapIndexed { i, Player ->
            Player(i + 1, boardGui, idToHomeMaps[playerClasses.size - 1][i + 1]!!)
        }
        return Game(boardGui, players, block)
    }
}