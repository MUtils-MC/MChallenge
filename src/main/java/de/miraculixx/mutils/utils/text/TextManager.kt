package de.miraculixx.mutils.utils.text

import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.mm
import de.miraculixx.mutils.utils.prefix
import net.kyori.adventure.text.Component
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player

fun msg(key: String, player: Player? = null, input: String? = null, input2: String? = null, pre: Boolean = true): String {
    val config = ConfigManager.getConfig(Configs.LANGUAGE)
    val msg = config.getString(key) ?: key
    var final = if (pre) "$prefix " else ""
    final += msg.replaceColor()

    if (player != null) final = final.replace("<PLAYER>", player.name)
    if (input != null) final = final.replace("<INPUT>", input)
    if (input2 != null) final = final.replace("<INPUT-2>", input2)

    return final
}

fun getMessageList(key: String, inline: String? = null, input: String? = null): List<String> {
    val config = ConfigManager.getConfig(Configs.LANGUAGE)
    val final = if (inline == null) config.getStringList(key).map { it.replaceColor().replace("<INPUT>", input ?: "none") }
    else config.getStringList(key).map { inline + it.replaceColor() }

    return final.ifEmpty { listOf("§c$key") }
}

fun getComponentList(key: String, inline: String? = null, input: String? = "   "): List<Component> {
    val config = ConfigManager.getConfig(Configs.LANGUAGE)
    val final = config.getStringList(key).map { mm.deserialize((inline ?: "") + it.replace("<INPUT>", input ?: "none")) }

    return final.ifEmpty { listOf(Component.text(key).color(cError)) }
}

fun String.cropColor(): String {
    return ChatColor.stripColor(this)
}

fun String.replaceColor(): String {
    return ChatColor.translateAlternateColorCodes('&', this)
}

fun MutableList<String>.addLines(vararg lines: String): MutableList<String> {
    lines.forEach { this.add(it) }
    return this
}

fun MutableList<String>.addLines(vararg lines: List<String>): MutableList<String> {
    lines.forEach { list ->
        list.forEach {
            this.add(it)
        }
    }
    return this
}