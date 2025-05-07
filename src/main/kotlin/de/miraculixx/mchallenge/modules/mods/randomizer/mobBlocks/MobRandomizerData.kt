package de.miraculixx.mchallenge.modules.mods.randomizer.mobBlocks

import de.miraculixx.kpaper.extensions.worlds
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.*
import org.bukkit.inventory.ItemStack
import java.util.*

class MobRandomizerData(private val random: Boolean) {
    private val blockList = HashMap<Material, EntityType>()

    fun generate() {
        val world = worlds[0]
        val random = Random(world.seed)
        blockList.clear()
        val list: List<EntityType> = fillList()
        Material.entries.forEach { material -> blockList[material] = list[random.nextInt(list.size)] }
    }

    private fun fillList(): ArrayList<EntityType> {
        val listDummy = EntityType.entries
        val list = ArrayList(listDummy)
        list.remove(EntityType.TRIDENT)
        list.remove(EntityType.ARROW)
        list.remove(EntityType.ARMOR_STAND)
        list.remove(EntityType.AREA_EFFECT_CLOUD)
        list.remove(EntityType.DRAGON_FIREBALL)
        list.remove(EntityType.ITEM)
        list.remove(EntityType.EGG)
        list.remove(EntityType.ENDER_PEARL)
        list.remove(EntityType.EYE_OF_ENDER)
        list.remove(EntityType.EXPERIENCE_ORB)
        list.remove(EntityType.FALLING_BLOCK)
        list.remove(EntityType.FIREWORK_ROCKET)
        list.remove(EntityType.FISHING_BOBBER)
        list.remove(EntityType.ITEM_FRAME)
        list.remove(EntityType.LEASH_KNOT)
        list.remove(EntityType.LIGHTNING_BOLT)
        list.remove(EntityType.LLAMA_SPIT)
        list.remove(EntityType.PAINTING)
        list.remove(EntityType.PLAYER)
        list.remove(EntityType.SHULKER_BULLET)
        list.remove(EntityType.SMALL_FIREBALL)
        list.remove(EntityType.SNOWBALL)
        list.remove(EntityType.SPECTRAL_ARROW)
        list.remove(EntityType.SPLASH_POTION)
        list.remove(EntityType.LINGERING_POTION)
        list.remove(EntityType.EXPERIENCE_BOTTLE)
        list.remove(EntityType.UNKNOWN)
        list.remove(EntityType.WITHER_SKULL)
        list.remove(EntityType.ENDER_DRAGON)
        list.removeIf { entityType: EntityType ->
            entityType.name.contains("MINECART") ||
            entityType.name.contains("BOAT", true)
        }
        return list
    }

    fun spawnEntity(block: Block) {
        val entity = if (!random) {
            block.world.spawnEntity(block.location.add(0.5, 0.0, 0.5), blockList[block.type] ?: EntityType.ZOMBIE)
        } else {
            val list = fillList()
            list.shuffle()
            block.world.spawnEntity(block.location.add(0.5, 0.0, 0.5), list[0])
        }
        if (entity is LivingEntity) {
            val type = block.type
            if (type.isAir || type == Material.FIRE || type == Material.SOUL_FIRE) return
            val item = block.world.dropItem(block.location, ItemStack(block.type))
            item.pickupDelay = 60
            entity.addPassenger(item)
        }
        when (entity) {
            is Fireball -> entity.yield = 1F
            is TNTPrimed -> entity.fuseTicks = 30
            is Rabbit -> entity.rabbitType = Rabbit.Type.THE_KILLER_BUNNY
            else -> {}
        }
    }
}