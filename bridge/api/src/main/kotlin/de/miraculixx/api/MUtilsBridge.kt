package de.miraculixx.api

import java.io.File
import java.util.*

abstract class MUtilsBridge {
    companion object {
        var INSTANCE: MUtilsBridge? = null
    }

    abstract fun getAccountStatus(): Boolean

    abstract suspend fun versionCheck(moduleVersion: Int, moduleName: String): Boolean

    abstract suspend fun fileUpdate(destination: File, moduleName: String)

    abstract suspend fun login(onFail: () -> Unit)

    abstract suspend fun activate(): Boolean

    abstract suspend fun getCOTM(): String

    abstract suspend fun sendData(t: String, s: String): Boolean

    abstract suspend fun receiveData(t: String): String

    abstract suspend fun likeData(t: String, id: String): Boolean

    abstract fun saveData(key: String? = null, uuid: UUID? = null, autoUpdate: Boolean? = null)
}