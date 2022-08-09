package de.miraculixx.mutils

import de.miraculixx.mutils.modules.ModuleManager
import de.miraculixx.mutils.modules.creator.data.CustomChallengeData
import de.miraculixx.mutils.modules.creator.data.EventData
import de.miraculixx.mutils.modules.creator.enums.CreatorAction
import de.miraculixx.mutils.modules.creator.enums.CreatorEvent
import de.miraculixx.mutils.modules.spectator.Spectator
import de.miraculixx.mutils.system.boot.StartUp
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.*
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.main.KSpigot
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import java.io.File
import java.nio.file.Paths
import java.util.*


class Main : KSpigot() {
    companion object {
        lateinit var INSTANCE: KSpigot
    }

    override fun load() {
        INSTANCE = this

        val custom = CustomChallengeData(UUID.randomUUID(), INSTANCE)
        custom.eventData[CreatorEvent.MOVE_GENERAL] = EventData(
            true, mapOf(
                CreatorAction.GIVE_ITEM_TARGET_PLAYER to listOf("DIRT", "STONE"),
                CreatorAction.DAMAGE_TARGET_PLAYER to listOf("1")
            )
        )
        custom.eventData[CreatorEvent.MOVE_BLOCK] = EventData(
            true, mapOf(
                CreatorAction.DAMAGE_TARGET_PLAYER to listOf("1")
            )
        )
        custom.saveConfig()


        val c = ConfigManager.getConfig(Configs.SETTINGS)
        if (c.getBoolean("Legacy Reset")) {
            consoleMessage("$prefix Delete old Worlds...")
            c.getStringList("Loaded Worlds").forEach {
                val currentRelativePath = Paths.get(it)
                val s = currentRelativePath.toAbsolutePath().toString()
                consoleMessage("$prefix World Path: $s")
                File(s).listFiles().forEach { file ->
                    file.deleteRecursively()
                    File("$s/playerdata").mkdirs()
                }
                Thread.sleep(1000)
            }
            consoleMessage("$prefix §aAvoid second restart...")
            consoleMessage("$prefix §aMUtils use a new System to prevent double restarts and saving issues at the same time!")
            c.set("Legacy Reset", false)
            c.set("Loaded Worlds", null)
            ConfigManager.save(Configs.SETTINGS)
        }
    }

    override fun startup() {
        consoleMessage("$prefix MUtils is booting up. This could take a second...")
        StartUp(this)
        Bukkit.getServer().scheduler.scheduleSyncRepeatingTask(this, TPS, 100L, 1L)
        consoleMessage("\n\n$prefix MUtils was loaded successfully! Have fun!\n")

    }

    override fun shutdown() {
        if (!premium) {
            broadcast("$prefix §cOnly MUtils Premium will save data! You can buy a Licence at https://mutils.de/shop")
            return
        }
        if (!isUpdating) {
            ModuleManager.save()
            ModuleManager.shutDown()
            Spectator.saveData()
            ConfigManager.saveAll()
            val barKeys = ArrayList<NamespacedKey>()
            Bukkit.getBossBars().forEach { barKeys.add(it.key) }
            barKeys.forEach { Bukkit.removeBossBar(it) }

            //SYNC DATA (UPLOAD)
            /*runBlocking {
            val settings = ConfigManager.getConfig(Configs.SETTINGS)
            if (settings.getBoolean("Sync Update.Modules"))
                ConfigManager.upload(Configs.MODULES, "modules")
        }
         */
        }

        API.end()
        consoleMessage("$prefix Everything successfully saved and shut down!")
    }

}