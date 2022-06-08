@file:Suppress("UNCHECKED_CAST")

package de.miraculixx.mutils.modules.challenge.mods.noSameItems

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.modules.challenge.Challenge
import de.miraculixx.mutils.modules.spectator.Spectator
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerJoinEvent

class NoSameItem : Challenge() {
    override val challenge = Modules.NO_SAME_ITEM
    private var manager: NoSameItemManager? = null

    override fun start(): Boolean {
        manager = NoSameItemManager()
        return true
    }

    override fun stop() {
        manager?.removeAll()
        manager = null
    }

    override fun register() {
        onJoin.register()
        onDie.register()
        onCraft.register()
        onInvClose.register()
        onCollect.register()
    }
    override fun unregister() {
        onJoin.unregister()
        onDie.unregister()
        onCraft.unregister()
        onInvClose.unregister()
        onCollect.unregister()
    }

    private val onJoin = listen<PlayerJoinEvent>(register = false) {
        if (Spectator.isSpectator(it.player.uniqueId)) return@listen
        manager?.createBossBar(it.player)
    }

    private val onDie = listen<PlayerDeathEvent>(register = false) {
        manager?.removePlayer(it.entity)
    }

    private val onCraft = listen<CraftItemEvent>(register = false) {
        if (Spectator.isSpectator(it.whoClicked.uniqueId)) return@listen
        if (it.currentItem == null) return@listen
        val items = ArrayList<Material>()
        items.add(it.currentItem!!.type)
        manager?.addItem(it.whoClicked as Player, "Crafting", items)
    }

    private val onInvClose = listen<InventoryCloseEvent>(register = false) {
        if (Spectator.isSpectator(it.player.uniqueId)) return@listen
        if (it.view.bottomInventory.type != InventoryType.PLAYER) return@listen
        val before = ArrayList<Material>()
        val after = ArrayList<Material>()
        //Füge zur Liste "before" alle für den Spieler registrierten Items hinzu
        manager?.getItems(it.player as Player)?.forEach { material ->
            before.add(material)
        }

        //Füge zur Liste "after" alle Items hinzu, welche sich im Inventar befinden
        it.player.inventory.forEach { item ->
            if (item != null) {
                if (!after.contains(item.type)) after.add(item.type)
            }
        }
        if (it.view.topInventory.type == InventoryType.CRAFTING || it.view.topInventory.type == InventoryType.WORKBENCH) {
            it.view.topInventory.setItem(0, null)
            it.view.topInventory.forEach { item ->
                if (item != null) {
                    if (!after.contains(item.type)) after.add(item.type)
                }
            }
        }
        if (!after.contains(it.view.cursor?.type) && it.view.cursor != null && !it.view.cursor!!.type.isAir) after.add(it.view.cursor!!.type)

        //Berechne Items, welche zum Inventar hinzugekommen sind
        val differenz = after.clone() as ArrayList<Material>
        before.forEach { material ->
            differenz.remove(material)
        }
        manager?.addItem(it.player as Player, "Inventory", differenz)
    }

    private val onCollect = listen<EntityPickupItemEvent>(register = false) {
        if (it.entity !is Player) return@listen
        if (Spectator.isSpectator(it.entity.uniqueId)) return@listen
        val list = ArrayList<Material>()
        list.add(it.item.itemStack.type)
        manager?.addItem(it.entity as Player, "Collect", list)
    }
}