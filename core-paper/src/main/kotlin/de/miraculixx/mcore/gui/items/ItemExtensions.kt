package de.miraculixx.mcore.gui.items


import com.destroystokyo.paper.profile.ProfileProperty
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import de.miraculixx.mvanilla.messages.*
import org.bukkit.Bukkit
import org.bukkit.inventory.meta.SkullMeta
import java.lang.reflect.Field
import java.util.*

fun SkullMeta.skullTexture(base64: String, uuid: UUID = UUID.randomUUID()): SkullMeta {
    val profile = Bukkit.createProfile(uuid)
    profile.setProperty(ProfileProperty("textures", base64))
    playerProfile = profile
    return this
}