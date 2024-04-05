package de.miraculixx.mchallenge.utils

import de.miraculixx.kpaper.extensions.bukkit.language
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mcommons.text.defaultLocale

fun getDominantLocale() = if (onlinePlayers.size > 1) defaultLocale else onlinePlayers.first().language()
