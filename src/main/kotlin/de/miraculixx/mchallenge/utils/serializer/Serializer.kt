package de.miraculixx.mchallenge.utils.serializer

interface Serializer<T> {
    fun toString(data: T): String
    fun toObject(data: String): T
}