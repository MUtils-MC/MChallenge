package de.miraculixx.mutils.module

import de.miraculixx.kpaper.extensions.console
import de.miraculixx.kpaper.utils.ItemStackSerializer
import de.miraculixx.kpaper.utils.LocationSerializer
import de.miraculixx.mutils.extensions.readJsonString
import de.miraculixx.mutils.extensions.round
import de.miraculixx.mutils.messages.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.io.File
import java.util.*

class PlayerData(private val playerID: UUID, private val topFolder: File) {
    private val saveData: MutableMap<String, String> = mutableMapOf()

    /**
     * Saving all loaded data to disk and override current data. After saving the current cache will be flushed
     */
    fun saveToDisk() {
        saveData.forEach { (path, data) ->
            val file = File(path)
            if (!file.exists()) file.parentFile.mkdirs()
            file.writeText(data)
            if (debug) consoleAudience.sendMessage(prefix + cmp("Saved player data to $path (${(data.length / 1024.0).round(2)}kb)"))
        }
        saveData.clear()
    }

    /**
     * Fetch player data
     */
    fun loadData(player: Player, category: String): Location? {
        val path = "${topFolder.path}/$category/$playerID.json"
        val saveString = saveData[path] ?: File(path).readJsonString(true)
        val data = try {
            json.decodeFromString<PlayerSaveData>(saveString)
        } catch (e: Exception) {
            consoleAudience.sendMessage(prefix + cmp("Invalid player save file at $path!"))
            if (debug) consoleAudience.sendMessage(prefix + cmp("Reason: ${e.message}"))
            return null
        }
        player.inventory.contents = data.inventory
        player.health = data.hp
        player.totalExperience = data.xp
        player.foodLevel = data.hunger
        data.effects.forEach { liteEffect ->
            val potion = liteEffect.toBukkit() ?: return@forEach
            player.addPotionEffect(potion)
        }
        player.gameMode = data.gameMode
        player.isFlying = data.isFlying
        player.fallDistance = data.fallDistance
        player.bedSpawnLocation = data.respawnPoint
        return data.location
    }

    /**
     * Save current [Player] data (in ram)
     */
    fun saveData(player: Player, category: String) {
        val path = "${topFolder.path}/$category/$playerID.json"
        val data = PlayerSaveData(
            player.location,
            player.health,
            player.totalExperience,
            player.foodLevel,
            player.activePotionEffects.map { it.toLite() },
            player.gameMode,
            player.isFlying,
            player.fallDistance,
            player.potentialBedLocation,
            player.inventory.contents
        )
        val saveString = jsonCompact.encodeToString(data)
        saveData[path] = saveString
        if (debug) consoleAudience.sendMessage(prefix + cmp("Saved player data ${player.name} (${(saveString.length / 1024.0).round(2)}kb)"))
    }


    @Serializable
    private data class PlayerSaveData(
        val location: @Serializable(with = LocationSerializer::class) Location? = null,
        val hp: Double = 20.0,
        val xp: Int = 0,
        val hunger: Int = 20,
        val effects: List<LitePotion> = emptyList(),
        val gameMode: GameMode = GameMode.SURVIVAL,
        val isFlying: Boolean = false,
        val fallDistance: Float = 0f,
        val respawnPoint: @Serializable(with = LocationSerializer::class) Location? = null,
        val inventory: Array<@Serializable(with = ItemStackSerializer::class) ItemStack?> = emptyArray(),
        ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as PlayerSaveData

            if (location != other.location) return false
            if (!inventory.contentEquals(other.inventory)) return false
            if (hp != other.hp) return false
            if (xp != other.xp) return false
            if (hunger != other.hunger) return false
            if (effects != other.effects) return false
            if (gameMode != other.gameMode) return false
            if (isFlying != other.isFlying) return false
            if (fallDistance != other.fallDistance) return false

            return true
        }

        override fun hashCode(): Int {
            var result = location.hashCode()
            result = 31 * result + inventory.contentHashCode()
            result = 31 * result + hp.hashCode()
            result = 31 * result + xp
            result = 31 * result + hunger
            result = 31 * result + effects.hashCode()
            result = 31 * result + gameMode.hashCode()
            result = 31 * result + isFlying.hashCode()
            result = 31 * result + fallDistance.hashCode()
            return result
        }
    }

    @Serializable
    data class LocationDataSerializer(@Serializable(with = LocationSerializer::class) val location: Location)

    @Serializable
    data class ItemDataSerializer(@Serializable(with = ItemStackSerializer::class) val itemStack: ItemStack)

    @Serializable
    private data class LitePotion(
        val type: String,
        val level: Int,
        val duration: Int,
        val ambiente: Boolean,
        val particle: Boolean,
        val icon: Boolean,
    )


    //-----------------------
    private fun PotionEffect.toLite(): LitePotion {
        return LitePotion(type.key.asString(), amplifier, duration, isAmbient, hasParticles(), hasIcon())
    }

    private fun LitePotion.toBukkit(): PotionEffect? {
        val key = NamespacedKey.fromString(type) ?: return null
        val effect = PotionEffectType.getByKey(key) ?: return null
        return PotionEffect(effect, duration, level, ambiente, particle, icon)
    }
}