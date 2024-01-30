package de.miraculixx.mchallenge.modules.mods.misc.chunkEffects

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mchallenge.global.Challenges
import de.miraculixx.mchallenge.global.challenges
import de.miraculixx.mchallenge.global.getSetting
import io.papermc.paper.configuration.WorldConfiguration.Chunks
import org.bukkit.Chunk
import org.bukkit.block.Biome
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class ChunkEffects: Challenge {
    private var sRandom = false
    private val map = mutableMapOf<Chunk, PotionEffectType>()
    private val randomMap = mutableMapOf<Player, PotionEffectType>()
    override fun start(): Boolean {
        val settings = challenges.getSetting(Challenges.CHUNK_EFFECTS).settings
        sRandom = settings["random"]?.toBool()?.getValue() ?: true
        return true
    }

    override fun stop() {
        map.clear()
        randomMap.clear()
    }

    override fun register() {
        chunkChangeListener.register()
    }

    override fun unregister() {
        chunkChangeListener.unregister()
        onlinePlayers.forEach {
            map[it.chunk]?.let { e -> it.removePotionEffect(e) }
            randomMap[it]?.let { e -> it.removePotionEffect(e) }
        }
    }

    private fun Chunk.effect(): PotionEffectType {
        if (!map.containsKey(this))
            map += this to PotionEffectType.values().random()

        return map[this]!!
    }

    private val chunkChangeListener = listen<PlayerMoveEvent>(register = false) {
        val from = it.from.chunk
        val to = it.to.chunk

        if (from == to)return@listen

        if (sRandom) {

            randomMap[it.player]?.let { e ->
                it.player.removePotionEffect(e)
            }
            randomMap[it.player] = PotionEffectType.values().random()
            println("effect: ${randomMap[it.player]}")
            it.player.addPotionEffect(PotionEffect(randomMap[it.player]!!, PotionEffect.INFINITE_DURATION, 0, true, false, true))

            return@listen
        }
        it.player.removePotionEffect(from.effect())

        it.player.addPotionEffect(PotionEffect(to.effect(), PotionEffect.INFINITE_DURATION, 0, true, false, true))
    }
}