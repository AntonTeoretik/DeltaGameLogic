package com.delta

import com.google.gson.Gson

enum class PlayerID {
    PLAYER_1,
    PLAYER_2,
    PLAYER_3,
    PLAYER_4
}

class GameBoard(val size: Int) {
    val board: Array<Array<PlayerID?>> = Array(size) { Array(size) { null } }

    fun getCell(row: Int, col: Int): PlayerID? = board.getOrNull(row)?.getOrNull(col)

    fun setCell(row: Int, col: Int, player: PlayerID) {
        board[row][col] = player
    }

    fun freeCell(raw: Int, col: Int) {
        board[raw][col] = null
    }

    fun isBaseCell(row: Int, col: Int): Boolean {
        val player = getCell(row, col) ?: return false
        return countFriendlyNeighborsCorners(row, col, player) == 8 || isCorner(row, col)
    }

    fun getAllUnstableCells(player: PlayerID): List<Pair<Int, Int>> {
        val stableCells = getAllStableCells(player)
        val unstableCells = mutableSetOf<Pair<Int, Int>>()
        for (r in 0 until size)
            for (c in 0 until size)
                if (r to c !in stableCells &&
                    getCell(r, c) == player
                )
                    unstableCells.add(r to c)

        return unstableCells.toList()
    }

    fun getAllStableCells(player: PlayerID): List<Pair<Int, Int>> {
        val visited = mutableSetOf<Pair<Int, Int>>()
        val stableCells = mutableSetOf<Pair<Int, Int>>()
        val baseCells = getAllBaseCells(player).toSet()

        fun dfs(row: Int, col: Int) {
            println("dfs:")
            println("$row, $col")
            visited.add(Pair(row, col))
            stableCells.add(Pair(row, col))

            getNeighbors(row, col).forEach { (r, c) ->
                println("neighbour: $r $c")

                if (getCell(r, c) == player &&
                    !visited.contains(Pair(r, c))
                ) {
                    dfs(r, c)
                }

            }
        }

        baseCells.forEach { (r, c) ->
            dfs(r, c)
        }

        return stableCells.toList()
    }

    private fun getAllBaseCells(player: PlayerID): List<Pair<Int, Int>> {
        val baseCells = mutableListOf<Pair<Int, Int>>()

        for (row in 0 until size) {
            for (col in 0 until size) {
                if (isBaseCell(row, col) &&
                    getCell(row, col) == player
                ) {
                    baseCells.add(row to col)
                }
            }
        }

        return baseCells
    }

    private fun getNeighbors(row: Int, col: Int): List<Pair<Int, Int>> {
        val neighbors = mutableListOf<Pair<Int, Int>>()

        for ((dRow, dCol) in listOf(-1 to 0, 0 to 1, 0 to -1, 1 to 0)) {
            val neighborRow = row + dRow
            val neighborCol = col + dCol

            if (neighborRow in 0 until size &&
                neighborCol in 0 until size
            ) {
                neighbors.add(neighborRow to neighborCol)
            }

        }

        return neighbors
    }

    fun isCorner(row: Int, col: Int): Boolean {
        val ends = listOf(0, size - 1)
        return (row in ends) && (col in ends)
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