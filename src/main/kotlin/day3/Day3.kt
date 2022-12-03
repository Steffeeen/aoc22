package day3

import common.loadInput

fun priority(char: Char): Int = when {
    char.isUpperCase() -> char.code - 'A'.code + 1 + 26
    char.isLowerCase() -> char.code - 'a'.code + 1
    else -> throw IllegalArgumentException("invalid char: $char")
}

fun String.toCharSet() = this.toCharArray().toSet()

fun findDuplicate(rucksack: String): Char {
    val left = rucksack.substring(0, rucksack.length / 2)
    val right = rucksack.substring(rucksack.length / 2, rucksack.length)

    val result = left.toCharSet().intersect(right.toCharSet())
    require(result.size == 1) { "intersection size is not 1" }

    return result.first()
}

fun findBadge(rucksacks: List<String>): Char {
    var result: Set<Char> = rucksacks.first().toCharSet()

    for(rucksack in rucksacks.drop(1)) {
        result = result.intersect(rucksack.toCharSet())
    }

    require(result.size == 1) {"intersection size is not 1"}

    return result.first()
}

fun main() {
    val input = loadInput(3)
    val sacks = input.split("\n")

    val partOne = sacks.sumOf { priority(findDuplicate(it)) }
    println("Part one: $partOne")

    val partTwo = sacks.chunked(3).sumOf { priority(findBadge(it)) }
    println("Part two: $partTwo")
}