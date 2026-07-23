package dev.shadowedleaves.discordrichpresence.presence;

import dev.shadowedleaves.discordrichpresence.DiscordConstants;
import dev.shadowedleaves.discordrichpresence.game.GameSnapshot;

import java.util.ArrayList;
import java.util.List;

public final class PresenceDetailCycler {
	private PresenceDetailCycler() {
	}

	public static String currentDetail(GameSnapshot snapshot) {
		return currentDetail(snapshot, System.currentTimeMillis());
	}

	public static String currentDetail(GameSnapshot snapshot, long epochMillis) {
		List<String> entries = buildEntries(snapshot);
		if (entries.isEmpty()) {
			return "";
		}
		if (entries.size() == 1) {
			return entries.getFirst();
		}
		int index = (int) ((epochMillis / DiscordConstants.UPDATE_INTERVAL_MS) % entries.size());
		return entries.get(index);
	}

	private static List<String> buildEntries(GameSnapshot snapshot) {
		List<String> entries = new ArrayList<>();
		switch (snapshot.playState()) {
			case MAIN_MENU -> entries.add(snapshot.minecraftVersion());
			case SINGLEPLAYER -> addIfPresent(entries, snapshot.worldName(), snapshot.dimensionName(), snapshot.minecraftVersion());
			case MULTIPLAYER -> addIfPresent(
					entries,
					snapshot.serverName(),
					snapshot.serverAddress(),
					snapshot.dimensionName(),
					formatPlayerCount(snapshot),
					snapshot.minecraftVersion()
			);
		}
		return entries;
	}

	private static void addIfPresent(List<String> entries, String... values) {
		for (String value : values) {
			if (value != null && !value.isEmpty()) {
				entries.add(value);
			}
		}
	}

	private static String formatPlayerCount(GameSnapshot snapshot) {
		if (snapshot.maxPlayers() > 0) {
			return snapshot.onlinePlayers() + "/" + snapshot.maxPlayers();
		}
		if (snapshot.onlinePlayers() > 0) {
			return String.valueOf(snapshot.onlinePlayers());
		}
		return "";
	}
}
