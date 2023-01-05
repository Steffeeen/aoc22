package day21

import common.loadInput

fun tryToSolve(monkey: String, lines: List<String>): Long? {
    val line = lines.find { it.startsWith("$monkey:") } ?: return null

    val expression = line.substringAfter(":").trim()
    if (expression.matches(Regex("\\d+"))) {
        return expression.toLong()
    }

    val (first, second) = expression.expressionToParts()

    val firstResolved = tryToSolve(first, lines)
    val secondSolved = tryToSolve(second, lines)

    if (firstResolved == null || secondSolved == null) return null

    return when {
        "+" in expression -> firstResolved + secondSolved
        "-" in expression -> firstResolved - secondSolved
        "*" in expression -> firstResolved * secondSolved
        "/" in expression -> firstResolved / secondSolved
        else -> error("unknown operator")
    }
}

fun String.expressionToParts(): Pair<String, String> {
    val split = this.split("+", "-", "*", "/")
    val first = split.first().trim()
    val second = split.last().trim()
    return first to second
}

fun solvePartTwo(initialLines: List<String>): Long {
    val lines = initialLines.filter { !it.startsWith("humn:") }
    val rootLine = lines.find { it.startsWith("root:") }!!
    val expression = rootLine.substringAfter(":").trim()
    val (first, second) = expression.expressionToParts()

    val firstResolved = tryToSolve(first, lines)
    val secondResolved = tryToSolve(second, lines)

    require(firstResolved == null && secondResolved != null || firstResolved != null && secondResolved == null)

    val toResolve = if (firstResolved == null) first else second
    val solved = firstResolved ?: secondResolved!!

    return tryToSolveTo(toResolve, solved, lines)
}

fun tryToSolveTo(toSolve: String, to: Long, lines: List<String>): Long {
    if (toSolve == "humn") return to

    val line = lines.find { it.startsWith(toSolve) }!!
    val expression = line.substringAfter(":").trim()
    val (first, second) = expression.expressionToParts()

    val firstResolved = tryToSolve(first, lines)
    val secondResolved = tryToSolve(second, lines)

    val toResolve = if (firstResolved == null) first else second
    val solved = firstResolved ?: secondResolved!!

    return when {
        "+" in expression -> tryToSolveTo(toResolve, to - solved, lines)
        "-" in expression && solved == firstResolved -> tryToSolveTo(toResolve, solved - to, lines)
        "-" in expression && solved == secondResolved -> tryToSolveTo(toResolve, solved + to, lines)
        "*" in expression -> tryToSolveTo(toResolve, to / solved, lines)
        "/" in expression && solved == firstResolved -> tryToSolveTo(toResolve, solved / to, lines)
        "/" in expression && solved == secondResolved -> tryToSolveTo(toResolve, solved * to, lines)
        else -> error("unknown operator")
    }
}

fun main() {
    val input = loadInput(21)

    val partOne = tryToSolve("root", input.lines())
    requireNotNull(partOne)
    println("Part one: $partOne")

    val partTwo = solvePartTwo(input.lines())
    println("Part two: $partTwo")
}
