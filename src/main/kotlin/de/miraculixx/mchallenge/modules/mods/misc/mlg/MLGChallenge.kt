package de.miraculixx.mchallenge.modules.mods.misc.mlg

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.kpaper.runnables.taskRunLater
import de.miraculixx.mchallenge.commands.utils.ExperimentalFeatureCommand
import de.miraculixx.mchallenge.modules.ChallengeManager
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.challenges.getSetting
import de.miraculixx.mchallenge.modules.global.DeathListener
import de.miraculixx.mcommons.extensions.title
import de.miraculixx.mcommons.text.cMark
import de.miraculixx.mcommons.text.cmp
import de.miraculixx.mcommons.text.emptyComponent
import de.miraculixx.mcommons.text.prefix
import org.bukkit.*
import org.bukkit.entity.*
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random

class MLGChallenge : Challenge {
    private val delayTime: IntRange
    private val mlgHeights: IntRange
    private val mlgTypes: Set<MLGType>

    private val surfaceBlocks = setOf(
        Material.OXIDIZED_CUT_COPPER, Material.EMERALD_BLOCK, Material.MYCELIUM,
        Material.PODZOL, Material.CHISELED_STONE_BRICKS, Material.BRICKS,
        Material.HONEYCOMB_BLOCK, Material.SMOOTH_STONE, Material.DARK_PRISMARINE,
        Material.REINFORCED_DEEPSLATE
    )
    private val surfaceNetherBlocks = setOf(
        Material.POLISHED_BLACKSTONE, Material.CRIMSON_NYLIUM, Material.BASALT,
        Material.SHROOMLIGHT, Material.WARPED_NYLIUM, Material.NETHER_BRICKS,
    )
    private val surfaceEndBlocks = setOf(
        Material.END_STONE_BRICKS, Material.PURPUR_BLOCK, Material.OBSIDIAN
    )

    private val playerInventory = mutableMapOf<Player, Array<ItemStack?>>()
    private val playerLocation = mutableMapOf<Player, Location>()
    private val mlgWorlds = mutableListOf<World>()
    private var running = false
    private var mlgActive = false


    init {
        val settings = challenges.getSetting(Challenges.MLG).settings
        val delaySection = settings["delay"]?.toSection()?.getValue()
        delayTime = (delaySection?.get("minDelay")?.toInt()?.getValue() ?: (60 * 2))..(delaySection?.get("maxDelay")?.toInt()?.getValue() ?: (60 * 4))

        val heightSelection = settings["height"]?.toSection()?.getValue()
        mlgHeights = (heightSelection?.get("minHeight")?.toInt()?.getValue() ?: 50)..(heightSelection?.get("maxHeight")?.toInt()?.getValue() ?: 100)

        mlgTypes = if (settings["hardMLGs"]?.toBool()?.getValue() == true) MLGType.entries.toSet()
        else MLGType.entries.filter { !it.hard }.toSet()
    }

    override fun start(): Boolean {
        if (!ExperimentalFeatureCommand.enabledFeatures.contains("update_1_21")) {
            broadcast(prefix, "event.experimental.missing", listOf("update_1_21"))
            task?.cancel()
            return false
        }
        onMove.register()
        onDeath.register()
        return true
    }

    override fun register() {
        running = true
    }

    override fun unregister() {
        running = false
    }

    override fun stop() {
        task?.cancel()
        onMove.unregister()
        onDeath.unregister()
        cleanUpMLG(false)
    }

