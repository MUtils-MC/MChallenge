package de.miraculixx.mutils.utils.gui.items

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.enums.modules.timer.TimerSettings
import de.miraculixx.mutils.modules.ModuleManager
import de.miraculixx.mutils.utils.text.ColorTools
import de.miraculixx.mutils.utils.text.addLines
import de.miraculixx.mutils.utils.text.getMessageList
import de.miraculixx.mutils.utils.text.msg
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemsTimer {
    fun getItems(id: Int, time: String? = null, title: String? = null): Map<ItemStack,Int> {
        val list = when (id) {
            1 -> g1(time)
            2 -> g2(title)
            else -> {
                mapOf(Pair(ItemStack(Material.BARRIER),1))
            }
        }
        /* ID Glossary
        1 -> Timer Menu
        2 -> Timer Design
         */
        return list
    }

    private fun g2(t: String?): Map<ItemStack,Int> {
        val tool = ColorTools()
        val c1 = ModuleManager.timerSettings(TimerSettings.COLOR_PRIMARY) as Char
        val c2 = ModuleManager.timerSettings(TimerSettings.COLOR_SECONDARY) as Char
        val l1 = listOf(" ","§7∙ §9§nSettings",
            "   §7Primär ≫ §9${tool.codeToColor(c1)}",
            "   §7Sekundär ≫ §9${ColorTools().codeToColor(c2)}",
            "   §7Primär Format ≫ §9${ColorTools().codeToColor(ModuleManager.timerSettings(TimerSettings.STYLE_PRIMARY) as Char)}",
            "   §7Sekundär Format ≫ §9${ColorTools().codeToColor(ModuleManager.timerSettings(TimerSettings.STYLE_SECONDARY) as Char)}",)
        val l2 = listOf(" ", "§7∙ §9§nCurrent Design",
            "   §7$t")
        val i1 = itemStack(Material.NAME_TAG) { meta {
            customModel = 1
            name = "§9Style"
            lore = ArrayList<String>().addLines(getMessageList("item.Timer.Style.l")).addLines(" ",
                "§7∙ §9§nStyles Example",
                "   §7COMPACT ≫ §91 Tag 09:05:12",
                "   §7BRACKETS ≫ §9[1 09:05:12]",
                "   §7Prefix ≫ §9Challenge: 1 09:05:12",
                "   §7EXACT ≫ §91d 9h 5m 12s").addLines(l2)
        }}
        val i2 = itemStack(tool.codeToMaterial(c1)) { meta {
            customModel = 2
            name = "§9Primary Color"
            lore = ArrayList<String>().addLines(getMessageList("item.Timer.Color1.l"),l1,l2)
        }}
        val i3 = itemStack(tool.codeToMaterial(c2)) { meta {
            customModel = 3
            name = "§9Secondary Color"
            lore = ArrayList<String>().addLines(getMessageList("item.Timer.Color2.l"),l1,l2)
        }}
        val i4 = itemStack(Material.ANVIL) { meta {
            customModel = 4
            name = "§9Primary Font"
            lore = ArrayList<String>().addLines(getMessageList("item.Timer.Font1.l"),l1,l2)
        }}
        val i5 = itemStack(Material.DAMAGED_ANVIL) { meta {
            customModel = 5
            name = "§9Secondary Font"
            lore = ArrayList<String>().addLines(getMessageList("item.Timer.Font2.l"),l1,l2)
        }}
        return mapOf(Pair(i1,11), Pair(i2,13), Pair(i3,14), Pair(i4,15), Pair(i5,16))
    }

    private fun g1(t: String?): Map<ItemStack,Int> {
        val l = listOf(" ",
            "§7∙ §9§nSettings",
            "   §7Time ≫ §9$t",
            "   §7Active ≫ §9${ModuleManager.isActive(Modules.TIMER)} §8(Default true)",
            "   §7Count Up ≫ §9${ModuleManager.timerSettings(TimerSettings.COUNT_UP)} §8(Default true)",
            " ")
        val i1 = itemStack(Material.GOLD_NUGGET) { meta {
            customModel = 1
            name = "§6§l${msg("modules.timer.seconds", pre = false)}"
            lore = timeLore("s",t)
        }}
        val i2 = itemStack(Material.SUNFLOWER) { meta {
            customModel = 2
            name = "§6§l${msg("modules.timer.minutes", pre = false)}"
            lore = timeLore("min",t)
        }}
        val i3 = itemStack(Material.GOLD_INGOT) { meta {
            customModel = 3
            name = "§6§l${msg("modules.timer.hours", pre = false)}"
            lore = timeLore("h",t)
        }}
        val i4 = itemStack(Material.GOLD_BLOCK) { meta {
            customModel = 4
            name = "§6§l${msg("modules.timer.days", pre = false).replace("/","")}"
            lore = timeLore("d",t)
        }}
        val i5 = itemStack(Material.CLOCK) { meta {
            customModel = 5
            name = "§9Toggle Timer"
            lore = ArrayList<String>().addLines(getMessageList("item.Timer.Toggle.l"), l)
                .addLines("§9Click§7 ≫ Toggle")
        }}
        val i5b = ModuleManager.isActive(Modules.TIMER)
        val i5c = if (i5b) Material.LIME_STAINED_GLASS_PANE
                    else Material.RED_STAINED_GLASS_PANE
        val i5a = itemStack(i5c) { meta {
            customModel = 5
            name = if (i5b) "§aActivated" else "§cDeactivated"
        }}
        val i6 = itemStack(Material.MAGENTA_GLAZED_TERRACOTTA) { meta {
            customModel = 6
            name = "§9Direction"
            lore = ArrayList<String>().addLines(getMessageList("item.Timer.Direction.l"), l)
                .addLines("§9Click§7 ≫ Toggle")
        }}
        val i6b = ModuleManager.timerSettings(TimerSettings.COUNT_UP) as Boolean
        val i6c = if (i6b) Material.LIME_STAINED_GLASS_PANE
        else Material.RED_STAINED_GLASS_PANE
        val i6a = itemStack(i6c) { meta {
            customModel = 6
            name = if (i6b) "§aCount Up" else "§cCount Down"
        }}
        val i7 = itemStack(Material.SHEARS) { meta {
            customModel = 7
            name = "§9Rules"
            lore = ArrayList<String>().addLines(getMessageList("item.Timer.Rules.l"), l)
                .addLines("§9Click§7 ≫ Open Menu")
        }}
        val i8 = itemStack(Material.NAME_TAG) { meta {
            customModel = 8
            name = "§9Design"
            lore = ArrayList<String>().addLines(getMessageList("item.Timer.Design.l"), l)
                .addLines("§9Click§7 ≫ Open Menu")
        }}
        val i9 = itemStack(Material.DRAGON_EGG) { meta {
            customModel = 9
            name = "§9Goals"
            lore = ArrayList<String>().addLines(getMessageList("item.Timer.Goals.l"), l)
                .addLines("§9Click§7 ≫ Open Menu")
        }}
        return mapOf(Pair(i1,10), Pair(i2,11), Pair(i3,19), Pair(i4,20), Pair(i5,16),
            Pair(i5a,25), Pair(i6,15), Pair(i6a,24), Pair(i7,14), Pair(i9,23), Pair(i8,13))
    }

    private fun timeLore(s: String, t: String?): List<String> {
        return listOf("§7>> §9$t", " ",
            "§71$s • Left Click",
            "§710$s • Shift Left Click",
            "§8§m                       ",
            "§7-1$s • Right Click",
            "§7-10$s • Shift Right Click")
    }
}