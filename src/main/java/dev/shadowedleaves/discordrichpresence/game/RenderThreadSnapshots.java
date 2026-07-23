package dev.shadowedleaves.discordrichpresence.game;

import dev.shadowedleaves.discordrichpresence.DiscordRichPresence;
import net.minecraft.client.Minecraft;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class RenderThreadSnapshots {
	private RenderThreadSnapshots() {
	}

	public static GameSnapshot collect(Minecraft client, SessionTracker sessionTracker) {
		if (client == null) {
			return null;
		}
		if (client.isSameThread()) {
			return collectOnClientThread(client, sessionTracker);
		}

		AtomicReference<GameSnapshot> snapshot = new AtomicReference<>();
		CountDownLatch latch = new CountDownLatch(1);
		client.execute(() -> {
			try {
				snapshot.set(collectOnClientThread(client, sessionTracker));
			} catch (Exception exception) {
				DiscordRichPresence.LOGGER.warn("Failed to collect game snapshot on the render thread", exception);
			} finally {
				latch.countDown();
			}
		});

		try {
			if (!latch.await(500L, TimeUnit.MILLISECONDS)) {
				DiscordRichPresence.LOGGER.warn("Timed out waiting for game snapshot on the render thread");
			}
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
		}

		return snapshot.get();
	}

	private static GameSnapshot collectOnClientThread(Minecraft client, SessionTracker sessionTracker) {
		PlayState playState = resolvePlayState(client);
		long sessionStartEpochSecond = sessionTracker.update(playState);
		return GameSnapshotCollector.collect(client, sessionStartEpochSecond);
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
}
