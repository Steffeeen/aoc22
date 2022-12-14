package day13

import common.LINE_SEPARATOR
import common.loadInput
import kotlin.math.max

interface Element
data class ListValue(val elements: List<Element>) : Element
data class IntValue(val value: Int) : Element

fun Pair<String, String>.parseElements() = Pair(first.parseElement(), second.parseElement())
fun String.parseElement(): ListValue = this.substringAfter("[").parseList().first

fun String.occursFirst(a: String, b: String): String {
    val aIndex = indexOf(a)
    val bIndex = indexOf(b)

    return when {
        aIndex < 0 -> b
        bIndex < 0 -> a
        aIndex < bIndex -> a
        aIndex > bIndex -> b
        else -> throw IllegalArgumentException("none of the two strings occur")
    }
}

fun String.parseList(): Pair<ListValue, String> {
    val splitter = occursFirst("[", "]")

    val firstElements = substringBefore(splitter).split(",").filter { it.isNotBlank() }.map { IntValue(it.toInt()) }
    val restElementsString = substringAfter(splitter)

    if (splitter == "]") {
        return ListValue(firstElements) to restElementsString
    }

    if (splitter == "[") {
        val result = restElementsString.parseList()
        val rest = result.second.parseList()
        if (rest.first.elements.isEmpty()) {
            return ListValue(firstElements + result.first) to rest.second
        }
        return ListValue(firstElements + result.first + rest.first.elements) to rest.second
    }

    throw IllegalArgumentException("oh no")
}

fun areInRightOrder(a: Element, b: Element): Boolean = when {
    a is IntValue && b is IntValue -> a.value < b.value
    a is ListValue && b is ListValue -> areListsInRightOrder(a, b)
    a is ListValue && b is IntValue -> areInRightOrder(a, ListValue(listOf(b)))
    a is IntValue && b is ListValue -> areInRightOrder(ListValue(listOf(a)), b)
    else -> throw IllegalArgumentException("oh no")
}

fun areListsInRightOrder(a: ListValue, b: ListValue): Boolean {
    val maxSize = max(a.elements.size, b.elements.size)

    for (i in 0 until maxSize) {
        val aValue = a.elements.getOrNull(i)
        val bValue = b.elements.getOrNull(i)

        if (aValue == null) return true
        if (bValue == null) return false
        if (equal(aValue, bValue)) continue
        return areInRightOrder(aValue, bValue)
    }
    return false
}

fun equal(a: Element, b: Element): Boolean = when {
    a is IntValue && b is IntValue -> a.value == b.value
    a is ListValue && b is ListValue -> listsEqual(a, b)
    a is ListValue && b is IntValue -> listsEqual(a, ListValue(listOf(b)))
    a is IntValue && b is ListValue -> listsEqual(ListValue(listOf(a)), b)
    else -> throw IllegalArgumentException("oh no")
}

fun listsEqual(a: ListValue, b: ListValue): Boolean {
    if(a.elements.size != b.elements.size) {
        return false
    }

    return a.elements.zip(b.elements).all { equal(it.first, it.second) }
}

fun main() {
    val input = loadInput(13)

    val pairs = input.split("$LINE_SEPARATOR$LINE_SEPARATOR").map { it.lines() }.map { Pair(it[0], it[1]) }
    val parsedPairs = pairs.map { it.parseElements() }

    val correctPairs = parsedPairs.withIndex().map { it.value to it.index }.filter { areInRightOrder(it.first.first, it.first.second) }
    val partOne = correctPairs.sumOf { it.second + 1 }

    println("Part one: $partOne")

    val dividerPackets = listOf("[[2]]", "[[6]]").map { it.parseElement() }
    val allPackets = input.lines().filter { it.isNotBlank() }.map { it.parseElement() } + dividerPackets
    val comparator = {a: ListValue, b: ListValue -> if (areInRightOrder(a, b)) -1 else 1 }
    val sortedPackets = allPackets.sortedWith(comparator)

    val partTwo = dividerPackets.map { sortedPackets.indexOf(it) + 1 }.fold(1) {acc, i -> acc * i}
    println("Part two: $partTwo")
}