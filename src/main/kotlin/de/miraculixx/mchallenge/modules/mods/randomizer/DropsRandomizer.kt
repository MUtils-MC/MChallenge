package de.miraculixx.mchallenge.modules.mods.randomizer

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.kpaper.items.getLivingMobs
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.challenges.getSetting
import de.miraculixx.mchallenge.utils.getItems
import de.miraculixx.mcommons.text.cmp
import de.miraculixx.mcommons.text.plus
import de.miraculixx.mcommons.text.prefix
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.loot.LootContext
import org.bukkit.loot.LootTables
import kotlin.random.Random

class DropsRandomizer : Challenge {
    private val random: Boolean
    private val itemMode: Boolean
    private val mapItems: MutableMap<EntityType, Set<Material>> = mutableMapOf()
    private val map: MutableMap<EntityType, EntityType> = mutableMapOf()
    private val list: MutableList<EntityType> = mutableListOf()
    private val items: MutableSet<Material> = mutableSetOf()
    private val announced: MutableSet<EntityType> = mutableSetOf()

    init {
        val settings = challenges.getSetting(Challenges.RANDOMIZER_ENTITY).settings
        random = settings["random"]?.toBool()?.getValue() ?: true
        itemMode = settings["itemMode"]?.toBool()?.getValue() ?: false
    }

    override fun start(): Boolean {
        val rnd = Random(worlds.first().seed)
        val randomMobList = getLivingMobs(false).shuffled(rnd)

        if (itemMode) { // entity to item
            items.addAll(getItems(false, false).shuffled(rnd))
            val itemsPerEntity = items.size / randomMobList.size
            val itemsChunked = items.chunked(itemsPerEntity)
            randomMobList.forEach { mob -> mapItems[mob] = itemsChunked.random(rnd).toSet() }

        } else { // entity to entity
            if (random) {
                list.addAll(randomMobList)
            } else {
                val drops = randomMobList.toMutableList()
                drops.shuffle(rnd)
                randomMobList.forEach { kill ->
                    val type = drops.random(rnd)
                    map[kill] = type
                    drops.remove(type)
                }
            }
        }

        return true
    }

    override fun register() {
        onKill.register()
    }

    override fun unregister() {
        onKill.unregister()
    }

    private val onKill = listen<EntityDeathEvent>(register = false) {
        it.drops.clear()
        val entity = it.entity
        val eType = entity.type

        if (itemMode) { // entity to item
            val amount = (1..3).random()
            val itemPool = if (random) items else mapItems[eType] ?: items
            val itemDrops = buildList {
                repeat(amount) { add(ItemStack(itemPool.random())) }
            }
            it.drops.clear()
            it.drops.addAll(itemDrops)

        } else { // entity to entity
            val targetType = if (random) {
                list[Random.nextInt(0, list.size - 1)]
            } else map[eType] ?: EntityType.ZOMBIE

            val loc = entity.location
            val context = LootContext.Builder(loc).killer(entity.killer).lootedEntity(entity).lootingModifier(2).build()
            val tableName = targetType.name
            val lootTable = LootTables.valueOf(tableName).lootTable
            if (announced.contains(eType)) {
                announced.add(eType)
                broadcast(prefix + cmp("${eType.name} >> ${targetType.name}"))
            }
            lootTable.populateLoot(java.util.Random(loc.world.seed), context).forEach { item ->
                loc.world.dropItem(loc, item)
            }
        }
    }
}