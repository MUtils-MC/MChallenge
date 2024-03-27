package de.miraculixx.mchallenge.gui.items

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mchallenge.MChallenge
import de.miraculixx.mchallenge.utils.config.ConfigManager
import de.miraculixx.mcore.gui.items.ItemProvider
import de.miraculixx.mcore.gui.items.skullTexture
import de.miraculixx.mvanilla.gui.Head64
import de.miraculixx.mvanilla.messages.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class ItemsSettings : ItemProvider {

    override fun getSlotMap(): Map<Int, ItemStack> {
        val settings = ConfigManager.settings
        return mapOf(
            11 to itemStack(Material.LOOM) {
                meta {
                    name = cmp(msgString("items.settings.theme.n"), cHighlight)
                    lore(
                        msgList("items.settings.theme.l") + listOf(
                            emptyComponent(),
                            getLoreSettings(),
                            cmp("   ") + cmp(msgString("items.settings.language.s")) + cmp(settings.language, cHighlight),
                            emptyComponent(),
                            msgClick + cmp(msgString("common.switch"))
                        )
                    )
                    customModel = 1
                }
            },

            12 to itemStack(Material.BARREL) {
                meta {
                    name = cmp(msgString("items.settings.compact.n"), cHighlight)
                    lore(
                        msgList("items.settings.compact.l") + listOf(
                            emptyComponent(),
                            getLoreSettings(),
                            cmp("   ") + cmp("- Compact", if (settings.gui.compact) cMark else cBaseTag),
                            cmp("   ") + cmp("- Detailed", if (!settings.gui.compact) cMark else cBaseTag),
                            emptyComponent(),
                            msgClick + cmp(msgString("common.switch"))
                        )
                    )
                    customModel = 2
                }
            },

            15 to itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    skullTexture(Head64.GLOBE.value)
                    name = cmp(msgString("items.settings.language.n"), cHighlight)
                    lore(
                        msgList("items.settings.language.l") + listOf(
                            emptyComponent(),
                            getLoreSettings(),
                            cmp("   ") + cmp(msgString("items.settings.language.s")) + cmp(settings.language, cHighlight),
                            emptyComponent(),
                            msgClick + cmp(msgString("common.switch"))
                        )
                    )
                    customModel = 3
                }
            }
        )
    }

    private fun getLoreSettings() = cmp("∙ ") + cmp(msgString("common.settings"), cHighlight, underlined = true)
}