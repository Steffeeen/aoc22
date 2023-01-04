package day19

import common.loadInput
import day19.Material.*
import kotlin.math.max
import kotlin.math.min

enum class Material { ORE, CLAY, OBSIDIAN, GEODE }
data class Cost(val material: Material, val count: Int)
data class Blueprint(val id: Int, val costs: Map<Material, List<Cost>>)

fun parseBlueprint(line: String): Blueprint {
    val ints = Regex("\\d+").findAll(line).map { it.value.toInt() }.toList()
    require(ints.size == 7)
    val costs = mapOf(
        ORE to listOf(Cost(ORE, ints[1])),
        CLAY to listOf(Cost(ORE, ints[2])),
        OBSIDIAN to listOf(Cost(ORE, ints[3]), Cost(CLAY, ints[4])),
        GEODE to listOf(Cost(ORE, ints[5]), Cost(OBSIDIAN, ints[6]))
    )
    return Blueprint(ints[0], costs)
}

fun enoughResourcesForRobot(blueprint: Blueprint, resources: Map<Material, Int>, robot: Material) = blueprint.costs[robot]!!.all { resources[it.material]!! >= it.count }

fun Map<Material, Int>.increment(material: Material) = this + Pair(material, this[material]!! + 1)

fun Map<Material, Int>.incrementAll(amounts: Map<Material, Int>): Map<Material, Int> {
    val newMap = this.toMutableMap()
    amounts.forEach {
        newMap.computeIfPresent(it.key) { _, count -> count + it.value }
    }
    return newMap
}

data class State(val robots: Map<Material, Int>, val resources: Map<Material, Int>, val timeRemaining: Int)

fun solve(blueprint: Blueprint, initialRobots: Map<Material, Int>, initialResources: Map<Material, Int>, timeLimit: Int): Int {
    val seen = mutableSetOf<State>()
    val queue = ArrayDeque(listOf(State(initialRobots, initialResources, timeLimit)))

    var best = 0

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        val (robots, resources, timeRemaining) = current

        best = max(best, resources[GEODE]!!)

        if (current.timeRemaining == 0) continue

        val costs = blueprint.costs.values.flatten()
        val maxCosts = mutableMapOf<Material, Int>()
        maxCosts[ORE] = costs.filter { it.material == ORE }.maxOf { it.count }
        maxCosts[CLAY] = costs.filter { it.material == CLAY }.maxOf { it.count }
        maxCosts[OBSIDIAN] = costs.filter { it.material == OBSIDIAN }.maxOf { it.count }

        val newResources = resources.toMutableMap()
        newResources[ORE] = min(newResources[ORE]!!, timeRemaining * maxCosts[ORE]!! - robots[ORE]!! * (timeRemaining - 1))
        newResources[CLAY] = min(newResources[CLAY]!!, timeRemaining * maxCosts[CLAY]!! - robots[CLAY]!! * (timeRemaining - 1))
        newResources[OBSIDIAN] = min(newResources[OBSIDIAN]!!, timeRemaining * maxCosts[OBSIDIAN]!! - robots[OBSIDIAN]!! * (timeRemaining - 1))

        val newRobots = robots.toMutableMap()
        newRobots[ORE] = min(newRobots[ORE]!!, maxCosts[ORE]!!)
        newRobots[CLAY] = min(newRobots[CLAY]!!, maxCosts[CLAY]!!)
        newRobots[OBSIDIAN] = min(newRobots[OBSIDIAN]!!, maxCosts[OBSIDIAN]!!)

        val state = State(newRobots, newResources, timeRemaining)

        if (state in seen) continue

        seen += state

        if (seen.size % 100000 == 0) {
            println("timeRemaining: $timeRemaining, best: $best")
        }

        queue.add(State(newRobots, newResources.incrementAll(robots), timeRemaining - 1))

        Material.values().forEach {
            if (enoughResourcesForRobot(blueprint, newResources, it)) {
                val resourcesWithoutRobotCosts = newResources + blueprint.costs[it]!!.map { Pair(it.material, newResources[it.material]!! - it.count) }
                queue.add(State(newRobots.increment(it), resourcesWithoutRobotCosts.incrementAll(newRobots), timeRemaining - 1))
            }
        }
    }

    return best
}

fun main() {
    val input = loadInput(19)

    val blueprints = input.lines().map { parseBlueprint(it) }

    val initialRobots = mapOf(
        ORE to 1,
        CLAY to 0,
        OBSIDIAN to 0,
        GEODE to 0
    )
    val initialResources = mapOf(
        ORE to 0,
        CLAY to 0,
        OBSIDIAN to 0,
        GEODE to 0
    )

    val partOne = blueprints.sumOf { it.id * solve(it, initialRobots, initialResources, 24) }
    println("Part one: $partOne")

    val partTwo = blueprints.take(3).fold(1) { acc, blueprint -> solve(blueprint, initialRobots, initialResources, 32) * acc }
    println("Part two: $partTwo")
}
