package day9

import common.loadInput
import kotlin.math.abs
import kotlin.math.sign

data class Knot(val x: Int = 0, val y: Int = 0)

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

fun direction(s: String) = when (s) {
    "U" -> Direction.UP
    "D" -> Direction.DOWN
    "L" -> Direction.LEFT
    "R" -> Direction.RIGHT
    else -> throw IllegalArgumentException("unknown direction")
}

data class Move(val direction: Direction, val distance: Int)

fun Knot.applyMove(move: Move): Knot = when (move.direction) {
    Direction.UP -> Knot(x, y + 1)
    Direction.DOWN -> Knot(x, y - 1)
    Direction.LEFT -> Knot(x - 1, y)
    Direction.RIGHT -> Knot(x + 1, y)
}

fun Knot.followHead(head: Knot): Knot {
    val horizontal = abs(x - head.x) <= 1
    val vertical = abs(y - head.y) <= 1
    val horizontalStrict = x == head.x
    val verticalStrict = y == head.y

    if (horizontal && vertical) {
        return this
    }

    return when {
        !horizontal && verticalStrict -> Knot(x + (head.x - x).sign, y)
        horizontalStrict && !vertical -> Knot(x, y + (head.y - y).sign)
        else -> Knot(x + (head.x - x).sign, y + (head.y - y).sign)
    }
}

data class PartOneAccumulator(val head: Knot = Knot(), val tail: Knot = Knot(), val visited: Set<Knot> = setOf(Knot()))

data class PartTwoAccumulator(val head: Knot = Knot(), val knots: List<Knot> = List(8) { Knot() }, val tail: Knot = Knot(), val visited: Set<Knot> = setOf(Knot()))

fun main() {
    val input = loadInput(9)
    val moves = input.lines().map { Move(direction(it.substringBefore(" ")), it.substringAfter(" ").toInt()) }

    val partOne = moves.fold(PartOneAccumulator()) { acc, move ->
        var head = acc.head
        var tail = acc.tail
        val visited = acc.visited.toMutableSet()

        for(i in 1..move.distance) {
            head = head.applyMove(move)
            tail = tail.followHead(head)
            visited += tail
        }

        PartOneAccumulator(head, tail, visited)
    }

    println("Part one: ${partOne.visited.size}")

    val partTwo = moves.fold(PartTwoAccumulator()) { acc, move ->
        var head = acc.head
        var knots = acc.knots
        var tail = acc.tail
        val visited = acc.visited.toMutableSet()

        for (i in 1..move.distance) {
            head = head.applyMove(move)
            val newKnots = mutableListOf<Knot>()
            knots.forEach {
                val newKnot = it.followHead(newKnots.lastOrNull() ?: head)
                newKnots.add(newKnot)
            }
            tail = tail.followHead(newKnots.last())
            knots = newKnots
            visited += tail
        }

        PartTwoAccumulator(head, knots, tail, visited)
    }

    println("Part two: ${partTwo.visited.size}")
}