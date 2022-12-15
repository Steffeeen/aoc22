package day14

import common.loadInput
import java.lang.Integer.max
import java.lang.StrictMath.min

data class Position(val x: Int, val y: Int)

data class Floor(val y: Int) {
    fun contains(position: Position): Boolean = this.y == position.y
}

fun Position.down() = this.copy(y = this.y + 1)
fun Position.downLeft() = this.copy(x = this.x - 1, y = this.y + 1)
fun Position.downRight() = this.copy(x = this.x + 1, y = this.y + 1)

fun parsePosition(string: String) = Position(string.substringBefore(",").toInt(), string.substringAfter(",").toInt())

fun parseRockPath(line: String): Set<Position> {
    return line.split("->").zipWithNext().flatMap {
        val a = parsePosition(it.first.trim())
        val b = parsePosition(it.second.trim())

        if (a.x == b.x) {
            val min = min(a.y, b.y)
            val size = max(a.y, b.y) - min + 1
            List(size) { Position(a.x, it + min) }
        } else if (a.y == b.y) {
            val min = min(a.x, b.x)
            val size = max(a.x, b.x) - min + 1
            List(size) { Position(it + min, a.y) }
        } else {
            throw IllegalArgumentException("invalid rock")
        }
    }.toSet()
}

fun Position.step(blocked: Set<Position>): Position {
    if (this.down() !in blocked) return this.down()
    if (this.downLeft() !in blocked) return this.downLeft()
    if (this.downRight() !in blocked) return this.downRight()

    return this
}

fun Position.step(blocked: Set<Position>, floor: Floor): Position {
    if (floor.contains(this.down()) || floor.contains(this.downLeft()) || floor.contains(this.downRight())) return this
    return this.step(blocked)
}

fun part1(initialCollidables: Set<Position>, sandSource: Position, maxRockY: Int): Int {
    val collidables: MutableSet<Position> = initialCollidables.toMutableSet()

    var currentSand = sandSource.copy()
    var numberOfRestingSand = 0

    while (currentSand.y <= maxRockY) {
        val newPos = currentSand.step(collidables)

        currentSand = if (newPos == currentSand) {
            collidables += currentSand
            numberOfRestingSand++
            sandSource.copy()
        } else {
            newPos
        }
    }

    return numberOfRestingSand
}

fun part2(initialCollidables: Set<Position>, floor: Floor, sandSource: Position): Int {
    val collidables: MutableSet<Position> = initialCollidables.toMutableSet()

    var currentSand = sandSource.copy()
    var numberOfRestingSand = 0

    while (true) {
        val newPos = currentSand.step(collidables, floor)

        if (newPos == sandSource) return numberOfRestingSand + 1

        currentSand = if (newPos == currentSand) {
            collidables += currentSand
            numberOfRestingSand++
            sandSource.copy()
        } else {
            newPos
        }
    }
}

fun main() {
    val input = loadInput(14)
    val rocks = input.lines().flatMap { parseRockPath(it) }.toSet()

    val sandSource = Position(500, 0)
    val maxRockY = rocks.maxOf { it.y }

    val partOne = part1(rocks, sandSource, maxRockY)
    println("Part one: $partOne")

    val partTwo = part2(rocks, Floor(maxRockY + 2), sandSource)
    println("Part two: $partTwo")
}