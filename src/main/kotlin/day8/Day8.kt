package day8

import common.loadInput

fun List<List<Tree>>.column(index: Int) = map { it[index] }

data class Tree(val x: Int, val y: Int, val height: Int)

fun Tree.isVisible(grid: List<List<Tree>>): Boolean {
    val column = grid.column(x)
    val row = grid[y]

    val left = row.subList(0, x)
    val right = row.subList(x + 1, row.size)
    val top = column.subList(0, y)
    val bottom = column.subList(y + 1, column.size)

    return left.all { it.height < height } || right.all { it.height < height } || top.all { it.height < height } || bottom.all { it.height < height }
}

// taken from https://gist.github.com/jivimberg/ff5aad3f5c6315deb420fd508a145c61
inline fun <T> List<T>.takeWhileInclusive(
    predicate: (T) -> Boolean
): List<T> {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = predicate(it)
        result
    }
}
fun Tree.scenicScore(grid: List<List<Tree>>): Int {
    val column = grid.column(x)
    val row = grid[y]

    val left = row.subList(0, x).reversed().takeWhileInclusive { it.height < height }
    val right = row.subList(x + 1, row.size).takeWhileInclusive { it.height < height }
    val top = column.subList(0, y).reversed().takeWhileInclusive { it.height < height }
    val bottom = column.subList(y + 1, column.size).takeWhileInclusive { it.height < height }

    return left.size * right.size * top.size * bottom.size
}

fun main() {
    val input = loadInput(8)
    val grid = input.lines().mapIndexed { y, line -> line.toCharArray().mapIndexed { x, char -> Tree(x, y, char.toString().toInt()) } }
    val partOne = grid.flatten().count { it.isVisible(grid) }
    println("Part one: $partOne")

    val partTwo = grid.flatten().maxOfOrNull { it.scenicScore(grid) }
    println("Part two: $partTwo")
}