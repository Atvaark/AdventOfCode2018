package aoc.day14

import java.io.File

data class Input1Checker(val input: Int, val size: Int, var result: String = "")
data class Input2Checker(val pattern: List<Int>, var matchIndex: Int=-1, var matchPos: Int=0, var result: Int = -1)

fun main(args: Array<String>) {
    val inputText = File("input/day14.txt").readText().trim()
    val inputPattern = inputText.toCharArray().map(Character::getNumericValue)
    val input = inputText.toInt()

    val recipies = mutableListOf<Int>()
    recipies.add(3)
    recipies.add(7)
    val elves = mutableMapOf<Int, Int>()
    elves[0] = 0
    elves[1] = 1

    val checker1 = Input1Checker(input, 10)
    val checker2 = Input2Checker(inputPattern)
    while (checker1.result == "" || checker2.result == -1) {
        var sum = 0
        for (entry in elves.iterator()) {
            sum += recipies[entry.value]
        }

        if (sum >= 10) {
            val new1 = (sum - sum.rem(10))/10
            recipies.add(new1)
            check1(recipies, checker1)
            check2(recipies, checker2)
        }

        val new2 = sum.rem(10)
        recipies.add(new2)
        check1(recipies, checker1)
        check2(recipies, checker2)

        for (entry in elves.iterator()) {
            val newPos = (entry.value + (1 + recipies[entry.value])) % recipies.size
            elves[entry.key] = newPos
        }
    }

    println(checker1.result)
    println(checker2.result)
}

fun check1(recipies: MutableList<Int>, checker: Input1Checker) {
    if (checker.result != "" || recipies.size < checker.input + checker.size) {
        return
    }

    var s = ""
    for (i in recipies.size-10 until recipies.size) {
        s += recipies[i]
    }

    checker.result = s
}

fun check2(recipies: MutableList<Int>, checker: Input2Checker) {
    if (recipies.size < checker.pattern.size || checker.result > 0) {
        return
    }

    val i = recipies.size-checker.pattern.size
    var found = true
    for (j in 0 until checker.pattern.size) {
        if (recipies[i+j] != checker.pattern[j]) {
            found = false
            break
        }
    }

    if (found) {
        checker.result = i
    }
}
