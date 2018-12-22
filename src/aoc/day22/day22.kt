package aoc.day22

import java.io.File
import java.util.*

data class Pos(val x: Int, val y:Int)

data class Node(val pos: Pos, val distance:Int, val tool: Tool)

enum class Tool {
    Neither,
    Torch,
    ClimbingGear
}

data class PosTool(val pos: Pos, val tool: Tool)


fun main(args: Array<String>) {
    val text = File("input/day22.txt").readText()
    val depth = Regex("""depth: (\d+)""").find(text)!!.groupValues[1].toInt()
    val targetMatches = Regex("""target: (\d+),(\d+)""").find(text)!!
    val targetPos = Pos(
        targetMatches.groupValues[1].toInt(),
        targetMatches.groupValues[2].toInt()
    )

    val erosionLevels = mutableMapOf<Pos, Int>()
    val types = mutableMapOf<Pos, Int>()
    var sum = 0
    val extra = 500
    val maxPos = Pos(targetPos.x+extra, targetPos.y+extra)
    for (y in 0..maxPos.y) {
        for (x in 0..maxPos.x) {
            val p = Pos(x,y)
            val value: Int = when {
                p == Pos(0,0) -> {
                    0
                }
                p == targetPos -> {
                    0
                }
                p.y == 0 -> {
                    p.x * 16807
                }
                p.x == 0 -> {
                    p.y * 48271
                }
                else -> {
                    val elX = erosionLevels[Pos(p.x-1, p.y)]!!
                    val elY = erosionLevels[Pos(p.x, p.y-1)]!!
                    elX * elY
                }
            }
            val el = (value + depth) % 20183
            erosionLevels[p] = el
            val t = el % 3
            types[p] = t

            if (p.x <= targetPos.x && p.y <= targetPos.y) {
                sum += t
            }
        }
    }

    // part 1
    println(sum)

    val queue = PriorityQueue<Node>(compareBy {it.distance})
    val start = Node(Pos(0,0), 0, Tool.Torch)
    queue.add(start)
    val distanceMap = mutableMapOf<PosTool, Int>()

    val target = PosTool(targetPos, Tool.Torch)
    while (queue.isNotEmpty()) {
        val current = queue.poll()!!
        val currentKey = PosTool(current.pos, current.tool)
        if (currentKey in distanceMap && distanceMap[currentKey]!! <= current.distance) {
            continue
        }
        distanceMap[currentKey] = current.distance
        if (currentKey == target) {
            break
        }

        val currentType = types[current.pos]!!
        for (i in enumValues<Tool>()) {
            if (i != current.tool && toolMatch(i, currentType)) {
                queue.add(Node(current.pos, current.distance + 7, i))
            }
        }

        for (delta in listOf(
            Pos(-1, 0),
            Pos(1, 0),
            Pos(0, -1),
            Pos(0, 1)
        )) {
            val newPos = Pos(
                current.pos.x + delta.x,
                current.pos.y + delta.y
            )
            if (newPos.x < 0
                ||
                newPos.y < 0
                ||
                newPos.x > maxPos.x
                ||
                newPos.y > maxPos.y
            ) {
                continue
            }

            val newType = types[newPos]!!
            if (!toolMatch(current.tool, newType)) {
                continue
            }

            queue.add(Node(newPos, current.distance + 1, current.tool))
        }
    }

    // part2
    val dist = distanceMap[target]!!
    println(dist)
}

fun toolMatch(tool: Tool, type: Int): Boolean {
    return when (type) {
        0 -> {
            // 0 rocky      climbing, torch
            tool == Tool.ClimbingGear || tool == Tool.Torch
        }
        1 -> {
            // 1 wet        climbing, neither
            tool == Tool.ClimbingGear || tool == Tool.Neither
        }
        2 -> {
            // 2 narrow     torch, neither
            tool == Tool.Torch || tool == Tool.Neither
        }
        else -> false
    }
}
