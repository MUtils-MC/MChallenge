package de.miraculixx.mchallenge.modules.mods.misc.rhythm

import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mcommons.text.cError
import de.miraculixx.mcommons.text.cmp
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

class RhythmBar(private val speed: Int) {
    private val emptyBar = BossBar.bossBar(cmp(" "), 1f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS)
    private val bossBar = BossBar.bossBar(cmp("Loading...", cError), 1f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS)

    private val charBar = "\uE001"
    private val charBarPressed = "\uE006"
    private val charNegativ = "\uE003"
    private val charStick = "\uE002"
    private val charEmpty = " "
    private val charCenter = "\uE005"

    private var isPressed = 0

    fun show(player: Player) {
        emptyBar.addViewer(player)
        bossBar.addViewer(player)
    }

    fun hide(player: Player) {
        emptyBar.removeViewer(player)
        bossBar.removeViewer(player)
    }

    fun press() {
       isPressed = 2
    }

    private var spaces = 0

    private val task = task(false, 0, 1) {
        if (spaces <= 0) spaces = speed else spaces--
//        onlinePlayers.forEach { p -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 0.5f, (spaces.toFloat() / speed) * 2.0f) }

        val textRight = buildString {
            repeat(spaces) { append(charEmpty) }
            append(charStick)
            repeat(8) {
                repeat(speed) { append(charEmpty) }
                append(charStick)
            }
            repeat(speed - spaces) { append(charEmpty) }
        }
        val textLeft = textRight.reversed()

        bossBar.name(cmp((if (isPressed > 0) charBarPressed else charBar) + charNegativ + textLeft + charCenter + textRight, NamedTextColor.WHITE))
        if (isPressed > 0) isPressed--
    }
}