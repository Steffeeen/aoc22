package day18

import common.loadInput

typealias Grid = Array<Array<Array<Boolean>>>

data class Cube(val x: Int, val y: Int, val z: Int)

fun Cube.neighbors(maxX: Int, maxY: Int, maxZ: Int): List<Cube> {
    val neighbors = mutableListOf<Cube>()
    if (this.x != 0) neighbors += this.copy(x = this.x - 1)
    if (this.x != maxX) neighbors += this.copy(x = this.x + 1)
    if (this.y != 0) neighbors += this.copy(y = this.y - 1)
    if (this.y != maxY) neighbors += this.copy(y = this.y + 1)
    if (this.z != 0) neighbors += this.copy(z = this.z - 1)
    if (this.z != maxZ) neighbors += this.copy(z = this.z + 1)
    return neighbors
}

fun countSides(grid: Grid, ignoreInnerFaces: Boolean): Int {
    val outerCubesGrid: Grid = Array(grid.size) { Array(grid[0].size) { Array(grid[0][0].size) { false } } }

    if (ignoreInnerFaces) {
        val start = Cube(0, 0, 0)

        val queue = ArrayDeque(listOf(start))
        val visited = mutableSetOf(start)
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()

            outerCubesGrid[current.x][current.y][current.z] = true

            val neighbors = current.neighbors(grid.size - 1, grid[0].size - 1, grid[0][0].size - 1)
            val neighborsToVisit = neighbors.filter { !grid[it.x][it.y][it.z] } - visited
            visited += neighborsToVisit
            queue += neighborsToVisit
        }
    }

    var sides = 0
    for (x in grid.indices) {
        val gridX = grid[x]
        for (y in gridX.indices) {
            for (z in gridX[y].indices) {
                if (!grid[x][y][z]) continue

                var sidesForCube = 6

                if (grid[x + 1][y][z] || ignoreInnerFaces && !outerCubesGrid[x + 1][y][z]) sidesForCube--
                if (grid[x - 1][y][z] || ignoreInnerFaces && !outerCubesGrid[x - 1][y][z]) sidesForCube--
                if (grid[x][y + 1][z] || ignoreInnerFaces && !outerCubesGrid[x][y + 1][z]) sidesForCube--
                if (grid[x][y - 1][z] || ignoreInnerFaces && !outerCubesGrid[x][y - 1][z]) sidesForCube--
                if (grid[x][y][z + 1] || ignoreInnerFaces && !outerCubesGrid[x][y][z + 1]) sidesForCube--
                if (grid[x][y][z - 1] || ignoreInnerFaces && !outerCubesGrid[x][y][z - 1]) sidesForCube--

                sides += sidesForCube
            }
        }
    }
    return sides
}


fun main() {
    val input = loadInput(18)
    val cubes = input.lines().map { val split = it.split(","); Cube(split[0].toInt(), split[1].toInt(), split[2].toInt()) }
    val xSize = cubes.maxOf { it.x } + 3
    val ySize = cubes.maxOf { it.y } + 3
    val zSize = cubes.maxOf { it.z } + 3
    val grid = Array(xSize) { x -> Array(ySize) { y -> Array(zSize) { z -> Cube(x - 1, y - 1, z - 1) in cubes } } }

    val partOne = countSides(grid, false)
    println("Part one: $partOne")

    val partTwo = countSides(grid, true)
    println("Part one: $partTwo")
}