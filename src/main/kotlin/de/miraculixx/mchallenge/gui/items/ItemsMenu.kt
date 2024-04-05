package de.miraculixx.mchallenge.gui.items

import de.miraculixx.kpaper.gui.items.ItemProvider
import de.miraculixx.kpaper.gui.items.skullTexture
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mchallenge.MChallenge
import de.miraculixx.mcommons.statics.KHeads
import de.miraculixx.mcommons.text.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

class ItemsMenu(private val locale: Locale) : ItemProvider {
    private val msgOpenMenu = locale.msgClick() + cmp(locale.msgString("common.openMenu"))
    private val phWhite = itemStack(Material.WHITE_STAINED_GLASS_PANE) { meta { name = emptyComponent() } }
    private val phBlue = itemStack(Material.CYAN_STAINED_GLASS_PANE) { meta { name = emptyComponent() } }

    override fun getSlotMap(): Map<Int, ItemStack> {
        return mapOf(
            11 to itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    name = cmp(locale.msgString("items.menu.premium.n"), cHighlight)
                    lore(buildList {
                        addAll(locale.msgList("items.menu.premium.l", inline = "<grey>"))
                        add(emptyComponent())
                        if (!MChallenge.bridgeAPI.getAccountStatus()) {
                            add(cmp("• Currently not logged in", cError, true))
                            add(emptyComponent())
                            add(locale.msgClickLeft() + cmp(locale.msgString("common.openMenu")))
                            add(locale.msgClickRight() + cmp("Login"))
                        } else {
                            add(cmp("• Premium Active", cSuccess))
                            add(emptyComponent())
                            add(msgOpenMenu)
                        }
                    })
                    skullTexture(KHeads.ENDER_CHEST)
                    customModel = 1
                }
            },

            13 to itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    name = cmp(locale.msgString("items.menu.normal.n"), cHighlight)
                    lore(locale.msgList("items.menu.normal.l", inline = "<grey>") + listOf(emptyComponent(), msgOpenMenu))
                    skullTexture(KHeads.CHEST)
                    customModel = 2
                }
            },

            15 to itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    name = cmp(locale.msgString("items.menu.addons.n"), cHighlight)
                    lore(buildList {
                        addAll(locale.msgList("items.menu.addons.l", inline = "<grey>"))
                        add(emptyComponent())
                        add(locale.msgClickLeft() + cmp(locale.msgString("common.openMenu")))
                        add(locale.msgClickRight() + cmp(locale.msgString("items.menu.addons.b")))
                    })
                    lore(locale.msgList("items.menu.addons.l", inline = "<grey>") + listOf(emptyComponent(), msgOpenMenu))
                    skullTexture(KHeads.CRYSTAL_CHEST)
                    customModel = 3
                }
            },

            21 to itemStack(Material.ENDER_EYE) {
                meta {
                    name = cmp(locale.msgString("items.menu.favorite.n"), cHighlight)
                    lore(locale.msgList("items.menu.favorite.l", inline = "<grey>") + listOf(emptyComponent(), msgOpenMenu))
                    customModel = 4
                }
            },

            23 to itemStack(Material.ENDER_PEARL) {
                meta {
                    name = cmp(locale.msgString("items.menu.last.n"), cHighlight)
                    lore(locale.msgList("items.menu.last.l", inline = "<grey>") + listOf(emptyComponent(), msgOpenMenu))
                    customModel = 5
                }
            },

            35 to itemStack(Material.HOPPER) {
                meta {
                    name = cmp(locale.msgString("items.menu.settings.n"), cHighlight)
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