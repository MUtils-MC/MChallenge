package de.miraculixx.mutils.utils.text

import net.axay.kspigot.extensions.onlinePlayers
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

fun broadcastTitle(main: String, sub: String, fade: Int, stay: Int, out: Int) {
    onlinePlayers.forEach { player ->
        player.sendTitle(main, sub, fade, stay, out)
    }
}

fun broadcastSound(sound: Sound, category: SoundCategory = SoundCategory.MASTER, volume: Float = 1f, pitch: Float = 1f) {
    onlinePlayers.forEach { player ->
        player.playSound(player.location, sound, category, volume, pitch)
    }
}

fun broadcastEffect(effect: PotionEffectType, duration: Int, amplifier: Int) {
    onlinePlayers.forEach { pl ->
        pl.addPotionEffect(PotionEffect(effect, duration, amplifier, false, false, false))
    }
}

fun consoleMessage(string: String) {
    Bukkit.getConsoleSender().sendMessage(string)
}

fun broadcast(text: TextComponent) {
    onlinePlayers.forEach { p ->
        p.spigot().sendMessage(text)
    }
}

fun consoleWarn(vararg string: String) {
    val log = Bukkit.getLogger()
    string.forEach {
        log.warning(ChatColor.stripColor(it))
    }
}