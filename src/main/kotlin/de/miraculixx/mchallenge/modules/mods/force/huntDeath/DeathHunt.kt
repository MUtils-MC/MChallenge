package de.miraculixx.mchallenge.modules.mods.force.huntDeath

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mchallenge.modules.challenges.interfaces.HuntChallenge
import de.miraculixx.mchallenge.utils.serializer.Serializer
import de.miraculixx.mcommons.extensions.enumOf
import de.miraculixx.mcommons.text.cHighlight
import de.miraculixx.mcommons.text.cMark
import io.papermc.paper.event.entity.TameableDeathMessageEvent
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.TranslatableComponent
import org.bukkit.EntityEffect
import org.bukkit.entity.EntityType
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class DeathHunt : Challenge, HuntChallenge<String>("deathhunt", "death_hunt") {
    override val typeName = "Death"
    override val allEntries = extractValidKeys()
    override val maxEntries = allEntries.size
    override val remainingEntries: MutableList<String> = mutableListOf()
    override var currentTarget: String? = null
    override val serializer: Serializer<String> = object: Serializer<String> {
        override fun toString(data: String) = data
        override fun toObject(data: String) = data
    }

    override fun register() {
        onDeath.register()
        onTamedDeath.register()
    }

    override fun unregister() {
        onDeath.unregister()
        onTamedDeath.unregister()
    }

    private val onDeath = listen<PlayerDeathEvent>(register = false) {
        val message = it.deathMessage() ?: return@listen
        val key = (message as? TranslatableComponent)?.key() ?: return@listen
        if (key != currentTarget) return@listen
        val player = it.player
        nextEntry(player.name, it.player)
        it.isCancelled = true
        broadcast(message)
        player.playEffect(EntityEffect.TOTEM_RESURRECT)
        player.addPotionEffect(PotionEffect(PotionEffectType.FIRE_RESISTANCE, 10 * 20, 1, false, false, false))
        player.addPotionEffect(PotionEffect(PotionEffectType.RESISTANCE, 10 * 20, 2, false, false, false))
        player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 3 * 20, 4, false, false, false))
        if (player.location.y < player.world.minHeight) {
            player.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, 20 * 5, 19, false, false, false))
            player.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, 20 * 10, 1, false, false, false))
        }
    }

    private val onTamedDeath = listen<TameableDeathMessageEvent>(register = false) {
        val message = it.deathMessage()
        val key = (message as? TranslatableComponent)?.key() ?: return@listen
        if (key != currentTarget) return@listen
        nextEntry(it.entity.owner?.name ?: "Unknown", Audience.audience(onlinePlayers))
        it.isCancelled = true
        broadcast(message)
    }

    override fun start(): Boolean {
        startHunt()
        return true
    }

    override fun stop() {
        stopHunt()
    }

    override fun getTranslationKey() = currentTarget?.let { "<lang:$it:'${cMark}Player/Pet$cHighlight':'${cMark}Something$cHighlight'>" }

    private fun extractValidKeys(): List<String> {
        val rawJson = javaClass.getResourceAsStream("/data/deathKeys.json")?.readBytes()?.decodeToString() ?: "{}"
        val mapType = object : TypeToken<Map<String, String>>() {}
        val resultMap = Gson().fromJson(rawJson, mapType) as Map<String, String>
        val keys = resultMap.keys.filter { it.startsWith("death.") && !it.endsWith(".item") }
        return keys - setOf(
            "death.attack.sting.player", "death.attack.freeze.player", "death.attack.cramming.player", "death.attack.fireworks.player",
            "death.attack.anvil.player", "death.attack.magic.player", "death.attack.genericKill.player", "death.attack.generic.player",
            "death.attack.generic", "death.attack.thrown", "death.attack.thrown.player", "death.attack.sonic_boom.player",
            "death.attack.fallingBlock", "death.attack.fallingBlock.player", "death.attack.message_too_long",
            "death.attack.dragonBreath", "death.attack.dragonBreath.player", "death.attack.even_more_magic",
            "death.attack.genericKill"
        )
    }
}