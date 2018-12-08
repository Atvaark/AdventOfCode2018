package aoc.day08

import java.io.File

fun main(args: Array<String>) {

    val text = File("input/day08.txt").readText()
    val input = text.split(Regex("""\s""")).map { it.toInt() }
    println(input)

    val sum = getNodeSum(input.iterator())
    println(sum)

    val value = getNodeValue(input.iterator())
    println(value)

}

fun getNodeSum(iterator: Iterator<Int>): Int {
    var sum = 0
    if (iterator.hasNext()){
        val childCount = iterator.next()
        val metaCount = iterator.next()

        for (c in 1..childCount) {
            sum += getNodeSum(iterator)
        }

        for (m in 1..metaCount) {
            sum += iterator.next()
        }
    }

    return sum
}

fun getNodeValue(iterator: Iterator<Int>): Int {
    var sum = 0
    if (iterator.hasNext()){
        val childCount = iterator.next()
        val metaCount = iterator.next()

        val childValues = mutableMapOf<Int, Int>()
        for (c in 1..childCount) {
            childValues[c] = getNodeValue(iterator)
        }

        for (m in 1..metaCount) {
            if (childValues.isEmpty()) {
                sum += iterator.next()
            } else {
                val index = iterator.next()
                if (childValues.contains(index)) {
                    sum += childValues[index]!!
                }
            }
        }
    }

    return sum
}
