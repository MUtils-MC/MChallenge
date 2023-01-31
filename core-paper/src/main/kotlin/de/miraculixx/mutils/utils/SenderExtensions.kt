package de.miraculixx.mutils.utils

import de.miraculixx.mutils.extensions.soundError
import de.miraculixx.mutils.messages.msg
import de.miraculixx.mutils.messages.plus
import de.miraculixx.mutils.messages.prefix
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