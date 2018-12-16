package aoc.day16

import java.io.File

class Sample(
    val opcodeId: Int,
    val before: List<Int>,
    val operands : List<Int>,
    val after: List<Int>
)

data class Instruction (
    val opcodeId: Int,
    val operands: List<Int>
)

fun main(args: Array<String>) {
    val lines = File("input/day16.txt").readLines()

    val (samples, instructions) = readLines(lines)

    val (result1, result2) = start(samples, instructions)
    println(result1)
    println(result2)
}

fun readLines(lines: List<String>): Pair<MutableList<Sample>, MutableList<Instruction>> {
    val samples = mutableListOf<Sample>()

    val iterator = lines.iterator()
    while (iterator.hasNext()) {
        val s = iterator.next()
        if (s == "") {
            iterator.next()
            break
        }

        val before = readNum(s)
        val instruction = readNum(iterator.next())
        val after = readNum(iterator.next())
        samples.add(Sample(
            instruction[0],
            before,
            instruction.subList(1, instruction.size),
            after
        ))

        iterator.next()
    }

    val instructions = mutableListOf<Instruction>()
    while (iterator.hasNext()) {
        val lineNums =readNum(iterator.next())
        instructions.add(Instruction(
            lineNums[0],
            lineNums.subList(1, lineNums.size)
        ))
    }

    return Pair(samples, instructions)
}

fun readNum(next: String): List<Int> {
    val regex = Regex("""\d+\b""")
    return regex.findAll(next)
        .map { it.value.toInt() }
        .toList()
}

@Suppress("EnumEntryName")
enum class Opcodes {
    addr,
    addi,

    mulr,
    muli,

    banr,
    bani,

    borr,
    bori,

    setr,
    seti,

    gtir,
    gtri,
    gtrr,

    eqir,
    eqri,
    eqrr
}

private fun getOps(): List<Opcodes> {
    return listOf(
        Opcodes.addr,
        Opcodes.addi,
        Opcodes.mulr,
        Opcodes.muli,
        Opcodes.banr,
        Opcodes.bani,
        Opcodes.borr,
        Opcodes.bori,
        Opcodes.setr,
        Opcodes.seti,
        Opcodes.gtir,
        Opcodes.gtri,
        Opcodes.gtrr,
        Opcodes.eqir,
        Opcodes.eqri,
        Opcodes.eqrr
    )
}

fun start(
    samples: List<Sample>,
    instructions: MutableList<Instruction>
): Pair<Int, Int> {
    val opcodes = getOps()
    var result1 = 0

    val opcodeToId = mutableMapOf<Opcodes, HashSet<Int>>()
    for (opcode in opcodes) {
        val s = HashSet<Int>()
        for (i in 0 until opcodes.size) {
            s.add(i)
        }
        opcodeToId[opcode] = s
    }

    for (sample in samples) {
        var match = 0
        for (opcode in opcodes) {
            if (matches(opcode, sample)) {
                match++
            } else {
                opcodeToId[opcode]!!.remove(sample.opcodeId)
            }
        }

        if (match >= 3) {
            result1++
        }
    }


    val idToOp = mutableMapOf<Int, Opcodes>()
    while (idToOp.size < opcodes.size) {
        for ((opcode, ids) in opcodeToId.toList()) {
            if (ids.size == 1) {
                val id = ids.single()
                idToOp[id] = opcode

                opcodeToId.remove(opcode)
                for (set in opcodeToId.values) {
                    set.remove(id)
                }
            }
        }
    }

    var registers = listOf(
        0,0,0,0
    )
    for (instruction in instructions) {
        val opcode = idToOp[instruction.opcodeId]!!
        val result = runInst(opcode, instruction.operands, registers) ?: continue
        registers = result
    }

    val result2 = registers[0]

    return Pair(result1, result2)
}

fun matches(opcode: Opcodes, sample: Sample): Boolean {
    val ok: Boolean
    val result: List<Int>? = runInst(opcode, sample.operands, sample.before)
    ok = sample.after == result
    return ok
}

