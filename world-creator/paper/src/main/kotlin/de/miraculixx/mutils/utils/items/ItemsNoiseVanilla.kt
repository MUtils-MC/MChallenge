package de.miraculixx.mutils.utils.items

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.api.data.GeneratorDefaults
import de.miraculixx.mutils.enums.gui.Head64
import de.miraculixx.mutils.extensions.msg
import de.miraculixx.mutils.gui.items.ItemProvider
import de.miraculixx.mutils.gui.items.skullTexture
import de.miraculixx.mutils.messages.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class ItemsNoiseVanilla(private val defaults: GeneratorDefaults) : ItemProvider {
    private val settingLore = listOf(emptyComponent(), cmp("• ") + cmp("Settings", cHighlight, underlined = true))

    override fun getSlotMap(): Map<ItemStack, Int> {
        val s1 = msgString("items.creator.noiseAlgo.s1")
        val s2 = msgString("items.creator.noiseAlgo.s2")
        val s3 = msgString("items.creator.noiseAlgo.s3")
        val s4 = msgString("items.creator.noiseAlgo.s4")
        val s5 = msgString("items.creator.noiseAlgo.s5")
        val s6 = msgString("items.creator.noiseAlgo.s6")

        return mapOf(
            itemStack(Material.PLAYER_HEAD) {
                meta {
                    name = cmp(msgString("items.creator.noiseAlgo.n"), cHighlight)
                    lore(
                        msgList("items.creator.noiseAlgo.l") + settingLore + listOf(
                            cmp("   ${s1}: ") + cmp(defaults.vanillaNoise.msg(), cHighlight),
                            cmp("   ${s2}: ") + cmp(defaults.vanillaCaves.msg(), cHighlight),
                            cmp("   ${s3}: ") + cmp(defaults.vanillaSurface.msg(), cHighlight),
                            cmp("   ${s4}: ") + cmp(defaults.vanillaFoliage.msg(), cHighlight),
                            cmp("   ${s5}: ") + cmp(defaults.vanillaMobs.msg(), cHighlight),
                            cmp("   ${s6}: ") + cmp(defaults.vanillaMobs.msg(), cHighlight)
                        )
                    )
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.GLOBE.value)
            } to 20,
            itemStack(Material.PLAYER_HEAD) {
                meta { name = emptyComponent() }
                itemMeta = (itemMeta as SkullMeta).skullTexture(Head64.ARROW_RIGHT_WHITE.value)
            } to 22,

            itemStack(Material.OBSERVER) {
                meta {
                    name = cmp(s1, cHighlight)
                    customModel = 1
                    lore(settingLore + listOf(cmp("   Active: ") + cmp(defaults.vanillaNoise.msg()), emptyComponent(), msgClick + cmp("Toggle")))
                }
            } to 15,
            itemStack(Material.MOSSY_COBBLESTONE) {
                meta {
                    name = cmp(s2, cHighlight)
                    customModel = 2
                    lore(settingLore + listOf(cmp("   Active: ") + cmp(defaults.vanillaCaves.msg()), emptyComponent(), msgClick + cmp("Toggle")))
                }
            } to 24,
            itemStack(Material.GRASS_BLOCK) {
                meta {
                    name = cmp(s3, cHighlight)
                    customModel = 3
                    lore(settingLore + listOf(cmp("   Active: ") + cmp(defaults.vanillaSurface.msg()), emptyComponent(), msgClick + cmp("Toggle")))
                }
            } to 33,
            itemStack(Material.GRASS) {
                meta {
                    name = cmp(s4, cHighlight)
                    customModel = 4
                    lore(settingLore + listOf(cmp("   Active: ") + cmp(defaults.vanillaFoliage.msg()), emptyComponent(), msgClick + cmp("Toggle")))
                }
            } to 16,
            itemStack(Material.TURTLE_EGG) {
                meta {
                    name = cmp(s5, cHighlight)
                    customModel = 5
                    lore(settingLore + listOf(cmp("   Active: ") + cmp(defaults.vanillaMobs.msg()), emptyComponent(), msgClick + cmp("Toggle")))
                }
            } to 25,
            itemStack(Material.SCAFFOLDING) {
                meta {
                    name = cmp(s6, cHighlight)
                    customModel = 6
                    lore(settingLore + listOf(cmp("   Active: ") + cmp(defaults.vanillaCaves.msg()), emptyComponent(), msgClick + cmp("Toggle")))
                }
            } to 34
        )
    }
}