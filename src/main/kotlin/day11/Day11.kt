package day11

import common.LINE_SEPARATOR
import common.loadInput
import kotlin.math.floor
import kotlin.math.roundToLong

// operation: new = old */+ x
// test: divisible by x -> throw to monkey y
data class Monkey(val number: Int, val items: List<Long>, val operation: (Long) -> Long, val testValue: Long, val trueValue: Int, val falseValue: Int)

fun Monkey.addItem(item: Long): Monkey {
    return copy(items = items + item)
}

fun parseMonkey(lines: List<String>): Monkey {
    val number = lines.first().substringAfter(" ").substringBefore(":").toInt()
    val startingItems = lines[1].substringAfter(":").split(",").map { it.trim().toLong() }
    val operation = parseOperation(lines[2])
    val testValue = lines[3].substringAfterLast(" ").toLong()
    val trueValue = lines[4].substringAfterLast(" ").toInt()
    val falseValue = lines[5].substringAfterLast(" ").toInt()
    return Monkey(number, startingItems, operation, testValue, trueValue, falseValue)
}

fun String.countSubstring(sub: String) = split(sub).size - 1

fun parseOperation(line: String): (Long) -> Long {
    val expression = line.substringAfter("=")
    return when {
        expression.countSubstring("old") == 2 && expression.contains("+") -> { x -> x + x }
        expression.countSubstring("old") == 2 && expression.contains("*") -> { x -> x * x }
        expression.contains("*") -> { x -> x * expression.substringAfter("*").trim().toInt() }
        expression.contains("+") -> { x -> x + expression.substringAfter("+").trim().toInt() }
        else -> throw IllegalArgumentException("unknown expression $expression")
    }
}

fun updateWorryLevel(current: Long): Long = floor(current / 3.0).roundToLong()

fun processRound(monkeys: List<Pair<Monkey, Int>>, lowerWorryLevel: Boolean): List<Pair<Monkey, Int>> {
    val newMonkeys = mutableListOf<Pair<Monkey, Int>>()
    val oldMonkeys = monkeys.toMutableList()

    val mod = monkeys.map { it.first }.fold(1L) {acc, monkey -> acc * monkey.testValue}

    oldMonkeys.forEachIndexed { index, pair ->
        val monkey = pair.first
        for (item in monkey.items) {
            val newLevelHigh = if (lowerWorryLevel) updateWorryLevel(monkey.operation(item)) else monkey.operation(item)
            val newLevel = newLevelHigh % mod
            val monkeyNumberToThrowTo = if(newLevel % monkey.testValue == 0L) monkey.trueValue else monkey.falseValue
            if (index > monkeyNumberToThrowTo) {
                newMonkeys[monkeyNumberToThrowTo] = newMonkeys[monkeyNumberToThrowTo].copy(first = newMonkeys[monkeyNumberToThrowTo].first.addItem(newLevel))
            } else {
                oldMonkeys[monkeyNumberToThrowTo] = oldMonkeys[monkeyNumberToThrowTo].copy(first = oldMonkeys[monkeyNumberToThrowTo].first.addItem(newLevel))
            }
        }
        newMonkeys += monkey.copy(items = listOf()) to (pair.second + monkey.items.size)
    }

    return newMonkeys
}

fun main() {
    val input = loadInput(11)
    val monkeys = input.split("$LINE_SEPARATOR$LINE_SEPARATOR").map { parseMonkey(it.lines()) }

    val initial = monkeys.map { it to 0 }

    val partOneEnd = (1..20).fold(initial) { acc, _ -> processRound(acc, true) }.sortedByDescending { it.second }
    val partOne = partOneEnd[0].second * partOneEnd[1].second
    println("Part one: $partOne")

    val partTwoEnd = (1..10000).fold(initial) { acc, _ -> processRound(acc, false) }.sortedByDescending { it.second }
    val partTwo = partTwoEnd[0].second.toLong() * partTwoEnd[1].second.toLong()
    println("Part two: $partTwo")
}