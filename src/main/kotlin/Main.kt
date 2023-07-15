import com.delta.*

fun main() {
    val gameLogic = GameLogic(GameBoard(5))
    gameLogic.removeUnstableCells()
}