@file:Suppress("UNCHECKED_CAST")

package de.miraculixx.mutils.modules.challenge.mods

import de.miraculixx.mutils.utils.enums.Challenge
import de.miraculixx.mutils.utils.enums.challenges.RivalCollectMode
import de.miraculixx.mutils.challenge.modules.Challenge
import de.miraculixx.mutils.challenge.utils.getItems
import de.miraculixx.mutils.challenge.utils.getLivingMobs
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.gui.items.skullTexture
import de.miraculixx.mutils.utils.text.broadcastSound
import de.miraculixx.mutils.utils.text.cError
import de.miraculixx.mutils.utils.text.cmp
import de.miraculixx.mutils.utils.text.msg
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.block.Biome
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

class RivalCollect : Challenge {
    override val challenge = Challenge.RIVALS_COLLECT
    private var items: List<Material>? = null
    private var biomes: List<Biome>? = null
    private var mobs: List<EntityType>? = null
    private var mode = RivalCollectMode.ITEMS

    private val progress = HashMap<UUID, MutableList<*>>()
    private val armorStands = HashMap<UUID, ArmorStand>()

    override fun start(): Boolean {
        val conf = ConfigManager.getConfig(Configs.MODULES)
        mode = RivalCollectMode.valueOf(conf.getString("RIVALS_COLLECT.Mode") ?: "ITEMS")
        val jokerAmount = conf.getInt("RIVALS_COLLECT.Joker")
        val jokerItem = itemStack(Material.PLAYER_HEAD) {
            meta<SkullMeta> {
                name = cmp("Joker", cError, bold = true)
                customModel = 501
                amount = jokerAmount
            }
            itemMeta = skullTexture(
                itemMeta as SkullMeta,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTJkZDExZGEwNDI1MmY3NmI2OTM0YmMyNjYxMmY1NGYyNjRmMzBlZWQ3NGRmODk5NDEyMDllMTkxYmViYzBhMiJ9fX0="
            )
        }
        when (mode) {
            RivalCollectMode.BIOMES -> {
                biomes = Biome.values().filter { it != Biome.CUSTOM }
                onlinePlayers.forEach {
                    val currentBiome = it.location.block.biome
                    val newBiome = biomes?.filter { b -> b != currentBiome }?.random() ?: Biome.PLAINS
                    val biomeMaterial = biomeToItem(newBiome)
                    progress[it.uniqueId] = mutableListOf(newBiome)
                    it.sendMessage(msg("modules.ch.rivalCollect.newItem", it, newBiome.name))
                    it.inventory.addItem(jokerItem)
                    taskRunLater(5) { createArmorStand(it).equipment.helmet = ItemStack(biomeMaterial) }
                }
            }
            RivalCollectMode.ITEMS -> {
                items = getItems(false)
                onlinePlayers.forEach {
                    val newItem = items?.random() ?: Material.STONE
                    progress[it.uniqueId] = mutableListOf(newItem)
                    it.playSound(it, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.5f)
                    it.sendMessage(msg("modules.ch.rivalCollect.newItem", it, newItem.name))
                    it.inventory.addItem(jokerItem)
                    taskRunLater(5) { createArmorStand(it).equipment.helmet = ItemStack(newItem) }
                }
            }
            RivalCollectMode.MOBS -> {
                mobs = getLivingMobs(true)
                onlinePlayers.forEach {
                    val newMob = mobs?.random() ?: EntityType.ZOMBIE
                    val mobMaterial = mobToItem(newMob)
                    progress[it.uniqueId] = mutableListOf(newMob)
                    it.playSound(it, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.5f)
                    it.sendMessage(msg("modules.ch.rivalCollect.newItem", it, newMob.name))
                    it.inventory.addItem(jokerItem)
                    taskRunLater(5) { createArmorStand(it).equipment.helmet = ItemStack(mobMaterial) }
                }
            }
        }
        return true
    }

    override fun stop() {
        biomes = null
        items = null
        mobs = null
        progress.clear()
        armorStands.clear()
        armorStands.forEach { (_, am) -> am.remove() }
    }

    override fun register() {
        onJokerClick.register()
        onMoveInPortal.register()
        when (mode) {
            RivalCollectMode.ITEMS -> {
                onCollect.register()
                onInvClick.register()
            }
            RivalCollectMode.BIOMES -> {
                onMoveInBiome.register()
            }
            RivalCollectMode.MOBS -> {
                onKill.register()
            }
        }
    }

