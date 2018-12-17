package aoc.day17

import java.io.File

data class Pos(val x:Int, val y:Int)

fun main(args: Array<String>) {
    val clays = readInput("input/day17.txt")
    val waters = mutableSetOf<Pos>()
    val flows = mutableSetOf<Pos>()
    val spring = Pos(500, 0)
    flows.add(spring)


    val minX = clays.minBy { it.x }!!.x-1
    val maxX = clays.maxBy { it.x }!!.x+1
    val minY = clays.minBy { it.y }!!.y
    val maxY = clays.maxBy { it.y }!!.y
    while (true) {
        val startWaterSize = waters.size + flows.size
        for (flow in flows.toList()) {
            val belowFlows = getPosBelow(flow, clays, waters, flows,  maxY)
            var belowFlowFound = false
            for (belowFlow in belowFlows.sortedWith(compareByDescending { it.y })) {
                if (belowFlowFound) {
                    flows.add(belowFlow)
                } else {
                    val (horizontals, isHorizontalFlow) = getHorizontal(belowFlow, clays, waters, minX, maxX, maxY)
                    for (horizontal in horizontals) {
                        if (isHorizontalFlow) {
                            flows.add(horizontal)
                            waters.remove(horizontal)
                        } else {
                            flows.remove(horizontal)
                            waters.add(horizontal)
                        }
                    }

                    if (isHorizontalFlow) {
                        belowFlowFound = true
                        continue
                    }
                }
            }
        }

        if (startWaterSize == (waters.size + flows.size)) {
            // no change
            break
        }
    }


    val wCount = waters.size + flows.filter { it.y >= minY && it.x <= maxY }.count()
    println(wCount)

    println(waters.size)
}

fun getHorizontal(flow: Pos, clays: Set<Pos>, waters: Set<Pos>, minX: Int, maxX: Int, maxY: Int): Pair<MutableSet<Pos>, Boolean> {
    val set = mutableSetOf<Pos>()
    if (flow.y == maxY) {
        set.add(flow)
        return Pair(set, true)
    }

    val fb = Pos(flow.x, flow.y+1)
    if (!clays.contains(fb) && !waters.contains(fb)) {
        set.add(flow)
        return Pair(set, true)
    }

    var isFlow = false
    // left
    for (x in (flow.x-1) downTo minX) {
        val p = Pos(x, flow.y)
        if (clays.contains(p)) {
            break
        }

        set.add(p)

        val pb = Pos(p.x, p.y+1)
        if (!clays.contains(pb) && !waters.contains(pb)) {
            isFlow = true
            break
        }
    }

    // mid
    set.add(flow)

    // right
    for (x in (flow.x+1)..maxX) {
        val p = Pos(x, flow.y)
        if (clays.contains(p)) {
            break
        }

        set.add(p)

        val pb = Pos(p.x, p.y+1)
        if (!clays.contains(pb) && !waters.contains(pb)) {
            isFlow = true
            break
        }
    }

    return Pair(set, isFlow)
}

fun getPosBelow(
    flow: Pos,
    clays: Set<Pos>,
    waters: MutableSet<Pos>,
    flows: MutableSet<Pos>,
    maxY: Int
): Set<Pos> {
    val set = mutableSetOf<Pos>()
    set.add(flow)

    for (y in (flow.y+1)..maxY) {
        val p = Pos(flow.x, y)
        if (clays.contains(p) || waters.contains(p) || flows.contains(p)) {
            break
        }

        set.add(p)
    }

    return set
}

fun readInput(path: String): Set<Pos> {
    val lines = File(path).readLines()
    val regex = Regex("""([xy])=(\d+), ([xy])=(\d+)\.\.(\d+)""")
    val clay = mutableSetOf<Pos>()
    for (line in lines) {
        val match = regex.matchEntire(line)
        if (match == null) {
            continue
        }


        val first = match.groupValues[1]
        val m = match.groupValues[2].toInt()
        val nMin = match.groupValues[4].toInt()
        val nMax = match.groupValues[5].toInt()

        for (n in nMin..nMax) {
            val p: Pos
            if (first == "x") {
                p = Pos(m,n)
            } else {
                p = Pos(n,m)
            }

            clay.add(p)
        }
    }
    return clay
}

//fun printGrid(
//    water:Set<Pos>,
//    flow:Set<Pos>,
//    clays:Set<Pos>) {
//    val minX = clays.minBy { it.x }!!.x-1
//    val maxX = clays.maxBy { it.x }!!.x+1
//    val minY = clays.minBy { it.y }!!.y
//    val maxY = clays.maxBy { it.y }!!.y
//    for (y in minY..maxY) {
//        for (x in minX..maxX) {
//            var p = Pos(x,y)
//            val icon = when {
//                water.contains(p) -> '~'
//                flow.contains(p) -> '|'
//                clays.contains(p) -> '#'
//                else -> ' '
//            }
//            print(icon)
//        }
//        println()
//    }
//    println()
//}