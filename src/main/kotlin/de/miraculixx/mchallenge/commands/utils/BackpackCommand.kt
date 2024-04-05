package de.miraculixx.mchallenge.commands.utils

import de.miraculixx.kpaper.extensions.bukkit.language
import de.miraculixx.kpaper.extensions.bukkit.msg
import de.miraculixx.kpaper.gui.GUIEvent
import de.miraculixx.kpaper.gui.InventoryUtils
import de.miraculixx.kpaper.gui.data.CustomInventory
import de.miraculixx.kpaper.gui.data.InventoryManager
import de.miraculixx.kpaper.gui.items.ItemProvider
import de.miraculixx.kpaper.gui.items.skullTexture
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.kpaper.serialization.ItemStackSerializer
import de.miraculixx.mchallenge.utils.config.Configurable
import de.miraculixx.mchallenge.utils.config.loadConfig
import de.miraculixx.mchallenge.utils.config.saveConfig
import de.miraculixx.mcommons.extensions.msg
import de.miraculixx.mcommons.statics.KHeads
import de.miraculixx.mcommons.text.*
import dev.jorel.commandapi.arguments.LiteralArgument
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.kotlindsl.*
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.io.File

class BackpackCommand : Configurable {
    private val file = File("${de.miraculixx.mchallenge.MChallenge.configFolder.path}/data/backpack.json")
    private val data = file.loadConfig(Data())

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
                    anyExecutor { sender, args ->
                        val isGlobal = args[0] as Boolean
                        data.global = isGlobal
                        sender.sendMessage(prefix + sender.msg("command.backpack.setGlobal", listOf(isGlobal.msg(sender.language()))))
                    }
                }
            }
            literalArgument("size") {
                integerArgument("size", 1, 5) {
                    anyExecutor { sender, args ->
                        val size = args[0] as Int
                        data.size = size
                        sender.sendMessage(prefix + sender.msg("command.backpack.setSize", listOf(size.toString())))
                    }
                }
            }
            literalArgument("reset") {
                anyExecutor { sender, _ ->
                    reset()
                    sender.sendMessage(prefix + sender.msg("command.backpack.reset"))
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
            val items = data.backpacks.getOrPut(id) { arrayOfNulls(5 * 9) }
            itemProvider = Items(items, data.global, id)
            val interact = Interact(items)
            clickAction = interact.run
            closeAction = interact.close
        }
    }

    override fun save() {
        file.saveConfig(data)
    }

    override fun reset() {
        data.backpacks.clear()
        save()
    }

    private class Items(private val items: Array<ItemStack?>, private val global: Boolean, private val owner: String) : ItemProvider {
        override fun getSlotMap(): Map<Int, ItemStack> {
            return buildMap {
                val ph = InventoryUtils.phPrimary
                repeat(9) { put(it, ph) }
                val header = itemStack(Material.PLAYER_HEAD) {
                    if (global) {
                        meta<SkullMeta> {
                            name = cmp("Global Backpack", cHighlight, true)
                            skullTexture(KHeads.GLOBE)
                        }
                    } else {
                        meta<SkullMeta> {
                            name = cmp("${this@Items.owner}'s Backpack", cHighlight, true)
                            owningPlayer = Bukkit.getOfflinePlayer(this@Items.owner)
                        }
                    }
                }
                put(4, header)

                val empty = ItemStack(Material.AIR)
                items.forEachIndexed { index, itemStack -> put(index + 9, itemStack ?: empty) }
                (9..9 * 6).forEach { putIfAbsent(it, empty) }
            }
        }
    }

    private class Interact(items: Array<ItemStack?>) : GUIEvent {
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
        var global: Boolean = true,
        var size: Int = 2,
        val backpacks: MutableMap<String, Array<@Serializable(with = ItemStackSerializer::class) ItemStack?>> = mutableMapOf()
    )
}