    override fun unregister() {
        onJokerClick.unregister()
        onMoveInPortal.unregister()
        when (mode) {
            RivalCollectMode.ITEMS -> {
                onCollect.unregister()
                onInvClick.unregister()
            }
            RivalCollectMode.BIOMES -> {
                onMoveInBiome.unregister()
            }
            RivalCollectMode.MOBS -> {
                onKill.unregister()
            }
        }

        //Announce Stats
        broadcastSound(Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 1f, 1f)
        broadcast("§9§m          §9[ Leaderboard ]§m          ")
        val ranking = progress.entries.sortedBy { it.value.size }.map { it.key }
        var counter = 1
        ranking.forEach {
            val name = Bukkit.getPlayer(it)?.name
            val rank = when (counter) {
                1 -> "§c§l①"
                2 -> "§6§l②"
                3 -> "§e§l③"
                else -> "§7$counter"
            }
            broadcast("$rank §7-§f $name (${progress[it]?.size?.minus(1)?:0})")
            counter++
        }
    }

    private val onCollect = listen<EntityPickupItemEvent>(register = false) {
        if (it.entity !is Player) return@listen
        checkItem(it.entity as Player, it.item.itemStack.type)
    }
    private val onInvClick = listen<InventoryClickEvent>(register = false) {
        if (it.whoClicked !is Player) return@listen
        checkItem(it.whoClicked as Player, it.currentItem?.type ?: return@listen)
    }

    private val onMoveInBiome = listen<PlayerMoveEvent>(register = false) {
        val to = it.to
        val from = it.from
        val biome = to.block.biome
        if (biome == from.block.biome) return@listen
        checkBiome(it.player, biome)
    }

    private val onKill = listen<EntityDamageByEntityEvent>(register = false) {
        val damager = it.damager
        val entity = it.entity
        if (entity !is LivingEntity || damager !is Player) return@listen
        if (entity.health.minus(it.finalDamage) <= 0)
            checkMob(damager, entity.type)
    }

    private val onJokerClick = listen<PlayerInteractEvent>(register = false) {
        val player = it.player
        val item = player.inventory.itemInMainHand
        if (item.type != Material.PLAYER_HEAD) return@listen
        if (item.hasItemMeta() && item.itemMeta.hasCustomModelData()) {
            if (item.itemMeta.customModel == 501) {
                //Joker has been activated
                when (mode) {
                    RivalCollectMode.ITEMS -> {
                        val newItem = (progress[player.uniqueId] as MutableList<Material>?)?.last() ?: Material.STONE
                        player.inventory.addItem(ItemStack(newItem))
                        checkItem(player, newItem)
                    }
                    RivalCollectMode.BIOMES -> {
                        val newBiome = (progress[player.uniqueId] as MutableList<Biome>?)?.last() ?: Biome.PLAINS
                        checkBiome(player, newBiome)
                    }
                    RivalCollectMode.MOBS -> {
                        val newMob = (progress[player.uniqueId] as MutableList<EntityType>?)?.last() ?: EntityType.ZOMBIE
                        checkMob(player, newMob)
                    }
                }

                if (item.amount > 1)
                    item.amount = item.amount - 1
                else item.type = Material.AIR
                player.inventory.setItemInMainHand(item)
                it.isCancelled = true
            }
        }
    }

    private val onMoveInPortal = listen<PlayerMoveEvent>(register = false) {
        val player = it.player
        val loc = it.to
        val block = loc.block

        if (block.type == Material.NETHER_PORTAL || block.type == Material.END_PORTAL) {
            val am = armorStands[player.uniqueId]!!
            if (player.passengers.isNotEmpty()) player.removePassenger(am)
            am.teleport(loc.clone().subtract(0.0, -1.4, 0.0))
        } else if (player.passengers.isEmpty()) {
            val am = armorStands[player.uniqueId]
            am?.teleport(player)
            player.addPassenger(am ?: return@listen)
        }
    }


