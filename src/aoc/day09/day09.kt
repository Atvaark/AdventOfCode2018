package aoc.day09

import java.io.File
import java.math.BigInteger

fun main(args: Array<String>) {
    val text = File("input/day09.txt").readText().trim()
    val matchEntire = Regex("""(\d+) players; last marble is worth (\d+) points""").matchEntire(text) ?: return

    val playerCount = matchEntire.groupValues[1].toInt()
    val lastMarbleScore = matchEntire.groupValues[2].toInt()

    println("$playerCount players; last marble is worth $lastMarbleScore points")

    val maxScore1 = getScore(playerCount, lastMarbleScore)
    println(maxScore1)

    val maxScore2 = getScore(playerCount, lastMarbleScore * 100)
    println(maxScore2)
}

private fun getScore(playerCount: Int, lastMarbleScore: Int): BigInteger {
    val ring = mutableListOf<Int>()
    ring.add(0)

    val scores = mutableMapOf<Int, BigInteger>()
    for (i in 1..playerCount) {
        scores[i] = BigInteger.valueOf(0)
    }

    var index = 0
    for (i in 1..lastMarbleScore) {
        val player = ((i - 1) % playerCount) + 1

        if (i % 23 == 0) {
            val removeIndex = (ring.size + index - 7) % ring.size
            val newScore = i + ring.removeAt(removeIndex)
            index = removeIndex % ring.size
            scores[player] = scores[player]!!.add(BigInteger.valueOf(newScore.toLong()))
        } else {
            var insertIndex: Int
            if (ring.size < 2) {
                insertIndex = index + 1
            } else {
                insertIndex = (index + 2) % (ring.size)
                if (insertIndex == 0) {
                    insertIndex = ring.size
                }
            }

            if (insertIndex == ring.size) {
                ring.add(i)
            } else {
                ring.add(insertIndex, i)
            }

            index = insertIndex
        }

//        println("turn $i player $player index $index")

//        print("[$player] ")
//        for (i in ring.indices) {
//            val v = ring[i]
//            if (i == index) {
//                print("($v) ")
//            } else {
//                print("$v ")
//            }
//        }
//        println()

    }

    return scores.maxBy { it.value }!!.value
}