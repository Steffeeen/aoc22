package day7

import common.loadInput

interface Entry {
    val name: String
}

data class File(override val name: String, val size: Int) : Entry
data class Dir(override val name: String, val entries: MutableList<Entry> = mutableListOf()) : Entry

val cdRegex = Regex("^\\$ cd (.+)")

fun Dir.size(): Int = entries.filterIsInstance<File>().sumOf { it.size } + entries.filterIsInstance<Dir>().sumOf { it.size() }

fun Dir.findSubDirs(): List<Dir> = entries.filterIsInstance<Dir>().map { it.findSubDirs() }.flatten() + this

fun main() {
    val input = loadInput(7).lines()

    val rootDir = Dir("/")

    var remaining = input

    val dirStack = ArrayDeque<Dir>()
    dirStack.addFirst(rootDir)
    var currentDir = rootDir

    while (remaining.drop(1).isNotEmpty()) {
        val line = remaining.take(1).first()

        if (line.startsWith("$ ls")) {
            val lsOutput = remaining.drop(1).takeWhile { !it.startsWith("$") }
            lsOutput.forEach {
                if (it.startsWith("dir")) {
                    currentDir.entries += Dir(it.substringAfter(" "))
                } else {
                    currentDir.entries += File(it.substringAfter(" "), it.substringBefore(" ").toInt())
                }
            }
            remaining = remaining.drop(1 + lsOutput.size)
            continue
        }

        val result = cdRegex.matchEntire(line)
        require(result != null) { "$line did not match regex" }
        val cdArg = result.groupValues[1]

        currentDir = when (cdArg) {
            ".." -> dirStack.removeFirst()
            "/" -> {
                dirStack.clear()
                dirStack.addFirst(rootDir)
                rootDir
            }
            else -> {
                val foundDir = currentDir.entries.find { it.name == cdArg }
                require(foundDir != null) { "no dir with name $cdArg" }
                require(foundDir is Dir) { "found is not a dir" }
                dirStack.addFirst(currentDir)
                foundDir
            }
        }

        remaining = remaining.drop(1)
    }

    val dirs = rootDir.findSubDirs()
    val sizes = dirs.map { it.size() }.filter { it <= 100000 }

    val partOne = sizes.sum()
    println("Part one: $partOne")

    val total = 70000000
    val needed = 30000000
    val available = total - rootDir.size()
    val dirToDelete = dirs.sortedBy { it.size() }.find { it.size() + available > needed }
    require(dirToDelete != null) {"found no dir to delete"}
    println("Part two: ${dirToDelete.size()}")
}