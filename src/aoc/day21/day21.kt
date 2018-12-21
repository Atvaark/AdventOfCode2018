package aoc.day21

import java.io.File

data class Input(val ip: Int, val instrs: List<Instruction>)

data class Instruction(val opcode: String, val operands: List<Int>)

fun main(args: Array<String>) {
    val input = readInput("input/day21.txt")

    val regs = mutableListOf(
            0, 0, 0, 0, 0, 0
    )
    val result = run(regs, input)
    println(result.first)
    println(result.second)
}

private fun run(
        regs: MutableList<Int>,
        input: Input
): Pair<Int?, Int?> {
    var least: Int? = null
    var most: Int? = null
    val set = HashSet<Int>()

    while (true) {
        val ip = regs[input.ip]
        if (ip < 0 || ip >= input.instrs.size) {
            break
        }

        val ins = input.instrs[ip]
        runInst(ins.opcode, ins.operands, regs)

        regs[input.ip]++

        if (regs[input.ip] == 29) {
            val v = regs[5]
            if (least == null) {
                // What is the lowest non-negative integer value for register 0 that causes the program to halt after executing the fewest instructions?
                least = v
            }

            if (set.contains(v)) {
                // What is the lowest non-negative integer value for register 0 that causes the program to halt after executing the most instructions?
                break
            } else {
                set.add(v)
                most = v
            }
        }
    }

    return Pair(least, most)
}

fun readInput(s: String): Input {
    val lines = File(s).readLines()

    val l = lines[0]
    val match = Regex("""^#ip (\d)$""").matchEntire(l) ?: throw error("ip not found")

    val ipRegisterId = match.groupValues[1].toInt()
    val lineRegex = Regex("""^(\w+) (\d+) (\d+) (\d+)$""")
    val instructions = mutableListOf<Instruction>()
    for (line in lines.drop(1)) {
        val lineMatch = lineRegex.matchEntire(line) ?: throw error("invalid line")

        instructions.add(
                Instruction(
                        lineMatch.groupValues[1],
                        listOf(
                                lineMatch.groupValues[2].toInt(),
                                lineMatch.groupValues[3].toInt(),
                                lineMatch.groupValues[4].toInt()
                        )
                ))
    }

    return Input(ipRegisterId, instructions)
}
private fun runInst(
        opcode: String,
        operands: List<Int>,
        registers: MutableList<Int>
) {
    when (opcode) {
        "addr" -> {
            registers[operands[2]] = registers[operands[1]] + registers[operands[0]]
        }
        "addi" -> {
            registers[operands[2]] = operands[1] + registers[operands[0]]
        }
        "mulr" -> {
            registers[operands[2]] = registers[operands[1]] * registers[operands[0]]
        }
        "muli" -> {
            registers[operands[2]] = operands[1] * registers[operands[0]]
        }
        "banr" -> {
            registers[operands[2]] = registers[operands[1]] and registers[operands[0]]
        }
        "bani" -> {
            registers[operands[2]] = operands[1] and registers[operands[0]]
        }
        "borr" -> {
            registers[operands[2]] = registers[operands[1]] or registers[operands[0]]
        }
        "bori" -> {
            registers[operands[2]] = operands[1] or registers[operands[0]]
        }
        "setr" -> {
            registers[operands[2]] = registers[operands[0]]
        }
        "seti" -> {
            registers[operands[2]] = operands[0]
        }
        "gtir" -> {
            registers[operands[2]] = if (operands[0] > registers[operands[1]]) 1 else 0
        }
        "gtri" -> {
            registers[operands[2]] = if (registers[operands[0]] > operands[1]) 1 else 0
        }
        "gtrr" -> {
            registers[operands[2]] = if (registers[operands[0]] > registers[operands[1]]) 1 else 0
        }
        "eqir" -> {
            registers[operands[2]] = if (operands[0] == registers[operands[1]]) 1 else 0
        }
        "eqri" -> {
            registers[operands[2]] = if (registers[operands[0]] == operands[1]) 1 else 0
        }
        "eqrr" -> {
            registers[operands[2]] = if (registers[operands[0]] == registers[operands[1]]) 1 else 0
        }
    }
}