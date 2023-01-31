package de.miraculixx.mutils.module.biomes

import de.miraculixx.mutils.data.GeneratorData
import org.bukkit.block.Biome
import org.bukkit.generator.BiomeProvider
import org.bukkit.generator.WorldInfo

/**
 * @param random true - Every biome will be choosen randomly, otherwise types are static. E.g. the randomizer picks taiga for plains, all plain biomes will be replaced with taiga
 */
class SwitchBiomes(biomeData: GeneratorData) : BiomeProvider() {
    private val biomeMap = HashMap<Biome, Biome>()
    private var biomeList: List<Biome>? = null
    private val random = biomeData.rnd ?: false

    override fun getBiome(worldInfo: WorldInfo, x: Int, y: Int, z: Int): Biome {
        val provider = worldInfo.vanillaBiomeProvider()
        return if (random) biomeList?.random() ?: Biome.PLAINS
        else biomeMap[provider.getBiome(worldInfo, x, y, z)] ?: Biome.PLAINS
    }

    override fun getBiomes(worldInfo: WorldInfo): MutableList<Biome> {
        return Biome.values().filter { it != Biome.CUSTOM }.toMutableList()
    }

    init {
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