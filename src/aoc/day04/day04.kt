package aoc.day04

import java.io.File

data class Entry(val id: String, val day: String, val hour: Int, val minute: Int, val event: String)

fun main(args: Array<String>) {
    val lines = File("Input/day04.txt").readLines()

    val entries = getEntries(lines)

    val (sleepSum, sleepByMinute) = getSleepInfo(entries)

    val result1 = get1(sleepSum, sleepByMinute)
    println(result1)

    val result2 = get2(sleepByMinute)
    println(result2)
}

private fun getSleepInfo(entries: MutableList<Entry>): Pair<MutableMap<String, Int>, MutableMap<String, MutableMap<Int, Int>>> {
    entries.sortWith(compareBy({ it.day }, { it.hour }, { it.minute }))
    var currentId = ""
    var sleepStartMinute = 0
    val sleepSum = mutableMapOf<String, Int>()
    val sleepByMinute = mutableMapOf<String, MutableMap<Int, Int>>()
    for (entry in entries) {
        when (entry.event) {
            "begins shift" -> {
                currentId = entry.id
            }
            "falls asleep" -> {
                sleepStartMinute = entry.minute
            }
            "wakes up" -> {
                val sleep = sleepSum.getOrDefault(currentId, 0)
                val newSleep = entry.minute - sleepStartMinute
                sleepSum[currentId] = sleep + newSleep

                val byMinute = sleepByMinute.getOrPut(currentId, defaultValue = { mutableMapOf() })
                for (i in sleepStartMinute until entry.minute) {
                    byMinute[i] = byMinute.getOrDefault(i, 0) + 1
                }
            }
        }
    }

    return Pair(sleepSum, sleepByMinute)
}

private fun getEntries(lines: List<String>): MutableList<Entry> {
    val lineRegex = Regex("""^\[(\d+-\d+-\d+) (\d{2}):(\d{2})] (.*$)""")
    val eventRegex = Regex("""(?:Guard #(\d+) )?(begins shift|falls asleep|wakes up)""")
    val entries = mutableListOf<Entry>()
    for (line in lines) {
        val lineMatches = lineRegex.matchEntire(line) ?: continue

        val day = lineMatches.groupValues[1]
        val hour = lineMatches.groupValues[2].toInt()
        val minute = lineMatches.groupValues[3].toInt()
        var event = lineMatches.groupValues[4]

        val eventMatches = eventRegex.matchEntire(event) ?: continue

        var id = ""
        if (eventMatches.groups.size == 3 && eventMatches.groupValues[2] == "begins shift") {
            id = eventMatches.groupValues[1]
            event = eventMatches.groupValues[2]
        }

        entries.add(Entry(id, day, hour, minute, event))
    }
    return entries
}

private fun get1(sleepSum: Map<String, Int>, sleepByMinute: Map<String, Map<Int, Int>>): Int {
    val maxEntry = sleepSum.maxWith(compareBy { it.value }) ?: return 0
    val maxMap = sleepByMinute[maxEntry.key] ?: return 0
    val maxMin = maxMap.maxWith(compareBy { it.value }) ?: return 0
    return maxEntry.key.toInt() * maxMin.key
}

private fun get2(entries: MutableMap<String, MutableMap<Int, Int>>): Int {
    var maxId = "-1"
    var maxCount = Pair(-1, 0)

    for (entry in entries) {
        val max = entry.value.maxBy { it.value }
        if (max != null && max.value > maxCount.second) {
            maxId = entry.key
            maxCount = max.toPair()
        }
    }

    return maxId.toInt() * maxCount.first
}