package de.miraculixx.mcore.utils

import de.miraculixx.mvanilla.extensions.sendMessage
import de.miraculixx.mvanilla.messages.*
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File


class BukkitConfig(private val targetFile: File, private val sourcePath: String) {

    private lateinit var config: FileConfiguration

    init {
        load(false)
    }

    private fun load(reset: Boolean) {
        if (!targetFile.exists() || reset) {
            targetFile.parentFile.mkdirs()
            javaClass.getResourceAsStream("/$sourcePath")?.let { targetFile.writeBytes(it.readAllBytes()) } ?: if (debug)
                consoleAudience.sendMessage(prefix + cmp("Failed to load config file $sourcePath from jar!", cError)) else Unit
        }
        config = YamlConfiguration()
        try {
            config.load(targetFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getConfig(): FileConfiguration {
        return config
    }

    fun save() {
        config.save(targetFile)
    }

    fun reset() {
        targetFile.delete()
        load(true)
    }

    fun reload(): FileConfiguration {
        load(false)
        return config
    }
}