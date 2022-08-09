package de.miraculixx.mutils.modules.challenge.mods.worldPeace

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.enums.modules.challenges.ChallengeStatus
import de.miraculixx.mutils.modules.ModuleManager
import de.miraculixx.mutils.modules.challenge.Challenge
import de.miraculixx.mutils.modules.challenge.ChallengeManager
import de.miraculixx.mutils.modules.challenge.utils.BasicItem
import de.miraculixx.mutils.modules.challenges
import de.miraculixx.mutils.utils.broadcastSound
import de.miraculixx.mutils.utils.msg
import de.miraculixx.mutils.utils.prefix
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.runnables.task
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftLivingEntity
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer
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
    override val challenge = Modules.WORLD_PEACE
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
        it.entity.getNearbyEntities(1.0,15.0,15.0).forEach { e ->
            if (e is Player) nearby = true
        }
        if (nearby) {
            broadcast("$prefix Das Mob §9${it.entity.type.name}§7 ist gestorben :(")
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
            val counter = 0
            task(true, 10, 10) {
                if (counter >= 20) {
                    entity.phase = EnderDragon.Phase.LAND_ON_PORTAL

                }
                when (counter) {
                    0 -> {
                        broadcast("\n$prefix §dIhr habt es bis zum Ende geschafft ohne einmal jemandem Schaden zu zu fügen...")
                        broadcastSound(Sound.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.MASTER, 1f, 1f)
                    }
                    2 -> broadcast("$prefix §dRespekt...")
                    20 -> {
                        broadcast("$prefix §dNun... Nehmt mein letztes Geschenk")
                        broadcastSound(Sound.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.MASTER, 1f, 1f)
                    }
                    24 -> broadcast("$prefix §dViel Erfolg!")
                    26 -> {
                        onlinePlayers.forEach { op ->
                            op.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 20, 1, true))
                            op.playSound(entity.location, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f)
                        }
                        ModuleManager.setTimerStatus(false)
                        val manager = ChallengeManager()
                        manager.stopChallenges(ModuleManager.getChallenges())
                        challenges = ChallengeStatus.PAUSED
                        broadcast("\n§3§l§m======================\n§6§l- Die Challenge wurde erfolgreich bestanden!")
                        broadcast(msg("modules.timer.playtime", input = ModuleManager.getTime()) + "§3§l§m======================")
                    }
                }
            }

            return@listen
        }

        if (alreadyTraded.contains(entity)) {
            player.sendMessage("\n$prefix Du hast bereits mit mir getraded :) Vielen Dank dafür!")
            return@listen
        }
        if (untradeable.contains(entity)) {
            player.sendMessage("\n$prefix Aktuell kann ich dir leider nichts bieten, aber trotzdem schön das du zurück bist :)")
            return@listen
        }

        val message = when (Random.nextInt(0, 3)) {
            0 -> "\n$prefix Hey ${player.name}! Bitte tu mir nicht weh :("
            1 -> "\n$prefix Lass heute mal nicht mit einander kämpfen!"
            2 -> "\n$prefix Wollen wir nicht lieber handeln anstelle von Kämpfen?"
            else -> "\n$prefix Wollen wir nicht lieber handeln anstelle von Kämpfen?"
        }
        player.sendMessage(message)

        if (!trades.containsKey(entity)) {
            if (Random.nextInt(0, 10) == 0) {
                untradeable.add(entity)
                player.sendMessage("$prefix Aktuell kann ich dir leider nichts bieten...")
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
            player.sendMessage("$prefix Vielen Dank für deine tollen Items! Gerne gebe ich dir meine §9$amount $name")
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
            .withRandom(java.util.Random())
            .withParameter(LootContextParams.THIS_ENTITY, this)
            .withParameter(LootContextParams.ORIGIN, position())
            .withParameter(LootContextParams.DAMAGE_SOURCE, DamageSource.GENERIC)
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
            player.sendMessage("$prefix Aktuell kann ich dir leider nichts bieten...")
            return
        }
        player.sendMessage("$prefix Könntest du mir §9${item.getAmount()} ${item.getMaterial().name}§7 bringen?")
        player.sendMessage("$prefix Als Gegenzug würde ich dir auch ein paar §9${drop.type.name}§7 geben!")
    }
}