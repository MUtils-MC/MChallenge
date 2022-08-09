@file:Suppress("unused")

package de.miraculixx.mutils.modules.spectator

import de.miraculixx.mutils.Main
import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.enums.settings.spectator.BlockBreak
import de.miraculixx.mutils.enums.settings.spectator.Hide
import de.miraculixx.mutils.enums.settings.spectator.ItemPickup
import de.miraculixx.mutils.enums.settings.spectator.SelfHide
import de.miraculixx.mutils.system.config.Config
import de.miraculixx.mutils.utils.cropColor
import de.miraculixx.mutils.utils.msg
import de.miraculixx.mutils.utils.tools.click
import de.miraculixx.mutils.utils.tools.gui.GUIBuilder
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.*
import org.bukkit.block.Barrel
import org.bukkit.block.Chest
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.*
import java.util.*

object Spectator {
    private val specs = ArrayList<UUID>()
    private val settings = HashMap<UUID, SpecCollection>()
    private val file = Config("utils/spectate")

    /*
    Data Handling
     */
    fun saveData() {
        val config = file.getConfig()
        val uuidStrings = ArrayList<String>()
        for (uuid in specs) {
            uuidStrings.add(uuid.toString())
        }
        config.set("Spectators", uuidStrings)

        //Settings Speichern
        settings.forEach { (uuid, setting) ->
            config.set("Settings.$uuid.HIDDEN", setting.hide.name)
            config.set("Settings.$uuid.SELFHIDDEN", setting.selfHide.name)
            config.set("Settings.$uuid.PICKUP", setting.itemPickup.name)
            config.set("Settings.$uuid.BLOCKS", setting.blockBreak.name)
            config.set("Settings.$uuid.SPEED", setting.flySpeed)
        }
        file.save()
    }

    private fun loadData() {
        val config = file.getConfig()
        for (s in config.getStringList("Spectators")) {
            val uuid = UUID.fromString(s) ?: continue
            specs.add(uuid)
        }

        //Settings Load
        for (uuid in specs) {
            val setting = SpecCollection()
            setting.flySpeed = config.getInt("Settings.$uuid.SPEED")
            setting.hide = Hide.valueOf(config.getString("Settings.$uuid.HIDDEN") ?: "HIDDEN")
            setting.selfHide = SelfHide.valueOf(config.getString("Settings.$uuid.SELFHIDDEN") ?: "SHOWN")
            setting.blockBreak = BlockBreak.valueOf(config.getString("Settings.$uuid.BLOCKS") ?: "DISABLED")
            setting.itemPickup = ItemPickup.valueOf(config.getString("Settings.$uuid.PICKUP") ?: "DISABLED")
            settings[uuid] = setting
        }
    }

    fun getSettings(uuid: UUID): SpecCollection {
        if (settings[uuid] == null) settings[uuid] = SpecCollection()
        return settings[uuid]!!
    }

    fun setSpectator(player: Player) {
        if (isSpectator(player.uniqueId)) return
        addSpectator(player.uniqueId)
        if (!settings.containsKey(player.uniqueId)) settings[player.uniqueId] = SpecCollection()
        performHide(player)
        player.inventory.clear()
        GUIBuilder(player, GUI.SPEC_HOTBAR).player()
        player.gameMode = GameMode.CREATIVE
    }

    fun unsetSpectator(player: Player) {
        if (!isSpectator(player.uniqueId)) return
        removeSpectator(player.uniqueId)
        removeHide(player.uniqueId)
        performReveal(player)
        player.inventory.clear()
    }

    //
    // Features
    //
    private val onDamage = listen<EntityDamageEvent> {
        if (isSpectator(it.entity.uniqueId)) it.isCancelled = true
    }

    private val onInteract = listen<PlayerInteractAtEntityEvent> {
        if (!specs.contains(it.player.uniqueId)) return@listen
        if (it.rightClicked !is Player) return@listen
        it.player.openInventory((it.rightClicked as Player).inventory)
    }

    private val onDrop = listen<PlayerDropItemEvent> {
        if (!isSpectator(it.player.uniqueId)) return@listen
        if (it.itemDrop.itemStack.itemMeta?.hasCustomModelData() == true) {
            it.isCancelled = true
            return@listen
        }
        if (!canPickUp(it.player.uniqueId)) it.isCancelled = true
    }
    private val onCollect = listen<EntityPickupItemEvent> {
        if (it.entity !is Player) return@listen
        val player = it.entity as Player
        if (!isSpectator(player.uniqueId)) return@listen
        if (it.item.itemStack.itemMeta?.hasCustomModelData() == true) {
            it.isCancelled = true
            return@listen
        }
        if (!canPickUp(player.uniqueId)) it.isCancelled = true
    }

