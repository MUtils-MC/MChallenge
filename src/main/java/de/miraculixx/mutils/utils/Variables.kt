package de.miraculixx.mutils.utils

import de.miraculixx.mutils.Main
import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.system.boot.API
import de.miraculixx.mutils.utils.tools.TPSCalculation
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.util.CachedServerIcon
import java.net.InetAddress


const val prefix = "§8§l[§9MUtils§8§l]§7"
val TPS = TPSCalculation()
val API = API()
val IP = InetAddress.getLocalHost().hostAddress + ":" + Main.INSTANCE.server.port
var ID = 0
var premium = false
var challengeOfTheMonth = Modules.FLY
var serverIcon: CachedServerIcon? = null

val plainSerializer = PlainTextComponentSerializer.plainText()
var isUpdating = false

val mm = MiniMessage.miniMessage()