    private val checkMLGList = mutableSetOf<Player>()
    private val onMove = listen<PlayerMoveEvent>(register = false) {
        val player = it.player
        if (!mlgActive || player.isDead || player.gameMode != GameMode.SURVIVAL) return@listen
        if (checkMLGList.contains(player)) return@listen
        if (it.to.blockY in -63..-61) {
            checkMLGList.add(player)
            taskRunLater(5) {
                if (player.isDead) {
                    checkMLGList.remove(player)
                    return@taskRunLater
                }
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1f, 1.3f)
            }
            taskRunLater(10) {
                checkMLGList.remove(player)
                if (player.isDead) return@taskRunLater
                player.teleportAsync(playerLocation.remove(player) ?: return@taskRunLater)
                player.inventory.contents = playerInventory[player] ?: emptyArray()
                player.persistentDataContainer.remove(DeathListener.key)

                // Finish this MLG drop
                taskRunLater(20 * 5) { cleanUpMLG(true) }
            }
        }
    }

    private val onDeath = listen<PlayerDeathEvent>(register = false) {
        val player = it.player
        player.respawnLocation = playerLocation.remove(player)
        if (player.persistentDataContainer.has(DeathListener.key)) {
            taskRunLater(20 * 5) { cleanUpMLG(true) }
        }
    }

    private fun cleanUpMLG(check: Boolean) {
        if (check && !mlgActive) return
        mlgActive = false
        playerLocation.forEach { (player, location) ->
            player.teleport(location)
            player.inventory.contents = playerInventory[player] ?: emptyArray()
            player.persistentDataContainer.remove(DeathListener.key)
        }
        playerLocation.clear()
        playerInventory.clear()
        if (check && !onlinePlayers.any { it.gameMode == GameMode.SURVIVAL }) {
            ChallengeManager.stopChallenges()
        }
        mlgWorlds.forEach { world ->
            Bukkit.unloadWorld(world, false)
            world.worldFolder.deleteRecursively()
        }
        mlgWorlds.clear()
    }

    private var tillNext = delayTime.random()
    private var nextMLG = MLGType.WATER_BUCKET
    private val task = task(true, 0, 20) {
        if (!running) return@task
        println(tillNext)

        if (tillNext <= 0) {
            tillNext = delayTime.random()
            // MLG
            val worlds = onlinePlayers.map { player ->
                val randomBlock = when (player.world.environment) {
                    World.Environment.NORMAL -> surfaceBlocks.random()
                    World.Environment.NETHER -> surfaceNetherBlocks.random()
                    World.Environment.THE_END -> surfaceEndBlocks.random()
                    else -> Material.GRASS_BLOCK
                }
                val world = WorldCreator("MLG-World-${player.name}")
                    .environment(World.Environment.NORMAL)
                    .generateStructures(false)
                    .type(WorldType.FLAT)
                    .generatorSettings("{\"layers\": [{\"block\": \"bedrock\", \"height\": 1}, {\"block\": \"${randomBlock.name.lowercase()}\", \"height\": 1}], \"biome\":\"the_void\"}")
                    .createWorld()
                world?.let {
                    mlgWorlds.add(it)
                    it.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true)
                }
                world to player
            }

            val mlgItem = when (nextMLG) {
                MLGType.WATER_BUCKET -> ItemStack(Material.WATER_BUCKET)
                MLGType.COBWEB -> ItemStack(Material.COBWEB, 2)
                MLGType.SLIME -> ItemStack(Material.SLIME_BLOCK)
                MLGType.ENDER_PEARL -> ItemStack(Material.ENDER_PEARL)
                MLGType.HORSE -> ItemStack(Material.AIR)
                MLGType.PIG -> ItemStack(Material.AIR)
                MLGType.VINE_LADDER -> ItemStack(if (Random.nextBoolean()) Material.VINE else Material.LADDER)
                MLGType.BOAT -> ItemStack(Material.OAK_BOAT)
                MLGType.MINECART -> ItemStack(Material.MINECART)
                MLGType.TWISTING_VINE -> ItemStack(Material.TWISTING_VINES, 2)
                MLGType.WINDCHARGE -> ItemStack(Material.WIND_CHARGE)
                MLGType.MACE -> ItemStack(Material.MACE)
            }
            val randomHeight = mlgHeights.random()
            worlds.forEach { (world, player) ->
                world ?: return@forEach

                when (nextMLG) {
                    MLGType.HORSE -> {
                        world.spawn(Location(world, 0.0, -62.0, 0.0), Horse::class.java).apply {
                            setAdult()
                            setAI(false)
                            isGlowing = true
                        }
                    }

                    MLGType.PIG -> {
                        world.spawn(Location(world, 0.0, -62.0, 0.0), Pig::class.java).apply {
                            setAdult()
                            setAI(false)
                            setSaddle(true)
                            isGlowing = true
                        }
                    }

                    MLGType.VINE_LADDER -> {
                        world.getBlockAt(0, -62, 1).type = Material.GOLD_BLOCK
                        world.getBlockAt(0, -61, 1).type = Material.GOLD_BLOCK
                    }

                    MLGType.MINECART -> world.getBlockAt(0, -62, 0).type = Material.RAIL
                    MLGType.MACE -> {
                        val set = setOf(EntityType.PIG, EntityType.SILVERFISH, EntityType.IRON_GOLEM, EntityType.CHICKEN, EntityType.FROG)
                        val entity = world.spawnEntity(Location(world, 0.0, -62.0, 0.0), set.random()) as LivingEntity
                        entity.setAI(false)
                        entity.isGlowing = true
                    }

                    else -> Unit
                }

                playerLocation[player] = player.location
                player.teleportAsync(Location(world, 0.5, randomHeight.toDouble() - 64.0, 0.5, 0f, 90f))
                player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
                playerInventory[player] = player.inventory.contents
                player.inventory.clear()
                player.inventory.setItem(4, mlgItem)
                player.inventory.heldItemSlot = 4
                player.persistentDataContainer.set(DeathListener.key, PersistentDataType.STRING, "mlg")
                player.setGravity(false)
                player.removePotionEffect(PotionEffectType.SLOW_FALLING)
            }
            mlgActive = true
            taskRunLater(30) {
                onlinePlayers.forEach { p ->
                    p.setGravity(true)
                    p.clearTitle()
                }
            }

        } else if (tillNext == 1) {
            nextMLG = MLGType.entries.random()
            onlinePlayers.forEach { player ->
                player.playSound(player, Sound.ENTITY_PUFFER_FISH_BLOW_UP, 1f, 1.3f)
                player.title(emptyComponent(), cmp("MLG ${nextMLG.name.replace('_', ' ')}", cMark))
            }
            tillNext--
        } else tillNext--
    }
}