package aoc.day10

import java.io.File
import java.math.BigInteger

data class Entry(var position: Pair<Int, Int>, val velocity: Pair<Int, Int>)

fun main(args: Array<String>) {
    val lines = File("input/day10.txt").readLines()
    val lineRegex = Regex("""position=<\s*(-?\d+),\s*(-?\d+)> velocity=<\s*(-?\d+),\s*(-?\d+)>""")

    val entries = mutableSetOf<Entry>()
    for (line in lines) {
        val matchResult = lineRegex.matchEntire(line) ?: continue

        entries.add(
            Entry(
                Pair(matchResult.groupValues[1].toInt(), matchResult.groupValues[2].toInt()),
                Pair(matchResult.groupValues[3].toInt(), matchResult.groupValues[4].toInt())
            ))
    }

    var prevMap = mutableMapOf<Pair<Int, Int>, Entry>()
    for (entry in entries) {
        prevMap[entry.position] = entry
    }
    var prevArea = getArea(prevMap)

    var secondCount = 0
    while (true) {
        val map = mutableMapOf<Pair<Int, Int>, Entry>()
        entries.forEach {
            it.position = Pair(
                it.position.first + it.velocity.first,
                it.position.second + it.velocity.second
            )

            map[it.position] = it
        }

        val area = getArea(map)
        if (area > prevArea) {
            printEntries(prevMap)
            print(secondCount)
            return
        } else {
            prevMap = map
            prevArea = area
            secondCount += 1
        }
    }
}

private fun getArea(prevMap: MutableMap<Pair<Int, Int>, Entry>): BigInteger {
    val minX = prevMap.keys.map { it.first }.min()!!
    val maxX = prevMap.keys.map { it.first }.max()!!
    val minY = prevMap.keys.map { it.second }.min()!!
    val maxY = prevMap.keys.map { it.second }.max()!!
    val width = Math.abs(maxX - minX)
    val height = Math.abs(maxY - minY)
    val area = BigInteger.valueOf(width.toLong()).multiply(BigInteger.valueOf(height.toLong()))
    return area
}

private fun printEntries(
    map: MutableMap<Pair<Int, Int>, Entry>
) {
    val minX = map.keys.map { it.first }.min()!!
    val maxX = map.keys.map { it.first }.max()!!
    val minY = map.keys.map { it.second }.min()!!
    val maxY = map.keys.map { it.second }.max()!!

    for (y in minY..maxY) {
        for (x in minX..maxX) {
            val entry = map[Pair(x, y)]
            if (entry == null) {
                print('.')
            } else {
                print('#')
            }
        }
        println()
    }

    println()
}