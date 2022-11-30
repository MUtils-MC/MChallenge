package de.miraculixx.mutils.modules.challenge.mods.force

import de.miraculixx.mutils.utils.enums.Challenge
import de.miraculixx.mutils.utils.enums.challenges.ForceChallenge
import de.miraculixx.mutils.challenge.modules.Challenge
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.text.msg
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.Material

class ForceCollect : Challenge {
    override val challenge = Challenge.FORCE_COLLECT
    private var forceObject: ForceObject? = null

    override fun start(): Boolean {
        val mat = ArrayList<String>()
        Material.values().forEach { material ->
            val name = material.name
            if (material.isItem
                && !name.contains("COMMAND_BLOCK")
                && !name.contains("CHORUS")
                && !name.endsWith("SPAWN_EGG")
                && !name.endsWith("HEAD")
                && !name.startsWith("END")
                && !name.startsWith("PURPUR")
                && !name.startsWith("STRUCTURE")
                && !name.contains("SHULKER")
            )
                mat.add(name)
        }
        mat.remove("SPAWNER")
        mat.remove("ELYTRA")
        mat.remove("JIGSAW")
        val c = ConfigManager.getConfig(Configs.MODULES)
        val cooldown = c.getInt("FORCE_COLLECT.Cooldown")
        forceObject = ForceObject(
            ForceChallenge.FORCE_COLLECT, mat,
            c.getInt("FORCE_COLLECT.MinSecs"),
            c.getInt("FORCE_COLLECT.MaxSecs"),
            cooldown,
            msg("modules.ch.force.item", pre = false)
        )
        taskRunLater(20L * cooldown) {
            forceObject?.startNewGoal()
        }

        return true
    }

    override fun stop() {
        forceObject?.stopChallenge()
        forceObject = null
    }

    override fun register() {
        forceObject?.continueChallenge()
    }

    override fun unregister() {
        forceObject?.pauseChallenge()
    }
}