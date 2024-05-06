package de.miraculixx.mchallenge.modules.challenges.interfaces

import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mcommons.text.*
import de.miraculixx.mweb.api.MWebAPI
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.event.ClickEvent
import java.io.File
import java.io.InputStream

interface RPCustom {
    val packMeta: String
        get() = "{\"pack\":{\"description\":\"MUtils-Challenge ResourcePack - AGPLv3 Licence\",\"pack_format\":13}}"
    val packIcon: InputStream?
        get() = javaClass.getResourceAsStream("/data/pack.png")
    val transparency: InputStream?
        get() = javaClass.getResourceAsStream("/data/transparent.png")


    fun getMWebAPI(audience: Audience?): MWebAPI? {
        MWebAPI.INSTANCE?.let { return it }

        (audience ?: Audience.audience(onlinePlayers.filter { it.isOp }))
            .sendMessage(
                prefix + cmp("MWeb is needed to play this Challenge! Please install it ", cError) + cmp("here", cError, underlined = true)
                    .clickEvent(ClickEvent.openUrl("https://modrinth.com/project/mweb"))
                    .addHover(cmp("Click to open"))
            )
        return null
    }

    fun createRPStructure(root: File): File {
        if (root.exists()) root.deleteRecursively()
        root.mkdirs()
        File(root, "pack.mcmeta").writeText(packMeta)
        packIcon?.readBytes()?.let { File(root, "pack.png").writeBytes(it) }
        val ressourceFolder = File(root, "assets/minecraft")
        ressourceFolder.mkdirs()
        File(ressourceFolder, "blockstates").mkdir()
        File(ressourceFolder, "textures").mkdir()
        File(ressourceFolder, "models").mkdir()
        return ressourceFolder
    }
}