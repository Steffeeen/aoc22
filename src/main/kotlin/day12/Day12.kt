package day12

import common.loadInput

fun Char.toElevation() = when (this) {
    'S' -> 0
    'E' -> 25
    else -> this.code - 'a'.code
}

data class Position(val elevation: Int, val char: Char, val x: Int, val y: Int)

fun String.toCharList() = toCharArray().toList()

fun buildGraph(input: String, stepHeight: Int): Map<Position, List<Position>> {
    val result = mutableMapOf<Position, MutableList<Position>>()
    val chars = input.lines().map { it.toCharList() }

    chars.forEachIndexed { y, charsInLine ->
        charsInLine.forEachIndexed { x, char ->
            val position = Position(char.toElevation(), char, x, y)
            result[position] = mutableListOf()

            for (offset in listOf(Pair(-1, 0), Pair(0, -1), Pair(1, 0), Pair(0, 1))) {
                val otherPosition = chars.getPositionAt(x + offset.first, y + offset.second) ?: continue
                if (otherPosition.elevation - stepHeight <= position.elevation) {
                    result[position]!!.add(otherPosition)
                }
            }
        }
    }

    return result
}

fun List<List<Char>>.getPositionAt(x: Int, y: Int): Position? {
    if (getOrNull(y) == null || this[y].getOrNull(x) == null) {
        return null
    }

    val char = this[y][x]
    return Position(char.toElevation(), char, x, y)
}

// breadth first search
fun Map<Position, List<Position>>.findShortestPath(start: Position, end: Position): List<Position>? {
    val parents = mutableMapOf<Position, Position>()
    val visited = mutableSetOf(start)
    val queue = ArrayDeque<Position>()
    queue.addLast(start)

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        if (current == end) {
            val path = mutableListOf<Position>()
            var currentPathNode = current
            path += currentPathNode
            while (parents[currentPathNode] != null) {
                currentPathNode = parents[currentPathNode]!!
                path += currentPathNode
            }
            return path.reversed()
        }

        this[current]?.filter { it !in visited }?.forEach {
            visited += it
            parents[it] = current
            queue.addLast(it)
        }
    }

    return null
}

fun main() {
    val input = loadInput(12)
    val graph = buildGraph(input, 1)

    val start = graph.keys.find { it.char == 'S' }
    val end = graph.keys.find { it.char == 'E' }

    requireNotNull(start)
    requireNotNull(end)

    val partOnePath = graph.findShortestPath(start, end)
    println("Part one: ${partOnePath!!.size - 1}")

    val startPositions = graph.keys.filter { it.elevation == 0 }
    require(start in startPositions)

    val partTwo = startPositions.mapNotNull { graph.findShortestPath(it, end)?.size?.minus(1) }.min()
    println("Part two: $partTwo")
}