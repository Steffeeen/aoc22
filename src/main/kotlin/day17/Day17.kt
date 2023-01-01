package day17

import common.LINE_SEPARATOR
import common.loadInput
import kotlin.math.max

val rocksString = """
    ####

    .#.
    ###
    .#.

    ..#
    ..#
    ###

    #
    #
    #
    #

    ##
    ##
""".trimIndent()

data class Position(val x: Long, val y: Long)
data class RelativePosition(val x: Int = 0, val y: Int = 0)
data class Rock(val shape: List<RelativePosition>, val id: Int)
data class FallingRock(val rock: Rock, val position: Position)

fun parseRocks(rocksString: String): List<Rock> {
    val rocks = rocksString.split("$LINE_SEPARATOR$LINE_SEPARATOR")
    return rocks.mapIndexed { index, it ->
        Rock(
            it.lines().reversed().flatMapIndexed { y, line -> line.toCharArray().toList().mapIndexedNotNull { x, char -> if (char == '#') RelativePosition(x, y) else null } },
            index
        )
    }
}

fun RelativePosition.toAbsolute(position: Position) = Position(position.x + this.x, position.y + this.y)
fun Rock.createFallingRock(x: Long, y: Long): FallingRock = FallingRock(this, Position(x, y))
fun FallingRock.absoluteShapes() = this.rock.shape.map { it.toAbsolute(this.position) }
fun FallingRock.left(leftBorder: Int, blockedPositions: Set<Position>): FallingRock {
    val moved = this.copy(position = this.position.copy(x = this.position.x - 1))
    return if (moved.absoluteShapes().any { it.x <= leftBorder || it in blockedPositions }) this else moved
}

fun FallingRock.right(rightBorder: Int, blockedPositions: Set<Position>): FallingRock {
    val moved = this.copy(position = this.position.copy(x = this.position.x + 1))
    return if (moved.absoluteShapes().any { it.x >= rightBorder || it in blockedPositions }) this else moved
}

fun FallingRock.down() = this.copy(position = this.position.copy(y = this.position.y - 1))
fun FallingRock.stop(): Set<Position> = this.rock.shape.map { it.toAbsolute(this.position) }.toSet()
fun FallingRock.isColliding(blockedPositions: Set<Position>, floor: Int) =
    this.absoluteShapes().any { it.y <= floor } || this.absoluteShapes().any { it in blockedPositions }

fun simulateRock(
    rock: Rock,
    blockedPositions: Set<Position>,
    highestY: Long,
    initialInstructionIndex: Int,
    floor: Int,
    leftBorder: Int,
    rightBorder: Int,
    instructions: String,
): Pair<FallingRock, Int> {
    val maxY = if (blockedPositions.isEmpty()) floor.toLong() else highestY
    var fallingRock = rock.createFallingRock((leftBorder + 3).toLong(), maxY + 4)
    var fall = false
    var instructionIndex = initialInstructionIndex
    while (true) {
        val newRock = if (fall) {
            fallingRock.down()
        } else {
            val char = instructions[instructionIndex]
            instructionIndex = (instructionIndex + 1) % instructions.length
            when (char) {
                '>' -> fallingRock.right(rightBorder, blockedPositions)
                '<' -> fallingRock.left(leftBorder, blockedPositions)
                else -> error("invalid char $char")
            }
        }

        if (newRock.isColliding(blockedPositions, floor)) {
            return fallingRock to instructionIndex
        }

        fallingRock = newRock
        fall = !fall
    }
}

data class Key(val rockId: Int, val remainingInstructions: Int, val blockedPositions: Set<Position>)

fun simulateRocks(n: Long, rocks: List<Rock>, instructions: String, floor: Int, chamberWidth: Int): Long {
    // key -> count, highestY
    val cache = mutableMapOf<Key, Pair<Long, Long>>()

    val currentBlocked = mutableSetOf<Position>()
    var instructionIndex = 0
    var highestY = 0L
    for (count in 1..n) {
        val rock = rocks[((count - 1) % rocks.size).toInt()]

        val topPositionsAbsolute = (1..chamberWidth).map { x ->
            val filtered = currentBlocked.filter { it.x == x.toLong() }
            if (filtered.isEmpty()) Position(x.toLong(), 0) else filtered.maxBy { it.y }
        }
        val minYForTopPositions = topPositionsAbsolute.minOf { it.y }
        val topPositions = topPositionsAbsolute.map { it.copy(y = it.y - minYForTopPositions) }.toSet()

        val key = Key(rock.id, instructionIndex, topPositions)
        if (key in cache) {
            val (cacheCount, cacheHighestY) = cache[key]!!

            val skipSize = count - cacheCount
            val skipCount = (n - (count - 1)).floorDiv(skipSize)
            val skipToGoal = (n - (count - 1)) % skipSize == 0L
            if (skipToGoal) {
                return highestY + (highestY - cacheHighestY) * skipCount
            }
        } else {
            cache[key] = count to highestY
        }

        val (fallingRock, newInstructionIndex) = simulateRock(rock, currentBlocked, highestY, instructionIndex, floor, 0, chamberWidth + 1, instructions)
        highestY = max(highestY, fallingRock.absoluteShapes().maxOf { it.y })

        currentBlocked += fallingRock.stop()
        instructionIndex = newInstructionIndex
    }

    return highestY
}

fun main() {
    val input = loadInput(17)
    val rocks = parseRocks(rocksString)

    val chamberWidth = 7
    val floor = 0

    val partOne = simulateRocks(2022, rocks, input, floor, chamberWidth)
    println("Part one: $partOne")

    val partTwo = simulateRocks(1_000_000_000_000, rocks, input, floor, chamberWidth)
    println("Part two: $partTwo")
}