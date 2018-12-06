package aoc.day06

import java.io.File

fun main(args: Array<String>) {
    val lines = File("input/day06.txt").readLines()

    val coordinates = mutableSetOf<Pair<Int, Int>>()
    val coordinateRegex = Regex("""^(\d+), (\d+)""")
    for (line in lines) {
        val matchResult = coordinateRegex.matchEntire(line)
        if (matchResult != null) {
            coordinates.add(Pair(
                matchResult.groupValues[1].toInt(),
                matchResult.groupValues[2].toInt()
            ))
        }
    }

    if (coordinates.size == 0) {
        return
    }

	val padding = 0 // HACK
    val minX = coordinates.minBy { it.first }!!.first - padding
    val maxX = coordinates.maxBy { it.first }!!.first + padding
    val minY = coordinates.minBy { it.second }!!.second - padding
    val maxY = coordinates.maxBy { it.second }!!.second + padding

    val distanceMap = mutableMapOf<Pair<Int, Int>, MutableMap<Pair<Int, Int>, Int>>() // location -> coordinate -> distance
    for (x in minX..maxX) {
        for (y in minY..maxY) {
            val distMap2 = mutableMapOf<Pair<Int, Int>, Int>()

            for (coordinate in coordinates) {
                val distance = Math.abs(x - coordinate.first) + Math.abs(y - coordinate.second)
                distMap2[coordinate] = distance
            }

            distanceMap[Pair(x, y)] = distMap2
        }
    }

    val coordinateToArea = mutableMapOf<Pair<Int, Int>, Int>() // coordinate -> area
    val locationToClosestCoordinate = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()
    for (locationEntry in distanceMap) {
        val grps = locationEntry.value.asIterable().groupBy { it.value }
        val minBy = grps.minBy { it.key }!!
        if (minBy.value.size == 1) {
            val minCoordinate = minBy.value.single().key

            locationToClosestCoordinate[locationEntry.key] = minCoordinate
            coordinateToArea[minCoordinate] = coordinateToArea.getOrDefault(minCoordinate, 0) + 1
        }
    }
    
    // remove regions on edges
	val infinite = mutableSetOf<Pair<Int,Int>>()
	for (x in minX..maxX) {
		if (locationToClosestCoordinate.contains(Pair(x, minY))) {
			infinite.add(locationToClosestCoordinate[Pair(x, minY)]!!)
		}
		if (locationToClosestCoordinate.contains(Pair(x, maxY))) {
			infinite.add(locationToClosestCoordinate[Pair(x, maxY)]!!)
		}
	}
	for (y in minY..maxY) {
		if (locationToClosestCoordinate.contains(Pair(minX, y))) {
			infinite.add(locationToClosestCoordinate[Pair(minX, y)]!!)
		}
		if (locationToClosestCoordinate.contains(Pair(maxX, y))) {
			infinite.add(locationToClosestCoordinate[Pair(maxX, y)]!!)
		}
	}

    val result1 = coordinateToArea.filter { (k,_) -> !infinite.contains(k) }.maxBy { it.value }!!
    println(result1.value)
	
	val maxDistance = 10000
	val regions2 = mutableSetOf<Pair<Int, Int>>()
    for (x in minX..maxX) {
        cols@ for (y in minY..maxY) {            
			var totalDistance = 0
            for (coordinate in coordinates) {
                val distance = Math.abs(x - coordinate.first) + Math.abs(y - coordinate.second)
                totalDistance += distance
				
				if (totalDistance >= maxDistance) {
					continue@cols
				}
            }
			
			regions2.add(Pair(x,y))            
        }
    }
	
	println(regions2.size)

	//// Draw
    //for (y in minY..maxY) {
    //    for (x in minX..maxX) {
    //        if (coordinates.contains(Pair(x, y))) {
    //            var indexOf = coordinates.indexOf(Pair(x, y))
    //            var toChar = (indexOf + 'A'.toInt())
    //            print(toChar.toChar())
    //        } else {
    //
    //            if (locationToClosestCoordinate.contains(Pair(x,y))) {
    //                var closest = locationToClosestCoordinate[Pair(x,y)]
    //
    //                var indexOf = coordinates.indexOf(closest)
    //                var toChar = (indexOf + 'a'.toInt())
    //                print(toChar.toChar())
    //
    //            } else {
    //                print(".")
    //            }
    //        }
    //    }
    //    println()
    //}
}