package aoc.day02

import java.io.File

fun main(args: Array<String>) {
    val lines = File("input/day02.txt").readLines()

    val result1 = get1(lines)
    println(result1)


    val result2 = get2(lines)
    println(result2) // efmyhuckqldtwjyvisipargno
}

private fun get1(lines: List<String>): Int {
    var count2 = 0
    var count3 = 0
    lines.forEach { line ->
        val charCounts = HashMap<Char, Int>()
        line.forEach {char ->
            val i = charCounts.getOrDefault(char, 0)
            charCounts[char] = i + 1
        }

        var mul2 = false
        var mul3 = false
        charCounts.forEach {
            if (it.value == 2) {
                mul2 = true
            } else if (it.value == 3) {
                mul3 = true
            }
        }

        if (mul2) {
            count2 += 1
        }

        if (mul3) {
            count3 += 1
        }
    }

    return count2 * count3
}


fun get2(lines: List<String>): String {
    for (i in 0 until lines.size) {
        val line = lines[i]
        check@ for (j in i + 1 until lines.size)  {
            val otherLine = lines[j]

            if (line.length == otherLine.length) {
                val common = mutableListOf<Char>()
                var uncommon = false
                for (charIndex in 0 until line.length) {
                    val c0 = line[charIndex]
                    val c1 = otherLine[charIndex]

                    if (c0 == c1) {
                        common.add(c0)
                    } else if (!uncommon) {
                        uncommon = true
                    } else {
                        continue@check
                    }
                }

                if (uncommon) {
                    return String(common.toCharArray())
                }
            }
        }
    }

    return ""
}