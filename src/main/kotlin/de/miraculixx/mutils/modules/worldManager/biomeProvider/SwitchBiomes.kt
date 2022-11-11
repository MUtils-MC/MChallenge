package de.miraculixx.mutils.modules.worldManager.biomeProvider

import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import org.bukkit.block.Biome
import org.bukkit.generator.BiomeProvider
import org.bukkit.generator.WorldInfo

class SwitchBiomes : BiomeProvider() {
    private val biomeMap = HashMap<Biome, Biome>()
    private var biomeList: List<Biome>? = null
    private var random = false

    override fun getBiome(worldInfo: WorldInfo, x: Int, y: Int, z: Int): Biome {
        val provider = worldInfo.vanillaBiomeProvider()
        return if (random) biomeList?.random() ?: Biome.PLAINS
            else biomeMap[provider.getBiome(worldInfo, x, y, z)] ?: Biome.PLAINS
    }

    override fun getBiomes(worldInfo: WorldInfo): MutableList<Biome> {
        return Biome.values().filter { it != Biome.CUSTOM }.toMutableList()
    }

    init {
        val config = ConfigManager.getConfig(Configs.MODULES)
        random = config.getBoolean("RANDOMIZER_BIOMES.Random")
        val key = Biome.values().filter { it != Biome.CUSTOM }.shuffled()

        if (random) biomeList = key.toList()
        else {
            val final = key.toList().shuffled()
            var counter = 0
            final.forEach { fBiome ->
                biomeMap[key[counter]] = fBiome
                counter++
            }
        }
    }
}