    private val onBlockBreak = listen<BlockBreakEvent> {
        if (!isSpectator(it.player.uniqueId)) return@listen
        if (canBreakBlock(it.player.uniqueId)) return@listen
        it.isCancelled = true
    }
    private val onBlockPlace = listen<BlockPlaceEvent> {
        if (!isSpectator(it.player.uniqueId)) return@listen
        if (canBreakBlock(it.player.uniqueId)) return@listen
        it.isCancelled = true
    }

    private val onEntityHit = listen<PlayerInteractEntityEvent> {
        if (!isSpectator(it.player.uniqueId)) return@listen
        val target = it.rightClicked
        if (target !is LivingEntity) return@listen
        val player = it.player
        if (player.inventory.itemInMainHand.itemMeta == null) return@listen
        if (!player.inventory.itemInMainHand.itemMeta?.hasCustomModelData()!!) return@listen
        it.isCancelled = true
        val env = it.rightClicked.world.environment.name.replace("NORMAL", "OVERWORLD")

        var output = "\n\n\n§f§m           §f[ §5INFO §f]§f§m           " +
                "\n§7Name: §9${target.name}" +
                "\n§7HP: §9${target.health.toInt()} §7+ §9" + target.absorptionAmount.toInt() +
                "\n§7World: §9${target.world.name} ($env)"

        if (target is Player) {
            output += "\n§7Level: §9${target.level}" +
                    "\n§7Render Distance: §9${target.clientViewDistance} chunks"
        } else if (target is Villager) {
            output += "\n§7Profession: §9${target.profession.name}" +
                    "\n§7Trade Level: §9${target.villagerLevel}"
            taskRunLater(2) {
                val dummyVillager = player.world.spawnEntity(player.location.add(0.0, 10.0, 0.0), EntityType.VILLAGER) as Villager
                dummyVillager.setAI(false)
                dummyVillager.isInvisible = true
                dummyVillager.isSilent = true
                dummyVillager.isInvulnerable = true
                dummyVillager.customName = "§9Trades"
                dummyVillager.villagerExperience = target.villagerExperience
                dummyVillager.villagerType = target.villagerType
                dummyVillager.profession = target.profession
                dummyVillager.villagerLevel = target.villagerLevel
                dummyVillager.recipes = target.recipes
                player.openMerchant(dummyVillager, true)
            }
        }
    }

    private val onJoin = listen<PlayerJoinEvent> {
        val player = it.player
        if (isSpectator(player.uniqueId)) {
            performHide(player)
            if (isSelfHidden(player.uniqueId)) performSelfHide(player)
            player.gameMode = GameMode.CREATIVE
            player.inventory.clear()
            GUIBuilder(player, GUI.SPEC_HOTBAR).player()
            it.joinMessage = null
            it.player.sendMessage(msg("modules.spectator.join"))
        } else {
            for (target in onlinePlayers) {
                if (isSpectator(target.uniqueId)) player.hidePlayer(Main.INSTANCE, target)
            }
        }
    }

    private val onQuit = listen<PlayerQuitEvent> {
        val player = it.player
        if (!isSpectator(player.uniqueId)) return@listen
        performReveal(player)
        player.inventory.clear()
        it.quitMessage = null
    }

