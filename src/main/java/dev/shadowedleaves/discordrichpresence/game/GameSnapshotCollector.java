package dev.shadowedleaves.discordrichpresence.game;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.protocol.status.ServerStatus;

public final class GameSnapshotCollector {
	private GameSnapshotCollector() {
	}

	public static GameSnapshot collect(Minecraft client, long sessionStartEpochSecond) {
		String playerName = resolvePlayerName(client);
		String playerUuid = resolvePlayerUuid(client);
		String minecraftVersion = MinecraftVersionFormatter.formattedVersion();
		PlayState playState = resolvePlayState(client);

		if (playState == PlayState.MAIN_MENU) {
			return new GameSnapshot(
					PlayState.MAIN_MENU,
					playerName,
					playerUuid,
					minecraftVersion,
					"",
					"",
					"",
					false,
					"",
					0,
					0,
					0L
			);
		}

		ClientLevel level = client.level;
		String dimensionName = level != null ? DimensionFormatter.format(level.dimension()) : "";
		String worldName = "";
		String serverName = "";
		String serverAddress = "";
		int onlinePlayers = 0;
		int maxPlayers = 0;
		boolean serverHasIcon = false;

		if (playState == PlayState.SINGLEPLAYER) {
			worldName = resolveWorldName(client);
		} else {
			ServerData serverData = resolveServerData(client);
			if (serverData != null) {
				serverName = sanitize(serverData.name);
				serverAddress = sanitize(serverData.ip);
				serverHasIcon = hasServerIcon(serverData);
			}
			PlayerCounts playerCounts = resolvePlayerCounts(client, serverData);
			onlinePlayers = playerCounts.online();
			maxPlayers = playerCounts.max();
		}

		return new GameSnapshot(
				playState,
				playerName,
				playerUuid,
				minecraftVersion,
				worldName,
				serverName,
				serverAddress,
				serverHasIcon,
				dimensionName,
				onlinePlayers,
				maxPlayers,
				sessionStartEpochSecond
		);
	}

	private static PlayState resolvePlayState(Minecraft client) {
		if (client.level == null || client.player == null) {
			return PlayState.MAIN_MENU;
		}
		if (client.isLocalServer()) {
			return PlayState.SINGLEPLAYER;
		}
		return PlayState.MULTIPLAYER;
	}

	private static String resolvePlayerName(Minecraft client) {
		if (client.getUser() != null && client.getUser().getName() != null) {
			return client.getUser().getName();
		}
		return "Player";
	}

	private static String resolvePlayerUuid(Minecraft client) {
		if (client.getUser() != null && client.getUser().getProfileId() != null) {
			return client.getUser().getProfileId().toString();
		}
		if (client.player != null) {
			return client.player.getUUID().toString();
		}
		return "";
	}

	private static String resolveWorldName(Minecraft client) {
		IntegratedServer server = client.getSingleplayerServer();
		if (server != null) {
			String levelName = server.getWorldData().getLevelName();
			if (levelName != null && !levelName.isEmpty()) {
				return levelName;
			}
		}
		return "Singleplayer World";
	}

	private static ServerData resolveServerData(Minecraft client) {
		ServerData serverData = client.getCurrentServer();
		if (serverData != null) {
			return serverData;
		}
		ClientPacketListener connection = client.getConnection();
		if (connection != null) {
			return connection.getServerData();
		}
		return null;
	}

	private static PlayerCounts resolvePlayerCounts(Minecraft client, ServerData serverData) {
		ClientPacketListener connection = client.getConnection();
		int online = 0;
		int max = 0;

		if (connection != null) {
			online = connection.getOnlinePlayers().size();
		}

		ServerStatus.Players players = null;
		if (connection != null && connection.getServerData() != null) {
			players = connection.getServerData().players;
		}
		if (players == null && serverData != null) {
			players = serverData.players;
		}
		if (players != null) {
			if (players.online() > online) {
				online = players.online();
			}
			max = players.max();
		}

		return new PlayerCounts(online, max);
	}

	private static boolean hasServerIcon(ServerData serverData) {
		return ServerData.validateIcon(serverData.getIconBytes()) != null;
	}

	private static String sanitize(String value) {
		if (value == null || value.isEmpty()) {
			return "Unknown";
		}
		return value;
	}

	private record PlayerCounts(int online, int max) {
	}
}
