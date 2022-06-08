@file:Suppress("DEPRECATION")

package de.miraculixx.mutils.modules.challenge.mods.realistic

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.modules.challenge.Challenge
import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.geometry.minus
import net.axay.kspigot.extensions.geometry.vecY
import net.axay.kspigot.runnables.task
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.BlockFace
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class Realistic: Challenge() {
    override val challenge = Modules.REALISTIC
    private var weight: InventoryWeight? = null
    private var pathLogic: PathLogic? = null
    private var drinkLogic: DrinkLogic? = null
    private var temperatureLogic: TemperatureLogic? = null
    private var infoBar: InfoBar? = null

    private fun getListener(): List<SingleListener<Event>> {
        val list = ArrayList<SingleListener<*>>()
        list.add(onMove)
        list.add(onQuit)
        list.add(onBlockPlace)
        list.add(onBlockBreak)
        list.add(onItemUse)
        list.add(onInvClick)
        list.add(onHunger)
        list.add(onDamage)
        list.add(onDeath)
        list.add(onDrink)
        list.add(onExplode)
        list.add(onExplodeV2)
        list.add(onTeleport)
        return list as ArrayList<SingleListener<Event>>
    }
    override fun start(): Boolean {
        weight = InventoryWeight()
        pathLogic = PathLogic()
        drinkLogic = DrinkLogic()
        temperatureLogic = TemperatureLogic(drinkLogic!!)
        infoBar = InfoBar(weight!!, drinkLogic!!, temperatureLogic!!)
        return true
    }

    override fun stop() {
        weight = null
        pathLogic = null
        drinkLogic = null
        temperatureLogic = null
        infoBar = null
    }

    override fun register() {
        onMove.register()
        onQuit.register()
        onBlockBreak.register()
        onBlockPlace.register()
        onItemUse.register()
        onHunger.register()
        onDrink.register()
        onInvClick.register()
        onExplode.register()
        onExplodeV2.register()
        onTeleport.register()
        onDeath.register()
        onDamage.register()
    }
    override fun unregister() {
        onMove.unregister()
        onQuit.unregister()
        onBlockBreak.unregister()
        onBlockPlace.unregister()
        onItemUse.unregister()
        onHunger.unregister()
        onDrink.unregister()
        onInvClick.unregister()
        onExplode.unregister()
        onExplodeV2.unregister()
        onTeleport.unregister()
        onDeath.unregister()
        onDamage.unregister()
    }

    private val onMove = listen<PlayerMoveEvent>(register = false) {
        //Path bildet sich beim Laufen vvv
        val block = ((it.to ?: return@listen) - vecY(1)).block
        when (block.type) {
            Material.GRASS_BLOCK, Material.DIRT, Material.PODZOL, Material.COARSE_DIRT, Material.MYCELIUM -> pathLogic?.addBlock(block)
            else -> {}
        }
        //Schuhe nutzen sich ab vvv
        if (it.player.inventory.getItem(36) != null) {
            if ((1..50).random() == 10) {
                val meta = it.player.inventory.getItem(36)!!.itemMeta as org.bukkit.inventory.meta.Damageable
                meta.damage = meta.damage + 1
                it.player.inventory.getItem(36)!!.itemMeta = meta
            }
        }
    }

    private val onQuit = listen<PlayerQuitEvent>(register = false) {
        infoBar?.removePlayer(it.player)
    }

    private val onBlockBreak = listen<BlockBreakEvent>(register = false) {
        if (Tag.LOGS.isTagged(it.block.type) || it.block.type.name.contains("MUSHROOM")) {
            BlockPhysics("TREE", it.block.location.clone().add(0.0, 1.0, 0.0).block)
            return@listen
        }
        BlockPhysics("BREAK", it.block)
    }

    private val onBlockPlace = listen<BlockPlaceEvent>(register = false) {
        val block = it.block.location.clone().subtract(0.0, 1.0, 0.0).block
        if (block.type.name.endsWith("AIR") || block.type == Material.WATER) {
            it.block.world.spawnFallingBlock(it.block.location.add(0.5, 0.0, 0.5), it.block.blockData)
            it.block.type = Material.AIR
        }
    }

    private val onItemUse = listen<PlayerInteractEvent>(register = false) {
        if (it.item?.type == Material.WATER_BUCKET && it.clickedBlock != null && it.action.isRightClick) {
            task(true, 15, 0, 1) { task ->
                if (it.blockFace == BlockFace.UP) it.clickedBlock!!.location.add(0.0, 1.0, 0.0).block.type = Material.AIR
                if (it.blockFace == BlockFace.DOWN) it.clickedBlock!!.location.add(0.0, -1.0, 0.0).block.type = Material.AIR
                if (it.blockFace == BlockFace.EAST) it.clickedBlock!!.location.add(1.0, 0.0, 0.0).block.type = Material.AIR
                if (it.blockFace == BlockFace.WEST) it.clickedBlock!!.location.add(-1.0, 0.0, 0.0).block.type = Material.AIR
                if (it.blockFace == BlockFace.NORTH) it.clickedBlock!!.location.add(0.0, 0.0, -1.0).block.type = Material.AIR
                if (it.blockFace == BlockFace.SOUTH) it.clickedBlock!!.location.add(0.0, 0.0, 1.0).block.type = Material.AIR
                task.cancel()
            }
        }
    }

    private val onHunger = listen<FoodLevelChangeEvent>(register = false) {
        drinkLogic?.modify(it.entity as Player, -1)
        if (temperatureLogic?.getPlayer(it.entity.uniqueId)!! >= 80) drinkLogic?.modify(it.entity as Player, -1)
    }

    private val onDrink = listen<PlayerItemConsumeEvent>(register = false) {
        if (it.item.type == Material.POTION) {
            drinkLogic?.modify(it.player, 8)
        }
    }

    private val onInvClick = listen<InventoryClickEvent>(register = false) {
        //Stackable Pots
        if (it.currentItem?.type == Material.POTION && it.cursor?.type == Material.POTION && it.click == ClickType.LEFT) {
            if ((it.currentItem!!.amount + it.cursor!!.amount) <= 6) {
                it.isCancelled = true
                it.currentItem!!.amount += it.cursor!!.amount
                it.cursor = null
                (it.whoClicked as Player).updateInventory()
            }
        } else
            if (it.currentItem == null && it.cursor?.type == Material.POTION && it.click == ClickType.LEFT) {
                it.isCancelled = true
                it.inventory.setItem(it.slot, it.cursor)
                it.cursor = null
                (it.whoClicked as Player).updateInventory()
            }

        //Inventory logic
        if (it.cursor != null && it.click == ClickType.LEFT) {
            when (it.cursor!!.type) {
                Material.TNT -> broadcast("TODO")//Redstone check
                Material.SAND, Material.RED_SAND, Material.GRAVEL -> broadcast("TODO")//fall down
                else -> {}
            }
            invSchedule(it.slot, it.inventory)

        }
    }

    private val onExplode = listen<EntityExplodeEvent>(register = false) {
        it.blockList().forEach { block -> BlockPhysics("EXPLODE", block) }
        it.blockList().clear()
    }
    private val onExplodeV2 = listen<BlockExplodeEvent>(register = false) {
        it.blockList().forEach { block -> BlockPhysics("EXPLODE", block) }
        it.blockList().clear()
    }

    private val onDeath = listen<EntityDeathEvent>(register = false) {
        if (it.entity.type == EntityType.BAT) {
            it.entity.world.dropItem(it.entity.location, ItemStack(Material.LEATHER, (1..2).random()))
        }
    }

    private val onTeleport = listen<PlayerTeleportEvent>(register = false) {
        if (it.cause == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            it.player.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION, 10 * 20, 1))
            it.to!!.world!!.spawnEntity(it.to!!, EntityType.ENDERMITE)
        }
    }

    private val onDamage = listen<EntityDamageEvent>(register = false) {
        if (it.entity is Player) {
            if (it.cause == EntityDamageEvent.DamageCause.FALL) {
                val p = (it.entity as Player).player
                val dmg = it.damage
                if (dmg < 3) {
                    p!!.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 40, 4, false, false, false))
                } else if (dmg > 3 && dmg < 6) {
                    p!!.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 140, 4, false, false, false))
                } else {
                    p!!.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 250, 9, false, false, false))
                }
                if ((0..2).random() == 1) {
                    p.dropItem(true)
                    p.inventory.getItem(p.inventory.heldItemSlot)?.type = Material.AIR
                    p.updateInventory()
                }
                if ((0..1).random() == 1) {
                    p.inventory.getItem(40)?.let { it1 -> p.world.dropItem(p.location, it1) }
                    p.inventory.setItem(40, null)
                }
            }
        }
    }

    //Essentials
    private fun invSchedule(slot: Int, inventory: Inventory) {
        task(true, 10, 10) {
            if (slot in 9..23) {
                if (inventory.getItem(slot - 9) == null) {
                    inventory.setItem(slot - 9, inventory.getItem(slot))
                    inventory.setItem(slot, ItemStack(Material.AIR))
                } else {
                    it.cancel()
                    return@task
                }
            } else if (slot in 24..35) {
                if (inventory.getItem(slot - 27) == null) {
                    inventory.setItem(slot - 27, inventory.getItem(slot))
                    inventory.setItem(slot, null)
                } else {
                    it.cancel()
                    return@task
                }
            }
        }
    }
}