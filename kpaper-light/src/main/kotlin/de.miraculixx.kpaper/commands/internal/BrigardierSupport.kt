@file:Suppress("MemberVisibilityCanBePrivate")

package de.miraculixx.kpaper.commands.internal

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.extensions.server
import de.miraculixx.kpaper.main.KSpigotMainInstance
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.server.level.ServerPlayer
import org.bukkit.event.player.PlayerJoinEvent

/**
 * This class provides Brigardier support. It does that
 * by using reflection once. Additionally, this class is
 * using some obfuscated functions.
 */
object BrigardierSupport {
    @PublishedApi
    internal val commands = LinkedHashSet<LiteralArgumentBuilder<CommandSourceStack>>()

    internal var executedDefaultRegistration = false
        private set

    init {
        listen<PlayerJoinEvent> { event ->
            val player = event.player
            val permAttachment = player.addAttachment(KSpigotMainInstance)
            commands.forEach {
                permAttachment.setPermission("minecraft.command.${it.literal}", true)
            }
        }
    }

    object ResolveCommandManager {
        val manager
            get() = kotlin.run {
                val clazz = Class.forName("net.minecraft.server.MinecraftServer")
                val sField = clazz.getDeclaredField("SERVER")
                sField.isAccessible = true
                val vcdField = clazz.getDeclaredField("vanillaCommandDispatcher")
                vcdField.isAccessible = true
                vcdField.get(sField.get(server)) as Commands
            }
    }

    internal fun registerAll() {
        executedDefaultRegistration = true

        // TODO unregister commands which are now missing due to a possible reload
        if (commands.isNotEmpty()) {
            commands.forEach {
                ResolveCommandManager.manager.dispatcher.register(it)
            }
            if (onlinePlayers.isNotEmpty())
                updateCommandTree()
        }
    }

    fun updateCommandTree() {
        onlinePlayers.forEach {
            // using reflection to get the server player
            val clazz = Class.forName("net.minecraft.server.level.ServerPlayer")
            val pField = clazz.getDeclaredField("player")
            pField.isAccessible = true
            val player = pField.get(it) as ServerPlayer

            // send the command tree
            ResolveCommandManager.manager.sendCommands(
                player
            )
        }
    }
}
