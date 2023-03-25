# M-Timer & Clock
Add a timer to your HUD to stop times or display the current time (ingame or real). You have full controle over your timer with design customization or even automated actions like time stops on certain events (like killing the Ender Dragon) and rules like automaticly set the gamemode to spectator after deaths.

You can also give players the oppertunity to opt in or out to the global timer and let them use their own customized timer, only visible for them!

## Access & customize the Timer
All commands (except config commands) can be used with `/timer` for globale timer or with `/ptimer` for personal timer -> `/timer resume` resumes global timer & `/ptimer pause` pauses your personal timer

| Command | Permission | Short Description |
|:-:|:-:|:-:|
| /timer ... | `mutils.command.timer` | Parent command for global timer |
| /ptimer ... | `mutils.command.ptimer` | Parent command for personal timers |
| ... **resume** | *Parent Permission* | Resumes the timer |
| ... **pause** | *Parent Permission* | Pauses the timer |
| ... **setup** | *Parent Permission* | Modify timer settings & design |
| ... **reset** | *Parent Permission* | Resets the time to zero |
| /timer **language** | `mutils.command.timer-config` | Switch between languages |
| /timer **config** | `mutils.command.timer-config` | Reload/ Reset/ Save your configuration |

![GUI](https://cdn.modrinth.com/data/kTmEpjUi/images/d5841d206994834ce297824cdde799875e6e0c3e.gif)

## Create & Share Designs
The timer provides a complete design creator inside an easy to use GUI. Custom designs support:
- Separate idle and running state design
- Placeholder for time values to change your syntax
- Separate prefix, suffix and hide rule for every placeholder
- Custom animations like gradients

[Adventure MiniMessage](https://webui.adventure.kyroi.net) styling is supported! All designs are saved in a separate json file.<br>
You can share your best designs in our global designs library for everyone to use or browse for designs you like to use yourself (Does not work in BETA!).



> ![timer1](https://cdn-raw.modrinth.com/data/kTmEpjUi/images/4811eff64f2b513797faa3e52607688b466f4b22.webp)
> ![timer2](https://cdn-raw.modrinth.com/data/kTmEpjUi/images/a579978b2e8f191376ef6a98a51fa6eacb687b23.webp)
> ![timer3](https://cdn-raw.modrinth.com/data/kTmEpjUi/images/8c2b9473f8294acbb678759e3f3f76faffba1c9c.webp)


## Support & FAQ
Check out our official FAQ and tutorials if you need help at [MUtils.de/timer](https://mutils.de/timer).

If you have any problems or unanswered questions join our [Discord](https://dc.mutils.de/) to contact us and share your experience with other users!

## Privacy
M-Timer communicates with the [MUtils.de](https://mutils.de) servers for:
- version checking (normaly only on startup)
- public design library access (only on manual access)

While communicating the server IP is shared but not saved on our servers. Uploading/ downloading from the public design library requires account authentication