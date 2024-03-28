package de.miraculixx.mchallenge.modules.mods.simple.fly

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mvanilla.messages.cHighlight
import de.miraculixx.mvanilla.messages.cmp
import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.mchallenge.modules.spectator.Spectator
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.challenges.getSetting
import org.bukkit.Color
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class FLY : Challenge {
    private val data: HashMap<Player, ItemStack?> = HashMap()
    private val flyBoost: Double
    private val wings = itemStack(Material.ELYTRA) {
        meta {
            name = cmp("Hero Wings", cHighlight)
            addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1)
            addItemFlags(ItemFlag.HIDE_ENCHANTS)
            isUnbreakable = true
        }
    }

    init {
        val settings = challenges.getSetting(Challenges.FLY).settings
        flyBoost = settings["power"]?.toDouble()?.getValue() ?: 1.0
    }

    override fun register() {
        onMove.register()
        onSneakToggle.register()
    }

    override fun unregister() {
        onMove.unregister()
        onSneakToggle.unregister()
    }

    private val onMove = listen<PlayerMoveEvent>(register = false) {
        if (Spectator.isSpectator(it.player.uniqueId)) return@listen
        val p: Player = it.player
        //isOnGround is deprecated, because Player could send a client packed to the Server
        //that modify the isOnGround tag. But this Plugin isn't a AntiCheat or smt else...
        if (!p.isOnGround) {
            if (data[p] == null) data[p] = p.inventory.getItem(38)
            if (data[p] == null) data[p] = ItemStack(Material.AIR)
            p.inventory.setItem(38, wings)
        } else {
            if (p.inventory.getItem(38) == null) return@listen
            if (p.inventory.getItem(38)?.type == Material.ELYTRA) {
                p.inventory.setItem(38, data[p])
                data.remove(p)
            }
        }
    }

    private val onSneakToggle = listen<PlayerToggleSneakEvent>(register = false) {
        if (Spectator.isSpectator(it.player.uniqueId)) return@listen
        if (it.isSneaking) {
            val p: Player = it.player
            p.teleport(p.location.add(0.0, 0.1, 0.0))
            p.velocity = p.eyeLocation.direction.normalize().multiply(2)
            p.isGliding = true
            p.fallDistance = 10f
            flyBooster(p)
        }
    }

    private fun flyBooster(p: Player) {
        task(true, 1, 1) {
            if (!p.isSneaking || !p.isGliding || p.isOnGround || p.gameMode == GameMode.SPECTATOR) {
                it.cancel()
                return@task
            }
            val red = Particle.DustOptions(Color.RED, 1f)
            p.spawnParticle<Particle.DustOptions>(Particle.REDSTONE, p.location, 2, 0.05, 0.05, 0.05, 1.0, red)
            p.spawnParticle(Particle.NAUTILUS, p.location, 3, 0.1, 0.1, 0.1, 0.2)
            p.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 2, 2, true))
            p.velocity = p.eyeLocation.direction.normalize().multiply(flyBoost / 2)
        }
    }
}