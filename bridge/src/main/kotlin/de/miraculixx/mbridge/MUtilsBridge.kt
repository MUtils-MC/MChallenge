package de.miraculixx.mbridge

import de.miraculixx.mvanilla.messages.*
import kotlinx.coroutines.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.io.File
import java.net.InetAddress
import java.util.*
import kotlin.time.Duration.Companion.hours

class MUtilsBridge(
    private val platform: MUtilsPlatform,
    private val module: MUtilsModule,
    private val serverVersion: String,
    port: Int
) {
    private val serverIP = InetAddress.getLocalHost().hostAddress + ":" + port
    private val accountData: AccountData
    private var accountConnected = false
    private val configFile: File
    private val noConnection = prefix + cmp(
        "API - Failed to communicate with MUtils-API! Please check the MUtils version or your firewall. " +
                "Free server hoster tend to block any outgoing communication, please consider switching to PloudOS in this case.", cError
    )
    private var loginFailTasks: MutableList<() -> Unit> = mutableListOf()
    private var activeLoginJob = false
    private var serviceVersion = 0

    fun getAccountStatus(): Boolean {
        return accountConnected
    }

    /**
     * @return false if plugin is too outdated to be used or if no connection could be established to MUtils API
     */
    suspend fun versionCheck(moduleVersion: Int, destination: File): Boolean {
        serviceVersion = moduleVersion
        val versions = serialize<ModuleVersion>(WebClient.get("https://api.mutils.de/public/version", headers = mapOf("Service" to module.module)))
        if (versions == null) {
            consoleAudience.sendMessage(prefix + cmp("Could not check current version! Proceed at your own risk", cError))
            consoleAudience.sendMessage(noConnection)
            return true
        }
        val outdated = cmp("Latest Version: ") + cmp(versions.latest.toString(), cSuccess) + cmp(" - Installed Version: ") + cmp(moduleVersion.toString(), cError)
        if (moduleVersion < versions.last) {
            consoleAudience.sendMessage(prefix + cmp("You are running a too outdated version of ${module.module}! An update is required due to security reasons or internal changes.", cError))
            consoleAudience.sendMessage(prefix + outdated)
            if (accountData.autoUpdate) {
                consoleAudience.sendMessage(prefix + cmp("Auto update is active. Try to install the latest version..."))
                fileUpdate(destination)
            }
            return false
        }
        if (moduleVersion < versions.latest) {
            consoleAudience.sendMessage(prefix + cmp("You are running an outdated version of ${module.module}!"))
            consoleAudience.sendMessage(prefix + outdated)
            if (accountData.autoUpdate) {
                consoleAudience.sendMessage(prefix + cmp("Auto update is active. Try to install the latest version..."))
                fileUpdate(destination)
            }
        }
        if (moduleVersion > versions.latest) {
            consoleAudience.sendMessage(prefix + cmp("You are running a beta version. Bugs may appear!"))
        }
        return true
      }

    /**
     * **ONLY PAPER BUILDS SUPPORTET ATM**
     */
    private fun fileUpdate(destination: File) {
        val moduleName = module.module

        CoroutineScope(Dispatchers.Default).launch {
            consoleAudience.sendMessage(prefix + cmp("Downloading latest $moduleName version...\n"))
            val response = WebClient.getFile("https://static.mutils.de/files/latest/$moduleName.jar")
            if (response == null) {
                consoleAudience.sendMessage(prefix + cmp("Download failed! Unexpected response: null", cError))
                return@launch
            }
            destination.mkdirs()
            val targetFile = File("${destination.path}/$moduleName.jar")
            if (targetFile.exists()) {
                try {
                    targetFile.delete()
                } catch (_: Exception) {
                    consoleAudience.sendMessage(prefix + cmp("Failed to delete old $moduleName version! Autoupdates only work on paper servers or on linux systems"))
                    return@launch
                }
            }
            try {
                targetFile.writeBytes(response)
            } catch (e: Exception) {
                consoleAudience.sendMessage(prefix + cmp("Download failed! Unexpected response: ${e.message}", cError))
                return@launch
            }
            consoleAudience.sendMessage(prefix + cmp("Latest version successfully downloaded! Restart your server to take effect of it"))
        }
    }

    suspend fun modrinthUpdate() {
        val target = "https://api.modrinth.com/v2/project/$module/version?loaders=[\"${platform.modrinth}\"]"
        val response = json.decodeFromString<List<ModrinthVersion>>(WebClient.get(target))
        response.firstOrNull()
    }

    /**
     * Login in a server instance
     */
    fun login(onFail: () -> Unit) {
        loginFailTasks.add(onFail)
        proceedLogin()
    }

    fun saveData(key: String? = null, uuid: UUID? = null, autoUpdate: Boolean? = null) {
        key?.let { accountData.key = it }
        autoUpdate?.let { accountData.autoUpdate = it }
        uuid?.let { accountData.uuid = it }
        if (!configFile.exists()) configFile.parentFile.mkdirs()
        configFile.writeText(json.encodeToString(accountData))
    }

    /**
     * Activate an account slot on this server/client
     */
    fun activate(uuid: UUID, key: String, onResponse: (Boolean, String) -> Unit) {
        CoroutineScope(Dispatchers.Default).launch {
            val response = WebClient.get("https://api.mutils.de/public/activate", mapOf("key" to key, "ip" to serverIP, "uuid" to uuid.toString()))
            val loginResponse = serialize<LoginResponse>(response)
            if (loginResponse == null) {
                consoleAudience.sendMessage(noConnection)
                onResponse.invoke(false, "Failed to connect to the MUtils servers - No internet connection?")
                return@launch
            } else if (!loginResponse.success) {
                val message = when (loginResponse.protocol) {
                    1 -> {
                        //Connection is already established but is ok
                        consoleAudience.sendMessage(prefix + cmp("API - Successfully reconnected your MUtils account to this server/account! Please restart to fully enable all features", cSuccess))
                        saveData(key, uuid)
                        proceedLogin()
                        onResponse.invoke(true, "")
                        return@launch
                    }

                    2 -> {
                        accountConnected = false
                        consoleAudience.sendMessage(prefix + cmp("API - Slots used: ") + cmp(loginResponse.slots.toString(), cMark))
                        "Your MUtils account reached the maximum amount of simultaneous connections! Consider updating your plan on https://mutils.de/shop"
                    }

                    3 -> {
                        accountConnected = false
                        "Your entered key ($key) is invalid! Join https://dc.mutils.de to receive your key or ask for help"
                    }

                    else -> "Unknown error occurred while connecting your MUtils account"
                }
                consoleAudience.sendMessage(prefix + cmp("API - $message"))
                onResponse.invoke(false, message)
                return@launch
            } else {
                consoleAudience.sendMessage(prefix + cmp("API - Successfully connected your MUtils account to this server/account! Please restart to fully enable all features", cSuccess))
                consoleAudience.sendMessage(prefix + cmp("API - Slots left: ") + cmp(loginResponse.slots.toString(), cMark))
                saveData(key, uuid)
                proceedLogin()
                onResponse.invoke(true, "")
                return@launch
            }
        }
    }

    /**
     * Receive the challenge of the month
     */
    fun getCOTM(): String {
        return "" //serialize<String>(WebClient.get("https://api.mutils.de/public/monthlychallenge")) ?: "" TODO
    }

    /**
     * @return Success or not
     */
    fun sendData(t: String, s: String, onResponse: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.Default).launch {
            when (val code = WebClient.get("https://api.mutils.de/public/lib", mapOf("MType" to t, "Key" to accountData.key, "UUID" to accountData.uuid.toString()), s)) {
                "0" -> {
                    consoleAudience.sendMessage(prefix + cmp("Unexpected server response - Please report to the MUtils Support", cError))
                    consoleAudience.sendMessage(prefix + cmp("Error Code: ${Base64.getUrlEncoder().encodeToString(t.toByteArray()).replace("=", "")}"))
                    onResponse.invoke(false)
                }
                "1" -> onResponse.invoke(true)
                "2", "3" -> {
                    consoleAudience.sendMessage(prefix + cmp("Unexpected server call - Please report to the MUtils Support", cError))
                    consoleAudience.sendMessage(prefix + cmp("Error Code: $code"))
                    onResponse.invoke(false)
                }
                else -> {
                    consoleAudience.sendMessage(prefix + cmp("Unauthorized server call - Please authenticate with /login"))
                    onResponse.invoke(false)
                }
            }
        }
    }

    fun receiveData(t: String, onResponse: (String) -> Unit) {
        CoroutineScope(Dispatchers.Default).launch {
            val response = WebClient.get("https://api.mutils.de/public/lib", mapOf("MType" to t, "Key" to accountData.key, "UUID" to accountData.uuid.toString()))
            onResponse.invoke(response)
        }
    }

    fun likeData(t: String, id: String) {
        CoroutineScope(Dispatchers.Default).launch {
            when (WebClient.get("https://api.mutils.de/public/lib", mapOf("MType" to t, "Key" to accountData.key, "UUID" to accountData.uuid.toString(), "Entry_ID" to id))) {
                "0" -> consoleAudience.sendMessage(prefix + cmp("Unexpected server response - Do you use an updated version?"))
                "1" -> true
                "2" -> if (debug) consoleAudience.sendMessage(prefix + cmp("Already liked that Entry"))
            }
        }
    }

    /*
     * Utilities
     */
    private inline fun <reified T> serialize(input: String): T? {
        return try {
            json.decodeFromString(input)
        } catch (e: IllegalArgumentException) {
            consoleAudience.sendMessage(prefix + cmp(e.message.toString(), cError))
            if (debug) consoleAudience.sendMessage(prefix + cmp(input, cError))
            consoleAudience.sendMessage(prefix + cmp("API - Failed to read api data! ^ Reason above ^", cError))
            null
        } catch (e: SerializationException) {
            consoleAudience.sendMessage(noConnection)
            null
        }
    }

    private fun proceedLogin() {
        CoroutineScope(Dispatchers.Default).launch {
            val loginData = serialize<LoginResponse>(WebClient.get("https://api.mutils.de/public/login", mapOf("key" to accountData.key, "ip" to serverIP, "uuid" to "${accountData.uuid}", "sversion" to serverVersion, "mversion" to "$serviceVersion")))
            accountConnected = !(loginData == null || !loginData.success)
            if (accountConnected) {
                if (debug) consoleAudience.sendMessage(prefix + cmp("API - Heartbeat success", cSuccess))
                if (!activeLoginJob) {
                    if (debug) consoleAudience.sendMessage(prefix + cmp("API - Launch heartbeat scheduler", cSuccess))
                    activeLoginJob = true
                    delay(1.hours)
                    activeLoginJob = false
                    proceedLogin()
                }
            } else {
                if (debug) consoleAudience.sendMessage(prefix + cmp("API - Heartbeat failed! Disabling premium features and abort heartbeats", cError))
                loginFailTasks.forEach { it.invoke() }
            }
        }
    }

    init {
        val dataFolder = when (platform) {
            MUtilsPlatform.PAPER -> File("plugins/MUtils/Bridge")
            MUtilsPlatform.FABRIC -> File("config/MUtils/Bridge")
        }
        if (!dataFolder.exists()) dataFolder.mkdirs()
        configFile = File("${dataFolder.path}/account.json")
        accountData = if (!configFile.exists()) AccountData()
        else serialize<AccountData>(configFile.readText()) ?: AccountData()
    }
}