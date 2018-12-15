package aoc.day15

import java.io.File
import java.util.*

enum class Type {
    None,
    Wall,
    Elf,
    Goblin
}

data class Tile(val type: Type, var hp: Int = 0, val attackPower: Int = 0)

fun main(args: Array<String>) {
    val lines = File("input/day15.txt").readLines()
    if (lines.isEmpty()) {
        return
    }


    val result1 = part1(lines)
    println(result1)
    val result2 = part2(lines)
    println(result2)
}

private fun part1(lines: List<String>): Int {
    val tiles = readTiles(lines, mapOf('E' to 3, 'G' to 3))
    val result = run(tiles)
    return result.first
}

fun part2(lines: List<String>): Int {
    var attackMin = 3
    var attack = 16
    var attackMax = Int.MAX_VALUE
    while (true) {
        val tiles = readTiles(lines, mapOf('E' to attack, 'G' to 3))
        val beforeCount = tiles.count { it.value.type == Type.Elf }
        val result = run(tiles)
        val afterCount = tiles.count { it.value.type == Type.Elf }
        if (result.second != Type.Elf || beforeCount != afterCount) {
            if (attack == attackMin && (attackMin == attackMax -1)) {
                attack += 1
            } else {
                attackMin = attack

                if (attackMax == Int.MAX_VALUE) {
                    attack *= 2
                } else {
                    attack = (attackMax - attackMin)/2 + attackMin// + 1
                }
            }

        } else {
            if (attack == attackMax && (attackMin == attackMax -1)) {
                return result.first
            }

            attackMax = attack

            attack = (attackMax - attackMin)/2 + attackMin

        }
    }
}

private fun readTiles(
    lines: List<String>,
    attackMap: Map<Char, Int>
): MutableMap<Pair<Int, Int>, Tile> {
    val tiles = mutableMapOf<Pair<Int, Int>, Tile>()
    for (row in lines.withIndex()) {
        for (column in row.value.withIndex()) {
            val hp = 200
            val attackPower = attackMap[column.value] ?: 0
            when (column.value) {
                '#' -> Tile(Type.Wall)
                'E' -> Tile(Type.Elf, hp, attackPower)
                'G' -> Tile(Type.Goblin, hp, attackPower)
                else -> null
            }?.let {
                tiles[Pair(column.index, row.index)] = it
            }
        }
    }
    return tiles
}

fun run(tiles: MutableMap<Pair<Int, Int>, Tile>): Pair<Int,Type> {
    var completedRounds = 0

    while (true) {
//        println("After $completedRounds round${if (completedRounds!=1) 's' else ' '}:")
//        printTiles(tiles)

        val turnOrder = getTurnOrder(tiles)


        var completed = true
        for (turnEntry in turnOrder) {
            if (turnEntry.value.hp <= 0) {
                continue
            }

            var curPos = turnEntry.key
            var adjacentTargets = getAdjacentTargets(tiles, turnEntry, curPos)
            if (adjacentTargets.isEmpty()) {
                val targets = getTargets(tiles, turnEntry.value.type)
                if (targets.isEmpty()){
                    completed = false
                    break
                }

                val targetSpaces = getTargetSpaces(tiles, targets)
                if (targetSpaces.isEmpty()) {
                    continue
                }

                val nearestSpace = getNearestSpace(tiles, targetSpaces, turnEntry)
                if (nearestSpace != null) {
                    tiles.remove(curPos)
                    tiles[nearestSpace]= turnEntry.value
                    curPos = nearestSpace
                }
            }

            adjacentTargets = getAdjacentTargets(tiles, turnEntry, curPos)
            if (adjacentTargets.any()) {
                val adjacentTarget: Map.Entry<Pair<Int,Int>,Tile>
                adjacentTarget = if (adjacentTargets.size == 1) {
                    adjacentTargets.asIterable().single()
                } else {
                    val sorted = adjacentTargets
                        .asIterable()
                        .sortedWith(compareBy({it.value.hp}, { it.key.second }, { it.key.first}))
                    sorted.first()
                }
                adjacentTarget.value.hp -= turnEntry.value.attackPower
                if (adjacentTarget.value.hp <= 0) {
                    tiles.remove(adjacentTarget.key)
                }
            }
        }


        if (completed) {
            completedRounds++
        }

        if (!completed || !targetsLeft(tiles)){
            val hpSum = tiles.values.sumBy { it.hp }
//            println("r $completedRounds hp $hpSum ")
//            println()
            val type = tiles.asIterable().first { it.value.type != Type.Wall }.value.type
            return Pair(completedRounds * hpSum, type)
        }
    }
}

