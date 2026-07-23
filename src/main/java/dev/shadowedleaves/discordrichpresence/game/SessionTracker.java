package dev.shadowedleaves.discordrichpresence.game;

import java.time.Instant;

public final class SessionTracker {
	private PlayState previousState = PlayState.MAIN_MENU;
	private long sessionStartEpochSecond;

	public long update(PlayState currentState) {
		if (currentState != PlayState.MAIN_MENU && previousState == PlayState.MAIN_MENU) {
			sessionStartEpochSecond = Instant.now().getEpochSecond();
		} else if (currentState == PlayState.MAIN_MENU) {
			sessionStartEpochSecond = 0L;
		}
		previousState = currentState;
		return sessionStartEpochSecond;
	}
}
