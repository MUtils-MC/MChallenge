package de.miraculixx.mutils.utils.config

import de.miraculixx.mutils.utils.enumOf
import de.miraculixx.mutils.utils.messages.*
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.InputStream
import kotlin.io.path.Path

class Config(stream: InputStream?, val name: String) {
    private val yaml: Yaml = Yaml()
    val configMap: Map<String, Any>

    inline fun <reified T> get(key: String): T? {
        var route: Any? = configMap
        key.split('.').forEach {
            if (route is Map<*,*>) route = (route as Map<*, *>)[it]
            else {
                if (debug) consoleAudience?.sendMessage(prefix + cmp("Failed to resolve config key '$key' because '$it' is no section or null ('$name.yml')"))
                return null
            }
        }
        return if (route is T) route as T else {
            if (debug) consoleAudience?.sendMessage(prefix + cmp("Failed to cast config value for key '$key' in config '$name.yml'"))
            null
        }
    }

    fun getString(key: String): String {
        return get<String>(key) ?: ""
    }

    fun getStringList(key: String): List<String> {
        val value = get<List<String>>(key)
        return value ?: emptyList()
    }

    inline fun <reified T> getObjectList(key: String): List<T> {
        val value = get<List<T>>(key)
        return value ?: emptyList()
    }

    fun getInt(key: String): Int {
        return get<Int>(key) ?: 0
    }

    fun getLong(key: String): Long {
        return get<Long>(key) ?: 0
    }

    fun getBoolean(key: String): Boolean {
        return get<Boolean>(key) ?: false
    }

    inline fun <reified T : Enum<T>> getEnum(key: String): T? {
        return enumOf<T>(getString(key))
    }


    private fun loadConfig(file: File, input: InputStream) {
        if (debug) consoleAudience?.sendMessage(prefix + cmp("Create new config file - $name"))
        if (!file.exists()) {
            file.createNewFile()
            file.writeBytes(input.readAllBytes())
        }
    }

    init {
        if (debug) consoleAudience?.sendMessage(prefix + cmp("Load config file - $name"))
        val file = Path("config/$name.yml").toFile()
        configMap = if (stream != null) {
            if (!file.exists()) loadConfig(file, stream)

            try {
                yaml.load(file.inputStream())
            } catch (e: Exception) {
                e.printStackTrace()
                consoleAudience?.sendMessage(prefix + cmp("Failed to load Configuration File '$name' ^^ Reason above ^^"))
                consoleAudience?.sendMessage(prefix + cmp("Config Path -> ${file.path}"))
                emptyMap()
            }
        } else {
            consoleAudience?.sendMessage(prefix + cmp("Configuration file '$name' is null"))
            emptyMap()
        }
    }
}