package de.miraculixx.mutils.utils.tools.gui.items

import de.miraculixx.mutils.modules.creator.data.CustomChallengeData
import de.miraculixx.mutils.utils.getComponentList
import de.miraculixx.mutils.utils.text.cHighlight
import de.miraculixx.mutils.utils.text.cmp
import de.miraculixx.mutils.utils.text.emptyComponent
import de.miraculixx.mutils.utils.text.plus
import de.miraculixx.mutils.utils.tools.gui.InvUtils
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class CreatorItems {
    /**
     * Item Library for Challenge Creator
     * - 1 -> Modify - main menu
     * - 2 -> Modify - Event Group Menu
     */
    fun getItems(id: Int, chData: CustomChallengeData): Map<ItemStack, Int> {
        return when (id) {
            1 -> g1(chData)

            else -> emptyMap()
        }
    }

    // <active-events> <-> <add-new-event>
    // 101-199
    private fun g2(chData: FileConfiguration): Material<ItemStack, Int> {
        val l = listOf(emptyComponent(), cmp("∙ ") + cmp("Info", cHighlight, underlined = true))
        return mapOf(
            buildItem(Material.CHEST, 101, cmp("Saved Events", cHighlight, bold = true), 
                buildList {
                    addAll(l)
                    addAll(getComponentList("item.CreatorCreate.savedEvents"))
                }
            )
        )
    }

    // 1-99
    private fun g1(chData: CustomChallengeData): Map<ItemStack, Int> {
        val l = listOf(emptyComponent(), cmp("∙ ") + cmp("Info", cHighlight, underlined = true))
        return mapOf(
            buildItem(Material.NAME_TAG, 1, cmp("Challenge Name", cHighlight, bold = true),
                buildList {
                    addAll(l)
                    addAll(getComponentList("item.CreatorCreate.name"))
                    add(emptyComponent())
                    add(cmp("Click ", cHighlight) + cmp("≫ Change Name"))
                }
            ) to 10,
            buildItem(Material.WRITABLE_BOOK, 2, cmp("Challenge Description", cHighlight, bold = true),
                buildList {
                    addAll(l)
                    addAll(getComponentList("item.CreatorCreate.description"))
                    add(emptyComponent())
                    add(cmp("Click ", cHighlight) + cmp("≫ Change Description"))
                }
            ) to 11,
            buildItem(Material.getMaterial(chData.data.icon) ?: Material.ITEM_FRAME, 3, cmp("Display Icon", cHighlight, bold = true),
                buildList {
                    addAll(l)
                    addAll(getComponentList("item.CreatorCreate.icon"))
                    add(emptyComponent())
                    add(cmp("Click ", cHighlight) + cmp("≫ Change Icon"))
                }
            ) to 15,
            buildItem(Material.TARGET, 4, cmp("Events", cHighlight, bold = true),
                buildList {
                    addAll(l)
                    addAll(getComponentList("item.CreatorCreate.events"))
                    add(emptyComponent())
                    add(cmp("Click ", cHighlight) + cmp("≫ Open Menu"))
                }
            ) to 16,

            chData.item.editMeta(modelData = 0) to 13,
            buildItem(InvUtils.secondaryPlaceholder.type, 200, cmp("${chData.uuid}", TextColor.color(0x0d0d0d)), emptyList()) to 0
        )
    }
}