private fun runInst(
    opcode: Opcodes,
    operands: List<Int>,
    registers: List<Int>
): List<Int>? {
    var result: List<Int>? = null
    when (opcode) {
        Opcodes.addr -> {
            if (isRegister(operands, registers, 0)
                &&
                isRegister(operands, registers, 1)
                &&
                isRegister(operands, registers, 2)
            ) {
                result = registers.toMutableList()
                result[operands[2]] =
                        registers[operands[1]] +
                        registers[operands[0]]
            }
        }
        Opcodes.addi -> {
            if (isRegister(operands, registers, 0)
                &&
                isRegister(operands, registers, 2)
            ) {
                result = registers.toMutableList()
                result[operands[2]] =
                        operands[1] +
                        registers[operands[0]]
            }
        }
        Opcodes.mulr -> {
            if (isRegister(operands, registers, 0)
                &&
                isRegister(operands, registers, 1)
                &&
                isRegister(operands, registers, 2)
            ) {
                result = registers.toMutableList()
                result[operands[2]] =
                        registers[operands[1]] *
                        registers[operands[0]]
            }
        }
        Opcodes.muli -> {
            if (isRegister(operands, registers, 0)
                &&
                isRegister(operands, registers, 2)
            ) {
                result = registers.toMutableList()
                result[operands[2]] =
                        operands[1] *
                        registers[operands[0]]
            }
        }
        Opcodes.banr -> {
            if (isRegister(operands, registers, 0)
                &&
                isRegister(operands, registers, 1)
                &&
                isRegister(operands, registers, 2)
            ) {
                result = registers.toMutableList()
                result[operands[2]] =
                        registers[operands[1]] and
                        registers[operands[0]]
            }
        }
        Opcodes.bani -> {
            if (isRegister(operands, registers, 0)
                &&
                isRegister(operands, registers, 2)
            ) {
                result = registers.toMutableList()
                result[operands[2]] =
                        operands[1] and
                        registers[operands[0]]
            }
        }
        Opcodes.borr -> {
            if (isRegister(operands, registers, 0)
                &&
                isRegister(operands, registers, 1)
                &&
                isRegister(operands, registers, 2)
            ) {
                result = registers.toMutableList()
                result[operands[2]] =
                        registers[operands[1]] or
                        registers[operands[0]]
            }
        }
        Opcodes.bori -> {
            if (isRegister(operands, registers, 0)
                &&
                isRegister(operands, registers, 2)
            ) {
                result = registers.toMutableList()
                result[operands[2]] =
                        operands[1] or
                        registers[operands[0]]
            }
        }
        Opcodes.setr -> {
            if (isRegister(operands, registers, 0)
                &&
                isRegister(operands, registers, 2)
            ) {
                result = registers.toMutableList()
                result[operands[2]] = registers[operands[0]]
            }
        }
        Opcodes.seti -> {
            if (isRegister(operands, registers, 2)) {
                result = registers.toMutableList()
                result[operands[2]] = operands[0]
            }
        }
        Opcodes.gtir -> {
            if (isRegister(operands, registers, 1)
                &&
                isRegister(operands, registers, 2)
            ) {
                result = registers.toMutableList()
                result[operands[2]] =
                        if (operands[0] > registers[operands[1]]) 1 else 0
            }
        }
        Opcodes.gtri -> {
            if (isRegister(operands, registers, 0)
                &&
                isRegister(operands, registers, 2)
            ) {
                result = registers.toMutableList()
                result[operands[2]] =
                        if (registers[operands[0]] > operands[1]) 1 else 0
            }
        }
        Opcodes.gtrr -> {
            if (isRegister(operands, registers, 0)
                &&
                isRegister(operands, registers, 1)
                &&
                isRegister(operands, registers, 2)
            ) {
                result = registers.toMutableList()
                result[operands[2]] =
                        if (registers[operands[0]] > registers[operands[1]]) 1 else 0
            }
        }
        Opcodes.eqir -> {
            if (isRegister(operands, registers, 1)
                &&
                isRegister(operands, registers, 2)
            ) {
                result = registers.toMutableList()
                result[operands[2]] =
                        if (operands[0] == registers[operands[1]]) 1 else 0
            }
        }
        Opcodes.eqri -> {
            if (isRegister(operands, registers, 0)
                &&
                isRegister(operands, registers, 2)
            ) {
                result = registers.toMutableList()
                result[operands[2]] =
                        if (registers[operands[0]] == operands[1]) 1 else 0
            }
        }
        Opcodes.eqrr -> {
            if (isRegister(operands, registers, 0)
                &&
                isRegister(operands, registers, 1)
                &&
                isRegister(operands, registers, 2)
            ) {
                result = registers.toMutableList()
                result[operands[2]] =
                        if (registers[operands[0]] == registers[operands[1]]) 1 else 0
            }
        }
    }
    return result
}

fun isRegister(operands: List<Int>, registers: List<Int>, i: Int): Boolean {
    val value = operands.getOrNull(i) ?: return false
    return value in 0..(registers.size-1)
}
