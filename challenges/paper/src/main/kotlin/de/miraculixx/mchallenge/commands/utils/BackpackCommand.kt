package de.miraculixx.mchallenge.commands.utils

import de.miraculixx.challenge.api.utils.CustomHeads
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.kpaper.utils.ItemStackSerializer
import de.miraculixx.mcore.gui.GUIEvent
import de.miraculixx.mcore.gui.InventoryUtils
import de.miraculixx.mcore.gui.data.CustomInventory
import de.miraculixx.mcore.gui.data.InventoryManager
import de.miraculixx.mcore.gui.items.ItemProvider
import de.miraculixx.mcore.gui.items.skullTexture
import de.miraculixx.mvanilla.extensions.msg
import de.miraculixx.mvanilla.extensions.readJsonString
import de.miraculixx.mvanilla.messages.*
import dev.jorel.commandapi.arguments.LiteralArgument
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.kotlindsl.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.io.File

class BackpackCommand {
    private val file = File("${de.miraculixx.mchallenge.MChallenge.configFolder.path}/data/backpack.json")
    private val data: Data = try {
        json.decodeFromString(file.readJsonString(true))
    } catch (e: Exception) {
        if (debug) {
            consoleAudience.sendMessage(prefix + cmp(e.message ?: "Unknown"))
            consoleAudience.sendMessage(prefix + cmp("Failed to load backpack data! ^ Reason above ^", cError))
        }
        Data(true, 3, mutableMapOf())
    }

    @Suppress("unused")
    val command = commandTree("backpack") {
        withAliases("bp")
        playerExecutor { player, _ ->
            openInventory(player, player.name)
        }
        argument(PlayerArgument("player").withPermission("command.backpack.other")) {
            playerExecutor { player, args ->
                val target = args[0] as Player
                openInventory(player, target.name)
            }
        }
        argument(LiteralArgument("setup").withPermission("mutils.backpack.setup")) {
            literalArgument("global") {
                booleanArgument("global") {
                    anyExecutor { commandSender, args ->
                        val isGlobal = args[0] as Boolean
                        data.global = isGlobal
                        commandSender.sendMessage(prefix + msg("command.backpack.setGlobal", listOf(isGlobal.msg())))
                    }
                }
            }
            literalArgument("size") {
                integerArgument("size", 1, 5) {
                    anyExecutor { commandSender, args ->
                        val size = args[0] as Int
                        data.size = size
                        commandSender.sendMessage(prefix + msg("command.backpack.setSize", listOf(size.toString())))
                    }
                }
            }
            literalArgument("reset") {
                anyExecutor { commandSender, _ ->
                    data.backpacks.clear()
                    commandSender.sendMessage(prefix + msg("command.backpack.reset"))
                }
            }
        }
    }

    private fun openInventory(player: Player, target: String) {
        player.playSound(player, Sound.BLOCK_ENDER_CHEST_OPEN, .6f, 1f)
        val id = if (data.global) "Global" else target
        InventoryManager.get("BACKPACK-$id")?.open(player) ?: InventoryManager.inventoryBuilder("BACKPACK-$id") {
            title = cmp("BP - $id", cHighlight)
            size = data.size.plus(1).coerceIn(1..6)
            this.player = player
            val items = data.backpacks.getOrPut(id) { arrayOfNulls(5*9) }
            itemProvider = Items(items, data.global, id)
            val interact = Interact(items)
            clickAction = interact.run
            closeAction = interact.close
        }
    }

    fun saveFile() {
        file.writeText(jsonCompact.encodeToString(data))
    }

    private class Items(private val items: Array<ItemStack?>, private val global: Boolean, private val owner: String): ItemProvider {
        override fun getSlotMap(): Map<Int, ItemStack> {
            return buildMap {
                val ph = InventoryUtils.phPrimary
                repeat(9) { put(it, ph) }
                val header = itemStack(Material.PLAYER_HEAD) {
                    if (global) {
                        meta {
                            name = cmp("Global Backpack", cHighlight, true)
                        }
                        itemMeta = (itemMeta as SkullMeta).skullTexture(CustomHeads.GLOBE.value)
                    } else {
                        meta<SkullMeta> {
                            name = cmp("${this@Items.owner}'s Backpack", cHighlight, true)
                            owningPlayer = Bukkit.getOfflinePlayer(this@Items.owner)
                        }
                    }
                }
                put(4, header)

                val empty = ItemStack(Material.AIR)
                items.forEachIndexed { index, itemStack ->  put(index + 9, itemStack ?: empty) }
                (9..9*6).forEach { putIfAbsent(it, empty) }
            }
        }
    }

    private class Interact(items: Array<ItemStack?>): GUIEvent {
        override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, _: CustomInventory ->
            if (it.clickedInventory == it.view.topInventory) it.isCancelled = it.slot in 0..8
        }

        override val close: ((InventoryCloseEvent, CustomInventory) -> Unit) = event@{ it: InventoryCloseEvent, _: CustomInventory ->
            it.view.topInventory.contents.forEachIndexed { index, itemStack ->
                if (index in 0..8) return@forEachIndexed
                items[index - 9] = itemStack
            }
        }
    }

    @Serializable
    data class Data(
        var global: Boolean,
        var size: Int,
        val backpacks: MutableMap<String, Array<@Serializable(with = ItemStackSerializer::class) ItemStack?>>
    )
}