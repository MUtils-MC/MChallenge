package de.miraculixx.mcore.utils

import de.miraculixx.kpaper.chat.sendMessage
import de.miraculixx.mvanilla.extensions.soundError
import de.miraculixx.mvanilla.messages.msg
import de.miraculixx.mvanilla.messages.plus
import de.miraculixx.mvanilla.messages.prefix
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun CommandSender.checkPermission(permission: String): Boolean {
    return if (hasPermission(permission)) true
    else {
        if (this is Player) soundError()
        sendMessage(prefix + msg("command.noPermission", listOf(permission)))
        false
    }
}