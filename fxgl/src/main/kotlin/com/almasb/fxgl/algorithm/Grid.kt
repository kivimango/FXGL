/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.algorithm

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Grid(val width: Int, val height: Int) {

    val rows = Array<Row>(height, { i -> Row(width) })
    val columns = Array<Column>(width, { i -> Column(height) })

    class Row(val width: Int) {
        val tiles = Array<Tile>(width, { i -> Tile() } )
    }

    class Column(val height: Int) {
        val tiles = Array<Tile>(height, { i -> Tile() } )
    }

    fun isValid(x: Int, y: Int) = x in 0..width-1 && y in 0..height-1

    fun getTile(x: Int, y: Int) = rows[y].tiles[x]

    fun getNeighbors(x: Int, y: Int): List<Tile> {
        val points = listOf(
                Pair(-1, 0),
                Pair(1, 0),
                Pair(0, 1),
                Pair(0, -1)
        )

        return points
                .map { Pair(it.first + x, it.second + y) }
                .filter { it.first in 0..width-1 && it.second in 0..height-1 }
                .map { getTile(it.first, it.second) }
    }

    override fun toString(): String {
        return rows.map { it.tiles.map { it.type.toString() }.fold("", {a, b -> a + b}) }
            .fold("", { row1, row2 -> row1 + "\n" + row2 })
    }
}