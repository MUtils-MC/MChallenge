package de.miraculixx.mtimer

import de.miraculixx.mvanilla.messages.cmp
import de.miraculixx.mvanilla.messages.consoleAudience
import de.miraculixx.mvanilla.messages.plus
import de.miraculixx.mvanilla.messages.prefix
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.kyori.adventure.platform.fabric.FabricServerAudiences
import net.minecraft.server.MinecraftServer


class MTimer : ModInitializer {

    override fun onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(ServerLifecycleEvents.ServerStarting { server: MinecraftServer? ->
            val adventure = FabricServerAudiences.of(server!!)
            consoleAudience = adventure.console()


        })

        ServerLifecycleEvents.SERVER_STOPPED.register(ServerLifecycleEvents.ServerStopped {

            consoleAudience.sendMessage(prefix + cmp("Successfully saved all data! Good Bye :)"))
        })
    }
}