fun targetsLeft(tiles: MutableMap<Pair<Int, Int>, Tile>): Boolean {
    val types = hashSetOf<Type>()
    for (tile in tiles.filter { it.value.type != Type.Wall }) {
        types.add(tile.value.type)

        if (types.size > 1) {
            return true
        }
    }

    return false
}

data class Node(val pos:Pair<Int,Int>, val dist: Int, val prev:Node?)

fun getNearestSpace(
    tiles: MutableMap<Pair<Int, Int>, Tile>,
    targetSpaces: MutableSet<Pair<Int, Int>>,
    turnEntry: Map.Entry<Pair<Int, Int>, Tile>
): Pair<Int, Int>? {
    val targetNodes = mutableListOf<Node>()
    val startSpaces = getTargetSpaces(tiles, mapOf(turnEntry.key to turnEntry.value))

//    if (turnEntry.value.type == Type.Goblin && turnEntry.key.first > 3 && turnEntry.key.second > 3) {
//        printTiles(tiles)
//    }

    loop@ for (targetSpace in targetSpaces) {
        val visited = hashSetOf<Pair<Int, Int>>()
        val queue = ArrayDeque<Node>()
        queue.offer(Node(targetSpace, 0, null))
        while (queue.isNotEmpty()) {
            val current = queue.poll()

            if (startSpaces.contains(current.pos)) {
                targetNodes.add(current)
//                continue@loop
            }

            val adjacentPositions = getAdjacentSpaces(tiles, current.pos)
            for (adjacentPosition in adjacentPositions) {
                val adjacentNode = Node(adjacentPosition, current.dist+1, current)

                if (!visited.contains(adjacentPosition)) {
                    queue.offer(adjacentNode)
                    visited.add(adjacentPosition)
                }
            }
        }
    }

//    spaceLoop@ for (targetSpace in targetSpaces) {
//        val visited = hashSetOf<Pair<Int, Int>>()
//        val queue = ArrayDeque<Node>()
//        queue.push(Node(targetSpace, 0, null))
//        while (queue.isNotEmpty()) {
//            val current = queue.pop()
//
//            val adjacentPositions = getAdjacentPositions(tiles, current.pos)
//            for (adjacentPosition in adjacentPositions) {
//                val adjacentNode = Node(adjacentPosition, current.dist+1, current)
//
//                if (startSpaces.contains(adjacentPosition)) {
//                    targetNodes.add(adjacentNode)
//                    continue@spaceLoop
//                }
//
//                if (!tiles.contains(adjacentPosition)
//                    &&
//                    !visited.contains(adjacentPosition)) {
//                    queue.push(adjacentNode)
//                }
//
//                visited.add(adjacentPosition)
//            }
//        }
//    }

    if (targetNodes.isEmpty()) {
        return null
    }

    val foundPos: Pair<Int,Int>
    val foundNodes = targetNodes.groupBy { it.dist }.asIterable().sortedBy { it.key }.first()
    foundPos = if (foundNodes.value.size == 1) {
        // single
        foundNodes.value.single().pos // start
    } else {
        // multiple
        val sorted = foundNodes.value
            .map {
                Pair(it.pos, getTargetPos(it)) // start target
            }.sortedWith(compareBy({ it.second.second }, { it.second.first })).toList()
        sorted.first().first

    }

    return foundPos
}

