# MChallenge API
You can use the MChallenge API to interact with the MUtils-Challenge plugin/mod by writing your own plugin/mod!
Addons can add new challenges and mods, manipulate the workflow, start and stop challenges and more!

More information - [MUtils Website](https://mutils.net)

> **IMPORTANT**<br>
> MChallenge addons must be published under the same licence as MUtils ([AGPL v3](https://www.gnu.org/licenses/agpl-3.0.en.html)) and must comply with the [MUtils TOS](https://mutils.net/legal/tos)

---
## Implement API
To use the MChallenge API simply add the following to your Gradle script. Do not shade the API into your jar!
````kotlin
dependencies {
    // The MChallenge API
    compileOnly("de.miraculixx:challenge-api:1.2.1")
}
````
Now you can access the MChallenge API with the static instance. If MUtils is installed on the server and your addon depends (or at least soft-depends) on ``MUtils-Challenge``, the API will never return null.
````kotlin
// Try to connect to MUtils API
val api = MChallengeAPI.instance
if (api == null) {
    println("Failed to connect to MUtils API")
    return
}
````
Done! Now you can freely interact with the API. Read the internal JavaDocs with ``ctr`` + `left click` while hovering over `MChallengeAPI` (or with the search function) to see more information and options.

## Add new Challenges & Mods
Addons can easily add new custom challenges and mods with your own logic and settings completely managed by MUtils.
You can simply add settings with the embedded settings system and all GUI and interactions are managed by MUtils.

To directly start adding new challenges clone our [addon template](https://github.com/MUtils-MC/MChallenge-Addon)!
<br>-> Template: https://github.com/MUtils-MC/MChallenge-Addon