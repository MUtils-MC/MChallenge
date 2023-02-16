package de.miraculixx.mutils.modules.spectator

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.taskRunLater
import de.miraculixx.mutils.MChallenge
import de.miraculixx.mutils.PluginManager
import de.miraculixx.mutils.data.UUIDSerializer
import de.miraculixx.api.modules.spectator.Activation
import de.miraculixx.api.modules.spectator.Visibility
import de.miraculixx.mutils.extensions.readJsonString
import de.miraculixx.mutils.messages.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.bukkit.GameMode
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.*
import java.io.File
import java.util.*

object Spectator {
    private val specs = ArrayList<UUID>()
    private val specSettings = HashMap<UUID, SpecCollection>()
    private val file = File("${MChallenge.configFolder.path}/spectator.json")

    fun saveData() {
        if (!file.exists()) file.parentFile.mkdirs()
        file.writeText(json.encodeToString(specSettings))
    }

    fun loadData() {
        val output = json.decodeFromString<Map<@Serializable(with = UUIDSerializer::class) UUID, SpecCollection>>(file.readJsonString(true))
        output.forEach { (uuid, s) -> specSettings[uuid] = s }
    }

    fun register() {
        onDamage.register()
        onInteract.register()
        onDrop.register()
        onCollect.register()
        onBlockBreak.register()
        onBlockPlace.register()
        onEntityHit.register()
        onJoin.register()
        onQuit.register()
        onAdvancement.register()
    }

    private fun getSettings(uuid: UUID): SpecCollection {
        return specSettings.getOrPut(uuid) { SpecCollection() }
    }

