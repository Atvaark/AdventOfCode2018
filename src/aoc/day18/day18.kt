package aoc.day18

import java.io.File

data class Pos(var x:Int, var y:Int)

enum class Type {
    Tree,
    Lumberyard
}

fun main(args: Array<String>) {
    val map = mutableMapOf<Pos, Type>()

    val lines = File("input/day18.txt").readLines()
    for (y in lines.withIndex()) {
        for (x in y.value.withIndex()) {
            when (x.value) {
                '|' -> Type.Tree
                '#' -> Type.Lumberyard
                else -> null
            }?.let {
                map[Pos(x.index, y.index)] = it
            }
        }
    }

    val r1 = run(map, 10)
    println(r1)

    val r2 = run(map, 1000000000)
    println(r2)
}

private fun run(map: MutableMap<Pos, Type>, mins: Int): Int {
    var cur = map
    val w = cur.map { it.key.x }.max()!!
    val h = cur.map { it.key.y }.max()!!
//    printGrid(cur, w, h)
    for (m in 1..mins) {
        val next = mutableMapOf<Pos, Type>()

        for (y in 0..h) {
            for (x in 0..w) {
                val p = Pos(x, y)
                val t = cur[p]
                when (t) {
                    null -> {
                        if (findType(cur, p, Type.Tree, 3, w, h)) {
                            next[p] = Type.Tree
                        }
                    }
                    Type.Tree -> {
                        if (findType(cur, p, Type.Lumberyard, 3, w, h)) {
                            next[p] = Type.Lumberyard
                        } else {
                            next[p] = Type.Tree
                        }
                    }
                    Type.Lumberyard -> {
                        if (findType(cur, p, Type.Lumberyard, 1, w, h)
                            &&
                            findType(cur, p, Type.Tree, 1, w, h)
                        ) {
                            next[p] = Type.Lumberyard
                        }
                    }
                }
            }
        }

        cur = next
//        printGrid(cur, w, h)
        if (m.rem(1000) == 0) {
            print('.')
            val tc = cur.filter { it.value == Type.Tree }.count()
            val lc = cur.filter { it.value == Type.Lumberyard }.count()
            val value = tc * lc
            println("Temporary: $m $value")
        }
        // -> Find final score via loop start pos and loop size
    }
    println()

    val tc = cur.filter { it.value == Type.Tree }.count()
    val lc = cur.filter { it.value == Type.Lumberyard }.count()
    return tc * lc
}

//fun printGrid(cur: MutableMap<Pos, Type>, w: Int, h: Int) {
//    for (y in 0..h) {
//        for (x in 0..w) {
//            val s = when (cur[Pos(x,y)]) {
//                null -> '.'
//                Type.Tree -> '|'
//                Type.Lumberyard -> '#'
//            }
//            print(s)
//        }
//        println()
//    }
//    println()
//}

fun findType(map: MutableMap<Pos, Type>, pos: Pos, type: Type, count: Int, w: Int, h: Int): Boolean {
    var found = 0

    for (y in (pos.y-1)..(pos.y+1)) {
        if (y < 0 || y > h) {
            continue
        }

        for (x in (pos.x-1)..(pos.x+1)) {
            if (x < 0 || x > w) {
                continue
            }

            val p = Pos(x,y)
            if (p == pos) {
                continue
            }

            map[p]?.let {
                if (it == type) {
                    found++
                }

                if (found == count) {
                    return true
                }
            }
        }
    }

    return false
}
