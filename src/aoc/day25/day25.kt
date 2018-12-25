package aoc.day25

import java.io.File

data class Pos(val x: Int, val y: Int, val z: Int, val t:Int, var constellation:Int?)

fun main(args: Array<String>) {
    val lines = File("input/day25.txt").readLines()

    val positions = mutableListOf<Pos>()
    for (line in lines) {
        val coords = line.split(',').map { it.trim().toInt() }.toList()
        val p = Pos(
            coords[0],
            coords[1],
            coords[2],
            coords[3],
            null
        )
        positions.add(p)
    }

    val maxdist = 3
    var constallation = 0
    for (p1 in positions) {
        if (p1.constellation == null) {
            p1.constellation = constallation++
        }

        for (p2 in positions) {
            if (p1 !== p2) {
                val d = distance(p1, p2)
                if (d <= maxdist) {
                    if (p2.constellation == null) {
                        p2.constellation = p1.constellation
                    } else {
                        val other = p2.constellation
                        positions
                            .filter { it.constellation == other }
                            .forEach { it.constellation = p1.constellation }
                    }
                }
            }
        }
    }

    val count = positions.distinctBy { it.constellation }.count()

    println(count)
}

fun distance(p1: Pos, p2: Pos): Int {
    return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y) + Math.abs(p1.z - p2.z) + Math.abs(p1.t - p2.t)
}