package dev.shadowedleaves.discordrichpresence.presence;

import de.jcm.discordgamesdk.activity.Activity;
import de.jcm.discordgamesdk.activity.ActivityType;
import dev.shadowedleaves.discordrichpresence.DiscordConstants;
import dev.shadowedleaves.discordrichpresence.game.GameSnapshot;

import java.time.Instant;

public final class PresenceComposer {
	private PresenceComposer() {
	}

	public static void apply(Activity activity, GameSnapshot snapshot) {
		activity.setType(ActivityType.PLAYING);
		String largeImage = PresenceImages.largeImage(snapshot);
		activity.assets().setLargeImage(largeImage);
		activity.assets().setLargeText(PresenceImages.largeImageTooltip(snapshot, largeImage));

		String smallImageTooltip = snapshot.playerName();
		if (smallImageTooltip == null || smallImageTooltip.isEmpty()) {
			smallImageTooltip = "Player";
		}
		activity.assets().setSmallImage(PresenceImages.playerHeadUrl(snapshot.playerUuid(), snapshot.playerName()));
		activity.assets().setSmallText(truncate(smallImageTooltip));

		if (snapshot.sessionStartEpochSecond() > 0L) {
			activity.timestamps().setStart(Instant.ofEpochSecond(snapshot.sessionStartEpochSecond()));
		} else {
			activity.timestamps().clear();
		}

		switch (snapshot.playState()) {
			case MAIN_MENU -> composeMainMenu(activity, snapshot);
			case SINGLEPLAYER -> composeSingleplayer(activity, snapshot);
			case MULTIPLAYER -> composeMultiplayer(activity, snapshot);
		}
	}

	private static void composeMainMenu(Activity activity, GameSnapshot snapshot) {
		activity.setDetails(truncate("In Main Menu"));
		activity.setState(truncate(PresenceDetailCycler.currentDetail(snapshot)));
	}

	private static void composeSingleplayer(Activity activity, GameSnapshot snapshot) {
		activity.setDetails(truncate("Playing Singleplayer"));
		activity.setState(truncate(PresenceDetailCycler.currentDetail(snapshot)));
	}

	private static void composeMultiplayer(Activity activity, GameSnapshot snapshot) {
		activity.setDetails(truncate("Playing Multiplayer"));
		activity.setState(truncate(PresenceDetailCycler.currentDetail(snapshot)));
	}

	private static String truncate(String value) {
		if (value == null) {
			return "";
		}
		String sanitized = value.replace('\n', ' ').replace('\r', ' ').trim();
		if (sanitized.length() <= DiscordConstants.MAX_PRESENCE_FIELD_LENGTH) {
			return sanitized;
		}
		return sanitized.substring(0, DiscordConstants.MAX_PRESENCE_FIELD_LENGTH - 1) + "\u2026";
	}
}
