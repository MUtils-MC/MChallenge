package de.miraculixx.mutils.modules.mods.huntMob

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mutils.MChallenge
import de.miraculixx.mutils.PluginManager
import de.miraculixx.mutils.commands.ModuleCommand
import de.miraculixx.api.modules.challenges.Challenges
import de.miraculixx.mutils.extensions.readJsonString
import de.miraculixx.mutils.messages.*
import de.miraculixx.api.modules.challenges.Challenge
import de.miraculixx.mutils.modules.ChallengeManager
import de.miraculixx.mutils.utils.getLivingMobs
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.io.File

class MobHunt : Challenge {
    override val challenge: Challenges = Challenges.MOB_HUNT
    private val dataFile = File("${MChallenge.configFolder.path}/data/mob_hunt.json")
    private val maxEntities = getLivingMobs(true).size
    private val remainingMobs: MutableList<EntityType> = mutableListOf()
    private var currentTarget: EntityType? = null
    private var bar = BossBar.bossBar(cmp("Waiting for server...", cError), 0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS)

    override fun register() {
        onKill.register()
    }

    override fun unregister() {
        onKill.unregister()
    }

    override fun start(): Boolean {
        val content = if (!dataFile.exists()) "" else dataFile.readJsonString(false)
        currentTarget = if (content.length > 5) {
            val input = json.decodeFromString<MobHuntData>(content)
            remainingMobs.addAll(input.remainingMobs)
            input.target
        } else {
            remainingMobs.addAll(getLivingMobs(true))
            remainingMobs.random()
        }
        remainingMobs.remove(currentTarget)
        calcBossBar()
        onlinePlayers.forEach { it.showBossBar(bar) }
        val cmdClass = MobHuntCommand(this)
        val cmdInstance = PluginManager.getCommand("mobhunt") ?: return false
        cmdInstance.setExecutor(cmdClass)
        cmdInstance.tabCompleter = cmdClass
        return true
    }

    override fun stop() {
        if (!dataFile.exists()) dataFile.parentFile.mkdirs()
        dataFile.writeText(json.encodeToString(MobHuntData(currentTarget, remainingMobs)))
        onlinePlayers.forEach { it.hideBossBar(bar) }
        ModuleCommand("mobhunt")
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

        nextMob(player.name, player)
    }

    fun nextMob(playerName: String, audience: Audience) {
        broadcast(prefix + msg("event.mobHunt.collect", listOf(playerName, currentTarget?.name?.fancy() ?: "")))
        audience.playSound(Sound.sound(Key.key("entity.chicken.egg"), Sound.Source.MASTER, 1f, 1.2f))
        val size = remainingMobs.size
        currentTarget = if (size == 0) {
            broadcast(prefix + msg("event.mobHunt.success"))
            ChallengeManager.stopChallenges()
            null
        } else remainingMobs.random()
        remainingMobs.remove(currentTarget)
        calcBossBar()
    }

    fun reset() {
        remainingMobs.clear()
        remainingMobs.addAll(getLivingMobs(true))
        currentTarget = remainingMobs.random()
        remainingMobs.remove(currentTarget)
        calcBossBar()
    }

    private fun calcBossBar() {
        val target = currentTarget?.name?.fancy() ?: "<green>Finished</green>"
        val collectedAmount = maxEntities - (remainingMobs.size + 1)
        bar.name(miniMessages.deserialize("<gray>Target:</gray> <blue><b>$target</b></blue>  <dark_gray>(<gray><green>$collectedAmount</green>/<red>$maxEntities</red></gray>)</dark_gray>"))
        bar.progress(collectedAmount.toFloat() / maxEntities)
    }

    @Serializable
    private data class MobHuntData(
        val target: EntityType? = null,
        val remainingMobs: List<EntityType> = emptyList(),
    )
}