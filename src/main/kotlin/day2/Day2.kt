package day2

import common.LINE_SEPARATOR
import common.loadInput

enum class Shape {
    ROCK,
    PAPER,
    SCISSORS
}

fun Shape.score(): Int = when(this) {
    Shape.ROCK -> 1
    Shape.PAPER -> 2
    Shape.SCISSORS -> 3
}

enum class Outcome {
    WIN,
    DRAW,
    LOSE,
}

fun Outcome.score(): Int = when(this) {
    Outcome.LOSE -> 0
    Outcome.DRAW -> 3
    Outcome.WIN -> 6
}

fun shapeFromFirstColumn(char: String): Shape = when(char) {
    "A" -> Shape.ROCK
    "B" -> Shape.PAPER
    "C" -> Shape.SCISSORS
    else -> throw IllegalArgumentException("$char is not one of A,B,C")
}

fun shapeFromSecondColumn(char: String): Shape = when(char) {
    "X" -> Shape.ROCK
    "Y" -> Shape.PAPER
    "Z" -> Shape.SCISSORS
    else -> throw IllegalArgumentException("$char is not one of X,Y,Z")
}

fun computeOutcome(me: Shape, other: Shape): Outcome = when {
    me == other -> Outcome.DRAW
    me == Shape.ROCK && other == Shape.SCISSORS -> Outcome.WIN
    me == Shape.PAPER && other == Shape.ROCK -> Outcome.WIN
    me == Shape.SCISSORS && other == Shape.PAPER -> Outcome.WIN
    else -> Outcome.LOSE
}

fun outcomeFromSecondColumn(char: String): Outcome = when(char) {
    "X" -> Outcome.LOSE
    "Y" -> Outcome.DRAW
    "Z" -> Outcome.WIN
    else -> throw IllegalArgumentException("$char is not one of X,Y,Z")
}

fun Shape.findWinning(): Shape = when(this) {
    Shape.ROCK -> Shape.PAPER
    Shape.PAPER -> Shape.SCISSORS
    Shape.SCISSORS -> Shape.ROCK
}

fun Shape.findDraw(): Shape = this

fun Shape.findLosing(): Shape = when(this) {
    Shape.ROCK -> Shape.SCISSORS
    Shape.PAPER -> Shape.ROCK
    Shape.SCISSORS -> Shape.PAPER
}

fun Shape.findMatching(outcome: Outcome): Shape = when(outcome) {
    Outcome.WIN -> this.findWinning()
    Outcome.LOSE -> this.findLosing()
    Outcome.DRAW -> this.findDraw()
}

fun main() {
    val input = loadInput(2)
    val lines = input.split(LINE_SEPARATOR)

    var oneScore = 0

    for (line in lines) {
        val entries = line.split(" ")
        val me = shapeFromSecondColumn(entries[1])
        val other = shapeFromFirstColumn(entries[0])
        val outcome = computeOutcome(me, other)
        oneScore += me.score() + outcome.score()
    }

    println("Part one: $oneScore")

    var secondScore = 0

    for(line in lines) {
        val entries = line.split(" ")
        val other = shapeFromFirstColumn(entries[0])
        val outcome = outcomeFromSecondColumn(entries[1])

        secondScore += outcome.score() + other.findMatching(outcome).score()
    }

    println("Part two: $secondScore")
}
