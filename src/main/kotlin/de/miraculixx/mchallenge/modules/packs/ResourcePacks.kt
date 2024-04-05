package de.miraculixx.mchallenge.modules.packs

import kotlinx.coroutines.runBlocking
import java.util.*

enum class ResourcePacks(val versionData: ModrinthVersion?, val uuid: UUID) {
    CLEAR_BOSSBAR(runBlocking { getModrinthVersion("4g3ZPiy5") }, UUID.randomUUID()),

    ;
}