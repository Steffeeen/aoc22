package day5
import common.LINE_SEPARATOR
import common.loadInput

data class Move(val count: Int, val from: Int, val to: Int)

val moveRegex = Regex("^move ([0-9]+) from ([0-9]+) to ([0-9]+)")
fun parseMove(line: String): Move {
    val groups = moveRegex.matchEntire(line)?.groupValues
    require(groups != null) {"no matches"}
    require(groups.size == 4) {"groups size is not 4, was ${groups.size}"}
    return Move(groups[1].toInt(), groups[2].toInt() - 1, groups[3].toInt() - 1)
}

fun parseContainers(lines: List<String>): List<ArrayDeque<String>> {
    val numberOfStacks = lines.asReversed()[0].substringAfterLast(" ").toInt()
    val result: List<ArrayDeque<String>> = List(numberOfStacks) { ArrayDeque() }
    lines.reversed().drop(1).forEach { line ->
        val map = parseLine(line)
        map.forEach {
            result[it.key].addFirst(it.value)
        }
    }

    return result
}

fun parseLine(line: String): Map<Int, String> {
    val result: MutableMap<Int, String> = mutableMapOf()

    line.chunked(4).forEachIndexed { index, it ->
        if (it.isNotBlank()) {
            val container = it.substringAfter("[").substringBefore("]")
            result[index] = container
        }
    }

    return result
}

fun List<ArrayDeque<String>>.applyMoves(moves: List<Move>, canMoveMultiple: Boolean): List<ArrayDeque<String>> {
    val copy = this.map { ArrayDeque(it) }.toMutableList()
    moves.forEach {
        if(canMoveMultiple) {
            val movingContainers = copy[it.from].take(it.count)
            copy[it.from] = ArrayDeque(copy[it.from].drop(it.count))
            copy[it.to].addAll(0, movingContainers)
        } else {
            for (i in 1..it.count) {
                copy[it.to].addFirst(copy[it.from].removeFirst())
            }
        }
    }
    return copy
}

fun List<ArrayDeque<String>>.createSubmitString() = joinToString(separator = "") { it.first() }

fun main() {
    val input = loadInput(5)

    val stacksInput = input.substringBefore("$LINE_SEPARATOR$LINE_SEPARATOR").split(LINE_SEPARATOR)
    val movesInput = input.substringAfter("$LINE_SEPARATOR$LINE_SEPARATOR").split(LINE_SEPARATOR)

    val stacks = parseContainers(stacksInput)
    val moves = movesInput.map { parseMove(it) }

    val stacksPartOne = stacks.applyMoves(moves, false)
    println("Part one: ${stacksPartOne.createSubmitString()}")

    val stacksPartTwo = stacks.applyMoves(moves, true)
    println("Part two: ${stacksPartTwo.createSubmitString()}")
}