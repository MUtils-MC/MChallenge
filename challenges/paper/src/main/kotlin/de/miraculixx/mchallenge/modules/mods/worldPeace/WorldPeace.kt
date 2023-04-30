package de.miraculixx.mchallenge.modules.mods.worldPeace

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mchallenge.utils.BasicItem
import de.miraculixx.mvanilla.messages.msg
import de.miraculixx.mvanilla.messages.plus
import de.miraculixx.mvanilla.messages.prefix
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftLivingEntity
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer
import org.bukkit.entity.EnderDragon
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityTargetLivingEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random

class WorldPeace : Challenge {
    private val foodPlants = ArrayList<Material>()
    private val water = ArrayList<Material>()
    private val saplings = ArrayList<Material>()
    private val flowers = ArrayList<Material>()
    private val resources = ArrayList<Material>()

    private val trades = HashMap<LivingEntity, BasicItem>()
    private val alreadyTraded = ArrayList<LivingEntity>()
    private val untradeable = ArrayList<LivingEntity>()

    private var dragonStart = false

    override fun start(): Boolean {
        foodPlants.add(Material.CARROT)
        foodPlants.add(Material.POTATO)
        foodPlants.add(Material.BEETROOT)
        foodPlants.add(Material.WHEAT)
        foodPlants.add(Material.BREAD)

        water.add(Material.WATER_BUCKET)
        water.add(Material.POTION)

        saplings.add(Material.OAK_SAPLING)
        saplings.add(Material.ACACIA_SAPLING)
        saplings.add(Material.SPRUCE_SAPLING)
        saplings.add(Material.BIRCH_SAPLING)
        saplings.add(Material.DARK_OAK_SAPLING)
        saplings.add(Material.BAMBOO)

        flowers.add(Material.DANDELION)
        flowers.add(Material.POPPY)
        flowers.add(Material.BLUE_ORCHID)
        flowers.add(Material.ALLIUM)
        flowers.add(Material.AZURE_BLUET)
        flowers.add(Material.RED_TULIP)
        flowers.add(Material.ORANGE_TULIP)
        flowers.add(Material.WHITE_TULIP)
        flowers.add(Material.OXEYE_DAISY)
        flowers.add(Material.CORNFLOWER)
        flowers.add(Material.LILY_OF_THE_VALLEY)

        resources.add(Material.COAL)
        resources.add(Material.IRON_NUGGET)
        resources.add(Material.COPPER_INGOT)
        resources.add(Material.GOLD_INGOT)
        resources.add(Material.REDSTONE)
        resources.add(Material.LAPIS_LAZULI)
        resources.add(Material.OAK_LOG)
        resources.add(Material.DARK_OAK_LOG)
        return true
    }

    override fun stop() {
        trades.clear()
        foodPlants.clear()
        untradeable.clear()
    }

    override fun register() {
        onNaturalDamage.register()
        onDamage.register()
        onTarget.register()
    }

    override fun unregister() {
        onNaturalDamage.unregister()
        onDamage.unregister()
        onTarget.unregister()
    }

    private val onTarget = listen<EntityTargetLivingEntityEvent>(register = false) {
        if (it.entity !is LivingEntity) return@listen
        val entity = it.entity as LivingEntity
        if (alreadyTraded.contains(entity)) {
            it.isCancelled = true
            it.target = null
        }
    }

    private val onNaturalDamage = listen<EntityDeathEvent>(register = false) {
        var nearby = false
        it.entity.getNearbyEntities(1.0, 15.0, 15.0).forEach { e ->
            if (e is Player) nearby = true
        }
        if (nearby) {
            broadcast(prefix + msg("event.worldPeace.death", listOf(it.entity.type.name)))
            if (onlinePlayers.isNotEmpty())
                onlinePlayers.first().damage(99.0)
        }
    }

