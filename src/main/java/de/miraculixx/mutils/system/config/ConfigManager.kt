package de.miraculixx.mutils.system.config

import de.miraculixx.mutils.Main
import de.miraculixx.mutils.utils.API
import de.miraculixx.mutils.utils.prefix
import de.miraculixx.mutils.utils.text.consoleMessage
import de.miraculixx.mutils.utils.text.consoleWarn
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.configuration.file.FileConfiguration
import java.io.File
import kotlin.io.path.Path

object ConfigManager {
    /*
    Config Files that are used in many Functions or in Objects
    are stored centrally here to prevent double loading.
     */

    //Config Files
    private val configMap = HashMap<Configs, Config>()

    fun getLicenceData(): Pair<String, String> {
        val verify = getConfig(Configs.VERIFY)
        val key = verify.getString("Licence Key") ?: "error"
        val uuid = verify.getString("Licence Owner.MC") ?: "error"
        return Pair(key, uuid)
    }

    init {
        // Loading Default Configs
        configMap[Configs.SETTINGS] = Config("settings")
        configMap[Configs.VERIFY] = Config("licence")

        // Create Config Instances
        configMap[Configs.WORLDS] = Config("modules/worlds")
        configMap[Configs.TIMER] = Config("modules/timer")
        configMap[Configs.SETTINGS] = Config("settings")
        configMap[Configs.MODULES] = Config("modules/modules")
        configMap[Configs.BACK] = Config("utils/back")
        configMap[Configs.BACKPACK] = Config("utils/backpack")
        configMap[Configs.SPEEDRUN] = Config("modules/speedrun")
        configMap[Configs.CUSTOM_CHALLENGE] = Config("modules/challengeCreator")

        saveLang()
        updateLang()
    }

    fun updateLang() {
        var langKey = configMap[Configs.SETTINGS]?.getConfig()?.getString("Language") ?: "DE_DE"
        val c = Config("language/$langKey")
        configMap[Configs.LANGUAGE] = c
        val translator = c.getConfig().getString("translator")
        if (translator == null) {
            consoleWarn("$prefix §cSelected language config §n$langKey§c can not be loaded!")
            langKey = "DE_DE"
            consoleWarn("$prefix §cUsing language config §n$langKey§c as fallback language...")
        }
        consoleMessage("$prefix Using language config §9$langKey§7 by §9$translator§7 to show messages")
    }

    private fun saveLang() {
        listOf("DE_DE", "EN_US").forEach {
            Config("language/$it").reset()
        }
    }

    fun reload(type: Configs) {
        configMap[type]?.reload()
    }

    fun saveAll() {
        configMap.forEach { (c, file) ->
            if (c != Configs.LANGUAGE) file.save()
        }
    }

    fun save(type: Configs) {
        configMap[type]?.save()
    }

    fun reset(type: Configs) {
        configMap[type]?.reset()
    }

    fun getConfig(type: Configs): FileConfiguration {
        return configMap[type]?.getConfig() ?: configMap[Configs.SETTINGS]!!.getConfig()
    }

    private val cooldown = ArrayList<Configs>()
    suspend fun upload(type: Configs, folder: String): Boolean {
        if (cooldown.contains(type)) return false
        cooldown.add(type)
        val verify = getLicenceData()
        val s = File.separator
        val path = Main.INSTANCE.dataFolder.path + s
        val name = type.name.lowercase()

        API.upload(Path(path + "$folder${s}$name.yml").toFile(), "$name.yml", verify.first, verify.second)
        taskRunLater(20*60) { cooldown.remove(type) }
        return true
    }
}