package aoc.day05

import java.io.File

fun main(args: Array<String>) {
    var text = File("input/day05.txt").readText().trimEnd()

    var input = react(text)
    val result1 = input.size
    println(result1)

    val types = text.toLowerCase().groupBy { it }.keys

    var minType = Char.MAX_VALUE
    var minLen = Int.MAX_VALUE
    for (type in types) {
        val text2 = String(text.toMutableList().filter { !it.equals(type, ignoreCase = true) }.toCharArray())
        val input2  = react(text2)

        if (input2.length < minLen) {
            minType = type
            minLen = input2.length
        }
    }
	
	val result2 = minLen
	println(result2)
}

private fun react(text: String): MutableList<Char> {
    var cont = true
    var input = text.toMutableList()
    while (cont) {
        cont = false
        val nextInput = mutableListOf<Char>()

        var index = 0
        while (index < input.size - 1) {
            val c0 = input[index]
            val c1 = input[index + 1]

            if (c0 != c1
                && c0.equals(c1, ignoreCase = true)
            ) {
                cont = true
                index++
				
                if (index == input.lastIndex) {
                    nextInput.add(input[index])
                }
            } else {
                nextInput.add(c0)

                if (index + 1 == input.lastIndex) {
                    nextInput.add(c1)
                }
            }

            index++
        }

        input = nextInput
    }

    return input
}