    private val onDamage = listen<EntityDamageByEntityEvent>(register = false) {
        if (it.damager !is Player) return@listen
        if (it.entity !is LivingEntity) return@listen
        it.isCancelled = true
        val player = it.damager as Player
        val entity = it.entity as LivingEntity

        //DRAGON CASE
        if (entity is EnderDragon) {
            if (dragonStart) return@listen
            dragonStart = true
            entity.phase = EnderDragon.Phase.FLY_TO_PORTAL
            var counter = 0
            task(true, 10, 10) {
                if (counter >= 20) {
                    entity.phase = EnderDragon.Phase.LAND_ON_PORTAL
                }
                when (counter) {
                    0 -> {
                        broadcast("\n$prefix §dIhr habt es bis zum Ende geschafft ohne einmal jemandem Schaden zu zu fügen...")
                        onlinePlayers.forEach { p -> p.playSound(p, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f) }
                    }

                    2 -> broadcast("$prefix §dRespekt...")
                    20 -> {
                        broadcast("$prefix §dNun... Nehmt mein letztes Geschenk")
                        onlinePlayers.forEach { p -> p.playSound(p, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f) }
                    }

                    24 -> broadcast("$prefix §dViel Erfolg!")
                    26 -> {
                        onlinePlayers.forEach { op ->
                            op.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 20, 1, true))
                            op.playSound(entity.location, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f)
                        }
                        //TODO Timer integration
                    }
                }
            }

            counter++
            return@listen
        }

        if (alreadyTraded.contains(entity)) {
            player.sendMessage(prefix + msg("event.worldPeace.alreadyTraded"))
            return@listen
        }
        if (untradeable.contains(entity)) {
            player.sendMessage(prefix + msg("event.worldPeace.noTrade"))
            return@listen
        }

        val message = when (Random.nextInt(0, 3)) {
            0 -> msg("event.worldPeace.msg1")
            1 -> msg("event.worldPeace.msg2")
            2 -> msg("event.worldPeace.msg3")
            else -> msg("event.worldPeace.msg3")
        }
        player.sendMessage(prefix + message)

        if (!trades.containsKey(entity)) {
            if (Random.nextInt(0, 10) == 0) {
                untradeable.add(entity)
                player.sendMessage(prefix + msg("event.worldPeace.noTrade"))
                return@listen
            }
            val mat = when (WorldPeaceTrades.values().random()) {
                WorldPeaceTrades.FOOD_PLANTS -> foodPlants.random()
                WorldPeaceTrades.WATER_BOTTLES -> water.random()
                WorldPeaceTrades.SAPLINGS -> saplings.random()
                WorldPeaceTrades.FLOWERS -> flowers.random()
                WorldPeaceTrades.RESOURCES -> resources.random()
            }

            val amount = when (mat.maxStackSize) {
                in 1..6 -> Random.nextInt(1, 5)
                in 7..17 -> Random.nextInt(5, 22)
                else -> Random.nextInt(8, 50)
            }
            val item = BasicItem(mat, amount)
            trades[entity] = item
            sendRequest(player, entity)
            return@listen
        }

        val item = trades[entity] ?: return@listen
        if (item.invContains(player.inventory)) {
            item.removeItem(player.inventory)
            val drops = (entity as CraftLivingEntity).handle.randomLoot((player as CraftPlayer).handle)
            val amount = Random.nextInt(1, 5)
            val name = drops?.bukkitStack?.type?.name
            player.sendMessage(prefix + msg("event.worldPeace.trade", listOf("$amount", name ?: "Unknown")))
            player.inventory.addItem(org.bukkit.inventory.ItemStack(drops?.bukkitStack!!.type, amount))
            trades.remove(entity)
            val newEntity = entity.world.spawnEntity(entity.location, entity.type) as LivingEntity
            alreadyTraded.add(newEntity)
            entity.remove()
            return@listen
        }
        sendRequest(player, entity)
    }

    private fun net.minecraft.world.entity.LivingEntity.randomLoot(player: ServerPlayer): ItemStack? {
        val lootContext = LootContext.Builder(player.getLevel())
            .withRandom(RandomSource.create())
            .withParameter(LootContextParams.THIS_ENTITY, this)
            .withParameter(LootContextParams.ORIGIN, position())
            .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
            .withOptionalParameter(LootContextParams.KILLER_ENTITY, player)
            .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, player)
            .create(LootContextParamSets.ENTITY)

        return player.server.lootTables[lootTable].getRandomItems(lootContext).randomOrNull()
    }

    private fun sendRequest(player: Player, entity: LivingEntity) {
        val item = trades[entity] ?: return
        val drops = (entity as CraftLivingEntity).handle.randomLoot((player as CraftPlayer).handle)
        val drop = drops?.bukkitStack
        if (drop == null || drop.type.isAir) {
            trades.remove(entity)
            untradeable.add(entity)
            player.sendMessage(prefix + msg("event.worldPeace.noTrade"))
            return
        }
        player.sendMessage(prefix + msg("event.worldPeace.tradeOffer", listOf(item.getAmount().toString(), item.getMaterial().name)))
        player.sendMessage(prefix + msg("event.worldPeace.tradeOffer2", listOf(drop.type.name)))
    }
}