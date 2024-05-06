package de.miraculixx.mchallenge.utils

import de.miraculixx.kpaper.extensions.broadcast
import net.kyori.adventure.text.Component

fun bc(prefix: Component, key: String, list: List<String> = emptyList()) = broadcast(prefix, key, list)

