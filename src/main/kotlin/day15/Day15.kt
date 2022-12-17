package day15

import common.loadInput
import kotlin.math.abs

data class Sensor(val x: Int, val y: Int, val beacon: Beacon) {
    val distanceToBeacon = this.distanceTo(beacon.x, beacon.y)
}
data class Beacon(val x: Int, val y: Int)

fun Sensor.distanceTo(x: Int, y: Int) = abs(this.x - x) + abs(this.y - y)

fun parseSensor(line: String): Sensor {
    val (sensorInput, beaconInput) = line.split(":")
    val sensorX = sensorInput.substringAfter("x=").substringBefore(",").toInt()
    val sensorY = sensorInput.substringAfter("y=").toInt()

    val beaconX = beaconInput.substringAfter("x=").substringBefore(",").toInt()
    val beaconY = beaconInput.substringAfter("y=").toInt()
    return Sensor(sensorX, sensorY, Beacon(beaconX, beaconY))
}

fun findBlockedPositions(minX: Int, maxX: Int, sensors: List<Sensor>, searchRow: Int): List<Int> {
    return (minX..maxX).filter { x -> sensors.any { !(it.beacon.x == x && it.beacon.y == searchRow) && it.distanceTo(x, searchRow) <= it.distanceToBeacon }}
}

fun tuningFrequency(x: Long, y: Long) = x * 4000000 + y

fun Pair<Int, Int>.isInRangeOfSensor(sensors: List<Sensor>) = sensors.any { it.distanceTo(first, second) <= it.distanceToBeacon  }

fun main() {
    val input = loadInput(15)

    val sensors = input.lines().map { parseSensor(it) }

    val searchRow = 2000000
    val minX = sensors.minOf { it.x - it.distanceToBeacon }
    val maxX = sensors.maxOf { it.x + it.distanceToBeacon }

    val partOne = findBlockedPositions(minX, maxX, sensors, searchRow).size
    println("Part one: $partOne")

    val xSearchRange = 0..4000000
    val ySearchRange = 0..4000000

    val positiveXBoundaries = sensors.flatMap { listOf(it.y - it.x + it.distanceToBeacon + 1, it.y - it.x - it.distanceToBeacon - 1) }
    val negativeXBoundaries = sensors.flatMap { listOf(it.x + it.y + it.distanceToBeacon + 1, it.x + it.y - it.distanceToBeacon - 1) }

    val potentialPoints = positiveXBoundaries.flatMap { a -> negativeXBoundaries.map { b -> a to b } }.map { Pair((it.second - it.first) / 2, (it.first + it.second) / 2) }.toSet()
    val foundPoints = potentialPoints.filter { it.first in xSearchRange && it.second in ySearchRange }.filterNot { it.isInRangeOfSensor(sensors) }
    require(foundPoints.size == 1)
    val partTwo = tuningFrequency(foundPoints.first().first.toLong(), foundPoints.first().second.toLong())
    println("Part two: $partTwo")
}