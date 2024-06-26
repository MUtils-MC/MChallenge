package de.miraculixx.mchallenge.modules.challenges

import de.miraculixx.challenge.api.settings.ChallengeSetting
import de.miraculixx.kpaper.gui.items.skullTexture
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.mcommons.extensions.enumOf
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

fun Challenges.getIcon(): ItemStack {
    return itemStack(enumOf<Material>(icon.material) ?: Material.BARRIER) {
        icon.texture?.let { itemMeta = (itemMeta as? SkullMeta)?.skullTexture(it) }
    }
}

fun ChallengeSetting<*>.getMaterial() = enumOf(materialKey) ?: Material.BARRIER