package de.miraculixx.mutils.extensions

fun <K, V>Map<K, V>.add(map: Map<K, V>): Map<K, V> {
    return buildMap {
        putAll(this@add)
        putAll(map)
    }
}

fun <K, V>List<K>.toMap(default: V): Map<K, V> {
    return buildMap {
        this@toMap.forEach { put(it, default) }
    }
}