package de.miraculixx.mchallenge.modules.competition

import de.miraculixx.kpaper.extensions.console
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.mcommons.text.cError
import de.miraculixx.mcommons.text.cmp
import de.miraculixx.mcommons.text.plus
import de.miraculixx.mcommons.text.prefix
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream
import java.net.URL
import java.util.*
import javax.imageio.ImageIO
import javax.swing.ImageIcon

class MapManager(configFolder: File) {
    private val staticBackgroundTasks = loadImage(javaClass.getResourceAsStream("/assets/competition-bg-tasks.jpg"))
    private val staticBackgroundLeader = loadImage(javaClass.getResourceAsStream("/assets/competition-bg-leader.jpg"))
    private val mcFont = Font.createFont(Font.TRUETYPE_FONT, javaClass.getResourceAsStream("/assets/minecraft-font-regular.otf"))
//    private val mcFont = Font.createFont(Font.TRUETYPE_FONT, File("plugins/MUtils/Challenges/data/minecraft-font-regular.ttf"))
    private val skinCacheFolder = File(configFolder, "/data/skins")

    private val playerMaps: MutableMap<UUID, CompetitionMapOverlay> = mutableMapOf()

    private fun createRender(data: CompetitionPlayerData?, rank: Int, top3: Map<UUID, CompetitionPlayerData?>): Image {
        val dynamicImage = BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB)
        val graphic = dynamicImage.createGraphics()
        val fontBig = mcFont.deriveFont(Font.PLAIN, 10f)
        val staticBackground = when (data?.mapView) {
            CompetitionMapView.PERSONAL_TASKS -> staticBackgroundTasks
            CompetitionMapView.LEADERBOARD -> staticBackgroundLeader
            else -> staticBackgroundTasks
        }
        graphic.drawImage(staticBackground, 0, 0, null)
        graphic.font = fontBig
        graphic.color = Color.WHITE
        graphic.drawString("Punkte: ${data?.points ?: 0}", 30, 19)
        graphic.drawString("Rank: ${rank.plus(1)}", 30, 31)

        when (data?.mapView) {
            CompetitionMapView.LEADERBOARD -> renderLeaderBoard(graphic, top3)
            CompetitionMapView.PERSONAL_TASKS -> renderTasks(graphic, data)
            else -> renderTasks(graphic, data)
        }
        return ImageIcon(dynamicImage).image
    }

    private fun renderTasks(graphic: Graphics2D, data: CompetitionPlayerData?) {
        val fontSmall = mcFont.deriveFont(Font.PLAIN, 10f)
        graphic.font = fontSmall
        graphic.drawString("Tasks", 10, 70)

        var counter = 0
        data?.remainingTasks?.forEach { (task, points) ->
            if (counter > 2) return@forEach
            graphic.drawString("- ${task.display} ($points)", 12, 82 + (10 * counter))
            counter++
        }
    }

    private fun renderLeaderBoard(graphic: Graphics2D, top3: Map<UUID, CompetitionPlayerData?>) {
        val fontSmall = mcFont.deriveFont(Font.PLAIN, 10f)
        graphic.font = fontSmall

        var rank = 1
        top3.forEach { (uuid, data) ->
            val file = File(skinCacheFolder, "$uuid.png")
            val skin = if (file.exists()) file.inputStream() else {
                val url = URL("https://mc-heads.net/avatar/$uuid/24")
                if (!skinCacheFolder.exists()) skinCacheFolder.mkdirs()
                File(skinCacheFolder, "$uuid.png").writeBytes(url.readBytes())
                url.openStream()
            }
            val skinFile = loadImage(skin)
            when (rank) {
                1 -> {
                    graphic.drawImage(skinFile, 52, 65, null)
                    graphic.drawString(buildScore(data?.points), 52 + 4, 65 + 24 + 10)
                }

                2 -> {
                    graphic.drawImage(skinFile, 23, 77, null)
                    graphic.drawString(buildScore(data?.points), 23 + 4, 77 + 24 + 10)
                }

                3 -> {
                    graphic.drawImage(skinFile, 81, 83, null)
                    graphic.drawString(buildScore(data?.points), 81 + 4, 83 + 24 + 10)
                }
            }
            rank++
        }
    }

    private fun buildScore(int: Int?): String {
        return when (int) {
            null -> "000"
            in -1..9 -> "00$int"
            in 10..99 -> "0$int"
            else -> "$int"
        }
    }

    fun getMapItem(uuid: UUID, top3: Map<UUID, CompetitionPlayerData>): ItemStack {
        return itemStack(Material.FILLED_MAP) {
            meta<MapMeta> {
                customModel = 1200
                mapView = Bukkit.createMap(worlds[0])
                val view = mapView ?: throw NullPointerException("Failed to resolve default map view!")

                view.renderers.forEach { r -> view.removeRenderer(r) }
                val overlay = CompetitionMapOverlay(createRender(null, 0, top3))
                view.addRenderer(overlay)
                playerMaps[uuid] = overlay
            }
        }
    }

    fun requestUpdate(uuid: UUID, playerData: CompetitionPlayerData, rank: Int, top3: Map<UUID, CompetitionPlayerData?>) {
        val overlay = playerMaps[uuid]
        if (overlay == null) {
            console.sendMessage(prefix + cmp("Failed to resolve map overlay from $uuid!", cError))
            return
        }
        overlay.image = createRender(playerData, rank, top3)
    }

    private fun loadImage(inputStream: InputStream?): BufferedImage? {
        return try {
            ImageIO.read(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}