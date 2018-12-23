package aoc.day23

import java.io.File

data class Pos(val x: Int, val y: Int, val z: Int)


fun main(args: Array<String>) {
    val lines = File("input/day23.txt").readLines()
    val re = Regex("""pos=<(-?\d+),(-?\d+),(-?\d+)>, r=(\d+)""")

    val botToStrength = mutableMapOf<Pos, Int>()
    for (line in lines) {
        val matchResult = re.matchEntire(line) ?: continue
        val p = Pos(
            matchResult.groupValues[1].toInt(),
            matchResult.groupValues[2].toInt(),
            matchResult.groupValues[3].toInt()
        )

        botToStrength[p] = matchResult.groupValues[4].toInt()
    }

    val strongestCount = part1(botToStrength)
    println(strongestCount)

    val shortestDist = part2(botToStrength)
    println(shortestDist)
}

private fun part1(botToStrength: MutableMap<Pos, Int>): Int {
    val strongest = botToStrength.maxBy { it.value }!!
    var strongestCount = 0
    for (bot in botToStrength.keys) {
        val dist = distance(strongest.key, bot)
        if (dist > strongest.value) {
            continue
        }
        strongestCount++
    }
    return strongestCount
}

fun part2(bots: MutableMap<Pos, Int>): Any {
    val origin = Pos(0,0,0)

    var xCoords = bots.keys.map { it.x }
    var yCoords = bots.keys.map { it.y }
    var zCoords = bots.keys.map { it.z }

    var distance = 1
    while (distance < xCoords.max()!! - xCoords.min()!!) {
        distance *= 2
    }

    while (true) {
        var foundPos: Pos? = null
        var foundPosDistance = 0
        var foundCount = 0

        val minX = xCoords.min()!!
        val maxX = xCoords.max()!!
        val minY = yCoords.min()!!
        val maxY = yCoords.max()!!
        val minZ = zCoords.min()!!
        val maxZ = zCoords.max()!!

        for (x in minX..maxX step distance) {
            for (y in minY..maxY step distance) {
                for (z in minZ..maxZ step distance) {
                    val pos = Pos(x, y, z)

                    var count = 0
                    for ((bot, range) in bots) {
                        val calc = distance(pos, bot)
                        if ((calc - range) / distance <= 0) {
                            count++
                        }
                    }

                    val dist = distance(pos, origin)
                    if (count > foundCount) {
                        foundPosDistance = dist
                        foundPos = pos
                        foundCount = count
                    } else if (count == foundCount) {
                        if (dist < foundPosDistance) {
                            foundPosDistance = dist
                            foundPos = pos
                        }
                    }

                }
            }
        }

        if (distance == 1) {
            return foundPosDistance
        } else {
            foundPos!!
            xCoords = listOf(
                foundPos.x - distance,
                foundPos.x + distance
            )
            yCoords = listOf(
                foundPos.y - distance,
                foundPos.y + distance

            )
            zCoords = listOf(
                foundPos.z - distance,
                foundPos.z + distance
            )

            distance /= 2
        }
    }
}

fun distance(p1: Pos, p2: Pos): Int {
    return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y) + Math.abs(p1.z - p2.z)
}
