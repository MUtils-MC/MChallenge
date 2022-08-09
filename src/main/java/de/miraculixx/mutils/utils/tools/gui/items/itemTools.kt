package de.miraculixx.mutils.utils.tools.gui.items

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import de.miraculixx.mutils.utils.consoleMessage
import de.miraculixx.mutils.utils.prefix
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

