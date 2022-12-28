package de.miraculixx.mutils.api

import de.miraculixx.mutils.messages.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.io.File
import java.net.URL
import java.util.*
import kotlin.time.Duration.Companion.hours

class MUtilsAPI(
    private val moduleName: String,
    private val moduleVersion: Int,
    dataFolder: File,
    serverIP: String?,
) {
    private val client = HttpClient(CIO)
    private val accountData: AccountData
    private var accountConnected = false
    private val configFile: File
    private val noConnection = prefix + cmp(
        "API - Failed to communicate with MUtils-API! Please check $moduleName version or your firewall. " +
                "Free server hoster tend to block any outgoing communication, please consider switching to PloudOS in this case.", cError
    )

    fun getAccountStatus(): Boolean {
        return accountConnected
    }

    /**
     * @return false if plugin is too outdated to be used or if no connection could be established to MUtils API
     */
    suspend fun versionCheck(): Boolean {
        val versions = serialize<ModuleVersion>(get("https://api.mutils.de/public/version/?$moduleName")) ?: return false
        val outdated = cmp("Latest Version: ") + cmp(versions.latest.toString(), cSuccess) + cmp(" - Installed Version: ") + cmp(moduleVersion.toString(), cError)
        if (moduleVersion < versions.last) {
            consoleAudience.sendMessage(prefix + cmp("You are running a too outdated version of $moduleName! An update is required due to security updates or internal changes.", cError))
            consoleAudience.sendMessage(prefix + outdated)
            return false
        }
        if (moduleVersion < versions.latest) {
            consoleAudience.sendMessage(prefix + cmp("You are running an outdated version of $moduleName!"))
            consoleAudience.sendMessage(prefix + outdated)
        }
        return true
    }

    /**
     * **ONLY PAPER BUILDS SUPPORTET ATM**
     */
    suspend fun modrinthUpdate(projectID: String, fileName: String) {
        if (!accountData.autoUpdate) return
        val response = get("https://api.modrinth.com/v2/project/$projectID/version?loaders=[%22paper%22]")
        val modrinthVersions = serialize<List<ModrinthVersion>>(response)
        val latestVersion = modrinthVersions?.firstOrNull()
        val file = latestVersion?.files?.firstOrNull()
        if (latestVersion == null || file == null) {
            consoleAudience.sendMessage(prefix + cmp("Failed to load latest $moduleName version! Modrinth responses with an unexpected data block", cError))
            return
        }

        consoleAudience.sendMessage(
            prefix + cmp("Downloading latest $moduleName version...\n") +
                    prefix + cmp("  |-> Project ID: ") + cmp("${latestVersion.project_id}\n", cSuccess) +
                    prefix + cmp("  |-> Version: ") + cmp("${latestVersion.name} (${latestVersion.version_number})\n", cSuccess) +
                    prefix + cmp("  |-> File: ") + cmp("${file.filename} (${file.size / 1024}kb)")
        )
        val updateFolder = File("plugins/update")
        if (!updateFolder.exists()) updateFolder.mkdirs()
        val fileData = getFile(file.url)
        val targetFile = File("${updateFolder.path}/$fileName")
        if (fileData != null) {
            targetFile.writeBytes(fileData)
            consoleAudience.sendMessage(prefix + cmp("Latest version successfully downloaded! Restart your server to take effect of it"))
        } else consoleAudience.sendMessage(prefix + cmp("Download failed! Unexpected response: null", cError))
    }

    /**
     * Login in a server instance
     */
    suspend fun login(key: String, ip: String, uuid: UUID) {
        proceedLogin("key=$key&ip=$ip&uuid=$uuid")
    }

    /**
     * Login in a client instance
     */
    suspend fun login(key: String, uuid: UUID) {
        proceedLogin("key=$key&uuid=$uuid")
    }

    /**
     * Activate an account slot on this server/client
     */
    suspend fun activate(key: String, ip: String, uuid: UUID): Boolean {
        val response = get("https://api.mutils.de/public/activate/?key=$key&ip=$ip&uuid=$uuid")
        val loginResponse = serialize<LoginResponse>(response)
        if (loginResponse == null) {
            consoleAudience.sendMessage(noConnection)
            return false
        } else if (!loginResponse.success) {
            when (loginResponse.protocol) {
                0 -> consoleAudience.sendMessage(prefix + cmp("API - Unknown error occurred while connecting your MUtils account", cError))
                1 -> consoleAudience.sendMessage(prefix + cmp("API - This server/account is already connected to your MUtils account", cError))
                2 -> {
                    consoleAudience.sendMessage(
                        prefix + cmp("API - Your MUtils account reached the maximum amount of simultaneous connections! Consider updating your plan on ") +
                                cmp("https://mutils.de/shop", cMark)
                    )
                    consoleAudience.sendMessage(prefix + cmp("API - Slots used: ") + cmp(loginResponse.slots.toString(), cMark))
                    applyData(key, uuid)
                    accountConnected = false
                }
                3 -> {
                    consoleAudience.sendMessage(prefix + cmp("API - Your entered key ($key) is invalid! Join https://dc.mutils.de to receive your key or ask for help"))
                    applyData(key, uuid)
                    accountConnected = false
                }
            }
            return false
        } else {
            consoleAudience.sendMessage(prefix + cmp("API - Successfully connected your MUtils account to this server/account! Please restart to fully enable all features", cSuccess))
            consoleAudience.sendMessage(prefix + cmp("API - Slots left: ") + cmp(loginResponse.slots.toString(), cMark))
            applyData(key, uuid)
            accountConnected = true
            return true
        }
    }

    /**
     * Receive the challenge of the month
     */
    suspend fun getCOTM(): String {
        return serialize<String>(get("https://api.mutils.de/public/monthlychallenge")) ?: ""
    }


    /*
     * Utilities
     */
    private suspend fun get(destination: String): String {
        return try {
            val url = URL(destination)
            val response: HttpResponse = client.get(url)
            response.bodyAsText()
        } catch (e: Exception) {
            if (debug) consoleAudience.sendMessage(noConnection)
            return ""
        }
    }

    private suspend fun getFile(destination: String): ByteArray? {
        return try {
            val url = URL(destination)
            val response: HttpResponse = client.get(url)
            response.body() as ByteArray
        } catch (e: Exception) {
            consoleAudience.sendMessage(noConnection)
            return null
        }
    }

    private inline fun <reified T> serialize(input: String): T? {
        return try {
            json.decodeFromString(input)
        } catch (e: IllegalArgumentException) {
            consoleAudience.sendMessage(prefix + cmp("API - Failed to read api data! Please validate your java version", cError))
            null
        } catch (e: SerializationException) {
            consoleAudience.sendMessage(noConnection)
            null
        }
    }

    private suspend fun proceedLogin(url: String) {
        val loginData = serialize<LoginResponse>(get("https://api.mutils.de/public/login/?$url"))
        accountConnected = !(loginData == null || !loginData.success)
        if (accountConnected) {
            if (debug) consoleAudience.sendMessage(prefix + cmp("API - Heartbeat success", cSuccess))
            delay(2.hours)
            proceedLogin(url)
        } else if (debug) consoleAudience.sendMessage(prefix + cmp("API - Heartbeat failed! Disabling premium features and abort heartbeats", cError))
    }

    private suspend fun applyData(key: String? = null, uuid: UUID? = null, autoUpdate: Boolean? = null) = coroutineScope {
        key?.let { accountData.key = it }
        autoUpdate?.let { accountData.autoUpdate = it }
        uuid?.let { accountData.uuid = it }
        configFile.writeText(jsonCompact.encodeToString(accountData))
    }

    init {
        if (!dataFolder.exists()) dataFolder.mkdirs()
        configFile = File("${dataFolder.path}/account.json")
        accountData = if (!configFile.exists()) AccountData()
        else serialize<AccountData>(configFile.readText()) ?: AccountData()

        CoroutineScope(Dispatchers.Default).launch {
            if (accountData.key.isNotBlank()) {
                if (serverIP != null) proceedLogin("key=${accountData.key}&ip=$serverIP&uuid=${accountData.uuid}")
                else if (accountData.uuid != null) login(accountData.key, accountData.uuid!!)
                else if (debug) consoleAudience.sendMessage(prefix + cmp("API - Malformed credentials? UUID & IP is blanc"))
            }
        }
    }
}