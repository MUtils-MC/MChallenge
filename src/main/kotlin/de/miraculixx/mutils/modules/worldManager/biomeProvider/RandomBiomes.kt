package de.miraculixx.mutils.modules.worldManager.biomeProvider

import org.bukkit.block.Biome
import org.bukkit.generator.BiomeProvider
import org.bukkit.generator.WorldInfo

class RandomBiomes : BiomeProvider() {
    override fun getBiome(worldInfo: WorldInfo, x: Int, y: Int, z: Int): Biome {
        while (true) {
            val rnd = Biome.values().random()
            if (rnd == Biome.CUSTOM) continue
            return rnd
        }
    }

    override fun getBiomes(worldInfo: WorldInfo): MutableList<Biome> {
        val list = ArrayList<Biome>(Biome.values().toMutableList())
        list.remove(Biome.CUSTOM)
        return list
    }
}