    private val onAdvancement = listen<PlayerAdvancementDoneEvent> {
        val player = it.player
        if (isSpectator(player.uniqueId)) {
            player.world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
            taskRunLater(2) {
                player.world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true)
            }
        }
    }


    /*
    Hide Checks
    */
    fun isSpectator(uuid: UUID): Boolean {
        return specs.contains(uuid)
    }

    private fun isHidden(uuid: UUID): Boolean {
        return when (settings[uuid]!!.hide) {
            Hide.SHOWN -> false
            Hide.HIDDEN -> true
        }
    }

    private fun isSelfHidden(uuid: UUID): Boolean {
        return when (settings[uuid]!!.selfHide) {
            SelfHide.SHOWN -> false
            SelfHide.HIDDEN -> true
        }
    }

    private fun canPickUp(uuid: UUID): Boolean {
        return when (settings[uuid]!!.itemPickup) {
            ItemPickup.DISABLED -> false
            ItemPickup.ENABLED -> true
        }
    }

    private fun canBreakBlock(uuid: UUID): Boolean {
        return when (settings[uuid]!!.blockBreak) {
            BlockBreak.DISABLED -> false
            BlockBreak.ENABLED -> true
        }
    }


    /*
    Hide System
     */
    private fun addSpectator(uuid: UUID) {
        if (!specs.contains(uuid))
            specs.add(uuid)
    }

    private fun addHide(uuid: UUID) {
        settings[uuid]?.hide = Hide.HIDDEN
    }

    private fun removeSpectator(uuid: UUID) {
        specs.remove(uuid)
    }

    private fun removeHide(uuid: UUID) {
        settings[uuid]?.hide = Hide.SHOWN
    }

    private fun performHide(player: Player) {
        for (target in onlinePlayers) {
            if (target.uniqueId != player.uniqueId) {
                if (!isSpectator(target.uniqueId)) {
                    target.hidePlayer(Main.INSTANCE, player)
                    continue
                }
                player.showPlayer(Main.INSTANCE, target)
            }
        }
    }

    private fun performSelfHide(player: Player) {
        for (target in onlinePlayers) {
            if (target.uniqueId != player.uniqueId) {
                if (isSpectator(target.uniqueId)) {
                    player.hidePlayer(Main.INSTANCE, target)
                } else {
                    player.showPlayer(Main.INSTANCE, target)
                }
            }
        }
    }

    private fun performReveal(player: Player) {
        for (target in onlinePlayers) {
            if (target.uniqueId != player.uniqueId) {
                if (!isSpectator(target.uniqueId)) {
                    target.showPlayer(Main.INSTANCE, player)
                    continue
                }
                player.hidePlayer(Main.INSTANCE, target)
            }
        }
    }

    private fun performSelfReveal(player: Player) {
        for (target in onlinePlayers) {
            if (target.uniqueId != player.uniqueId) {
                player.showPlayer(Main.INSTANCE, target)
            }
        }
    }

    /*
    Inventory
     */
    private val onInventoryClick = listen<InventoryClickEvent> {
        if (isSpectator(it.whoClicked.uniqueId)) {
            val item = it.currentItem
            if (item?.itemMeta?.hasCustomModelData() == false) return@listen

            it.isCancelled = true
            val player = it.whoClicked as Player
            val id = item?.itemMeta?.customModelData
            when (it.view.title) {
                "§9Player Menu" -> {
                    when (id) {
                        200 -> {
                            player.closeInventory()
                            val target = Bukkit.getPlayer(it.currentItem!!.itemMeta!!.name!!.cropColor())
                            if (target == null || !target.isOnline) {
                                player.sendMessage(msg("command.notOnline"))
                                player.playSound(player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 0.4f, 1f)
                                return@listen
                            }
                            player.teleport(target)
                        }
                    }
                }
                "§9Spectator Settings" -> {
                    when (id) {
                        102 -> {
                            //Show Player
                            removeHide(player.uniqueId)
                            performReveal(player)
                        }
                        103 -> {
                            //Hide Player
                            addHide(player.uniqueId)
                            performHide(player)
                        }
                        108 -> {
                            //Disable Item PickUp
                            settings[player.uniqueId]!!.itemPickup = ItemPickup.DISABLED
                        }
                        109 -> {
                            //Enable Item PickUp
                            settings[player.uniqueId]!!.itemPickup = ItemPickup.ENABLED
                        }
                        110 -> {
                            //Disable Block Break
                            settings[player.uniqueId]!!.blockBreak = BlockBreak.DISABLED
                        }
                        111 -> {
                            //Enable Block Break
                            settings[player.uniqueId]!!.blockBreak = BlockBreak.ENABLED
                        }
                        112 -> {
                            //Show other Specs
                            performSelfReveal(player)
                            settings[player.uniqueId]!!.selfHide = SelfHide.SHOWN
                        }
                        113 -> {
                            //Hide other Specs
                            performSelfHide(player)
                            settings[player.uniqueId]!!.selfHide = SelfHide.HIDDEN
                        }
                        114 -> {
                            //Fly Speed
                            val flySpeed = settings[player.uniqueId]!!.flySpeed
                            if (it.isRightClick) {
                                //-1
                                if (flySpeed <= -10) return@listen
                                player.flySpeed = (flySpeed - 1) / 10f
                                settings[player.uniqueId]!!.flySpeed = flySpeed - 1
                            } else {
                                //+1
                                if (flySpeed >= 10) return@listen
                                player.flySpeed = (flySpeed + 1) / 10f
                                settings[player.uniqueId]!!.flySpeed = flySpeed + 1
                            }
                        }

                        else -> return@listen
                    }
                    player.click()
                    GUIBuilder(player, GUI.SPEC_SETTINGS, GUIAnimation.WATERFALL_OPEN).custom().open()
                }
                "§9Troll Menu" -> {
                    when (id) {
                        101 -> GUIBuilder(player, GUI.SPEC_TROLL_BLOCKS, GUIAnimation.WATERFALL_OPEN).custom().open()
                        102 -> GUIBuilder(player, GUI.SPEC_TROLL_SOUNDS, GUIAnimation.WATERFALL_OPEN).custom().open()
                    }
                }
            }
            GUIBuilder(player, GUI.SPEC_HOTBAR).player()
        }
    }

    private val onInvClose = listen<InventoryCloseEvent> {
        if (!isSpectator(it.player.uniqueId)) return@listen
        if (it.view.topInventory.holder == null || it.view.topInventory.holder !is Villager) return@listen
        val villager = it.view.topInventory.holder as Villager
        if (villager.customName == null || villager.customName != "§9Trades") return@listen
        villager.remove()
    }

    private val onItemClick = listen<PlayerInteractEvent> {
        if (!isSpectator(it.player.uniqueId)) return@listen
        if (it.clickedBlock?.type == Material.CHEST || it.clickedBlock?.type == Material.BARREL) it.isCancelled = true
        val item = it.item ?: return@listen
        if (item.itemMeta == null) return@listen
        if (!item.itemMeta!!.hasCustomModelData()) return@listen
        val player = it.player
        when (item.itemMeta!!.customModelData) {
            101, 202 -> {
                //Teleporter
                if (player.isSneaking) {
                    val size = when (onlinePlayers.size) {
                        in 0..8 -> 9
                        in 9..17 -> 18
                        in 18..26 -> 27
                        in 27..35 -> 36
                        in 36..44 -> 45
                        else -> 54
                    }
                    val inventory = Bukkit.createInventory(null, size, "§9Player Menu")
                    for ((i, p) in onlinePlayers.withIndex()) {
                        val i1 = itemStack(Material.PLAYER_HEAD) {
                            meta {
                                customModel = 201
                                name = "§9${p.displayName}"
                                lore = listOf(
                                    " ",
                                    "§7∙ §9§nPlayer Info",
                                    "   §7HP: §9${p.health.toInt()}",
                                    "   §7FOOD: §9${p.foodLevel}",
                                    "   §7LVL: §9${p.level}",
                                    "   §7DIM: §9${p.world.environment.name.replace('_', ' ')}",
                                    " ",
                                    "§9Click§7 ≫ Teleport",
                                )
                            }
                        }
                        inventory.setItem(i, i1)
                    }
                    player.openInventory(inventory)
                    player.playSound(player.location, Sound.BLOCK_ENDER_CHEST_OPEN, 0.4f, 1f)
                } else teleportRandom(player)
            }
            106 -> GUIBuilder(player, GUI.SPEC_TROLL, GUIAnimation.WATERFALL_OPEN).custom().open()
            107 -> GUIBuilder(player, GUI.SPEC_SETTINGS, GUIAnimation.WATERFALL_OPEN).custom().open()
            104 -> {
                //Info Stick
                if (it.clickedBlock == null) return@listen
                when (it.clickedBlock!!.type) {
                    Material.CHEST -> {
                        val chest = it.clickedBlock!!.state as Chest
                        val inventory = Bukkit.createInventory(null, chest.inventory.size, "§9Chest")
                        for ((i, itemStack) in chest.inventory.withIndex()) {
                            inventory.setItem(i, itemStack)
                        }
                        player.openInventory(inventory)
                    }
                    Material.BARREL -> {
                        val barrel = it.clickedBlock!!.state as Barrel
                        val inventory = Bukkit.createInventory(null, barrel.inventory.size, "§9Barrel")
                        for ((i, itemStack) in barrel.inventory.withIndex()) {
                            inventory.setItem(i, itemStack)
                        }
                        player.openInventory(inventory)
                    }
                    else -> {}
                }
            }

            201 -> {
                //Spieler Teleport
                player.closeInventory()
                val target = Bukkit.getPlayer(item.itemMeta!!.name!!.cropColor())
                if (target == null || !target.isOnline) {
                    player.sendMessage(msg("command.notOnline"))
                    player.playSound(player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 0.4f, 1f)
                    return@listen
                }
                player.teleport(target)
            }
            else -> return@listen
        }
        it.isCancelled = true
    }

    private val onF = listen<PlayerSwapHandItemsEvent> {
        if (!isSpectator(it.player.uniqueId)) return@listen
        it.isCancelled = true
        val item = it.offHandItem ?: return@listen
        if (item.itemMeta?.hasCustomModelData() == false) return@listen
        val player = it.player
        when (item.itemMeta?.customModelData) {
            101 -> GUIBuilder(player, GUI.SPEC_HOTBAR_QUICK).player()
            202 -> GUIBuilder(player, GUI.SPEC_HOTBAR).player()
        }
    }

    private fun teleportRandom(player: Player) {
        val list = ArrayList<Player>()
        for (onlinePlayer in onlinePlayers) {
            if (!isSpectator(onlinePlayer.uniqueId)) list.add(onlinePlayer)
        }
        if (list.isEmpty()) {
            player.sendMessage(msg("modules.spectator.noPlayer"))
            return
        }
        list.shuffle()
        player.teleport(list[0])
    }

    init {
        loadData()
    }
}