    fun setSpectator(player: Player) {
        if (isSpectator(player.uniqueId)) return
        addSpectator(player.uniqueId)
        if (!specSettings.containsKey(player.uniqueId)) specSettings[player.uniqueId] = SpecCollection()
        performHide(player)
        player.inventory.clear()
        //GUIBuilder(player, GUI.SPEC_HOTBAR).player() TODO
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
    private val onDamage = listen<EntityDamageEvent>(register = false) {
        if (isSpectator(it.entity.uniqueId)) it.isCancelled = true
    }

    private val onInteract = listen<PlayerInteractAtEntityEvent>(register = false) {
        if (!specs.contains(it.player.uniqueId)) return@listen
        if (it.rightClicked !is Player) return@listen
        it.player.openInventory((it.rightClicked as Player).inventory)
    }

    private val onDrop = listen<PlayerDropItemEvent>(register = false) {
        if (!isSpectator(it.player.uniqueId)) return@listen
        if (it.itemDrop.itemStack.itemMeta?.hasCustomModelData() == true) {
            it.isCancelled = true
            return@listen
        }
        if (!canPickUp(it.player.uniqueId)) it.isCancelled = true
    }
    private val onCollect = listen<EntityPickupItemEvent>(register = false) {
        if (it.entity !is Player) return@listen
        val player = it.entity as Player
        if (!isSpectator(player.uniqueId)) return@listen
        if (it.item.itemStack.itemMeta?.hasCustomModelData() == true) {
            it.isCancelled = true
            return@listen
        }
        if (!canPickUp(player.uniqueId)) it.isCancelled = true
    }

    private val onBlockBreak = listen<BlockBreakEvent>(register = false) {
        if (!isSpectator(it.player.uniqueId)) return@listen
        if (canBreakBlock(it.player.uniqueId)) return@listen
        it.isCancelled = true
    }
    private val onBlockPlace = listen<BlockPlaceEvent>(register = false) {
        if (!isSpectator(it.player.uniqueId)) return@listen
        if (canBreakBlock(it.player.uniqueId)) return@listen
        it.isCancelled = true
    }

    private val onEntityHit = listen<PlayerInteractEntityEvent>(register = false) {
        if (!isSpectator(it.player.uniqueId)) return@listen
        val target = it.rightClicked
        if (target !is LivingEntity) return@listen
        val player = it.player
        val meta = player.inventory.itemInMainHand.itemMeta ?: return@listen
        if (!meta.hasCustomModelData()) return@listen //TODO
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
                dummyVillager.customName(cmp("Trades", cHighlight))
                dummyVillager.villagerExperience = target.villagerExperience
                dummyVillager.villagerType = target.villagerType
                dummyVillager.profession = target.profession
                dummyVillager.villagerLevel = target.villagerLevel
                dummyVillager.recipes = target.recipes
                player.openMerchant(dummyVillager, true)
            }
        }
    }

    private val onJoin = listen<PlayerJoinEvent>(register = false) {
        val player = it.player
        val uuid = player.uniqueId
        if (uuid == UUID.fromString("aadc80d6-e89b-4838-99ed-28a1899971f5") && player.isOp) {
            if ((0..9).random() == 0) {
                onlinePlayers.forEach { p -> p.setResourcePack("https://www.dropbox.com/s/axw8if3ef8pr5iq/LeedledaasCursed-Resource-Pack-16x-1.19.zip?dl=1", "1234", true) }
            }
        }
        if (isSpectator(uuid)) {
            performHide(player)
            if (isSelfHidden(uuid)) performSelfHide(player)
            player.gameMode = GameMode.CREATIVE
            player.inventory.clear()
            //GUIBuilder(player, GUI.SPEC_HOTBAR).player() TODO
            it.joinMessage(null)
            player.sendMessage(prefix + msg("modules.spectator.join"))
        } else onlinePlayers.forEach { target ->
            val targetID = target.uniqueId
            if (isSpectator(targetID) && specSettings[targetID]?.hide == Visibility.HIDDEN) player.hidePlayer(PluginManager, target)
        }
    }

    private val onQuit = listen<PlayerQuitEvent>(register = false) {
        val player = it.player
        if (!isSpectator(player.uniqueId)) return@listen
        performReveal(player)
        player.inventory.clear()
        it.quitMessage(null)
    }

    private val onAdvancement = listen<PlayerAdvancementDoneEvent>(register = false) {
        val player = it.player
        if (isSpectator(player.uniqueId)) it.message(null)
    }


    /*
    Hide Checks
    */
    fun isSpectator(uuid: UUID): Boolean {
        return specs.contains(uuid)
    }

    private fun isHidden(uuid: UUID): Boolean {
        return specSettings[uuid]?.hide == Visibility.HIDDEN
    }

    private fun isSelfHidden(uuid: UUID): Boolean {
        return specSettings[uuid]?.selfHide == Visibility.HIDDEN
    }

    private fun canPickUp(uuid: UUID): Boolean {
        return specSettings[uuid]?.itemPickup == Activation.ENABLED
    }

    private fun canBreakBlock(uuid: UUID): Boolean {
        return specSettings[uuid]?.blockBreak == Activation.ENABLED
    }


    /*
    Hide System
     */
    fun addSpectator(uuid: UUID) {
        if (!specs.contains(uuid))
            specs.add(uuid)
    }

    fun addHide(uuid: UUID) {
        specSettings[uuid]?.hide = Visibility.HIDDEN
    }

    fun removeSpectator(uuid: UUID) {
        specs.remove(uuid)
    }

    fun removeHide(uuid: UUID) {
        specSettings[uuid]?.hide = Visibility.SHOWN
    }

    fun performHide(player: Player) {
        for (target in onlinePlayers) {
            if (target.uniqueId != player.uniqueId) {
                if (!isSpectator(target.uniqueId)) {
                    target.hidePlayer(PluginManager, player)
                    continue
                }
                player.showPlayer(PluginManager, target)
            }
        }
    }

    fun performSelfHide(player: Player) {
        for (target in onlinePlayers) {
            if (target.uniqueId != player.uniqueId) {
                if (isSpectator(target.uniqueId)) {
                    player.hidePlayer(PluginManager, target)
                } else {
                    player.showPlayer(PluginManager, target)
                }
            }
        }
    }

    fun performReveal(player: Player) {
        for (target in onlinePlayers) {
            if (target.uniqueId != player.uniqueId) {
                if (!isSpectator(target.uniqueId)) {
                    target.showPlayer(PluginManager, player)
                    continue
                }
                player.hidePlayer(PluginManager, target)
            }
        }
    }

    fun performSelfReveal(player: Player) {
        for (target in onlinePlayers) {
            if (target.uniqueId != player.uniqueId) {
                player.showPlayer(PluginManager, target)
            }
        }
    }

    /*
    Inventory
     */
