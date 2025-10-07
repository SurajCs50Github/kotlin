import kotlin.random.Random


fun <T : Comparable<T>> insertionSort(list: MutableList<T>) {
    for (i in 1 until list.size) {
        val key = list[i]
        var j = i - 1
        while (j >= 0 && list[j] > key) {
            list[j + 1] = list[j]
            j--
        }
        list[j + 1] = key
    }
}

fun <T : Comparable<T>> selectionSort(list: MutableList<T>) {
    for (i in 0 until list.size - 1) {
        var minIndex = i
        for (j in i + 1 until list.size) {
            if (list[j] < list[minIndex]) {
                minIndex = j
            }
        }
        if (minIndex != i) {
            val temp = list[i]
            list[i] = list[minIndex]
            list[minIndex] = temp
        }
    }
}

fun <T : Comparable<T>> mergeSort(list: List<T>): List<T> {
    if (list.size <= 1) return list
    val middle = list.size / 2
    val left = list.subList(0, middle)
    val right = list.subList(middle, list.size)
    return merge(mergeSort(left), mergeSort(right))
}

private fun <T : Comparable<T>> merge(left: List<T>, right: List<T>): List<T> {
    val result = mutableListOf<T>()
    var leftIndex = 0
    var rightIndex = 0
    while (leftIndex < left.size && rightIndex < right.size) {
        if (left[leftIndex] < right[rightIndex]) {
            result.add(left[leftIndex++])
        } else {
            result.add(right[rightIndex++])
        }
    }
    while (leftIndex < left.size) result.add(left[leftIndex++])
    while (rightIndex < right.size) result.add(right[rightIndex++])
    return result
}


fun <T : Comparable<T>> quickSort(list: MutableList<T>, low: Int = 0, high: Int = list.size - 1) {
    if (low < high) {
        val pi = partition(list, low, high)
        quickSort(list, low, pi - 1)
        quickSort(list, pi + 1, high)
    }
}

private fun <T : Comparable<T>> partition(list: MutableList<T>, low: Int, high: Int): Int {
    val pivotIndex = Random.nextInt(low, high + 1)
    list.swap(pivotIndex, high)
    val pivot = list[high]
    var i = low - 1
    for (j in low until high) {
        if (list[j] < pivot) {
            i++
            list.swap(i, j)
        }
    }
    list.swap(i + 1, high)
    return i + 1
}

private fun <T> MutableList<T>.swap(i: Int, j: Int) {
    val temp = this[i]
    this[i] = this[j]
    this[j] = temp
}