    private fun checkItem(player: Player, material: Material) {
        val uuid = player.uniqueId
        val list = (progress[uuid] ?: return) as MutableList<Material>
        val current = list.last()
        if (current == material) {
            //Next Item
            val newItems = items?.toMutableList()
            newItems?.removeAll(list)
            val newItem = newItems?.random() ?: Material.STONE
            list.add(newItem)
            val itemName = newItem.name.replace('_', ' ')
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.5f)
            onlinePlayers.forEach {
                if (it != player)
                    it.sendMessage(msg("modules.ch.rivalCollect.itemFound", player, itemName, material.name.replace('_', ' ')))
            }
            player.sendMessage(msg("modules.ch.rivalCollect.newItem", player, itemName))
            val armorStand = armorStands[uuid] ?: createArmorStand(player)
            armorStand.equipment.helmet = ItemStack(newItem)
        }
    }

    private fun checkBiome(player: Player, biome: Biome) {
        val uuid = player.uniqueId
        val list = (progress[uuid] ?: return) as MutableList<Biome>
        val current = list.last()
        if (current == biome) {
            //Next Biome
            val newBiomes = biomes?.toMutableList()
            newBiomes?.removeAll(list)
            val newBiome = newBiomes?.random() ?: Biome.PLAINS
            list.add(newBiome)
            val biomeName = newBiome.name.replace('_', ' ')
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.5f)
            onlinePlayers.forEach {
                if (it != player)
                    it.sendMessage(msg("modules.ch.rivalCollect.itemFound", player, biomeName, biome.name.replace('_', ' ')))
            }
            player.sendMessage(msg("modules.ch.rivalCollect.newItem", player, biomeName))
            val armorStand = armorStands[uuid] ?: createArmorStand(player)
            armorStand.equipment.helmet = ItemStack(biomeToItem(newBiome))
        }
    }

    private fun checkMob(player: Player, mob: EntityType) {
        val uuid = player.uniqueId
        val list = (progress[uuid] ?: return) as MutableList<EntityType>
        val current = list.last()
        if (current == mob) {
            //Next Mob
            val newMobs = mobs?.toMutableList()
            newMobs?.removeAll(list)
            val newMob = newMobs?.random() ?: EntityType.ZOMBIE
            list.add(newMob)
            val mobName = newMob.name.replace('_', ' ')
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.5f)
            onlinePlayers.forEach {
                if (it != player)
                    it.sendMessage(msg("modules.ch.rivalCollect.itemFound", player, mobName, mob.name.replace('_', ' ')))
            }
            player.sendMessage(msg("modules.ch.rivalCollect.newItem", player, mobName))
            val armorStand = armorStands[uuid] ?: createArmorStand(player)
            armorStand.equipment.helmet = ItemStack(mobToItem(newMob))
        }
    }

    private fun biomeToItem(biome: Biome): Material {
        val name = biome.name
        return when {
            name.contains("FROZEN") -> Material.ICE
            name.contains("SAVANNA") -> Material.ACACIA_WOOD
            name.contains("TAIGA") -> Material.SPRUCE_WOOD
            name.contains("BIRCH") -> Material.BIRCH_WOOD
            name.contains("JUNGLE") -> Material.JUNGLE_WOOD
            name.contains("BEACH") || name.contains("DESERT") -> Material.SAND
            name.contains("BADLANDS") -> Material.RED_SAND
            name.contains("FLOWER") -> Material.SUNFLOWER
            name.contains("CRIMSON") -> Material.CRIMSON_STEM
            name.contains("WARPED") -> Material.WARPED_STEM
            name.contains("BASALT") -> Material.BASALT
            name.contains("SOUL") -> Material.SOUL_SAND
            name.contains("WASTES") -> Material.NETHERRACK
            name.contains("LUSH") -> Material.MOSS_BLOCK
            name.contains("MUSHROOM") -> Material.MYCELIUM

            name.contains("FOREST") -> Material.OAK_SAPLING
            name.contains("SNOWY") -> Material.SNOW_BLOCK
            name.contains("OCEAN") -> Material.WATER_BUCKET
            name.contains("HILLS") || name.contains("PEAKS") -> Material.STONE

            else -> Material.GRASS_BLOCK
        }
    }

    private fun mobToItem(mob: EntityType): Material {
        return try {
            Material.valueOf("${mob}_SPAWN_EGG")
        } catch (e: Exception) {
            Material.POLAR_BEAR_SPAWN_EGG
        }
    }

    private fun createArmorStand(player: Player): ArmorStand {
        val armorStand = player.world.spawnEntity(player.location, EntityType.ARMOR_STAND) as ArmorStand
        armorStand.isVisible = false
        armorStand.isMarker = true
        armorStand.isSmall = true
        armorStand.setBasePlate(false)
        EquipmentSlot.values().forEach { armorStand.addDisabledSlots(it) }
        player.addPassenger(armorStand)
        armorStands[player.uniqueId] = armorStand
        return armorStand
    }
}