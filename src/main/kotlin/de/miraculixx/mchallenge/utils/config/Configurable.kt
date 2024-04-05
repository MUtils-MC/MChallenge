package de.miraculixx.mchallenge.utils.config

interface Configurable {
    fun save()
    fun load() {}
    fun reset() {}
}