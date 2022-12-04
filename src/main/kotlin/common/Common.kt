package common

import java.nio.file.Files
import java.nio.file.Paths

val LINE_SEPARATOR: String = System.lineSeparator()

fun loadInput(day: Int): String {
    return Files.readString(Paths.get("src/main/resources/inputs/day$day.txt")).trimIndent()
}