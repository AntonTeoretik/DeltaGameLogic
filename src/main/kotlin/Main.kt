import com.delta.*

fun main() {

    val board = GameBoard(10)
    board.setCell(1, 1, PlayerID.PLAYER_3)

    val game_logic = GameLogic(GameBoard(10))
    println(game_logic.toJson())
}