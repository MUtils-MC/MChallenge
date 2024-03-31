package de.miraculixx.mchallenge.modules.mods.multiplayer.limitedSkills

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mcore.gui.items.ItemProvider
import de.miraculixx.mcore.gui.items.skullTexture
import de.miraculixx.mvanilla.gui.Head64
import de.miraculixx.mvanilla.messages.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class LimitSkillsItems(private val challengeInstance: LimitedSkills): ItemProvider {
    private val msgSeeName = msgString("items.event.LIMITED_SEE.n")
    private val msgDamageName = msgString("items.event.LIMITED_DAMAGE.n")
    private val msgSeeLore = msgList("items.event.LIMITED_SEE.l")
    private val msgDamageLore = msgList("items.event.LIMITED_DAMAGE.l")
    private val msgClickLore = msgClick + cmp("Choose")
    private val confirmButton = itemStack(Material.PLAYER_HEAD) {
        meta {
            name = cmp(msgString("common.confirm"), cHighlight)
            customModel = 3
        }
        itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.CHECKMARK_GREEN.value)
    }

    override fun getItemList(from: Int, to: Int): List<ItemStack> {
        val selection = challengeInstance.selection
        return listOf(
            itemStack(Material.ENDER_EYE) {
                meta {
                    name = cmp(msgSeeName, cHighlight)
                    customModel = 1
                    lore(msgSeeLore + buildList {
                        add(emptyComponent())
                        selection.forEach { (uuid, state) ->
                            val player = Bukkit.getPlayer(uuid)
                            if (state) add(cmp("- ${player?.name ?: "Unknown"}"))
                        }
                        add(emptyComponent())
                        add(msgClickLore)
                    })
                }
            },
            itemStack(Material.DIAMOND_SWORD) {
                meta {
                    name = cmp(msgDamageName, cHighlight)
                    customModel = 2
                    lore(msgDamageLore + buildList {
                        add(emptyComponent())
                        selection.forEach { (uuid, state) ->
                            val player = Bukkit.getPlayer(uuid)
                            if (!state) add(cmp("- ${player?.name ?: "Unknown"}"))
                        }
                        add(emptyComponent())
                        add(msgClickLore)
                    })
                    addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                }
            }
        )
    }

    override fun getExtra(): List<ItemStack> {
        return listOf(confirmButton)
    }
}