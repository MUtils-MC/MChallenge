package de.miraculixx.mutils.modules.mods.vampire

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.bukkit.realMaxHealth
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.kpaper.runnables.sync
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mutils.enums.Challenges
import de.miraculixx.mutils.messages.*
import de.miraculixx.mutils.utils.settings.challenges
import de.miraculixx.mutils.utils.settings.getSetting
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataType
import java.util.UUID

class VampireData(private val uuid: UUID) {
    //Settings - START
    private var blood: Double
    private var maxBlood: Int
    private var healthToBlood: Double
    private var bloodLoosing: Double

    init {
        val settings = challenges.getSetting(Challenges.VAMPIRE).settings
        blood = settings["startBlood"]?.toInt()?.getValue()?.toDouble() ?: 0.0
        maxBlood = settings["maxBlood"]?.toInt()?.getValue() ?: 0
        healthToBlood = settings["healthToBlood"]?.toDouble()?.getValue() ?: 0.0
        bloodLoosing = settings["bloodLoose"]?.toDouble()?.getValue() ?: 0.0
    }
    //Settings - END

    private val msgBlood = msgString("event.bloodPack")
    private val keyBloodPack = NamespacedKey(namespace, "challenge.vampire.blood")

    var running = true
        set(value) {
            if (value) {
                killEntity.register()
                consumePacks.register()
                onRegen.register()
            } else {
                killEntity.unregister()
                consumePacks.unregister()
                onRegen.unregister()
            }
            field = value
        }
    private val bar = BossBar.bossBar(cmp("Wait for server...", cError), 1f, BossBar.Color.RED, BossBar.Overlay.PROGRESS)
    private var cachedPlayer: Player?

    private val killEntity = listen<EntityDamageByEntityEvent> {
        val target = it.entity as? LivingEntity ?: return@listen
        if (target.health - it.finalDamage > 0) return@listen

        val damager = it.damager
        val source = if (damager is Projectile) {
            val shooter = damager.shooter
            if (shooter is Player && shooter.uniqueId == uuid) shooter else return@listen
        } else if (damager is Player && damager.uniqueId == uuid) damager else return@listen

        val rewardedBlood = (target.realMaxHealth * healthToBlood).toInt()
        if (source.isSneaking) {
            val pack = itemStack(Material.POTION) {
                meta<PotionMeta> {
                    customModel = 100
                    color = Color.fromRGB(146, 0, 0)
                    name = cmp(msgBlood, cError) + cmp(" (${rewardedBlood.div(2)}hp)")
                    addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)
                    persistentDataContainer.set(keyBloodPack, PersistentDataType.INTEGER, rewardedBlood / 2)
                }
            }
            source.inventory.addItem(pack)
            source.playSound(source, Sound.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH, 1f, 1.3f)
        } else blood = (blood + rewardedBlood).coerceAtMost(maxBlood.toDouble())
    }

    private val consumePacks = listen<PlayerItemConsumeEvent> {
        val player = it.player
        if (player.uniqueId != uuid) return@listen
        val item = it.item
        val meta = item.itemMeta
        if (item.itemMeta?.customModel != 100) return@listen
        val content = meta.persistentDataContainer.get(keyBloodPack, PersistentDataType.INTEGER) ?: return@listen

        val maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 20.0
        val missingHP = maxHealth - player.health
        player.health = (player.health + content).coerceAtMost(maxHealth)
        val remainingBlood = content - missingHP

        if (remainingBlood > 0) blood = (blood + remainingBlood).coerceAtMost(maxBlood.toDouble())
        player.playSound(player, Sound.ENTITY_SHEEP_SHEAR, 1f, 0.8f)
    }

    private val onRegen = listen<EntityRegainHealthEvent> {
        if (it.entity.uniqueId != uuid) return@listen
        it.isCancelled = true
    }

    private val scheduler = task(false, 0, 20) {
        if (!running) return@task
        val player = loadPlayer() ?: return@task
        val lightLevel = player.location.block.lightLevel // 0 - 15
        val amplifier = (lightLevel / 15.0).coerceAtLeast(0.1)
        blood = (blood - (bloodLoosing * amplifier)).coerceAtLeast(0.0)
        if (blood <= 0.0) {
            sync { player.damage(999.0) }
            running = false
        }

        val lightIcon = when (lightLevel) {
            in 0.toByte()..3.toByte() -> '☽'
            in 4.toByte()..10.toByte() -> '☁'
            else -> '☼'
        }
        bar.progress(blood.toFloat() / maxBlood)
        bar.name(cmp("${blood.toInt()}", cError) + cmp("/") + cmp("$maxBlood", cError) + cmp(" ◆", NamedTextColor.DARK_RED) + cmp(" - $lightIcon"))
    }

    private fun loadPlayer(): Player? {
        return if (cachedPlayer == null || cachedPlayer?.isOnline == false) {
            Bukkit.getPlayer(uuid)
        } else cachedPlayer
    }

    fun stop() {
        if (running) running = false
        scheduler?.cancel()
        loadPlayer()?.hideBossBar(bar)
    }

    init {
        val player = Bukkit.getPlayer(uuid)
        cachedPlayer = if (player != null) {
            player.showBossBar(bar)
            player.playSound(player, Sound.AMBIENT_BASALT_DELTAS_MOOD, 1f, 1f)
            player
        } else {
            broadcast(prefix + cmp("Failed to resolve all players! Check if you enabled onlinemode"))
            null
        }
    }
}