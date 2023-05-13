# MTimer API
You can use the MTimer API to interact with the MUtils-Timer plugin/mod by writing your own plugin/mod!
Addons can add their own events, manipulate the workflow, start and stop the timer and more!

More information - [MUtils Website](https://mutils.net)

> **IMPORTANT**<br>
> MTimer addons must be published under the same licence as MUtils ([AGPL v3](https://www.gnu.org/licenses/agpl-3.0.en.html)) and must comply with the [MUtils TOS](https://mutils.net/legal/tos)

---
## Implement API
To use the MTimer API simply add the following to your Gradle script. Do not shade the API into your jar!
````kotlin
dependencies {
    // The MTimer API
    compileOnly("de.miraculixx:timer-api:1.1.2")
}
````
Now you can access the MTimer API with the static instance. If MUtils is installed on the server and your addon depends (or at least soft-depends) on ``MUtils-Timer``, the API will never return null.
````kotlin
// Try to connect to MUtils API
val api = MTimer.instance
if (api == null) {
    println("Failed to connect to MUtils API")
    return
}
````
Done! Now you can freely interact with the API. Read the internal JavaDocs with ``ctr`` + `left click` while hovering over `MTimerAPI` (or with the search function) to see more information and options.

## Listen to timer events
Addons can easily add their own logic to timer by listening to events! For example, if you want to start your own code on timer starting, listen to the ``onStartLogic``:
````kotlin
api.onStartLogic {
    // Your start logic
    println("Hurrah! The timer is now active")
}
````
In comparison to the onStart and onStop listener, the tick listener extends the current timer time as a duration:
````kotlin
api.addTickLogic { time ->
    // Your tick logic
    println("The current time -> $time")
}
````

## Manipulate the global & personal timer
You can manipulate all timer instances (global and personal). To manipulate the global timer just use all available functions without providing any UUID:
````kotlin
// Current global timer status
val globalStatus = getTimerStatus()

// Start the global timer
startTimer()
````
To manipulate personal user timer, simply use all available functions with the players UUID. If no personal timer is available to the provided player, a new one will be created.

````kotlin
val targetPlayer: UUID

// Current personal timer status
val globalStatus = getTimerStatus(targetPlayer)

// Start the personal timer
startTimer(targetPlayer)
````