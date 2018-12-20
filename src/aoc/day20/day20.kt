package aoc.day20

import java.io.File
import java.util.*

data class Pos(val x: Int, val y: Int)

fun main(args: Array<String>) {
    val regex = File("input/day20.txt").readText().trim()

    val map = mutableMapOf<Pos, Int>() // Pos -> Distance
    var curPos = Pos(0, 0)
    map[curPos] = 0

    val branchPos = Stack<Pos>()
    for (char in regex) {
        when (char) {
            'N', 'E', 'S', 'W' -> {
                val nextPos = getNextPos(curPos, char)
                val nextPosDistance = map.getOrDefault(nextPos, map[curPos]!! + 1)
                val curPosDistance = map[curPos]!! + 1
                map[nextPos] = if (nextPosDistance < curPosDistance) nextPosDistance else curPosDistance
                curPos = nextPos
            }
            '(' -> {
                branchPos.push(curPos)
            }
            ')' -> {
                curPos = branchPos.pop()
            }
            '|' -> {
                curPos = branchPos.peek()
            }
        }
    }

    println(map.map { it.value }.max())

    val minDistance = 1000
    println(map.filter { it.value >= minDistance }.count())
}

fun getNextPos(p: Pos, c: Char): Pos {
    return when (c) {
        'N' -> Pos(p.x, p.y-1)
        'E' -> Pos(p.x+1, p.y)
        'S' -> Pos(p.x, p.y+1)
        'W' -> Pos(p.x-1, p.y)
        else -> throw Error()
    }
}
