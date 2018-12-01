package demo

import java.io.File

fun main(args : Array<String>) {
    var sum = 0
    val lines = File("input/day01.txt").readLines()
    lines.forEach {
        sum += it.toInt()
    }
    println(sum)

    var freq = 0
    var found = false
    val set = hashSetOf<Int>()
    set.add(freq)
    while (!found) {
        run loop@ {
            lines.forEach {
                freq += it.toInt()
                found = set.contains(freq)
                if (found) {
                    return@loop
                }

                set.add(freq)
            }
        }
    }
    println(freq)
}
