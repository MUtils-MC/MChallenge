package de.miraculixx.mutils.modules.challenge.mods.checkpoints

import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*

class CheckpointsData(player: Player, zombie: Zombie) {
    private val onlyTeleport = ConfigManager.getConfig(Configs.MODULES).getBoolean("CHECKPOINTS.Teleport")
    private val inventory: Inventory
    private val zombie: Zombie
    private val uuid: UUID
    private val xp: Int
    private val hunger: Int
    private val health: Double
    private val saturation: Float
    private val brokenBlocks: HashMap<Location, Material>
    private val placeBlocks: MutableList<Location>
    private val entities: HashMap<Location, EntityType>

    init {
        uuid = player.uniqueId
        this.zombie = zombie
        hunger = player.foodLevel
        saturation = player.saturation
        health = player.health
        xp = player.level
        placeBlocks = ArrayList()
        brokenBlocks = HashMap()
        entities = HashMap()
        val inventory = Bukkit.createInventory(null, InventoryType.PLAYER)
        for ((slot, itemStack) in player.inventory.withIndex()) {
            if (itemStack == null) {
                inventory.setItem(slot, ItemStack(Material.AIR))
            } else {
                inventory.setItem(slot, itemStack)
            }
        }
        this.inventory = inventory
    }

    fun addBrokenBlock(block: Block) {
        brokenBlocks[block.location] = block.type
    }

    fun addPlacedBlock(block: Block) {
        placeBlocks.add(block.location)
    }

    fun addEntity(entity: Entity) {
        entities[entity.location] = entity.type
    }

    fun reset() {
        var slot = 0
        val player = Bukkit.getPlayer(uuid)
        player!!.teleport(zombie)
        zombie.remove()
        player.level = xp
        player.health = health
        player.saturation = saturation
        player.foodLevel = hunger
        player.fallDistance = 0f
        player.fireTicks = 0
        if (!onlyTeleport) {
            for (itemStack in inventory) {
                player.inventory.setItem(slot, itemStack)
                slot++
            }
            for (nearbyEntity in player.getNearbyEntities(100.0, 100.0, 100.0)) {
                if (nearbyEntity.type == EntityType.DROPPED_ITEM) nearbyEntity.remove()
            }
            brokenBlocks.forEach { (location: Location, material: Material?) -> location.world!!.getBlockAt(location).type = material }
            for (placeBlock in placeBlocks) {
                if (placeBlock.world!!.getBlockAt(placeBlock).type == Material.CHEST) {
                    placeBlock.world!!.getBlockAt(placeBlock).type = Material.BARREL
                    return
                }
                if (placeBlock.world!!.getBlockAt(placeBlock).type == Material.BARREL) {
                    placeBlock.world!!.getBlockAt(placeBlock).type = Material.CHEST
                    return
                }
                placeBlock.world!!.getBlockAt(placeBlock).type = Material.AIR
            }
            entities.forEach { (location: Location, type: EntityType?) -> location.world!!.spawnEntity(location, type) }
        }
    }
}