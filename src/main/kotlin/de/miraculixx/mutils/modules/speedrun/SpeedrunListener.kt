package de.miraculixx.mutils.modules.speedrun

import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.event.entity.EntityDropItemEvent
import org.bukkit.event.entity.EntityTargetLivingEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionType
import kotlin.random.Random

object SpeedrunListener {

    fun register() {
        onTarget.register()
        onPiglinTrade.register()
    }

    fun unregister() {
        onTarget.unregister()
        onPiglinTrade.unregister()
    }

    private val onTarget = listen<EntityTargetLivingEntityEvent> {
        if (it.entity.type != EntityType.PIGLIN_BRUTE) return@listen
        val c = ConfigManager.getConfig(Configs.SPEEDRUN)
        if (!c.getBoolean("Disable Brutes")) return@listen
        it.entity.remove()
    }


    private val onPiglinTrade = listen<EntityDropItemEvent> {
        if (it.entity.type != EntityType.PIGLIN) return@listen
        val c = ConfigManager.getConfig(Configs.SPEEDRUN)
        if (!c.getBoolean("Old Trading")) return@listen
        val item = when (Random.nextInt(1, 424)) {
            in 1..5 -> itemStack(Material.ENCHANTED_BOOK) {
                meta<BookMeta> {
                    addEnchantment(Enchantment.SOUL_SPEED, Random.nextInt(1, 4))
                }
            }
            in 6..13 -> itemStack(Material.IRON_BOOTS) {
                meta {
                    addEnchant(Enchantment.SOUL_SPEED, Random.nextInt(1, 4), false)
                }
            }
            in 14..23 -> itemStack(Material.IRON_NUGGET) {
                amount = Random.nextInt(9, 37)
            }
            in 24..33 -> itemStack(Material.POTION) {
                meta<PotionMeta> {
                    basePotionData = PotionData(PotionType.FIRE_RESISTANCE)
                }
            }
            in 34..43 -> itemStack(Material.SPLASH_POTION) {
                meta<PotionMeta> {
                    basePotionData = PotionData(PotionType.FIRE_RESISTANCE)
                }
            }
            in 44..63 -> itemStack(Material.QUARTZ) {
                amount = Random.nextInt(8, 17)
            }
            in 64..83 -> itemStack(Material.GLOWSTONE_DUST) {
                amount = Random.nextInt(5, 12)
            }
            in 84..103 -> itemStack(Material.ENDER_PEARL) {
                amount = Random.nextInt(4, 9)
            }
            in 104..123 -> itemStack(Material.STRING) {
                amount = Random.nextInt(8, 25)
            }
            in 124..163 -> itemStack(Material.FIRE_CHARGE) {
                amount = Random.nextInt(1, 6)
            }
            in 164..203 -> itemStack(Material.GRAVEL) {
                amount = Random.nextInt(8, 17)
            }
            in 204..243 -> itemStack(Material.LEATHER) {
                amount = Random.nextInt(4, 11)
            }
            in 244..283 -> itemStack(Material.NETHER_BRICK) {
                amount = Random.nextInt(4, 17)
            }
            in 284..323 -> ItemStack(Material.OBSIDIAN)
            in 324..363 -> itemStack(Material.CRYING_OBSIDIAN) {
                amount = Random.nextInt(1, 4)
            }
            in 364..403 -> itemStack(Material.SOUL_SAND) {
                amount = Random.nextInt(4, 17)
            }
            in 404..423 -> itemStack(Material.MAGMA_CREAM) {
                amount = Random.nextInt(2, 7)
            }
            else -> ItemStack(Material.AIR)
        }
        val loc = it.entity.location
        val world = it.entity.world
        val i = world.spawnEntity(loc.add(0.0, 1.0, 0.0), EntityType.DROPPED_ITEM) as Item
        i.itemStack = item
        i.velocity = it.itemDrop.velocity
        it.isCancelled = true
    }
}