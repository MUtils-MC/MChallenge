package de.miraculixx.mchallenge.modules.mods.force.huntMob

import de.miraculixx.mvanilla.messages.cError
import de.miraculixx.mvanilla.messages.cmp
import de.miraculixx.mvanilla.messages.plus
import de.miraculixx.mvanilla.messages.prefix
import net.kyori.adventure.audience.Audience

interface HuntCommand <T> {
    val typeNameList: List<String>
    val typeList: List<T>
    fun getTabComplete(args: Array<out String>?, blacklist: List<String>): MutableList<String> {
        return when (args?.size ?: 0) {
            0, 1 -> listOf("skip", "reset")
            2 -> if (args?.getOrNull(0) == "blacklist") {
                listOf("add", "remove")
            } else emptyList()
            3 -> when (args?.getOrNull(1)) {
                "add" -> typeNameList
                "remove" -> blacklist
                else -> emptyList()
            }

            else -> emptyList()
        }.filter { it.contains(args?.lastOrNull() ?: "") }.toMutableList()
    }

    fun HuntObject<T>.handleCommand(sender: Audience, senderName: String, args: Array<out String>?) {
        when (args?.getOrNull(0)?.lowercase()) {
            "skip" -> nextEntry(senderName, sender)
            "reset" -> reset(typeList)
            "blacklist" -> {
                val arg3 = args.getOrNull(2)
                val type = getType(arg3)
                if (type == null) {
                    sender.sendMessage(prefix + cmp("Please enter a valid type!", cError))
                    return
                }

                when (args.getOrNull(1)?.lowercase()) {
                    "add" -> addBlacklist(type)
                    "remove" -> removeBlacklist(type)
                    else -> sender.sendMessage(prefix + cmp("Invalid command structure", cError))
                }
            }

            else -> sender.sendMessage(prefix + cmp("Invalid command structure", cError))
        }
    }

    fun getType(input: String?): T?
}