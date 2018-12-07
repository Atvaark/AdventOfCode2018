import java.io.File

fun main(args: Array<String>) {
    val lines = File("input/day07.txt").readLines()

    val order = mutableMapOf<String, MutableSet<String>>() // step -> reqs
    val lineRegex = Regex("""Step (\S+) must be finished before step (\S)+ can begin.""")
    for (line in lines) {
        val matchResult = lineRegex.matchEntire(line) ?: continue

        val step1 = matchResult.groupValues[1]
        val step2 = matchResult.groupValues[2]

        val reqs = order.getOrPut(step2) { mutableSetOf() }
        reqs.add(step1)
    }


    val result1 = get1(order)
    println(result1)

    val result2 = get2(order)
    println(result2)
}


private fun get1(order: MutableMap<String, MutableSet<String>>): String {
    var result1 = ""
    val stepsLeft = mutableSetOf<String>()
    stepsLeft.addAll(order.keys)
    order.values.forEach { stepsLeft.addAll(it) }

    while (!stepsLeft.isEmpty()) {
        val readySteps = getReadySteps(stepsLeft, order)

        val nextStep = readySteps.sortedBy { it }.first()
        result1 += nextStep
        stepsLeft.remove(nextStep)

    }
    return result1
}

private fun getReadySteps(
    stepsLeft: MutableSet<String>,
    order: MutableMap<String, MutableSet<String>>
): MutableSet<String> {
    val readySteps = mutableSetOf<String>()
    for (step in stepsLeft) {
        val stepReqs = order[step]
        if (stepReqs == null) {
            readySteps.add(step)
        } else {
            val count = stepReqs.filter { stepsLeft.contains(it) }.count()
            if (count == 0) {
                readySteps.add(step)
            }
        }
    }
    return readySteps
}

private fun get2(order: MutableMap<String, MutableSet<String>>): Int {
    val workerCount = 5
    val taskFixedDuration = 60

    val stepsLeft = mutableSetOf<String>()
    stepsLeft.addAll(order.keys)
    order.values.forEach { stepsLeft.addAll(it) }

    val inProgressSteps = mutableSetOf<String>()

    val workers = mutableMapOf<Int, Pair<String, Int>?>()
    for (i in 1..workerCount) {
        workers[i] = null
    }

    var second = 0
    do {
        // flush done
        val workingWorkers = workers.filter { it.value != null }
        for (workingWorker in workingWorkers) {
            val task = workingWorker.value!!
            if (task.second == second) {
                stepsLeft.remove(task.first)
                inProgressSteps.remove(task.first)
                workers[workingWorker.key] = null
            }
        }

        // add work
        val availWorkers = workers.filter { it.value == null }
        if (availWorkers.size > 0) {
            for (availWorker in availWorkers) {
                var readySteps = getReadySteps(stepsLeft, order).asIterable()
                readySteps = readySteps.filter { !inProgressSteps.contains(it) }
                val next = readySteps.sortedBy { it }.firstOrNull()
                if (next != null) {
                    val stepDuration = taskFixedDuration + (next[0].toInt() - 'A'.toInt()) + 1
                    val doneAt = second + stepDuration
                    workers[availWorker.key] = Pair(next, doneAt)
                    inProgressSteps.add(next)
//                    println("worker $availWorker.key start $next ")
                }
            }

        }

        if (!stepsLeft.isEmpty()) {
            second++
        }
    } while (!stepsLeft.isEmpty())

    return second
}