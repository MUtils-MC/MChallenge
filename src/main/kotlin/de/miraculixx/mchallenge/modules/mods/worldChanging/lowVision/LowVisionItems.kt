package de.miraculixx.mchallenge.modules.mods.worldChanging.lowVision

import de.miraculixx.kpaper.gui.items.ItemProvider
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mcommons.text.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

class LowVisionItems(private val locale: Locale) : ItemProvider {
    val selected: MutableSet<Material> = mutableSetOf()
    var filter: String? = null

    private val allMaterials = Material.entries.filter { it.isBlock && !it.isAir }.toMutableSet()

    init {
        allMaterials.removeAll(setOf(Material.LAVA, Material.END_PORTAL, Material.NETHER_PORTAL, Material.END_GATEWAY))
    }

    override fun getBooleanMap(from: Int, to: Int): Map<ItemStack, Boolean> {
        val materials = allMaterials.filter { filter?.let { f -> it.name.contains(f, true) } ?: true }
        val finalMaterials = if (materials.isEmpty()) emptyList()
        else materials.subList(from, (to).coerceAtMost(materials.size - 1))

        return buildMap {
            if (finalMaterials.isEmpty()) {
                put(itemStack(Material.BARRIER) {
                    meta { name = cmp("No Blocks", cError) }
                }, false)
            }

            finalMaterials.forEach { material ->
                val isSelected = selected.contains(material)
                put(itemStack(getMaterial(material)) {
                    meta {
                        name = cmpTranslatableVanilla(material.translationKey(), cHighlight)
                        lore(listOf(emptyComponent(), locale.msgClick() + cmp("${if (isSelected) "Hide" else "Show"} Block")))
                        customModel = 1
                    }
                }, isSelected)
            }
        }
    }

    private fun getMaterial(material: Material): Material {
        return when (material) {
            Material.WATER -> Material.WATER_BUCKET
            Material.FIRE -> Material.FLINT_AND_STEEL
            Material.SOUL_FIRE -> Material.FIRE_CHARGE
            else -> material
        }
    }

    override fun getExtra(): List<ItemStack> {
        return listOf(
            itemStack(Material.OAK_SIGN) {
                meta {
                    name = cmp("Filter", cHighlight, true)
                    lore(listOf(emptyComponent(), locale.msgClick() + cmp("Edit Filter")))
                    customModel = 10
                }
            }
        )
    }
}