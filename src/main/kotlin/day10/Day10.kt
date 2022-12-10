package day10

import common.loadInput
import kotlin.math.abs

interface Instruction {
    val numberOfCycles: Int
    fun execute(x: Int): Int
}

data class AddInstruction(val value: Int) : Instruction {
    override val numberOfCycles: Int = 2
    override fun execute(x: Int): Int = x + value
}

object NoopInstruction : Instruction {
    override val numberOfCycles: Int = 1
    override fun execute(x: Int): Int = x
}

fun parseLine(line: String): Instruction = when {
    line.trim() == "noop" -> NoopInstruction
    line.startsWith("addx") -> AddInstruction(line.substringAfter(" ").toInt())
    else -> throw IllegalArgumentException("unexpected instruction $line")
}

data class ExecutedInstruction(val x: Int = 1, val numberOfCycles: Int = 0, val instruction: Instruction? = null)

fun main() {
    val input = loadInput(10)

    val instructions = input.lines().map { parseLine(it) }

    val results = instructions.runningFold(ExecutedInstruction()) { acc, instruction ->
        val numberOfCycles = acc.numberOfCycles + instruction.numberOfCycles
        val x = instruction.execute(acc.x)
        ExecutedInstruction(x, numberOfCycles, instruction)
    }.drop(1)

    val expandedResults = listOf(Pair(1, 0), Pair(1, 1)) + results.flatMapIndexed { index, e ->
        List(e.instruction!!.numberOfCycles) {
            if ((it + 1) == e.instruction.numberOfCycles) {
                Pair(e.x, e.numberOfCycles - e.instruction.numberOfCycles + it + 2)
            } else {
                Pair(results.getOrElse(index - 1) { ExecutedInstruction() }.x, e.numberOfCycles - e.instruction.numberOfCycles + it + 2)
            }
        }
    }

    val partOne = expandedResults.slice(20..expandedResults.size step 40).map{it.first * it.second}.sum()
    println("Part one: $partOne")

    val lines = expandedResults.drop(1).dropLast(1).chunked(40).mapIndexed { index, list ->
        list.joinToString("") {if (abs((index * 40 + it.first) - (it.second - 1)) <= 1) "#" else "." }
    }
    println("Part two:")
    lines.forEach { println(it) }
}