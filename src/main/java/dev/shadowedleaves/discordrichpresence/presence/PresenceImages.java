package dev.shadowedleaves.discordrichpresence.presence;

import dev.shadowedleaves.discordrichpresence.DiscordConstants;
import dev.shadowedleaves.discordrichpresence.game.GameSnapshot;
import dev.shadowedleaves.discordrichpresence.game.PlayState;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class PresenceImages {
	private static final Set<String> SUPPRESSED_SERVER_ICON_ADDRESSES = ConcurrentHashMap.newKeySet();

	private PresenceImages() {
	}

	public static void clearServerIconSuppressions() {
		SUPPRESSED_SERVER_ICON_ADDRESSES.clear();
	}

	public static void suppressServerIcon(String serverAddress) {
		if (serverAddress == null || serverAddress.isEmpty() || "Unknown".equals(serverAddress)) {
			return;
		}
		SUPPRESSED_SERVER_ICON_ADDRESSES.add(serverAddress);
	}

	public static String largeImage(GameSnapshot snapshot) {
		if (snapshot.playState() != PlayState.MULTIPLAYER || !snapshot.serverHasIcon()) {
			return DiscordConstants.MINECRAFT_LOGO_ASSET_KEY;
		}
		String address = snapshot.serverAddress();
		if (address == null || address.isEmpty() || "Unknown".equals(address)) {
			return DiscordConstants.MINECRAFT_LOGO_ASSET_KEY;
		}
		if (SUPPRESSED_SERVER_ICON_ADDRESSES.contains(address)) {
			return DiscordConstants.MINECRAFT_LOGO_ASSET_KEY;
		}
		String url = serverIconUrl(address);
		return url != null ? url : DiscordConstants.MINECRAFT_LOGO_ASSET_KEY;
	}

	public static String largeImageTooltip(GameSnapshot snapshot, String largeImage) {
		if (snapshot.playState() == PlayState.MULTIPLAYER
				&& !DiscordConstants.MINECRAFT_LOGO_ASSET_KEY.equals(largeImage)) {
			String serverName = snapshot.serverName();
			if (serverName != null && !serverName.isEmpty() && !"Unknown".equals(serverName)) {
				return truncateTooltip(serverName);
			}
		}
		return DiscordConstants.LARGE_IMAGE_TOOLTIP;
	}

	public static String playerHeadUrl(String playerUuid, String playerName) {
		String url;
		if (playerUuid != null && !playerUuid.isEmpty()) {
			url = DiscordConstants.PLAYER_HEAD_URL_PREFIX + playerUuid;
		} else if (playerName != null && !playerName.isEmpty()) {
			url = DiscordConstants.PLAYER_HEAD_URL_PREFIX + playerName;
		} else {
			url = DiscordConstants.PLAYER_HEAD_URL_PREFIX + "Steve";
		}
		if (url.length() <= DiscordConstants.MAX_PRESENCE_IMAGE_KEY_LENGTH) {
			return url;
		}
		return DiscordConstants.PLAYER_HEAD_URL_PREFIX + "Steve";
	}

	private static String serverIconUrl(String serverAddress) {
		String encoded = URLEncoder.encode(serverAddress, StandardCharsets.UTF_8);
		String url = DiscordConstants.SERVER_ICON_URL_PREFIX + encoded;
		if (url.length() <= DiscordConstants.MAX_PRESENCE_IMAGE_KEY_LENGTH) {
			return url;
		}
		return null;
	}

	private static String truncateTooltip(String value) {
		if (value.length() <= DiscordConstants.MAX_PRESENCE_FIELD_LENGTH) {
			return value;
		}
		return value.substring(0, DiscordConstants.MAX_PRESENCE_FIELD_LENGTH - 1) + "\u2026";
	}
}
