package aoc.day03

import java.io.File

data class Entry(val id: Int, val left: Int, val top: Int, val width: Int, val height: Int)

fun main(args: Array<String>) {
    val lines = File("input/day03.txt").readLines()

    val regex = Regex("""^#(\d+) @ (\d+),(\d+): (\d+)x(\d+)$""")
    val entries = mutableListOf<Entry>()
    for (line in lines) {
        val matchResult = regex.matchEntire(line)
        if (matchResult != null) {

            val entry = Entry(
                matchResult.groups[1]?.value?.toInt() ?: 0,
                matchResult.groups[2]?.value?.toInt() ?: 0,
                matchResult.groups[3]?.value?.toInt() ?: 0,
                matchResult.groups[4]?.value?.toInt() ?: 0,
                matchResult.groups[5]?.value?.toInt() ?: 0
            )
            entries.add(entry)
        }
    }

    val hitMap = mutableMapOf<Pair<Int, Int>, Int>()
    for (entry in entries) {
        for (x in entry.left until entry.left+entry.width) {
            for (y in entry.top  until entry.top+entry.height) {
                val c = Pair(x, y)
                val hitCount = hitMap.getOrDefault(c, 0)
                hitMap[c] = hitCount + 1
            }
        }
    }

    val count = get1(hitMap)
    println(count)

    val id: Int? = get2(hitMap, entries)
    println(id)
}

private fun get1(hitMap: MutableMap<Pair<Int, Int>, Int>): Int {
    var multipleClaimCount = 0
    hitMap.forEach { _, hitCount ->
        if (hitCount > 1) {
            multipleClaimCount++
        }
    }
    return multipleClaimCount
}

private fun get2(
    hitMap: MutableMap<Pair<Int, Int>, Int>,
    entries: MutableList<Entry>
): Int? {
    var id: Int? = null
    entryLoop@ for (entry in entries) {
        for (x in entry.left until entry.left + entry.width) {
            for (y in entry.top until entry.top + entry.height) {
                val c = Pair(x, y)
                val hitCount = hitMap.getOrDefault(c, 0)
                if (hitCount > 1) {
                    continue@entryLoop
                }
            }
        }

        id = entry.id
        break
    }
    return id
}