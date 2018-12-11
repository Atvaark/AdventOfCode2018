package aoc.day11

import java.io.File

fun main(args: Array<String>) {
    val serialId = File("input/day11.txt").readText().trim().toInt()

    val size = 300

    val columns = Array(size) { Array(size) {0} }
    for (columnIndex in columns.indices) {
        val column = columns[columnIndex]
        for (rowIndex in column.indices) {
            val rackId = (columnIndex+1) + 10
            var powerLevel = rackId
            powerLevel *= (rowIndex+1)
            powerLevel += serialId
            powerLevel *= rackId
            powerLevel -= (powerLevel % 100)
            powerLevel /= 100
            powerLevel %= 10
            powerLevel -= 5

            column[rowIndex] = powerLevel
        }
    }

    var maxPower = 0
    var maxSquare = Triple(-1, -1, -1)
    val squareSize33 = 3
    val size33 = getSize(size, squareSize33, columns, maxPower, maxSquare)
    maxPower = size33.first
    maxSquare = size33.second
    println("${maxSquare.first},${maxSquare.second}")

    for (squareSize in 1..300) {
        val result = getSize(size, squareSize, columns, maxPower, maxSquare)
        maxPower = result.first
        maxSquare = result.second
    }
    println("${maxSquare.first},${maxSquare.second},${maxSquare.third}")
}

private fun getSize(
    size: Int,
    squareSize: Int,
    columns: Array<Array<Int>>,
    maxPower: Int,
    maxSquare: Triple<Int, Int, Int>
): Pair<Int, Triple<Int, Int, Int>> {
    var maxPower1 = maxPower
    var maxSquare1 = maxSquare
    for (x in 0 until size - squareSize + 1) {
        for (y in 0 until size - squareSize + 1) {

            var power = 0
            for (xx in 0 until squareSize) {
                for (yy in 0 until squareSize) {
//                    println("$x $xx $y $yy")
                    power += columns[x + xx][y + yy]
                }
            }

            if (power > maxPower1) {
                maxPower1 = power
                maxSquare1 = Triple(x + 1, y + 1, squareSize)
            }
        }
    }
    return Pair(maxPower1, maxSquare1)
}