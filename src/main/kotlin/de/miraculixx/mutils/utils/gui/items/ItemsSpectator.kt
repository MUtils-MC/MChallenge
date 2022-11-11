package de.miraculixx.mutils.utils.gui.items

import de.miraculixx.mutils.enums.settings.spectator.BlockBreak
import de.miraculixx.mutils.enums.settings.spectator.Hide
import de.miraculixx.mutils.enums.settings.spectator.ItemPickup
import de.miraculixx.mutils.enums.settings.spectator.SelfHide
import de.miraculixx.mutils.modules.spectator.SpecCollection
import de.miraculixx.mutils.modules.spectator.Spectator
import de.miraculixx.mutils.utils.text.addLines
import de.miraculixx.mutils.utils.text.getMessageList
import de.miraculixx.mutils.utils.text.msg
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class ItemsSpectator {

    fun getItems(id: Int, s: SpecCollection? = null, b: Boolean? = null): Map<ItemStack?, Int> {
        val list = when (id) {
            1 -> g1(s!!)
            2 -> g2()
            5 -> g5(b!!)
            else -> {
                mapOf<ItemStack?,Int>(Pair(ItemStack(Material.BARRIER),1))
            }
        }
        /* ID Glossary
        1 -> Spec Settings
        2 -> Troll Select
        3 -> Troll Sounds
        4 -> Troll Blocks
        5 -> Hotbar
         */
        return list
    }

    private fun g5(b: Boolean): Map<ItemStack?, Int> {
        //true - Default Hotbar | false - Quick Hotbar
        val list = if (b) listOf()
        else {
            val l = ArrayList<Player>()
            onlinePlayers.forEach {
                if (!Spectator.isSpectator(it.uniqueId)) l.add(it)
            }
            l
        }
        val i1 = itemStack(Material.COMPASS) { meta {
            customModel = if (b) 101 else 102
            name = "§9Teleport"
            lore = ArrayList<String>().addLines(" ",
                "§7∙ §9§nTeleport").addLines(getMessageList("item.Spec.Compass.l", "   ")).addLines(" ",
                "§9Click§7 ≫ Random Teleport",
                "§9Shift Click§7 ≫ Teleport Menu",
                "§9F§7 ≫ Quick Teleport Hotbar")
        }}
        val i2 = if (b) itemStack(Material.FIRE_CHARGE) { meta {
            customModel = 107
            name = "§9Settings"
            lore = ArrayList<String>().addLines(" ",
                "§7∙ §9§nSettings").addLines(getMessageList("item.Spec.Settings.l", "   ")).addLines(" ",
                "§9Click§7 ≫ Open Menu")
        }}
        else buildHead(list,0)
        val i3 = if (b) null else buildHead(list,1)
        val i4 = if (b) null else buildHead(list,2)
        val i5 = if (b) itemStack(Material.STICK) { meta {
            customModel = 104
            name = "§9Info Stick"
            lore = ArrayList<String>().addLines(" ",
                "§7∙ §9§nInfo Stick").addLines(getMessageList("item.Spec.InfoStick.l", "   ")).addLines(" ",
                "§9Hit Entity§7 ≫ Target Info")
        }}
        else buildHead(list, 3)
        val i6 = if (b) null else buildHead(list,4)
        val i7 = if (b) null else buildHead(list,5)
        val i9 = itemStack(Material.BELL) { meta {
            customModel = 106
            name = "§9Troll Menu"
            lore = ArrayList<String>().addLines(" ",
                "§7∙ §9§nTroll Menu").addLines(getMessageList("item.Spec.Troll.l", "   ")).addLines(" ",
                "§9Click§7 ≫ Open Menu")
        }}
        return mapOf(Pair(i1,0), Pair(i2,1), Pair(i3,2), Pair(i4,3), Pair(i5,4), Pair(i6,5), Pair(i7,6), Pair(i9,8))
    }

    private fun buildHead(l: List<Player>, id: Int): ItemStack? {
        val p = if (l.size <= id) return null
        else l[id]
        return itemStack(Material.PLAYER_HEAD) { meta<SkullMeta> {
            owningPlayer = p
            customModel = 201
            name = "§9${p.displayName}"
            lore = listOf(" ",
                "§7∙ §9§nPlayer Info",
                "   §7HP: §9${p.health}",
                "   §7FOOD: §9${p.foodLevel}",
                "   §7LVL: §9${p.level}",
                "   §7DIM: §9${p.world.environment.name.replace("_", " ")}",
                " ",
                "§9Click§7 ≫ Teleport",)
        }}
    }

    /*
        player.inventory.setItem(1, item)
        player.inventory.setItem(2, null)
        player.inventory.setItem(3, null)
        item = ItemBuilder(Material.STICK).setCustomModelData(1004).setName("§9Info Stick").setLore(
            " ",
            "§7∙ §9§nInfo Stick",
            "   §7Schlage hiermit einen Spieler,",
            "   §7um genau Informationen über",
            "   §7diesen zu bekommen!",
            " ",
            "§9Hit Player§7 ≫ Player Info"
        ).itemStack
        player.inventory.setItem(4, item)
        player.inventory.setItem(5, null)
        player.inventory.setItem(6, null)
        item = ItemBuilder(Material.BELL).setCustomModelData(1006).setName("§9Troll Menu").setLore(
            " ",
            "§7∙ §9§nTroll Menu",
            "   §7Spiele Fake Sounds ab",
            "   §7oder lasse Spielern Fake Blöcke",
            "   §7anzeigen!",
            " ",
            "§9Click§7 ≫ Open Menu"
        ).itemStack
        player.inventory.setItem(8, item)
     */

    private fun g2(): Map<ItemStack?, Int> {
        val i1 = itemStack(Material.LIGHT) { meta {
            customModel = 101
            name = "§9Fake Blocks"
            lore = ArrayList<String>().addLines(" ",
                "§7∙ §9§nFake Blocks").addLines(getMessageList("item.Spec.FakeB.l","   ")).addLines(" ",
                "§9Click §7≫ Open Selection")
        }}
        val i2 = itemStack(Material.BELL) { meta {
            customModel = 102
            name = "§9Fake Sounds"
            lore = ArrayList<String>().addLines(" ",
                "§7∙ §9§nFake Sounds").addLines(getMessageList("item.Spec.FakeS.l","   ")).addLines(" ",
                "§9Click §7≫ Open Selection")
        }}
        return mapOf(Pair(i1,12), Pair(i2,14))
    }

    private fun g1(s: SpecCollection): Map<ItemStack?, Int> {
        val i1 = itemStack(Material.ENDER_EYE) { meta {
            val on = s.hide == Hide.SHOWN
            val key = if (on) "on" else "off"
            customModel = if (on) 103 else 102
            name = if (on) "§aVisible §8(shown)" else "§cVisible §8(hidden)"
            lore = ArrayList<String>().addLines(" ",
                "§7 • §9§nHide").addLines(getMessageList("item.Spec.Hide.l", "   ")).addLines(" ",
                "§7 • §9§nInfo", "   "+ msg("item.Spec.Hide.$key", pre = false), " ",
                "§9Click §7≫ Toggle")
        }}
        val i2 = itemStack(Material.SPYGLASS) { meta {
            val on = s.selfHide == SelfHide.SHOWN
            val key = if (on) "on" else "off"
            customModel = if (on) 113 else 112
            name = if (on) "§aOther Specs Visible §8(shown)" else "§cOther Specs Invisible §8(hidden)"
            lore = ArrayList<String>().addLines(" ",
                "§7 • §9§nOther Specs Visible").addLines(getMessageList("item.Spec.SelfHide.l", "   ")).addLines(" ",
                "§7 • §9§nInfo", "   "+ msg("item.Spec.SelfHide.$key", pre = false), " ",
                "§9Click §7≫ Toggle")
        }}
        val i3 = itemStack(Material.HOPPER) { meta {
            val on = s.itemPickup == ItemPickup.ENABLED
            val key = if (on) "on" else "off"
            customModel = if (on) 108 else 109
            name = if (on) "§aCollect / Drop Items §8(enabled)" else "§cCollect / Drop Items §8(disabled)"
            lore = ArrayList<String>().addLines(" ",
                "§7 • §9§nInteract Items").addLines(getMessageList("item.Spec.Items.l", "   ")).addLines(" ",
                "§7 • §9§nInfo", "   "+ msg("item.Spec.Items.$key", pre = false), " ",
                "§9Click §7≫ Toggle")
        }}
        val i4 = itemStack(Material.DIAMOND_PICKAXE) { meta {
            val on = s.blockBreak == BlockBreak.ENABLED
            val key = if (on) "on" else "off"
            customModel = if (on) 110 else 111
            name = if (on) "§aBreak / Place Blocks §8(enabled)" else "§cBreak / Place Blocks §8(disabled)"
            lore = ArrayList<String>().addLines(" ",
                "§7 • §9§nInteract Blocks").addLines(getMessageList("item.Spec.Blocks.l", "   ")).addLines(" ",
                "§7 • §9§nInfo", "   "+ msg("item.Spec.Blocks.$key", pre = false), " ",
                "§9Click §7≫ Toggle")
        }}
        val i5 = itemStack(Material.FEATHER) { meta {
            val i = s.flySpeed
            customModel = 114
            name = "§9Fly Speed §8($i)"
            lore = ArrayList<String>().addLines(" ",
                "§7 • §9§nInteract Blocks").addLines(getMessageList("item.Spec.Blocks.l", "   ")).addLines(" ",
                "§7 • §9§nInfo", "   Speed: §9$i", " ",
                "§9Right Click §7≫ -1",
                "§9Left Click §7≫ +1")
        }}
        return mapOf(Pair(i1,10), Pair(i2,11), Pair(i5,13), Pair(i3,15), Pair(i4,16))
    }
}