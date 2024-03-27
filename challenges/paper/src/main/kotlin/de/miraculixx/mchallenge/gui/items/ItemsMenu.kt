package de.miraculixx.mchallenge.gui.items

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mchallenge.MChallenge
import de.miraculixx.mcore.gui.items.ItemProvider
import de.miraculixx.mcore.gui.items.skullTexture
import de.miraculixx.mvanilla.gui.Head64
import de.miraculixx.mvanilla.messages.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class ItemsMenu : ItemProvider {
    private val msgOpenMenu = msgClick + cmp(msgString("common.openMenu"))
    private val phWhite = itemStack(Material.WHITE_STAINED_GLASS_PANE) { meta { name = emptyComponent() } }
    private val phBlue = itemStack(Material.CYAN_STAINED_GLASS_PANE) { meta { name = emptyComponent() } }

    override fun getSlotMap(): Map<Int, ItemStack> {
        return mapOf(
            11 to itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    name = cmp(msgString("items.menu.premium.n"), cHighlight)
                    lore(buildList {
                        addAll(msgList("items.menu.premium.l", inline = "<grey>"))
                        add(emptyComponent())
                        if (!MChallenge.bridgeAPI.getAccountStatus()) {
                            add(cmp("• Currently not logged in", cError, true))
                            add(emptyComponent())
                            add(msgClickLeft + cmp(msgString("common.openMenu")))
                            add(msgClickRight + cmp("Login"))
                        } else {
                            add(cmp("• Premium Active", cSuccess))
                            add(emptyComponent())
                            add(msgOpenMenu)
                        }
                    })
                    skullTexture(Head64.ENDER_CHEST.value)
                    customModel = 1
                }
            },

            13 to itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    name = cmp(msgString("items.menu.normal.n"), cHighlight)
                    lore(msgList("items.menu.normal.l", inline = "<grey>") + listOf(emptyComponent(), msgOpenMenu))
                    skullTexture(Head64.CHEST.value)
                    customModel = 2
                }
            },

            15 to itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    name = cmp(msgString("items.menu.addons.n"), cHighlight)
                    lore(buildList {
                        addAll(msgList("items.menu.addons.l", inline = "<grey>"))
                        add(emptyComponent())
                        add(msgClickLeft + cmp(msgString("common.openMenu")))
                        add(msgClickRight + cmp(msgString("items.menu.addons.b")))
                    })
                    lore(msgList("items.menu.addons.l", inline = "<grey>") + listOf(emptyComponent(), msgOpenMenu))
                    skullTexture(Head64.CRYSTAL_CHEST.value)
                    customModel = 3
                }
            },

            21 to itemStack(Material.ENDER_EYE) {
                meta {
                    name = cmp(msgString("items.menu.favorite.n"), cHighlight)
                    lore(msgList("items.menu.favorite.l", inline = "<grey>") + listOf(emptyComponent(), msgOpenMenu))
                    customModel = 4
                }
            },

            23 to itemStack(Material.ENDER_PEARL) {
                meta {
                    name = cmp(msgString("items.menu.last.n"), cHighlight)
                    lore(msgList("items.menu.last.l", inline = "<grey>") + listOf(emptyComponent(), msgOpenMenu))
                    customModel = 5
                }
            },

            35 to itemStack(Material.HOPPER) {
                meta {
                    name = cmp(msgString("items.menu.settings.n"), cHighlight)
                    customModel = 6
                }
            },

            // placeholder
            12 to phWhite,
            14 to phWhite,
            22 to phBlue,
        )
    }
}