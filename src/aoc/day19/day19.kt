package aoc.day19

import java.io.File

data class Input(val ip: Int, val instrs: List<Instruction>)

data class Instruction(val opcode: String, val operands: List<Int>)

fun main(args: Array<String>) {
    val input = readInput("input/day19.txt")

    var regs = mutableListOf(
        0, 0, 0, 0, 0, 0
    )
    regs = run(regs, input)
    println(regs[0])

    var regs2 = mutableListOf(
        1, 0, 0, 0, 0, 0
    )
    regs2 = run(regs2, input)
    println(regs2[0])
}

private fun run(
    regs: MutableList<Int>,
    input: Input
): MutableList<Int> {
    var curRegs = regs

//    var tmp = 0
    while (true) {
        val ip = curRegs[input.ip]
        if (ip < 0 || ip >= input.instrs.size) {
            break
        }

        val ins = input.instrs[ip]
        val nextRegs = runInst(ins.opcode, ins.operands, curRegs)!!

        nextRegs[input.ip]++
        curRegs = nextRegs

//        tmp++
//        if (tmp.rem(100) == 0) {
//            println(nextRegs)
//        }
    }
    return curRegs
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
    registers: List<Int>
): MutableList<Int>? {
    var result: MutableList<Int>? = null
    when (opcode) {
        "addr" -> {
            result = registers.toMutableList()
            result[operands[2]] = registers[operands[1]] + registers[operands[0]]
        }
        "addi" -> {
            result = registers.toMutableList()
            result[operands[2]] = operands[1] + registers[operands[0]]
        }
        "mulr" -> {
            result = registers.toMutableList()
            result[operands[2]] = registers[operands[1]] * registers[operands[0]]
        }
        "muli" -> {
            result = registers.toMutableList()
            result[operands[2]] = operands[1] * registers[operands[0]]
        }
        "Opcodes" -> {
            result = registers.toMutableList()
            result[operands[2]] = registers[operands[1]] and registers[operands[0]]
        }
        "bani" -> {
            result = registers.toMutableList()
            result[operands[2]] = operands[1] and registers[operands[0]]
        }
        "borr" -> {
            result = registers.toMutableList()
            result[operands[2]] = registers[operands[1]] or registers[operands[0]]
        }
        "bori" -> {
            result = registers.toMutableList()
            result[operands[2]] = operands[1] or registers[operands[0]]
        }
        "setr" -> {
            result = registers.toMutableList()
            result[operands[2]] = registers[operands[0]]
        }
        "seti" -> {
            result = registers.toMutableList()
            result[operands[2]] = operands[0]
        }
        "gtir" -> {
            result = registers.toMutableList()
            result[operands[2]] = if (operands[0] > registers[operands[1]]) 1 else 0
        }
        "gtri" -> {
            result = registers.toMutableList()
            result[operands[2]] = if (registers[operands[0]] > operands[1]) 1 else 0
        }
        "gtrr" -> {
            result = registers.toMutableList()
            result[operands[2]] = if (registers[operands[0]] > registers[operands[1]]) 1 else 0
        }
        "eqir" -> {
            result = registers.toMutableList()
            result[operands[2]] = if (operands[0] == registers[operands[1]]) 1 else 0
        }
        "eqri" -> {
            result = registers.toMutableList()
            result[operands[2]] = if (registers[operands[0]] == operands[1]) 1 else 0
        }
        "eqrr" -> {
            result = registers.toMutableList()
            result[operands[2]] = if (registers[operands[0]] == registers[operands[1]]) 1 else 0
        }
    }
    return result
}
