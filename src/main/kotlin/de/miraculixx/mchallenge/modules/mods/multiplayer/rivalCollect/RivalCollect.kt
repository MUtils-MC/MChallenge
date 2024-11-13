package de.miraculixx.mchallenge.modules.mods.multiplayer.rivalCollect

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.bukkit.language
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.gui.items.skullTexture
import de.miraculixx.kpaper.items.*
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.challenges.getSetting
import de.miraculixx.mchallenge.modules.spectator.Spectator
import de.miraculixx.mchallenge.utils.getItems
import de.miraculixx.mcommons.extensions.enumOf
import de.miraculixx.mcommons.serializer.miniMessage
import de.miraculixx.mcommons.text.*
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.Sound
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
    private var items: List<Material>
    private var biomes: List<Biome>
    private var mobs: List<EntityType>
    private var modes: List<RivalCollectMode>
    private val jokerAmount: Int

    private val progress: MutableMap<UUID, MutableList<CollectProgress>> = mutableMapOf()
    private val playerData: MutableMap<UUID, RivalPlayerData> = mutableMapOf()

    init {
        val settings = challenges.getSetting(Challenges.RIVALS_COLLECT).settings
        val modeSection = settings["mode"]?.toSection()?.getValue()
        modes = buildList {
            items = modeSection?.get("items")?.toBool()?.getValue()?.let {
                if (it) {
                    add(RivalCollectMode.ITEMS)
                    getItems(false, false)
                } else emptyList()
            } ?: emptyList()

            Registry.BIOME
            biomes = modeSection?.get("biomes")?.toBool()?.getValue()?.let {
                if (it) {
                    add(RivalCollectMode.BIOMES)
                    Registry.BIOME.toList()
                } else emptyList()
            } ?: emptyList()

            mobs = modeSection?.get("mobs")?.toBool()?.getValue()?.let {
                if (it) {
                    add(RivalCollectMode.MOBS)
                    getLivingMobs(true)
                } else emptyList()
            } ?: emptyList()
        }
        jokerAmount = settings["joker"]?.toInt()?.getValue() ?: 3
    }

    override fun start(): Boolean {
        val jokerItem = itemStack(Material.PLAYER_HEAD) {
            meta<SkullMeta> {
                name = cmp("Joker", cError, bold = true)
                customModel = 501
                amount = jokerAmount
                skullTexture(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTJkZDExZGEwNDI1MmY3NmI2OTM0YmMyNjYxMmY1NGYyNjRmMzBlZWQ3NGRmODk5NDEyMDllMTkxYmViYzBhMiJ9fX0="
                )
            }
        }

        onlinePlayers.forEach { player ->
            if (Spectator.isSpectator(player.uniqueId) || player.gameMode == GameMode.SPECTATOR) return@forEach
            player.announceNext(null)
            player.inventory.addItem(jokerItem)
        }

        return true
    }

    override fun stop() {
        progress.clear()
        playerData.clear()
        playerData.forEach { (_, am) -> am.armorStand?.remove() }
    }

    override fun register() {
        onJokerClick.register()
        onMoveInPortal.register()

        if (modes.contains(RivalCollectMode.ITEMS)) {
            onCollect.register()
            onInvClick.register()
        }
        if (modes.contains(RivalCollectMode.BIOMES)) onMoveInBiome.register()
        if (modes.contains(RivalCollectMode.MOBS)) onKill.register()
    }

    override fun unregister() {
        onJokerClick.unregister()
        onMoveInPortal.unregister()

        if (modes.contains(RivalCollectMode.ITEMS)) {
            onCollect.unregister()
            onInvClick.unregister()
        }
        if (modes.contains(RivalCollectMode.BIOMES)) onMoveInBiome.unregister()
        if (modes.contains(RivalCollectMode.MOBS)) onKill.unregister()

        //Announce Stats
        onlinePlayers.forEach { it.playSound(it, Sound.BLOCK_BEACON_POWER_SELECT, 1f, 1f) }
        broadcast(miniMessage.deserialize("<blue><st>          </st>[ Leaderboard ]<st>          "))
        val ranking = progress.entries.sortedBy { it.value.size }.map { it.key }
        var counter = 1
        ranking.forEach {
            val name = Bukkit.getPlayer(it)?.name
            val rank = when (counter) {
                1 -> cmp("①", cError, bold = true)
                2 -> cmp("②", NamedTextColor.GOLD, bold = true)
                3 -> cmp("③", NamedTextColor.YELLOW, bold = true)
                else -> cmp("$counter", NamedTextColor.GRAY)
            }
            broadcast(rank + cmp(" - ") + cmp("$name (${progress[it]?.size?.minus(1) ?: 0})", NamedTextColor.WHITE))
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
        if (item.itemMeta?.customModel == 501) {
            //A Joker has been activated
            val uuid = player.uniqueId
            val key = progress[uuid]?.lastOrNull()?.key ?: return@listen
            when (playerData[uuid]?.currentType) {
                RivalCollectMode.ITEMS -> {
                    val newItem = enumOf<Material>(key) ?: Material.STONE
                    player.inventory.addItem(ItemStack(newItem))
                    checkItem(player, newItem)
                }

                RivalCollectMode.BIOMES -> {
                    val biome = Registry.BIOME.get(NamespacedKey("minecraft", key)) ?: Biome.PLAINS
                    checkBiome(player, biome)
                }

                RivalCollectMode.MOBS -> {
                    val newMob = enumOf<EntityType>(key) ?: EntityType.ZOMBIE
                    checkMob(player, newMob)
                }

                else -> Unit
            }

            if (item.amount > 1) item.amount -= 1
            else item.type = Material.AIR
            player.inventory.setItemInMainHand(item)
            it.isCancelled = true
        }
    }

    private val onMoveInPortal = listen<PlayerMoveEvent>(register = false) {
        val player = it.player
        val loc = it.to
        val block = loc.block

        if (block.type == Material.NETHER_PORTAL || block.type == Material.END_PORTAL) {
            val am = playerData[player.uniqueId]?.armorStand ?: return@listen
            if (player.passengers.isNotEmpty()) player.removePassenger(am)
            am.teleport(loc.clone().subtract(0.0, -1.4, 0.0))
        } else if (player.passengers.isEmpty()) {
            val am = playerData[player.uniqueId]?.armorStand ?: return@listen
            am.teleport(player)
            player.addPassenger(am)
        }
    }


    private fun checkItem(player: Player, material: Material) {
        val uuid = player.uniqueId
        val list = progress[uuid] ?: return
        val current = list.last()
        if (current.key == material.name) {
            player.announceNext(material.translationKey())
        }
    }

    private fun checkBiome(player: Player, biome: Biome) {
        val uuid = player.uniqueId
        val list = progress[uuid] ?: return
        val current = list.last()
        if (current.key == biome.key.key) {
            player.announceNext(biome.translationKey())
        }
    }

    private fun checkMob(player: Player, mob: EntityType) {
        val uuid = player.uniqueId
        val list = progress[uuid] ?: return
        val current = list.last()
        if (current.key == mob.name) {
            player.announceNext(mob.translationKey())
        }
    }

    private fun Player.announceNext(oldKey: String?) {
        // Calculate Next Task
        val newMode = modes.random()
        val progress = this@RivalCollect.progress.getOrPut(uniqueId) { mutableListOf() }
        val data = when (newMode) {
            RivalCollectMode.ITEMS -> {
                val copy = items.toMutableList()
                copy.removeAll(progress.filter { it.type == RivalCollectMode.ITEMS }.map { enumOf<Material>(it.key) }.toSet())
                val newEntry = copy.random()
                RivalObject(newEntry.translationKey(), newEntry, newEntry.name)
            }

            RivalCollectMode.BIOMES -> {
                val copy = biomes.toMutableList()
                copy.removeAll(progress.filter { it.type == RivalCollectMode.BIOMES }.mapNotNull { Registry.BIOME.get(NamespacedKey("minecraft", it.key)) }.toSet())
                val newEntry = copy.random()
                RivalObject(newEntry.translationKey(), biomeToItem(newEntry), newEntry.key.key)
            }

            RivalCollectMode.MOBS -> {
                val copy = mobs.toMutableList()
                copy.removeAll(progress.filter { it.type == RivalCollectMode.MOBS }.map { enumOf<EntityType>(it.key) }.toSet())
                val newEntry = copy.random()
                RivalObject(newEntry.translationKey(), mobToItem(newEntry), newEntry.name)
            }
        }
        progress.add(CollectProgress(data.enumKey, newMode))

        val locale = language()
        playSound(this, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.5f)
        if (oldKey != null) {
            onlinePlayers.forEach {
                if (it != player) it.sendMessage(prefix + locale.msg("event.rivalCollect.itemFound", listOf(name, "<lang:$oldKey>")))
            }
        }
        sendMessage(prefix + locale.msg("event.rivalCollect.newItem", listOf("<lang:${data.translationKey}>")))

        // Apply new data
        val playerData = playerData.getOrPut(uniqueId) { RivalPlayerData(createArmorStand(this), newMode) }
        playerData.currentType = newMode
        val armorStand = playerData.armorStand
        if (armorStand == null) {
            val newArmorStand = createArmorStand(this)
            playerData.armorStand = newArmorStand
            newArmorStand.equipment.helmet = ItemStack(data.display)
        } else armorStand.equipment.helmet = ItemStack(data.display)
    }

    private fun biomeToItem(biome: Biome): Material {
        val name = biome.key.key.uppercase()
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
        } catch (_: Exception) {
            Material.POLAR_BEAR_SPAWN_EGG
        }
    }

    private fun createArmorStand(player: Player): ArmorStand {
        val armorStand = player.world.spawnEntity(player.location, EntityType.ARMOR_STAND) as ArmorStand
        armorStand.isVisible = false
        armorStand.isMarker = true
        armorStand.isSmall = true
        armorStand.setBasePlate(false)
        EquipmentSlot.entries.forEach { armorStand.addDisabledSlots(it) }
        player.addPassenger(armorStand)
        val data = playerData.getOrPut(player.uniqueId) { RivalPlayerData(armorStand, RivalCollectMode.ITEMS) }
        data.armorStand = armorStand
        return armorStand
    }

    private data class CollectProgress(
        val key: String,
        val type: RivalCollectMode
    )

    private data class RivalPlayerData(
        var armorStand: ArmorStand?,
        var currentType: RivalCollectMode
    )

    private data class RivalObject(
        val translationKey: String,
        val display: Material,
        val enumKey: String
    )
}