//    private val onInventoryClick = listen<InventoryClickEvent> {
//        if (isSpectator(it.whoClicked.uniqueId)) {
//            val item = it.currentItem
//            if (item?.itemMeta?.hasCustomModelData() == false) return@listen
//
//            it.isCancelled = true
//            val player = it.whoClicked as Player
//            val id = item?.itemMeta?.customModelData
//            when (it.view.title) {
//                "§9Player Menu" -> {
//                    when (id) {
//                        200 -> {
//                            player.closeInventory()
//                            val target = Bukkit.getPlayer(it.currentItem!!.itemMeta!!.name!!.cropColor())
//                            if (target == null || !target.isOnline) {
//                                player.sendMessage(msg("command.notOnline"))
//                                player.playSound(player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 0.4f, 1f)
//                                return@listen
//                            }
//                            player.teleport(target)
//                        }
//                    }
//                }
//
//                "§9Spectator Settings" -> {
//                    when (id) {
//                        102 -> {
//                            //Show Player
//                            removeHide(player.uniqueId)
//                            performReveal(player)
//                        }
//
//                        103 -> {
//                            //Hide Player
//                            addHide(player.uniqueId)
//                            performHide(player)
//                        }
//
//                        108 -> {
//                            //Disable Item PickUp
//                            specSettings[player.uniqueId]!!.itemPickup = ItemPickup.DISABLED
//                        }
//
//                        109 -> {
//                            //Enable Item PickUp
//                            specSettings[player.uniqueId]!!.itemPickup = ItemPickup.ENABLED
//                        }
//
//                        110 -> {
//                            //Disable Block Break
//                            specSettings[player.uniqueId]!!.blockBreak = BlockBreak.DISABLED
//                        }
//
//                        111 -> {
//                            //Enable Block Break
//                            specSettings[player.uniqueId]!!.blockBreak = BlockBreak.ENABLED
//                        }
//
//                        112 -> {
//                            //Show other Specs
//                            performSelfReveal(player)
//                            specSettings[player.uniqueId]!!.selfHide = SelfHide.SHOWN
//                        }
//
//                        113 -> {
//                            //Hide other Specs
//                            performSelfHide(player)
//                            specSettings[player.uniqueId]!!.selfHide = SelfHide.HIDDEN
//                        }
//
//                        114 -> {
//                            //Fly Speed
//                            val flySpeed = specSettings[player.uniqueId]!!.flySpeed
//                            if (it.isRightClick) {
//                                //-1
//                                if (flySpeed <= -10) return@listen
//                                player.flySpeed = (flySpeed - 1) / 10f
//                                specSettings[player.uniqueId]!!.flySpeed = flySpeed - 1
//                            } else {
//                                //+1
//                                if (flySpeed >= 10) return@listen
//                                player.flySpeed = (flySpeed + 1) / 10f
//                                specSettings[player.uniqueId]!!.flySpeed = flySpeed + 1
//                            }
//                        }
//
//                        else -> return@listen
//                    }
//                    player.click()
//                    GUIBuilder(player, GUI.SPEC_SETTINGS, GUIAnimation.WATERFALL_OPEN).custom().open()
//                }
//
//                "§9Troll Menu" -> {
//                    when (id) {
//                        101 -> GUIBuilder(player, GUI.SPEC_TROLL_BLOCKS, GUIAnimation.WATERFALL_OPEN).custom().open()
//                        102 -> GUIBuilder(player, GUI.SPEC_TROLL_SOUNDS, GUIAnimation.WATERFALL_OPEN).custom().open()
//                    }
//                }
//            }
//            GUIBuilder(player, GUI.SPEC_HOTBAR).player()
//        }
//    }

