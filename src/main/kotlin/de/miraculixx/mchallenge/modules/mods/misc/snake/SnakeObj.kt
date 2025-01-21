package de.miraculixx.mchallenge.modules.mods.misc.snake

import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.challenges.getSetting
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player

class SnakeObj(private val player: Player) {
    private var delay: Int
    private var blockList = HashMap<Block, Int>()
    private var replaceBlocks = HashMap<Block, Material>()
    private var active = true

    init {
        val settings = challenges.getSetting(Challenges.SNAKE).settings
        delay = settings["speed"]?.toInt()?.getValue() ?: 1

        //Erster Block
        val loc = Location(player.location.world, player.location.blockX.toDouble(), player.location.blockY - 1.0, player.location.blockZ.toDouble())
        val block = loc.block
        val material = block.type
        block.type = Material.GREEN_GLAZED_TERRACOTTA
        blockList[block] = 0
        replaceBlocks[block] = material
        blockPlacer()
    }

    fun stop() {
        active = false
    }

    fun addBlock(block: Block) {
        val material = block.type
        block.type = Material.RED_GLAZED_TERRACOTTA
        blockList[block] = 5
        replaceBlocks[block] = material
    }

    private fun blockPlacer() {
        task(true, 20L / delay, 20L / delay) {
            val length = 10
            if (!active) {
                it.cancel()
                return@task
            }

            var loc: Location? = null
            val list = ArrayList<Block>()
            //Up count
            val count: Int = length + player.level / 2
            blockList.forEach { (block: Block, counter: Int) ->
                if (counter >= count) {
                    list.add(block)
                }
                if (counter == 0) {
                    loc = (block.location)
                    block.type = Material.RED_GLAZED_TERRACOTTA
                }
                blockList.replace(block, counter + 1)
            }

            for (block in list) {
                if (block.world !== player.world) {
                    //Dimensions wechsel
                    blockList.forEach { (block1: Block, _: Int?) -> block1.type = replaceBlocks[block1]!! }
                    blockList.clear()
                    replaceBlocks.clear()

                    //Erster Block
                    val loc1: Location = player.location.clone().subtract(0.0, 1.0, 0.0)
                    val block1 = loc1.block
                    val material = block1.type
                    block1.type = Material.GREEN_GLAZED_TERRACOTTA
                    blockList[block1] = 0
                    replaceBlocks[block1] = material
                }

                block.world.getBlockAt(block.location).type = replaceBlocks[block]!!
                replaceBlocks.remove(block)
                blockList.remove(block)
            }
            if (loc != null) placeBlocks(loc)
        }
    }

    private fun placeBlocks(loc: Location) {
        if (loc.world == null) return
        val locOriginal = loc.clone()
        //Move nach vorne
        //0 aufwärts  (South -> West -> North -> East) (0 -> 90 -> 180 -> 270)
        //90 each     (316 - 45 | 46 - 135 | 136 - 225 | 226 - 315)
        val yaw: Float = player.location.yaw
        if (yaw < 0) {
            if (yaw >= -45 || yaw <= -315) loc.add(0.0, 0.0, 1.0)
            if (yaw >= -314 && yaw <= -226) loc.add(-1.0, 0.0, 0.0)
            if (yaw >= -225 && yaw <= -135) loc.add(0.0, 0.0, -1.0)
            if (yaw >= -134 && yaw <= -46) loc.add(1.0, 0.0, 0.0)
        } else {
            if (yaw >= 316 || yaw <= 45) loc.add(0.0, 0.0, 1.0)
            if (yaw in 46.0..135.0) loc.add(-1.0, 0.0, 0.0)
            if (yaw in 136.0..225.0) loc.add(0.0, 0.0, -1.0)
            if (yaw in 226.0..315.0) loc.add(1.0, 0.0, 0.0)
        }

        //Checke, ob Block 1 drüber Luft ist
        var block = loc.block
        val blockUP = locOriginal.clone().add(0.0, 1.0, 0.0).block
        if (block.type.name.endsWith("AIR") || block.type.name.contains("GLAZED_TERRACOTTA")) {
            block = loc.clone().subtract(0.0, 1.0, 0.0).block //1 unter letzten Block
        } else if (!blockUP.type.name.endsWith("AIR")) {
            block = locOriginal.clone().add(0.0, 1.0, 0.0).block //1 über letzten Block
        }

        //Wenn Snake Block, dann Tot
        if (block.type == Material.RED_GLAZED_TERRACOTTA) {
            player.addScoreboardTag("SnakeDeath")
            player.damage(999.0)
            active = false
        }

        //Setzte Snake Block
        val material = block.type
        block.type = Material.GREEN_GLAZED_TERRACOTTA
        blockList[block] = 0
        replaceBlocks[block] = material

        //Player check
        if (player.isSleeping) {
            player.addScoreboardTag("Invincible")
        } else if (player.isOnGround && !player.scoreboardTags.contains("Invincible") &&
            !player.location.clone().subtract(0.0, 1.0, 0.0).block.type.name.contains("GLAZED_TERRACOTTA") &&
            !player.location.clone().subtract(0.0, 1.0, 0.0).block.type.name.endsWith("AIR")
        ) {
            player.addScoreboardTag("SnakeLeave")
            player.damage(999.0)
            active = false
        }
        if (!player.isSleeping) {
            player.removeScoreboardTag("Invincible")
        }
    }
}