package aoc.day12

import java.io.File

fun main(args: Array<String>) {
    val lines = File("input/day12.txt").readLines()
    val initial = lines[0].substring("initial state: ".length)
    val input = mutableListOf<Pair<String, Char>>()
    val re = Regex("""([.#].+) => ([.#])""")
    for (i in 2..lines.lastIndex) {
        val matchResult = re.matchEntire(lines[i]) ?: continue
        val a = matchResult.groupValues[1]
        val b = matchResult.groupValues[2][0]
        if (b != '#') {
            continue
        }

        input.add(Pair(a,b))
    }


    val sum1 = run(initial, input, 20)
    println("Result 1: $sum1") // 2281


    val sum2 = run(initial, input, 50000000000)
    println("Result 2: $sum2")
}

private fun run(
    initial: String,
    input: MutableList<Pair<String, Char>>,
    genCount: Long
): Int {
    var currentState = mutableMapOf<Int, Char>()
    initial.withIndex().iterator().forEach {
        if (it.value == '#') {
            currentState[it.index] = '#'
        }
    }

    for (gen in 1..genCount) {
        val nextState = mutableMapOf<Int, Char>()

        val minIndex = currentState.keys.min()!! - 3
        val maxIndex = currentState.keys.max()!! + 3
        for (i in minIndex..maxIndex) {
            val (shouldGrow, growChar) = shouldGrow(i, currentState, input)
            if (shouldGrow) {
                nextState[i] = growChar
            }
        }

        currentState = nextState


        if (gen.rem(1000) <= 0) {
            val tmp1 = sumPlants(currentState)
            println("gen $gen result $tmp1")
        }
    }

    val sum = sumPlants(currentState)
    return sum
}

private fun sumPlants(currentState: MutableMap<Int, Char>): Int {
    var sum = 0
    for (entry in currentState) {
        sum += entry.key
    }
    return sum
}

//fun printState(gen: Int, currentState: MutableMap<Int, Char>) {
//    print("$gen: ")
//    for (i in -3..35) {
//        val c = currentState[i] ?: '.'
//        print(c)
//    }
//    println()
//}

fun shouldGrow(i: Int, currentState: Map<Int, Char>, mutations: MutableList<Pair<String, Char>>): Pair<Boolean, Char> {
    mutationLoop@ for (mutation in mutations) {
        for (j in 0..mutation.first.lastIndex) {
            val expectedChar = mutation.first[j]
            val actualChar = currentState[i+j-2] ?: '.'
            if (expectedChar != actualChar) {
                continue@mutationLoop
            }
        }

        return Pair(true, mutation.second)
    }

    return Pair(false, '.')
}