fun getTargetPos(node: Node): Pair<Int, Int> {
    var n = node
    while (n.prev != null) {
        n = n.prev!!
    }

    return n.pos
}

fun getAdjacentSpaces(tiles: MutableMap<Pair<Int, Int>, Tile>, pos: Pair<Int, Int>): List<Pair<Int, Int>> {
    return listOf(
        Pair(pos.first, pos.second - 1),
        Pair(pos.first - 1, pos.second),
        Pair(pos.first + 1, pos.second),
        Pair(pos.first, pos.second + 1)
    )
        .filter {
            !tiles.contains(it)
        }.toList()
}


fun getAdjacentTargets(
    tiles: MutableMap<Pair<Int, Int>, Tile>,
    current: Map.Entry<Pair<Int, Int>, Tile>,
    currentPos: Pair<Int, Int>
): Map<Pair<Int, Int>, Tile> {
    val targetType = when (current.value.type) {
        Type.Elf -> Type.Goblin
        Type.Goblin -> Type.Elf
        else -> Type.None
    }

    return listOf(
        Pair(currentPos.first, currentPos.second - 1),
        Pair(currentPos.first - 1, currentPos.second),
        Pair(currentPos.first + 1, currentPos.second),
        Pair(currentPos.first, currentPos.second + 1))
        .filter {
            tiles.contains(it)
        }
        .map {
            Pair(it, tiles[it]!!)
        }
        .filter {
            it.second.type == targetType
        }
        .toMap()
}

fun getTargetSpaces(tiles: MutableMap<Pair<Int, Int>, Tile>, targets: Map<Pair<Int, Int>, Tile>): MutableSet<Pair<Int, Int>> {
    val targetSpaces = mutableSetOf<Pair<Int, Int>>()

    for (target in targets) {
        val pos = target.key
        for (spacePos in listOf(
            Pair(pos.first, pos.second - 1),
            Pair(pos.first - 1, pos.second),
            Pair(pos.first + 1, pos.second),
            Pair(pos.first, pos.second + 1)
        ).filter {
            !tiles.containsKey(it)
        }) {
            targetSpaces.add(spacePos)
        }
    }

    return targetSpaces
}

fun getTargets(tiles: MutableMap<Pair<Int, Int>, Tile>, type: Type): Map<Pair<Int, Int>, Tile> {
    val targetType = when (type) {
        Type.Elf -> Type.Goblin
        Type.Goblin -> Type.Elf
        else -> Type.None
    }

    return tiles.filter {
        it.value.type == targetType
    }.toMap()
}

fun getTurnOrder(tiles: MutableMap<Pair<Int, Int>, Tile>): List<Map.Entry<Pair<Int, Int>, Tile>> {
    return tiles
        .asIterable()
        .filter { it.value.type == Type.Elf || it.value.type == Type.Goblin }
        .sortedWith(compareBy({ it.key.second }, { it.key.first }))
        .toList()
}

//fun printTiles(tiles: MutableMap<Pair<Int, Int>, Tile>) {
//    val minX = tiles.keys.minBy { it.first }!!.first
//    val maxX = tiles.keys.maxBy { it.first }!!.first
//    val minY = tiles.keys.minBy { it.second }!!.second
//    val maxY = tiles.keys.maxBy { it.second }!!.second
//
//    for (y in minY..maxY) {
//        for (x in minX..maxX) {
//            val tile = tiles[Pair(x, y)]
//            val tileSymbol = when {
//                tile == null -> '.'
//                tile.type == Type.Wall -> '#'
//                tile.type == Type.Elf -> 'E'
//                tile.type == Type.Goblin -> 'G'
//                else -> ' '
//            }
//            print(tileSymbol)
//        }
//        println()
//    }
//
//    println()
//}
