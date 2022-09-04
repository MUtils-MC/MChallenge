@file:Suppress("DEPRECATION")

package de.miraculixx.mutils.modules.worldManager

import de.miraculixx.mutils.enums.modules.worldCreator.BiomeProviders
import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.modules.worldManager.biomeProvider.RandomBiomes
import de.miraculixx.mutils.modules.worldManager.biomeProvider.SingleBiomes
import de.miraculixx.mutils.modules.worldManager.biomeProvider.SwitchBiomes
import de.miraculixx.mutils.utils.await.AwaitChatMessage
import de.miraculixx.mutils.utils.gui.GUIBuilder
import de.miraculixx.mutils.utils.gui.InvUtils
import de.miraculixx.mutils.utils.gui.items.skullTexture
import de.miraculixx.mutils.utils.plainSerializer
import de.miraculixx.mutils.utils.text.addLines
import de.miraculixx.mutils.utils.text.getMessageList
import de.miraculixx.mutils.utils.text.msg
import de.miraculixx.mutils.utils.tools.click
import de.miraculixx.mutils.utils.tools.soundError
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.bukkit.title
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material
import org.bukkit.World.Environment
import org.bukkit.block.Biome
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class WorldCreator(private val player: Player) {
    // World Data
    private var wName: String? = null
    private var wSeed: Long? = null
    private var wGen = BiomeProviders.VANILLA
    private var wEnv = Environment.NORMAL
    private var wBiom: Biome? = null

    private val gui = GUIBuilder(player, GUI.CUSTOM).custom(true, mapOf(), 3, "§9World Creator").get()!!

    private val onInvClose = listen<InventoryCloseEvent> {
        if (player != it.player) return@listen
        if (it.reason == InventoryCloseEvent.Reason.PLUGIN) return@listen
        player.sendMessage(msg("modules.worldManager.cancel"))
        unregister()
    }

    private val onClick = listen<InventoryClickEvent> {
        if (it.whoClicked != player) return@listen
        if (it.view.title != "§9World Creator") return@listen
        val item = it.currentItem
        it.isCancelled = true

        when (item?.itemMeta?.customModelData) {
            101 -> AwaitChatMessage(true, player, "DUMMY_WORLD_NAME", 60, { message ->
                wName = plainSerializer.serialize(message)
                wBiom = null
                update()
            }, {
                player.openInventory(gui)
            })
            102 -> AwaitChatMessage(true, player, "DUMMY_WORLD_SEED",60, { message ->
                val input = plainSerializer.serialize(message).toLong(10)
                wSeed = input
                update()
            }, {
                player.openInventory(gui)
            })
            103 -> {
                wEnv = InvUtils.enumRotate(Environment.values().filter { e -> e != Environment.CUSTOM }.toTypedArray(), wEnv)
                player.click()
            }
            104 -> {
                wGen = InvUtils.enumRotate(BiomeProviders.values(), wGen)
                player.click()
            }
            105 -> {
                wBiom = InvUtils.enumRotate(Biome.values().filter { b -> b != Biome.CUSTOM }.toTypedArray(), wBiom ?: Biome.PLAINS)
                wName = wBiom?.name
                player.click()
            }
            2 -> player.soundError()
            1 -> {
                finish()
                return@listen
            }
        }
        update()
    }

    private fun unregister() {
        onClick.unregister()
        onInvClose.unregister()
    }
    private fun finish() {
        player.closeInventory()
        player.title("§6World Creator", "§eWorld $wName is being created...", 0, 9999, 0)
        unregister()

        val creator = org.bukkit.WorldCreator(wName!!)
        val provider = when (wGen) {
            BiomeProviders.SINGLE_BIOMES -> SingleBiomes()
            BiomeProviders.RANDOM_BIOMES -> RandomBiomes()
            BiomeProviders.BIOME_SWITCH -> SwitchBiomes()
            BiomeProviders.VANILLA -> null
        }
        creator.biomeProvider(provider)
        creator.environment(wEnv)
        if (wSeed != null) creator.seed(wSeed!!)
        val world = creator.createWorld()
        player.title(" ", " ", 0, 0, 0)
        if (world == null) {
            player.sendMessage("modules.worldManager.worldNotExist")
            return
        }

        player.sendMessage(
            "§9§m        §9[ §fWorld Info §9]§9§m        \n" +
                    "§7Name ≫ §9${world.name}\n" +
                    "§7Environment ≫ §9${world.environment.name}\n" +
                    "§7Current Biom ≫ §9${world.getBiome(player.location)}"
        )
        GUIBuilder(player, GUI.WORLD_OVERVIEW).storage(null).open()
    }

    private fun update() {
        gui.setItem(11, getItemName())
        gui.setItem(12, getItemSeed())
        gui.setItem(14, getItemEnv())
        gui.setItem(15, getItemGen())
        if (wGen == BiomeProviders.SINGLE_BIOMES)
            gui.setItem(16, getItemBiom())
        else gui.setItem(16, placeholder)

        val valid = if (wGen == BiomeProviders.SINGLE_BIOMES) wBiom != null else true
        if (wName != null && valid)
            gui.setItem(22, getItemConfirm())
        else gui.setItem(22, getItemMissing())
    }

    // Inventory Items
    private fun getItemConfirm(): ItemStack {
        return itemStack(Material.PLAYER_HEAD) {
            meta<SkullMeta> {
                customModel = 1
                name = "§a§lConfirm"
                lore = listOf(
                    "§7Confirm your current settings", "§7and create a new World", " ",
                    "§9World Name: §7$wName",
                    "§9World Seed: §7${wSeed ?: "None"}",
                    "§9Environment: §7${wEnv.name}",
                    "§9Generator: §7${wGen.name}"
                )
                itemMeta = skullTexture(
                    this,
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0="
                )
            }
        }
    }

    private fun getItemMissing(): ItemStack {
        return itemStack(Material.PLAYER_HEAD) {
            meta<SkullMeta> {
                customModel = 2
                name = "§c§lMissing Values"
                lore = listOf("§7One or multiple important", "§7settings are missing!")
                itemMeta = skullTexture(
                    this,
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmZmZmE2MDJlMzY4MjE0ZGQ2MmNlY2Q2ODE1ZjE0OTI2ZWU1N2I5NDgxNDM0OTVlOTMxYTc3NjM2MzcyYmU1YSJ9fX0="
                )
            }
        }
    }

    private fun getItemName(): ItemStack {
        return itemStack(Material.NAME_TAG) {
            meta {
                customModel = 101
                name = "§9§lWorld Name"
                lore = ArrayList<String>().addLines(getMessageList("item.WorldCreator.Name.l"))
                    .addLines(
                        " ", "§7∙ §9§nSettings",
                        "   §7Name:§9 ${wName ?: "None"}", " ",
                        "§9Click§7 ≫ Change Value"
                    )
            }
        }
    }

    private fun getItemSeed(): ItemStack {
        return itemStack(Material.WHEAT_SEEDS) {
            meta {
                customModel = 102
                name = "§9§lWorld Seed"
                lore = ArrayList<String>().addLines(getMessageList("item.WorldCreator.Seed.l"))
                    .addLines(
                        " ", "§7∙ §9§nSettings",
                        "   §7Seed:§9 ${wSeed ?: "None"}", " ", "§9Click§7 ≫ Change Value"
                    )
            }
        }
    }

    private fun getItemEnv(): ItemStack {
        return itemStack(Material.PLAYER_HEAD) {
            meta {
                customModel = 103
                name = "§9§lWorld Environment"
                lore = ArrayList<String>().addLines(getMessageList("item.WorldCreator.Env.l"))
                    .addLines(
                        " ", "§7∙ §9§nSettings",
                        "   §7Environment:§9 $wEnv", " ", "§9Click§7 ≫ Change Value"
                    )
            }
            skullTexture(
                itemMeta as SkullMeta,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmUyY2M0MjAxNWU2Njc4ZjhmZDQ5Y2NjMDFmYmY3ODdmMWJhMmMzMmJjZjU1OWEwMTUzMzJmYzVkYjUwIn19fQ=="
            )
        }
    }

    private fun getItemGen(): ItemStack {
        return itemStack(Material.FILLED_MAP) {
            meta {
                customModel = 104
                name = "§9§lWorld Generator"
                addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                lore = ArrayList<String>().addLines(getMessageList("item.WorldCreator.Generator.l"))
                    .addLines(
                        " ", "§7∙ §9§nSettings",
                        "   §7Generator:§9 $wGen", " ", "§9Click§7 ≫ Change Value"
                    )
            }
        }
    }

    private fun getItemBiom(): ItemStack {
        return itemStack(Material.MAP) {
            meta {
                customModel = 105
                name = "§9§lSingle Biom Setting"
                lore = ArrayList<String>().addLines(getMessageList("item.WorldCreator.SingleBiom.l"))
                    .addLines(
                        " ", "§7∙ §9§nSettings",
                        "   §7Biom:§9 ${wBiom ?: "None"}", " ", "§9Click§7 ≫ Change Value"
                    )
            }
        }
    }

    private var placeholder: ItemStack? = null

    init {
        placeholder = gui.getItem(16)
        update()
        player.openInventory(gui)
    }
}