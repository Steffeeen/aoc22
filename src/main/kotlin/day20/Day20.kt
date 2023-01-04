package day20

import common.loadInput

fun List<Long>.mix(n: Long): List<Long> {
    val indexed = this.mapIndexed { index, it -> Pair(it, index) }
    var current = indexed
    for(i in 1..n) {
        current = indexed.fold(current) { acc, pair ->
            if (pair.first == 0L) return@fold acc

            val result = acc.toMutableList()

            val index = acc.indexOf(pair)
            result.removeAt(index)

            // doesn't work for the example, wtf, cost me an hour to figure out
            var newIndex = (index + pair.first) % (this.size - 1)
            if (newIndex < 0) {
                newIndex += (this.size - 1)
            }


            result.add(newIndex.toInt(), pair)

            result
        }
    }
    return current.map { it.first }
}


fun main() {
    val input = loadInput(20)
    val numbers = input.lines().map { it.toLong() }

    val key = 811589153
    val multipliedNumbers = numbers.map { it * key }

    val mixed = numbers.mix(1)
    val zeroIndex = mixed.indexOf(0)

    val partOne = mixed[(zeroIndex + 1000) % mixed.size] + mixed[(zeroIndex + 2000) % mixed.size] + mixed[(zeroIndex + 3000) % mixed.size]
    println("Part one: $partOne")

    val mixed10Times = multipliedNumbers.mix(10)
    val zeroIndexPartTwo = mixed10Times.indexOf(0)

    val partTwo = mixed10Times[(zeroIndexPartTwo + 1000) % mixed10Times.size] + mixed10Times[(zeroIndexPartTwo + 2000) % mixed10Times.size] + mixed10Times[(zeroIndexPartTwo + 3000) % mixed10Times.size]
    println("Part two: $partTwo")
}