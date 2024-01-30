package de.miraculixx.mchallenge.modules.challenges

import de.miraculixx.challenge.api.settings.ChallengeSetting
import de.miraculixx.mchallenge.global.Challenges
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.mvanilla.extensions.enumOf
import de.miraculixx.mcore.gui.items.skullTexture
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

@Suppress("unused")
fun Challenges.getIcon(): ItemStack {
    return itemStack(enumOf<Material>(icon.material) ?: Material.BARRIER) {
        icon.texture?.let { itemMeta = (itemMeta as? SkullMeta)?.skullTexture(it.value) }
    }
}

fun ChallengeSetting<*>.getMaterial() = enumOf(materialKey) ?: Material.BARRIER