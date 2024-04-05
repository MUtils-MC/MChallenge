package de.miraculixx.mutils.utils.items

import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mcore.gui.items.ItemFilterProvider
import de.miraculixx.mutils.module.WorldManager
import de.miraculixx.mutils.utils.WorldFilter
import de.miraculixx.mvanilla.extensions.enumOf
import de.miraculixx.mvanilla.messages.*
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

class ItemsWorlds(private val currentWorld: UUID) : ItemFilterProvider {
    override var filter = WorldFilter.NO_FILTER.name
    private val clickLore = listOf(emptyComponent(), msgClickLeft + cmp("Teleport"), msgClickRight + cmp("Copy"), msgShiftClickRight + cmp("Delete"))
    private val msgSeed = msgString("event.seed")
    private val msgDimension = msgString("event.dimension")
    private val msgType = msgString("event.type")
    private val msgBiome = msgString("event.biomeProvider")
    private val msgNoise = msgString("event.noiseProvider")
    private val msgNone = msgString("common.none")
    private val msgCategory = msgString("event.category")

    override fun getBooleanMap(from: Int, to: Int): Map<ItemStack, Boolean> {
        return buildMap {
            worlds.filter {
                val env = it.environment
                val filterObj = enumOf<WorldFilter>(filter) ?: WorldFilter.NO_FILTER
                filterObj == WorldFilter.NO_FILTER ||
                        (filterObj == WorldFilter.OVERWORLD && env == World.Environment.NORMAL) ||
                        (filterObj == WorldFilter.NETHER && env == World.Environment.NETHER) ||
                        (filterObj == WorldFilter.END && env == World.Environment.THE_END)

            }.map { world ->
                val material = when (world.environment) {
                    World.Environment.NORMAL -> Material.GRASS_BLOCK
                    World.Environment.NETHER -> Material.NETHERRACK
                    World.Environment.THE_END -> Material.END_STONE
                    World.Environment.CUSTOM -> Material.STRUCTURE_BLOCK
                }
                val uuid = world.uid
                put(itemStack(material) {
                    meta {
                        val worldData = WorldManager.getWorldData(uuid)
                        name = cmp(world.name, cHighlight)
                        lore(listOf(
                            cmp("$msgCategory ≫ ") + cmp(worldData?.category ?: "Vanilla", cHighlight),
                            cmp("$msgSeed ≫ ") + cmp("${world.seed}", cHighlight),
                            cmp("$msgDimension ≫ ") + cmp(msgString("event.env.${world.environment.name}"), cHighlight),
                            cmp("$msgType ≫ ") + cmp(msgString("event.gen.${worldData?.worldType?.name ?: "NORMAL"}"), cHighlight),
                            cmp("$msgBiome ≫ ") + cmp(worldData?.biomeProvider?.algorithm?.let { msgString("items.algo.${it.name}") } ?: msgNone, cHighlight),
                            cmp("$msgNoise ≫ ") + cmp("[${worldData?.chunkProviders?.size ?: 0} Rules]", cHighlight)
                        ) + clickLore)
                        customModel = 1
                        persistentDataContainer.set(NamespacedKey(namespace, "gui.worlds.uuid"), PersistentDataType.STRING, uuid.toString())
                    }
                }, currentWorld == uuid)
            }
        }
    }
}