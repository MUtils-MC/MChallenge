# 🧩 MChallenges

<!-- modrinth_exclude.start -->
### - [DOWNLOAD](https://modrinth.com/plugin/template) -
<!-- modrinth_exclude.end -->

MChallenges provides a variety of modifications (``challenges``) to change up your gameplay experience.<br>
Challenges can make the game **harder**, more **interesting**, or just plain **silly**. 
Or everything at once!



Part from [**MUtils**](https://mutils.net)<br>
Works perfectly with [**MTimer**](https://modrinth.com/project/timer) to count your time & death punishments!

### ⇒ **Latest Update Trailer**
<!-- modrinth_exclude.start -->
[![Latest Update Trailer](.github/assets/thumbnail.png)](https://www.youtube.com/video/dQpM0Lkeh5c)
<!-- modrinth_exclude.end -->

<!-- github_exclude.start -->
<iframe width="560" height="315" src="https://www.youtube-nocookie.com/embed/dQpM0Lkeh5c" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen></iframe><br>
(Can contain features not available for everyone*)
<!-- github_exclude.end -->

<br>
<br>
<br>

# 🎯 Challenges
Each challenge can be toggled and configured individually inside the Challenge menu (``/challenge``).<br>
All challenges can be combined and played in multiplayer or singleplayer (some require at least two players).

**▪ Current Challenge count** ⇒ ``<!-- challenge-count -->``

<!-- modrinth_exclude.start -->
> **Important**<br>
> *Some Challenges require MUtils Premium to keep all projects and needed servers alive :)<br>
> Read more about it [here](https://mutils.net/premium).
<!-- modrinth_exclude.end -->

<!-- github_exclude.start -->
|                                                                                                                                                                      |
|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Important**<br>*Some Challenges require MUtils Premium to keep all projects and needed servers alive :)<br> Read more about it [here](https://mutils.net/premium). |
<!-- github_exclude.end -->

To filter and search for Challenges, visit our own [Challenge List](https://mutils.net/ch/list) or use `ctrl` + `f`
<details><summary><img src="https://i.imgur.com/HOuMmwT.png" width="40%"></summary>

<!-- challenges -->

</details>

<br>
<br>
<br>

## 💎 Utilities & Commands
To fully enjoy all new content, we also added some neat extras that can be disabled if unwanted.

▪ First column indicates if the command is usable without OP.<br>
▪ To see more about permissions look inside our [documentation](https://mutils.net/ch/info)!

|    | **▾Command▾**                  |                       **▾Description▾**                       |
|:--:|:-------------------------------|:-------------------------------------------------------------:|
| ❌  | `/challenge [<...>]`           |         Main Command to access Challenges & settings          |
| ❌  | `/rule <rule> <new-value>`     |         Activate / change custom game rules like pvp          |
| ✔  | `/backpack [<player>, setup]`  |         Access or setup a personal or global backpack         |
| ❌  | `/heal [<players>]`            |      Restore health & saturation for yourself or targets      |
| ❌  | `/hide [<players>]` `/show`    |     Fully hide yourself or targets from all other players     |
| ❌  | `/invsee <player>`             |         See & modify the inventory from other players         |
| ✔  | `/position <new, get, remove>` |       Save your current position to find it later again       |
| ❌  | `/reset [<seed>]`              | Delete current world and generate a new one (restarts server) |

<br>
<br>
<br>

## 🔌 API & Challenge Addons
MChallenge provides a feature rich environment for Challenges with GUI's, settings menus and saving and more.
You can add your own Challenges to the Addon Category in a few seconds!

▪ [Challenge addon template](https://github.com/MUtils-MC/MChallenge-Addon)<br>
▪ If you want to publish your addon, please notify me on Discord, so I can add it to our [list](https://mutils.net/ch/addons) :)

If you want to inject into the Challenge lifecycle or even manipulate it with your own plugin, you can access the [**API**](https://github.com/MUtils-MC/MChallenge-API):
```kts
dependencies {
    compileOnly("de.miraculixx:challenge-api:1.5.0")
}
```
