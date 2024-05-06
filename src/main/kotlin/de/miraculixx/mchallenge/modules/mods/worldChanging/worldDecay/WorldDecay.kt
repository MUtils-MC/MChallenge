package de.miraculixx.mchallenge.modules.mods.worldChanging.worldDecay

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mchallenge.MChallenge
import de.miraculixx.mchallenge.PluginManager
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.modules.challenges.interfaces.RPCustom
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.challenges.getSetting
import de.miraculixx.mcommons.text.*
import de.miraculixx.mweb.api.MWebAPI
import net.kyori.adventure.audience.Audience
import org.bukkit.Material
import java.io.File

class WorldDecay: Challenge, RPCustom {
    private val visibleBlocks: MutableSet<Material> = Material.values().filter { it.isBlock }.toMutableSet()
    private val repeats: Int
    private val blocksPerInterval: Int
    private val delay: Int

    private lateinit var mWebAPI: MWebAPI
    private val groundBlocks = setOf(
        Material.STONE,
        Material.DIRT,
        Material.GRASS_BLOCK,
        Material.SAND,
        Material.RED_SAND,
        Material.DEEPSLATE,
        Material.NETHERRACK
    )

    private val texturepackFolder = File(MChallenge.configFolder, "data/resourcepacks/GLOBAL")
    private val resourceFolder = createRPStructure(texturepackFolder)
    private val blockstateFolder = File(resourceFolder, "blockstates")
    private val blockstateScript = "{\"variants\":{\"\":{\"model\":\"block/transparent_all\"}}}"

    init {
        val settings = challenges.getSetting(Challenges.WORLD_DECAY).settings
        repeats = settings["steps"]?.toInt()?.getValue() ?: 10
        delay = settings["delay"]?.toInt()?.getValue() ?: 60
        blocksPerInterval = visibleBlocks.size / repeats

        val blockModelFolder = File(resourceFolder, "models/block")
        blockModelFolder.mkdir()
        File(blockModelFolder, "transparent_all.json").writeText("{\"parent\":\"block/transparent\",\"textures\":{\"particle\":\"#all\",\"down\":\"#all\",\"up\":\"#all\",\"north\":\"#all\",\"east\":\"#all\",\"south\":\"#all\",\"west\":\"#all\"}}")
        File(blockModelFolder, "transparent.json").writeText("{\"parent\":\"block/block\",\"textures\":{\"all\":\"block/transparent\"}}")
        File(resourceFolder, "textures/block").mkdir()
        transparency?.readBytes()?.let { File(resourceFolder, "textures/block/transparent.png").writeBytes(it) }
        visibleBlocks.remove(Material.LAVA)
        visibleBlocks.remove(Material.BEDROCK)
    }

    override fun register() {
        paused = false
    }

    override fun unregister() {
        paused = true
    }

    override fun start(): Boolean {
        if (!PluginManager.server.pluginManager.isPluginEnabled("MUtils-Web")) return error(Audience.audience(onlinePlayers + consoleAudience))
        MWebAPI.INSTANCE?.let { mWebAPI = it } ?: error(Audience.audience(onlinePlayers + consoleAudience))
        startScheduler()
        return true
    }

    override fun stop() {
        stopped = true
    }


    private var paused = false
    private var stopped = false
    private fun startScheduler() {
        var finishedRepeats = 0
        var percentTillFinish = 0.0
        task(false, 0, 20L * delay) {
            if (stopped) it.cancel()
            if (paused) return@task
            if (visibleBlocks.isEmpty()) return@task
            val blockSet =  if (percentTillFinish > 0.5) {
                visibleBlocks
            } else {
                val new = visibleBlocks.toMutableSet()
                new.removeAll(groundBlocks)
                new
            }
            repeat(blocksPerInterval) {
                if (blockSet.isEmpty()) return@repeat
                val block = blockSet.random()
                val name = block.name.lowercase()
                if (block == Material.WATER) {
                    val transparency = transparency?.readBytes() ?: return@repeat
                    File(resourceFolder, "textures/block/water_flow.png").writeBytes(transparency)
                    File(resourceFolder, "textures/block/water_still.png").writeBytes(transparency)
                    File(resourceFolder, "textures/block/water_overlay.png").writeBytes(transparency)
                } else File(blockstateFolder, "${name}.json").writeText(blockstateScript)
                visibleBlocks.remove(block)
            }
            mWebAPI.sendFileAsResourcePack(texturepackFolder.path, onlinePlayers.map { it.uniqueId }.toSet(), true)
            finishedRepeats++
            percentTillFinish = finishedRepeats.toDouble() / repeats
        }
    }
}