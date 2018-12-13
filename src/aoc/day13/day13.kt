package aoc.day13

import java.io.File
import java.lang.Error

data class CartState(val state: Int, val dir: Char)

fun main(args: Array<String>) {
    val lines = File("input/day13.txt").readLines()

    val tracks = mutableMapOf<Pair<Int, Int>, Char>()
    val carts = mutableMapOf<Pair<Int, Int>, CartState>()

	// read
    for (row in lines.withIndex()) {
        for (col in row.value.withIndex()) {
            var c = col.value

            when (c) {
                '<', '^', '>', 'v' -> {				
                    carts[Pair(col.index, row.index)] = CartState(0, c)

                    c = when (c) {
                        '<', '>' -> '-'
                        '^', 'v' -> '|'
                        else -> {
                            throw Error("Unexpected symbol")
                        }
                    }
                }
            }

            when (c) {
                '|','/', '-', '\\', '+' -> {
                    tracks[Pair(col.index,row.index)] = c
                }
				' ' -> {}
				else -> {
					throw Error("Unexpected symbol")
				}
            }
        }
    }

    // run	
	var firstCrashPos: Pair<Int, Int>? = null
	val lastCartPos :Pair<Int, Int>
	runLoop@ while (true) {
		val orderedPositions = carts.keys.sortedWith(compareBy({it.second}, {it.first}))
		
		for (orderedPosition in orderedPositions) {			
			val cart = carts.remove(orderedPosition) ?: continue

			var newState = cart.state
			var newDir = cart.dir
			var newPos:Pair<Int,Int>
			val track = tracks[orderedPosition]
			
			when (track) {
				'|','-' -> {}
				'/' -> {
					newDir = when (cart.dir) {
						'<' -> {
							'v'
						}
						'^' -> {
							'>'
						}
						'>' -> {
							'^'
						}
						'v' -> {
							'<'
						}
						else -> throw Error("Unexpected symbol")
					}
				}
				'\\' -> {
					newDir = when (cart.dir) {
						'<' -> {
							'^'
						}
						'^' -> {
							'<'
						}
						'>' -> {
							'v'
						}
						'v' -> {
							'>'
						}
						else -> throw Error("Unexpected symbol")
					}
				}
				'+' -> {
					when (cart.state) {
						0 -> {
							// left
							newDir = when (cart.dir) {
								'<' -> 'v'
								'^' -> '<'
								'>' -> '^'
								'v' -> '>'
								else -> throw Error()
							}							
						}
						1 -> {
							// straight
							newDir = cart.dir
						}
						2 -> {
							// right
							newDir = when (cart.dir) {
								'<' -> '^'
								'^' -> '>'
								'>' -> 'v'
								'v' -> '<'
								else -> throw Error()
							}
						}
						else -> throw Error("Unexpected symbol")
					}
					newState = (newState + 1) % 3

				}
				else -> throw Error()
			}

			newPos = when (newDir) {
				'<' -> {
					Pair(orderedPosition.first-1, orderedPosition.second)
				}
				'^' -> {
					Pair(orderedPosition.first, orderedPosition.second-1)
				}
				'>' -> {
					Pair(orderedPosition.first+1, orderedPosition.second)
				}
				'v' -> {
					Pair(orderedPosition.first, orderedPosition.second+1)
				}
				else -> throw Error("Unexpected symbol")
			}
			
			if (carts.contains(newPos)) {
				carts.remove(newPos)

				if (firstCrashPos == null) {
					firstCrashPos = newPos
				}
			} else {
				val newCart = CartState(newState, newDir)
				carts[newPos] = newCart
			}
		}
		
		if (carts.size == 1) {
			lastCartPos = carts.keys.single()
			break@runLoop
		}	
	}

	if (firstCrashPos == null) {
		throw Error()
	}

	println("${firstCrashPos.first},${firstCrashPos.second}")
	println("${lastCartPos.first},${lastCartPos.second}")
}