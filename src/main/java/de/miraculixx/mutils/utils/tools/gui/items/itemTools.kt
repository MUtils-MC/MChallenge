package de.miraculixx.mutils.utils.tools.gui.items

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import de.miraculixx.mutils.utils.consoleMessage
import de.miraculixx.mutils.utils.prefix
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.lang.reflect.Field
import java.util.*

fun skullTexture(meta: SkullMeta, base64: String): SkullMeta {
    val profile = GameProfile(UUID.randomUUID(), "")
    profile.properties.put("textures", Property("textures", base64))
    val profileField: Field?
    try {
        profileField = meta.javaClass.getDeclaredField("profile")
        profileField.isAccessible = true
        profileField[meta] = profile
    } catch (e: Exception) {
        e.printStackTrace()
        consoleMessage("$prefix §cHead Builder failed to apply Base64 Code to Skull!")
        consoleMessage("$prefix §cCode: §7$base64")
    }
    return meta
}

fun buildItem(material: Material, id: Int, name: Component, lore: List<Component>, base64: String? = null): ItemStack {
    return itemStack(material) {
        meta {
            displayName(name)
            lore(lore)
            customModel = id
        }
        if (base64 != null && material == Material.PLAYER_HEAD) {
            itemMeta = skullTexture(itemMeta as SkullMeta, base64)
        }
    }
}

fun ItemStack.editMeta(name: Component? = null, modelData: Int? = null): ItemStack {
    editMeta {
        if (name != null) it.displayName(name)
        if (modelData != null) it.customModel = modelData
    }
    return this
}
