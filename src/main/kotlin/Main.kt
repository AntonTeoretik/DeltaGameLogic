import com.delta.*

fun main() {
    val gameLogic = GameLogic(GameBoard(5))

//    gameLogic.board.setCell(0,1, PlayerID.PLAYER_1)
//    println(gameLogic.toJson())
//
//    println(gameLogic.board.getAllUnstableCells(PlayerID.PLAYER_1))

    gameLogic.removeUnstableCells()

}