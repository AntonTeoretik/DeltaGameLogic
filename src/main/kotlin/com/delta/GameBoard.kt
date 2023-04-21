package com.delta

import com.google.gson.Gson

enum class PlayerID {
    PLAYER_1,
    PLAYER_2,
    PLAYER_3,
    PLAYER_4
}

class GameBoard(val size: Int) {
    private val board: Array<Array<PlayerID?>> = Array(size) { Array(size) { null } }

    fun getCell(row: Int, col: Int): PlayerID? = board.getOrNull(row)?.getOrNull(col)

    fun setCell(row: Int, col: Int, player: PlayerID) {
        board[row][col] = player
    }

    fun countFriendlyNeighbors(row: Int, col: Int, player: PlayerID): Int {
        var count = 0
        for ((dx, dy) in listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)) {
            val x = row + dx
            val y = col + dy
            if (getCell(x, y) == player) {
                count++
            }
        }
        return count
    }

    fun countFriendlyNeighborsCorners(row: Int, col: Int, player: PlayerID): Int {
        var count = 0
        for (dx in -1..1) {
            for (dy in -1..1) {
                val x = row + dx
                val y = col + dy
                if ((dx to dy) != (0 to 0) &&
                    isValidCoordinate(x, y) &&
                    getCell(x, y) == player
                ) {
                    count++
                }
            }
        }
        return count
    }

    fun countEnemyNeighbors(row: Int, col: Int, player: PlayerID): Int {
        var count = 0
        for ((dx, dy) in listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)) {
            val x = row + dx
            val y = col + dy
            if (isValidCoordinate(x, y) && getCell(x, y) !in setOf(null, player)) {
                count++
            }
        }
        return count
    }

    fun isValidCoordinate(x: Int, y: Int): Boolean = (x in 0 until size) and (y in 0 until size)

    fun toJson(): String {
        return Gson().toJson(this)
    }

    companion object {
        fun fromJson(json: String): GameBoard {
            return Gson().fromJson(json, GameBoard::class.java)
        }
    }
}