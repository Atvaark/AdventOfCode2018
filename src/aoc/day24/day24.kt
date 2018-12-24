package aoc.day24

import java.io.File

enum class Team {
    ImmuneSystem,
    Infection
}

data class Group(
    val team: Team,
    val group: Int,

    var num: Int,
    val hp: Int,

    val immunities: Set<String>,
    val weaknesses: Set<String>,

    val attack: Int,
    val attackType: String,
    val initiative: Int
)

fun main(args: Array<String>) {
    val input = "input/day24.txt"

    var boost = 0
    val result1 = run(input, boost)
    println(result1.first)

    while (true) {
        boost++

        val result2 = run(input, boost)
        if (result2.second == Team.ImmuneSystem) {
            println(result2.first)
            break
        }
    }
}

private fun run(input: String, boost: Int): Pair<Int, Team?> {
    val lines = File(input).readLines()
    val groups = mutableListOf<Group>()

    var nextGroup = 0
    var team: Team? = null

    loop@ for (line in lines) {
        when (line) {
            "Immune System:" -> {
                team = Team.ImmuneSystem
                nextGroup = 1
                continue@loop
            }
            "Infection:" -> {
                team = Team.Infection
                nextGroup = 1
                continue@loop
            }
        }

        if (line == "") {
            continue@loop
        }

        var num = -1
        var hp = -1
        var attack = -1
        var attackType = ""
        var initiative = -1
        var immunity = setOf<String>()
        var weakness = setOf<String>()


        val numHp = Regex("""(\d+) units each with (\d+) hit points """)
        numHp.find(line)?.let {
            num = it.groupValues[1].toInt()
            hp = it.groupValues[2].toInt()
        }

        val im = Regex("""immune to ([^;)]+)""")
        im.find(line)?.let { it ->
            immunity = it.groupValues[1].split(',')
                .map { it.trim() }
                .toSet()
        }
        val w = Regex("""weak to ([^;)]+)""")
        w.find(line)?.let {
            weakness = it.groupValues[1].split(',')
                .map { v -> v.trim() }
                .toSet()
        }

        val at = Regex("""with an attack that does (\d+) (\w+) damage at initiative (\d+)""")
        at.find(line)?.let {
            attack = it.groupValues[1].toInt()
            attackType = it.groupValues[2]
            initiative = it.groupValues[3].toInt()

            if (team == Team.ImmuneSystem) {
                attack += boost
            }
        }

        val g = Group(
            team!!,
            nextGroup,
            num,
            hp,
            immunity,
            weakness,
            attack,
            attackType,
            initiative
        )

        groups.add(g)
        nextGroup++
    }

    while (true) {
        // target
        val targets = mutableMapOf<Int, Int>()
        val targetOrdered = groups.withIndex()
            .map { Pair(it, it.value.num * it.value.attack) }
            .sortedWith(compareByDescending<Pair<IndexedValue<Group>, Int>> { it.second }.thenByDescending { it.first.value.initiative })
            .toList()
        for (group in targetOrdered.map { it.first.value }) {
                groups
                    .withIndex()
                    .filter { it.value.team != group.team }
                    .filter { group.attackType !in it.value.immunities }
                    .filter { it.index !in targets.values }
                    .map { it.value }
                    .map { Triple(it, getDamage(group, it), it.attack * it.num) }
                    .sortedWith(compareByDescending<Triple<Group, Int, Int>> { it.second }.thenByDescending { it.third }.thenByDescending { it.first.initiative })
                .firstOrNull()
                ?.let {
                    targets[groups.indexOf(group)] = groups.indexOf(it.first)
                }
        }

        // attack
        var anyTargetsKilled = false
        for ((groupIndex, group) in groups.withIndex().sortedByDescending { it.value.initiative }) {
            if (group.num <= 0) {
                continue
            }

            targets[groupIndex]?.let { targetIndex ->
                val target = groups[targetIndex]
                val damage = getDamage(group, target)
                val kills = damage / target.hp
                if (kills > 0) {
                    anyTargetsKilled = true
                    target.num -= kills
                }
            }
        }

        groups.removeAll { it.num <= 0 }

        if (groups.groupBy { it.team }.count() == 1) {
            break
        }

        if (!anyTargetsKilled) {
            return Pair(-1, null)
        }
    }


    val sum = groups.sumBy { it.num }
    return Pair(sum, groups.first().team)
}

fun getDamage(attack: Group, defend: Group): Int {
    if (defend.immunities.contains(attack.attackType)) {
        return 0
    }

    var ep = attack.attack * attack.num
    if (defend.weaknesses.contains(attack.attackType)) {
        ep *= 2
    }

    return ep
}
