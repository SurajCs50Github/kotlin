import kotlin.random.Random
import kotlin.time.DurationUnit
import kotlin.time.measureTime

typealias SortingFunction = (MutableList<Int>) -> Unit

fun main() {
    val algorithms: Map<String, SortingFunction> = mapOf(
        "Insertion Sort" to ::insertionSort,
        "Selection Sort" to ::selectionSort,
        "Quick Sort" to { list -> quickSort(list) },
        "Merge Sort" to { list ->
            val sorted = mergeSort(list)
            list.clear()
            list.addAll(sorted)
        },
        "Kotlin's built-in sort" to { list -> list.sort() }
    )

    val sizes = listOf(10, 100, 1000, 10000, 50000)
    val trials = 5

    println("Running benchmark with $trials trials per size...")
    println("-".repeat(80))
    println("%-25s | %-12s | %-18s".format("Algorithm", "List Size", "Avg. Time (ms)"))
    println("-".repeat(80))

    for (algoName in algorithms.keys) {
        val sorter = algorithms[algoName]!!
        for (size in sizes) {
            if ((algoName.contains("Insertion") || algoName.contains("Selection")) && size > 10000) {
                println("%-25s | %-12d | %-18s".format(algoName, size, "SKIPPED (>10s)"))
                continue
            }

            var totalDuration = 0.0
            for (i in 1..trials) {
                val list = (1..size).map { Random.nextInt(0, size * 10) }.toMutableList()
                val duration = measureTime { sorter(list) }
                totalDuration += duration.toDouble(DurationUnit.MILLISECONDS)
            }

            val avgDuration = totalDuration / trials
            println("%-25s | %-12d | %-18.4f".format(algoName, size, avgDuration))
        }
        println("-".repeat(80))
    }
}