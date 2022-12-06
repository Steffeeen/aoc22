package day6

import common.loadInput
import day3.toCharSet

fun main() {
    val input = loadInput(6)
    val partOneString = input.windowed(4).find { it.toCharSet().size == 4}
    require(partOneString != null) {"no string found for part one"}

    val partOne = input.indexOf(partOneString) + 4
    println("Part one: $partOne")

    val partTwoString = input.windowed(14).find { it.toCharSet().size == 14 }
    require(partTwoString != null) {"no string found for part two"}

    val partTwo = input.indexOf(partTwoString) + 14
    println("Part two: $partTwo")
}