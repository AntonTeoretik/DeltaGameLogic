package com.delta

import com.google.gson.Gson
import kotlin.math.max


class GameLogic(private val board: GameBoard) {
    private val playersResources = PlayerID.values().associateWith { 0 }.toMutableMap()
    private val playersLoose = PlayerID.values().associateWith { false }.toMutableMap()

    private var currentPlayer = PlayerID.PLAYER_1

    private var gameIsOver = false

    init {
        // Place one cell for each player in the corners
        board.setCell(0, 0, PlayerID.PLAYER_1)
        board.setCell(0, board.size - 1, PlayerID.PLAYER_2)
        board.setCell(board.size - 1, 0, PlayerID.PLAYER_3)
        board.setCell(board.size - 1, board.size - 1, PlayerID.PLAYER_4)

        playersResources[PlayerID.PLAYER_1] = 1

    }

    fun getCurrentPlayer(): PlayerID {
        return currentPlayer
    }

    fun getPlayerResources() = playersResources

    fun getNextPlayer(): PlayerID {
        return when (currentPlayer) {
            PlayerID.PLAYER_1 -> PlayerID.PLAYER_2
            PlayerID.PLAYER_2 -> PlayerID.PLAYER_3
            PlayerID.PLAYER_3 -> PlayerID.PLAYER_4
            PlayerID.PLAYER_4 -> PlayerID.PLAYER_1
        }
    }

    // Game checks
    fun isProductive(row: Int, col: Int, player: PlayerID): Boolean {
        if (board.getCell(row, col) != player) return false

        return board.countFriendlyNeighbors(row, col, player) == 1 &&
                board.countEnemyNeighbors(row, col, player) == 0
    }

    fun isProductive(row: Int, col: Int): Boolean {
        val player = board.getCell(row, col) ?: return false
        return isProductive(row, col, player)
    }

    fun isBaseCell(row: Int, col: Int): Boolean = board.isBaseCell(row, col)

    fun removeUnstableCells() {
        PlayerID.values()
            .flatMap(board::getAllUnstableCells)
            .forEach { board.freeCell(it.first, it.second) }
    }

    private fun countCellsWithCondition(condition : (Int, Int) -> Boolean): Int {
        var count = 0
        for (i in 0 until board.size)
            for (j in 0 until board.size)
                if (condition(i, j)) count += 1

        return count
    }

    fun countProductiveCells(player: PlayerID): Int =
        countCellsWithCondition { i, j -> isProductive(i, j, player) }

    fun countFreeCells() : Int =
        countCellsWithCondition { i, j -> board.getCell(i, j) == null}

    fun countFriendlyCells(player: PlayerID) : Int =
        countCellsWithCondition { i, j -> board.getCell(i, j) == player }

    fun getDefencePoints(row: Int, col: Int): Int {
        val player = board.getCell(row, col) ?: return 1
        return max(1, board.countFriendlyNeighborsCorners(row, col, player) - 1)
    }

    fun isValidCellToPlace(row: Int, col: Int, player: PlayerID) : Boolean {
        return player == currentPlayer &&
                board.countFriendlyNeighbors(row, col, player) > 0 &&
                board.getCell(row, col) != player &&
                board.isValidCoordinate(row, col) &&
                playersResources[player] != null
    }

    fun getCell(row: Int, col: Int): PlayerID? {
        return board.getCell(row, col)
    }

    fun getBoardSize(): Int {
        return board.size
    }

    fun placeCell(row: Int, col: Int, player: PlayerID): Boolean {
        if (gameIsOver) return false
        if (!isValidCellToPlace(row, col, player)) return false

        val def = getDefencePoints(row, col)
        if (def > playersResources[player]!!) return false

        // Update the game
        playersResources[player] = playersResources[player]!! - def
        board.setCell(row, col, player)
        removeUnstableCells()

        updatePlayerStates()
        setGameOver()

        return true
    }

    private fun updatePlayerStates() {
        PlayerID.values().forEach {
            playersLoose[it] = countFriendlyCells(it) == 0
        }
    }

    fun endPlayersTurn(player: PlayerID): Boolean {
        if(currentPlayer != player) return false
        currentPlayer = getNextPlayer()
        playersResources[currentPlayer] = playersResources[currentPlayer]!! + countProductiveCells(currentPlayer) + 1

        setGameOver()

        return true
    }

    fun isGameOver() = gameIsOver

    private fun setGameOver() {
        gameIsOver = playersLoose.values.filter { !it }.size == 1
    }

    fun getWinners() : List<PlayerID>? {
        if (!gameIsOver) return null
        return playersLoose.filter { !it.value }.keys.toList()
    }

    fun toJson() : String {
        return Gson().toJson(this)
    }

}