package dev.shadowedleaves.discordrichpresence.game;

import java.util.Objects;

public final class GameSnapshot {
	private final PlayState playState;
	private final String playerName;
	private final String playerUuid;
	private final String minecraftVersion;
	private final String worldName;
	private final String serverName;
	private final String serverAddress;
	private final boolean serverHasIcon;
	private final String dimensionName;
	private final int onlinePlayers;
	private final int maxPlayers;
	private final long sessionStartEpochSecond;

	public GameSnapshot(
			PlayState playState,
			String playerName,
			String playerUuid,
			String minecraftVersion,
			String worldName,
			String serverName,
			String serverAddress,
			boolean serverHasIcon,
			String dimensionName,
			int onlinePlayers,
			int maxPlayers,
			long sessionStartEpochSecond
	) {
		this.playState = playState;
		this.playerName = playerName;
		this.playerUuid = playerUuid;
		this.minecraftVersion = minecraftVersion;
		this.worldName = worldName;
		this.serverName = serverName;
		this.serverAddress = serverAddress;
		this.serverHasIcon = serverHasIcon;
		this.dimensionName = dimensionName;
		this.onlinePlayers = onlinePlayers;
		this.maxPlayers = maxPlayers;
		this.sessionStartEpochSecond = sessionStartEpochSecond;
	}

	public PlayState playState() {
		return playState;
	}

	public String playerName() {
		return playerName;
	}

	public String playerUuid() {
		return playerUuid;
	}

	public String minecraftVersion() {
		return minecraftVersion;
	}

	public String worldName() {
		return worldName;
	}

	public String serverName() {
		return serverName;
	}

	public String serverAddress() {
		return serverAddress;
	}

	public boolean serverHasIcon() {
		return serverHasIcon;
	}

	public String dimensionName() {
		return dimensionName;
	}

	public int onlinePlayers() {
		return onlinePlayers;
	}

	public int maxPlayers() {
		return maxPlayers;
	}

	public long sessionStartEpochSecond() {
		return sessionStartEpochSecond;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof GameSnapshot snapshot)) {
			return false;
		}
		return playState == snapshot.playState
				&& serverHasIcon == snapshot.serverHasIcon
				&& onlinePlayers == snapshot.onlinePlayers
				&& maxPlayers == snapshot.maxPlayers
				&& sessionStartEpochSecond == snapshot.sessionStartEpochSecond
				&& Objects.equals(playerName, snapshot.playerName)
				&& Objects.equals(playerUuid, snapshot.playerUuid)
				&& Objects.equals(minecraftVersion, snapshot.minecraftVersion)
				&& Objects.equals(worldName, snapshot.worldName)
				&& Objects.equals(serverName, snapshot.serverName)
				&& Objects.equals(serverAddress, snapshot.serverAddress)
				&& Objects.equals(dimensionName, snapshot.dimensionName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(
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
}
