package de.miraculixx.mchallenge.modules.mods.force.huntMob

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.items.getLivingMobs
import de.miraculixx.mchallenge.MChallenge
import de.miraculixx.mchallenge.PluginManager
import de.miraculixx.mchallenge.commands.ModuleCommand
import de.miraculixx.mchallenge.modules.ChallengeManager
import de.miraculixx.mchallenge.utils.config.loadConfig
import de.miraculixx.mchallenge.utils.config.saveConfig
import de.miraculixx.mcommons.text.cmp
import de.miraculixx.mcommons.text.prefix
import kotlinx.serialization.Serializable
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import java.io.File

class MobHunt : Challenge, HuntObject<EntityType> {
    private val dataFile = File("${MChallenge.configFolder.path}/data/mob_hunt.json")
    private var currentTarget: EntityType? = null
    override val maxEntries = getLivingMobs(true).size
    override val remainingEntries = mutableListOf<EntityType>()
    override val blacklist = mutableListOf<EntityType>()
    override val bar = BossBar.bossBar(cmp("Waiting for server..."), 0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS)

    override fun register() {
        onKill.register()
    }

    override fun unregister() {
        onKill.unregister()
    }

    override fun start(): Boolean {
        val preData = dataFile.loadConfig(MobHuntData())
        val preTarget = preData.target
        currentTarget = if (preTarget == null) {
            remainingEntries.addAll(getLivingMobs(true))
            remainingEntries.random()
        } else {
            remainingEntries.addAll(preData.remainingMobs)
            preTarget
        }
        remainingEntries.remove(currentTarget)
        calcBar(getCurrentEntryName())
        onlinePlayers.forEach { it.showBossBar(bar) }
        val cmdClass = MobHuntCommand(this)
        val cmdInstance = PluginManager.getCommand("mobhunt") ?: return false
        cmdInstance.setExecutor(cmdClass)
        cmdInstance.tabCompleter = cmdClass
        onJoin.register()
        return true
    }

    override fun stop() {
        dataFile.saveConfig(MobHuntData(currentTarget, remainingEntries))
        onlinePlayers.forEach { it.hideBossBar(bar) }
        ModuleCommand("mobhunt")
        onJoin.unregister()
    }

    private val onJoin = listen<PlayerJoinEvent>(register = false) {
        bar.addViewer(it.player)
    }

    private val onKill = listen<EntityDamageByEntityEvent>(register = false) {
        val target = it.entity
        if (target !is LivingEntity) return@listen
        val player = when (val damager = it.damager) {
            is Player -> damager
            is Projectile -> damager.shooter as? Player ?: return@listen
            else -> return@listen
        }

        if (target.health - it.finalDamage > 0.0) return@listen
        val type = target.type
        if (type != currentTarget) return@listen

        nextEntry(player.name, player)
    }

    override fun nextEntry(playerName: String, audience: Audience) {
        broadcast(prefix, "event.mobHunt.collect", listOf(playerName, currentTarget?.let { "<lang:${it.translationKey()}>" } ?: ""))
        audience.playSound(Sound.sound(Key.key("entity.chicken.egg"), Sound.Source.MASTER, 1f, 1.2f))
        val size = remainingEntries.size
        currentTarget = if (size == 0) {
            broadcast(prefix,"event.mobHunt.success")
            ChallengeManager.stopChallenges()
            null
        } else remainingEntries.random()
        remainingEntries.remove(currentTarget)
        calcBar(getCurrentEntryName())
    }

    override fun getCurrentEntryName() = currentTarget?.name

    @Serializable
    private data class MobHuntData(
        val target: EntityType? = null,
        val remainingMobs: List<EntityType> = emptyList(),
        val blacklist: List<Material> = emptyList()
    )
}