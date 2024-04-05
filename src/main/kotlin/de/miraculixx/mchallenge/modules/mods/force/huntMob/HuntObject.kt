package de.miraculixx.mchallenge.modules.mods.force.huntMob

import de.miraculixx.mcommons.serializer.miniMessage
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar

interface HuntObject<T> {
    val bar: BossBar
    val maxEntries: Int
    val remainingEntries: MutableList<T>
    val blacklist: MutableList<T>

    fun addBlacklist(entry: T) {
        blacklist.add(entry)
        remainingEntries.remove(entry)
        calcBar(getCurrentEntryName())
    }

    fun removeBlacklist(entry: T) {
        if (blacklist.remove(entry))
            remainingEntries.add(entry)
    }

    fun reset(allEntries: List<T>) {
        remainingEntries.clear()
        remainingEntries.addAll(allEntries)
        remainingEntries.removeAll(blacklist)
        calcBar(getCurrentEntryName())
    }

    fun getCurrentEntryName(): String?

    fun nextEntry(playerName: String, audience: Audience)

    fun calcBar(entryName: String?) {
        val collectedAmount = maxEntries - remainingEntries.size
        val target = entryName?.let { "<lang:$it>" } ?: "<green>Finished</green>"
        bar.name(miniMessage.deserialize("<gray>Item:</gray> <blue><b>$target</b></blue>  <dark_gray>(<gray><green>$collectedAmount</green>/<red>$maxEntries</red></gray>)</dark_gray>"))
    }
}