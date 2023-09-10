package de.miraculixx.mchallenge.modules.mods.multiplayer.hitOrder

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mchallenge.global.Challenges
import de.miraculixx.mchallenge.global.challenges
import de.miraculixx.mchallenge.global.getSetting
import de.miraculixx.mchallenge.modules.global.DeathListener
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.persistence.PersistentDataType
import java.util.UUID

class HitOrder: Challenge {
    private val msgNextPlayer = msgString("event.hitOrder.nextPlayer")
    private val wrongDamage: Double
    private val randomOrder: Boolean
    private val visual: Boolean

    private val bossBar = BossBar.bossBar(cmp("Loading...", cError), 1f, BossBar.Color.RED, BossBar.Overlay.PROGRESS)
    private val fixOrder = onlinePlayers.shuffled().map { it.uniqueId }.toMutableList()
    private var currentPlayer = nextPlayer(fixOrder.random())


    init {
        val settings = challenges.getSetting(Challenges.HIT_ORDER).settings
        wrongDamage = settings["wrongDamage"]?.toDouble()?.getValue() ?: 10.0
        randomOrder = settings["randomOrder"]?.toBool()?.getValue() ?: false
        visual = settings["visual"]?.toBool()?.getValue() ?: true
    }

    override fun start(): Boolean {
        onlinePlayers.forEach { it.showBossBar(bossBar) }
        return true
    }

    override fun stop() {
        onlinePlayers.forEach { it.hideBossBar(bossBar) }
    }

    override fun register() {
        onDamage.register()
        onJoin.register()
        onQuit.register()
    }

    override fun unregister() {
        onDamage.unregister()
        onJoin.unregister()
        onQuit.unregister()
    }

    private val onDamage = listen<EntityDamageByEntityEvent>(register = false) {
        val player = when (val damager = it.damager) {
            is Projectile -> damager.shooter as? Player ?: return@listen
            is Player -> damager
            else -> return@listen
        }

        if (player.uniqueId != currentPlayer) {
            if ((player.health - wrongDamage) <= 0) player.persistentDataContainer.set(DeathListener.key, PersistentDataType.STRING, "hitOrder")
            player.damage(wrongDamage)
            it.isCancelled = true
            return@listen
        }

        currentPlayer = nextPlayer(currentPlayer)
    }

    private fun nextPlayer(current: UUID): UUID {
        val next =  if (randomOrder) fixOrder.random() else {
            val index = fixOrder.lastIndexOf(current) + 1
            fixOrder[index.takeUnless { index >= fixOrder.size } ?: 0]
        }
        bossBar.name(cmp(msgNextPlayer) + cmp(Bukkit.getPlayer(next)?.name ?: "Unknown", cHighlight))
        onlinePlayers.forEach { it.playSound(it, Sound.ENTITY_ENDER_EYE_DEATH, 0.8f, 1.2f) }
        return next
    }

    private val onQuit = listen<PlayerQuitEvent>(register = false) {
        val uuid = it.player.uniqueId
        fixOrder.remove(uuid)
        if (uuid == currentPlayer) currentPlayer = nextPlayer(currentPlayer)
    }

    private val onJoin = listen<PlayerJoinEvent>(register = false) {
        fixOrder.add(it.player.uniqueId)
        it.player.showBossBar(bossBar)
    }
}