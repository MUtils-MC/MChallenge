@file:Suppress("unused")

package de.miraculixx.mutils.modules.utils.tools

import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.premium
import de.miraculixx.mutils.utils.serverIcon
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.onlinePlayers
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.ServerListPingEvent
import java.util.*

object Connection {

    private val join = listen<PlayerJoinEvent> {
        it.joinMessage = "§a>> §9${it.player.name}"
        if (!premium) {
            val player = it.player
            player.sendMessage("§9§m                                            \n§9§lMUtils Demo\n ")
            if (player.isOp)
                player.sendMessage("§7You own a Key? Activate it via §9/verify")
            player.sendMessage(
                "§9MUtils Unlimited §7->§b https://mutils.de/m/shop\n" +
                        "§9MUtils Overview §7->§b https://mutils.de/\n" +
                        "§9§m                                            "
            )
        }

    }
    private val quit = listen<PlayerQuitEvent> {
        it.quitMessage = "§c<< §9${it.player.name}"
    }

    private val onPing = listen<ServerListPingEvent> {
        val conf = ConfigManager.getConfig(Configs.SETTINGS)
        it.motd = conf.getString("MOTD") ?: Bukkit.getMotd()
        if (serverIcon != null) {
            it.setServerIcon(serverIcon)
        }
        it.maxPlayers = conf.getInt("Slots")
    }
    private val onLogIn = listen<AsyncPlayerPreLoginEvent> {
        val conf = ConfigManager.getConfig(Configs.SETTINGS)
        val maxPlayers = conf.getInt("Slots")
        val uuid = it.playerProfile.id ?: UUID.randomUUID()
        val player = Bukkit.getOfflinePlayer(uuid)
        if (Bukkit.getOperators().contains(player)) return@listen
        if (maxPlayers <= onlinePlayers.size)
            it.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, Component.text("The Server is full!").color(TextColor.fromHexString("#FF5555")))
    }
}