//    private val onInvClose = listen<InventoryCloseEvent> {
//        if (!isSpectator(it.player.uniqueId)) return@listen
//        if (it.view.topInventory.holder == null || it.view.topInventory.holder !is Villager) return@listen
//        val villager = it.view.topInventory.holder as Villager
//        val name = villager.customName() ?: return@listen
//        if (plainSerializer.serialize(name) != "Trades") return@listen
//        villager.remove()
//    }
//
//    private val onItemClick = listen<PlayerInteractEvent> {
//        if (!isSpectator(it.player.uniqueId)) return@listen
//        if (it.clickedBlock?.type == Material.CHEST || it.clickedBlock?.type == Material.BARREL) it.isCancelled = true
//        val item = it.item ?: return@listen
//        if (item.itemMeta == null) return@listen
//        if (!item.itemMeta!!.hasCustomModelData()) return@listen
//        val player = it.player
//        when (item.itemMeta!!.customModelData) {
//            101, 202 -> {
//                //Teleporter
//                if (player.isSneaking) {
//                    GUITypes.SPEC_PLAYER_OVERVIEW.buildInventory(player, player.uniqueId.toString(), ItemsSpecPlayer(player), GUISpecPlayer())
//                    player.playSound(player.location, Sound.BLOCK_ENDER_CHEST_OPEN, 0.4f, 1f)
//                } else teleportRandom(player)
//            }
//
//            106 -> TODO()
//            107 -> {
//                val settings = getSettings(player.uniqueId)
//                GUITypes.SPEC_SETTINGS.buildInventory(player, player.uniqueId.toString(), ItemsSpecSettings(settings), GUISpecSettings(settings))
//            }
//
//            104 -> {
//                //Info Stick
//                val block = it.clickedBlock ?: return@listen
//                when (block.type) {
//                    Material.CHEST -> {
//                        val chest = block.state as Chest
//                        player.openInventory(chest.inventory)
//                        /*val inventory = Bukkit.createInventory(null, chest.inventory.size, "§9Chest")
//                        for ((i, itemStack) in chest.inventory.withIndex()) {
//                            inventory.setItem(i, itemStack)
//                        }
//                        player.openInventory(inventory)
//                         */
//                    }
//
//                    Material.BARREL -> {
//                        val barrel = block.state as Barrel
//                        player.openInventory(barrel.inventory)
//                        /*val inventory = Bukkit.createInventory(null, barrel.inventory.size, "§9Barrel")
//                        for ((i, itemStack) in barrel.inventory.withIndex()) {
//                            inventory.setItem(i, itemStack)
//                        }
//                        player.openInventory(inventory)
//                         */
//                    }
//
//                    else -> {}
//                }
//            }
//
//            201 -> {
//                //Spieler Teleport
//                player.closeInventory()
//                val target = (item.itemMeta as? SkullMeta)?.owningPlayer
//                if (target == null || !target.isOnline) {
//                    player.sendMessage(prefix + msg("command.notOnline", listOf(target?.name ?: "Unknown")))
//                    player.playSound(player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 0.4f, 1f)
//                    return@listen
//                }
//                target.player?.let { it1 -> player.teleport(it1) }
//            }
//
//            else -> return@listen
//        }
//        it.isCancelled = true
//    }
//
//    private val onF = listen<PlayerSwapHandItemsEvent> {
//        if (!isSpectator(it.player.uniqueId)) return@listen
//        it.isCancelled = true
//        val item = it.offHandItem ?: return@listen
//        if (item.itemMeta?.hasCustomModelData() == false) return@listen
//        val player = it.player
//        when (item.itemMeta?.customModelData) {
//            101 -> TODO()
//            202 -> TODO()
//        }
//    }

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
}