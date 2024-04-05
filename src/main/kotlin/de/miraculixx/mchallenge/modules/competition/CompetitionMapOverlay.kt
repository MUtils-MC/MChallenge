package de.miraculixx.mchallenge.modules.competition

import org.bukkit.entity.Player
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import java.awt.Image

class CompetitionMapOverlay(var image: Image): MapRenderer() {
    override fun render(map: MapView, canvas: MapCanvas, player: Player) {
        canvas.drawImage(0, 0, image)
    }
}