package day1

import common.LINE_SEPARATOR
import common.loadInput

data class ElfInv(val items: List<Int>)

fun main() {
    val input = loadInput(1)

    val invs: MutableList<ElfInv> = mutableListOf()

    val invStrings = input.split("$LINE_SEPARATOR$LINE_SEPARATOR")
    invStrings.forEach {
        val calories = it.split(LINE_SEPARATOR).map { Integer.parseInt(it) }
        invs += ElfInv(calories)
    }

    val max = invs.maxOf { it.items.sum() }
    println("Part one: $max")

    invs.sortByDescending { it.items.sum() }
    val topThreeSum = invs.subList(0, 3).sumOf { it.items.sum() }
    println("Part two: $topThreeSum")
}