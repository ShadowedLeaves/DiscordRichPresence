# Discord Rich Presence

A lightweight Fabric mod that displays your Minecraft activity on Discord using the Discord Game SDK.

**Supported versions:** Minecraft `1.21` through `1.21.x`  
**Java:** 21  
**Side:** Client only

## Features

- Connects to Discord automatically on launch
- Reconnects if Discord opens after Minecraft
- Updates for main menu, singleplayer, and multiplayer every 3 seconds
- Cycles world, server, and session info on a second line below the play status
- Shows your player head as the small image
- Shows session elapsed time while in a world
- No in-game configuration required



## Rich Presence


|            | Main Menu         | Singleplayer                   | Multiplayer                                       |
| ---------- | ----------------- | ------------------------------ | ------------------------------------------------- |
| **Details** | In Main Menu      | Playing Singleplayer           | Playing Multiplayer                               |
| **State** | Minecraft version | World name, dimension, version | Server name, IP, dimension, player count, version |


State shows one value at a time and rotates every 3 seconds. Empty values are skipped.

## Requirements

- Fabric Loader `>=0.19.3`
- Fabric API (matching your Minecraft version)
- Minecraft `1.21.x`
- Java 21
- Discord desktop app



## Support

- Source: [GitHub](https://github.com/ShadowedLeaves/DiscordRichPresence)
- Issues: [GitHub Issues](https://github.com/ShadowedLeaves/DiscordRichPresence/issues)



## License

MIT

## Author

ShadowedLeaves