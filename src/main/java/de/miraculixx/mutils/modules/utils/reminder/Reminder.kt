package de.miraculixx.mutils.modules.utils.reminder

import de.miraculixx.mutils.utils.text.msg
import net.axay.kspigot.runnables.task
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.Sound
import org.bukkit.entity.Player

class Reminder(private val duration: Long, private val message: String?, private val player: Player) {
    init {
        remind()
    }

    private fun remind() {
        taskRunLater(duration, false) {
            player.sendMessage(msg("command.reminder.remind"))
            if (message != null) player.sendMessage("§e$message")
            task(false, 0, 10, 5) {
                player.playSound(player.location, Sound.BLOCK_BELL_USE, 1f, 1f)
            }
        }
    }
}