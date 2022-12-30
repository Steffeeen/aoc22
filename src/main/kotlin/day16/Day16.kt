package day16

import common.loadInput
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.math.max
import kotlin.math.pow

data class Valve(val name: String, val flowRate: Int)

typealias Graph = Map<Valve, List<Valve>>

fun buildGraph(lines: List<String>): Graph {
    val graph = mutableMapOf<Valve, List<Valve>>()

    val valves = lines.map {
        val name = it.substringAfter("Valve").substringBefore("has").trim()
        val flowRate = it.substringAfter("rate=").substringBefore(";").toInt()
        Valve(name, flowRate)
    }

    fun find(name: String): Valve = valves.find { it.name == name }!!

    lines.forEach { line ->
        val leadsTo = if (line.contains("valves")) {
            line.substringAfter("valves").trim().split(",").map { it.trim() }
        } else {
            listOf(line.substringAfterLast("valve").trim())
        }
        val name = line.substringAfter("Valve").substringBefore("has").trim()
        graph[find(name)] = leadsTo.map { find(it) }
    }

    return graph
}

fun Graph.findPath(from: Valve, to: Valve): List<Valve> {
    val visited = mutableSetOf(from)
    val queue = ArrayDeque(listOf(from))
    val parents = mutableMapOf<Valve, Valve>()

    fun buildPath(from: Valve): List<Valve> {
        var current = from
        val path = mutableListOf(current)
        while (parents.containsKey(current)) {
            current = parents[current]!!
            path += current
        }

        return path.reversed()
    }

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        if (current == to) {
            return buildPath(current)
        }

        queue.addAll(this[current]!!.filter { it !in visited }.also { nodes -> visited += nodes; nodes.forEach { parents[it] = current } })
    }

    error("no path found")
}

fun Graph.findPathsBetween(valves: List<Valve>): List<List<Valve>> {
    return valves.flatMap { valve1 -> valves.map { this.findPath(valve1, it) } }
}

fun Graph.maxFlow(valve: Valve, pressure: Int, minutesRemaining: Int, remainingValves: MutableSet<Valve>, paths: List<List<Valve>>): Int {
    if (minutesRemaining <= 0) {
        return pressure
    }

    val newPressure = pressure + valve.flowRate * minutesRemaining

    var max = -1

    val remainingValvesCopy = remainingValves.toMutableSet()

    for (remainingValve in remainingValvesCopy) {
        if (remainingValve == valve) continue

        val path = paths.find { it.first() == valve && it.last() == remainingValve }
        requireNotNull(path)
        remainingValves.remove(valve)
        max = max(max, maxFlow(remainingValve, newPressure, minutesRemaining - path.size, remainingValves, paths))
        remainingValves.add(valve)
    }

    if (max == -1) {
        return newPressure
    }

    return max
}

fun <T> Iterable<IndexedValue<T>>.nonIndexed() = this.map { it.value }

fun main() {
    val input = loadInput(16)

    val graph = buildGraph(input.lines())

    val startValve = graph.keys.find { it.name == "AA" }
    require(startValve != null)

    val usefulValves = graph.keys.filter { it.flowRate > 0 }
    val usefulPaths = graph.findPathsBetween(usefulValves).filter { it.first() != it.last() }
    val paths = usefulPaths + usefulValves.map { graph.findPath(startValve, it) }

    val partOne = graph.maxFlow(startValve, 0, 30, usefulValves.toMutableSet(), paths)
    println("Part one: $partOne")

    val range = 0 until 2.0.pow(usefulValves.size).toInt()
    val partitions = range.map { index ->
        val bitset = BitSet.valueOf(longArrayOf(index.toLong()))
        val partition = usefulValves.withIndex().partition { bitset[it.index] }
        Pair(partition.first.nonIndexed(), partition.second.nonIndexed())
    }

    val result = partitions.withIndex().toList().parallelStream().map {
        val me = graph.maxFlow(startValve, 0, 26, it.value.first.toMutableSet(), paths)
        val elephant = graph.maxFlow(startValve, 0, 26, it.value.second.toMutableSet(), paths)
        me + elephant
    }.max(Comparator.comparingInt { it })

    val partTwo = result.get()
    println("Part two: $partTwo")
}
