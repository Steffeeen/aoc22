package day4

import common.loadInput

fun parseRange(range: String): IntRange {
    val split = range.split("-")
    return IntRange(split[0].toInt(), split[1].toInt())
}

fun IntRange.contains(other: IntRange): Boolean {
    return this.first <= other.first && this.last >= other.last
}

fun main() {
    val input = loadInput(4)
    val pairs = input.split("\n").map { it.split(",") }.map { Pair(parseRange( it[0]), parseRange( it[1])) }

    val partOne = pairs.count { it.first.contains(it.second) || it.second.contains(it.first) }
    println("Part one: $partOne")

    val partTwo = pairs.count { it.first.intersect(it.second).isNotEmpty() }
    println("Part two: $partTwo")
}