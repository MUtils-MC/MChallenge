package de.miraculixx.mutils.extensions

inline fun <reified T : Enum<T>> enumOf(type: String?): T? {
    if (type == null) return null
    return try {
        java.lang.Enum.valueOf(T::class.java, type)
    } catch (e: IllegalArgumentException) {
        null
    }
}

fun <T> Array<T>.enumRotate(current: T): T {
    val currentValue = lastIndexOf(current)
    val lastValue = size - 1
    return if (currentValue < lastValue) this[currentValue + 1]
    else this[0]
}

