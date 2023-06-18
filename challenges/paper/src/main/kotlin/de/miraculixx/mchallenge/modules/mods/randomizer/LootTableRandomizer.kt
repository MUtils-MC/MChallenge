package de.miraculixx.mchallenge.modules.mods.randomizer

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.mchallenge.global.Challenges
import de.miraculixx.mchallenge.global.challenges
import de.miraculixx.mchallenge.global.getSetting
import de.miraculixx.mcore.utils.getMaterials
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.world.LootGenerateEvent
import kotlin.random.Random

class LootTableRandomizer: Challenge {
    private val items = getMaterials(false)
    private val randomEnchanting: Boolean

    init {
        val settings = challenges.getSetting(Challenges.RANDOMIZER_CHESTS).settings
        randomEnchanting = settings["enchanting"]?.toBool()?.getValue() ?: false
    }

    override fun register() {
        onLootTable.register()
    }

    override fun unregister() {
        onLootTable.unregister()
    }

    private val onLootTable = listen<LootGenerateEvent> {
        if (it.isPlugin) return@listen
        it.loot.forEach { item ->
            item.type = items.random()
            if (randomEnchanting) {
                if (Random.nextBoolean()) item.addEnchantment(Enchantment.values().random(), (0..3).random())
            }
        }
    }
}