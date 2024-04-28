package de.miraculixx.mchallenge.modules.mods.force.huntDeath

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
import de.miraculixx.mchallenge.modules.mods.force.huntMob.HuntObject
import de.miraculixx.mchallenge.utils.config.loadConfig
import de.miraculixx.mchallenge.utils.config.saveConfig
import de.miraculixx.mcommons.serializer.miniMessage
import de.miraculixx.mcommons.text.cMark
import de.miraculixx.mcommons.text.cmp
import de.miraculixx.mcommons.text.prefix
import io.papermc.paper.event.entity.TameableDeathMessageEvent
import kotlinx.serialization.Serializable
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.TranslatableComponent
import net.minecraft.util.GsonHelper
import org.bukkit.EntityEffect
import org.bukkit.Material
import org.bukkit.Statistic
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.json.simple.JSONObject
import java.io.File

class DeathHunt : Challenge, HuntObject<String> {
    private val dataFile = File("${MChallenge.configFolder.path}/data/death_hunt.json")
    val allDeathKeys: List<String> = extractValidKeys()
    private var currentTarget: String? = null
    override val maxEntries = allDeathKeys.size
    override val remainingEntries = mutableListOf<String>()
    override val blacklist = mutableListOf<String>()
    override val bar = BossBar.bossBar(cmp("Waiting for server..."), 0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS)

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
        player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10 * 20, 2, false, false, false))
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
        val preData = dataFile.loadConfig(DeathHuntData())
        val preTarget = preData.target
        currentTarget = if (preTarget == null) {
            remainingEntries.addAll(allDeathKeys)
            remainingEntries.random()
        } else {
            remainingEntries.addAll(preData.remainingDeaths)
            preTarget
        }
        remainingEntries.remove(currentTarget)
        calcBar(getCurrentEntryName())
        onlinePlayers.forEach { it.showBossBar(bar) }
        val cmdClass = DeathHuntCommand(this)
        val cmdInstance = PluginManager.getCommand("deathhunt") ?: return false
        cmdInstance.setExecutor(cmdClass)
        cmdInstance.tabCompleter = cmdClass
        onJoin.register()
        return true
    }

    override fun stop() {
        dataFile.saveConfig(DeathHuntData(currentTarget, remainingEntries))
        onlinePlayers.forEach { it.hideBossBar(bar) }
        ModuleCommand("mobhunt")
        onJoin.unregister()
    }

    private val onJoin = listen<PlayerJoinEvent>(register = false) {
        bar.addViewer(it.player)
        it.player.getStatistic(Statistic.ANIMALS_BRED)
    }

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



    override fun nextEntry(playerName: String, audience: Audience) {
        broadcast(prefix, "event.deathHunt.collect", listOf(playerName, currentTarget?.let { "<lang:${it}>" } ?: ""))
        audience.playSound(Sound.sound(Key.key("entity.chicken.egg"), Sound.Source.MASTER, 1f, 1.2f))
        val size = remainingEntries.size
        currentTarget = if (size == 0) {
            broadcast(prefix,"event.deathHunt.success")
            ChallengeManager.stopChallenges()
            null
        } else remainingEntries.random()
        remainingEntries.remove(currentTarget)
        calcBar(getCurrentEntryName())
    }

    override fun getCurrentEntryName() = currentTarget

    override fun calcBar(entryName: String?) {
        val collectedAmount = maxEntries - remainingEntries.size
        val target = entryName?.let { "<lang:$it:'${cMark}Target<grey>':'${cMark}Entity<grey>'>" } ?: "<green>Finished</green>"
        bar.name(miniMessage.deserialize("<grey>$target  <dark_gray>(<gray><green>$collectedAmount</green>/<red>$maxEntries</red></gray>)</dark_gray>"))
    }

    @Serializable
    private data class DeathHuntData(
        val target: String? = null,
        val remainingDeaths: List<String> = emptyList(),
        val blacklist: List<String> = emptyList()
    )
}