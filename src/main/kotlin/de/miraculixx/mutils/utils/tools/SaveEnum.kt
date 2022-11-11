package de.miraculixx.mutils.utils.tools

inline fun <reified T : Enum<T>> enumOf(type: String): T? {
    return try {
        java.lang.Enum.valueOf(T::class.java, type)
    } catch (e: IllegalArgumentException) {
        null
    }
}