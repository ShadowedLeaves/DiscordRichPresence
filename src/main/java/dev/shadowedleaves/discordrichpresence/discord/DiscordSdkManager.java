package dev.shadowedleaves.discordrichpresence.discord;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.GameSDKException;
import de.jcm.discordgamesdk.Result;
import de.jcm.discordgamesdk.activity.Activity;
import dev.shadowedleaves.discordrichpresence.DiscordConstants;
import dev.shadowedleaves.discordrichpresence.DiscordRichPresence;
import dev.shadowedleaves.discordrichpresence.game.GameSnapshot;
import dev.shadowedleaves.discordrichpresence.game.RenderThreadSnapshots;
import dev.shadowedleaves.discordrichpresence.game.SessionTracker;
import dev.shadowedleaves.discordrichpresence.presence.PresenceComposer;
import dev.shadowedleaves.discordrichpresence.presence.PresenceDetailCycler;
import dev.shadowedleaves.discordrichpresence.presence.PresenceImages;
import dev.shadowedleaves.discordrichpresence.game.PlayState;
import net.minecraft.client.Minecraft;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public final class DiscordSdkManager {
	private final AtomicBoolean running = new AtomicBoolean(false);
	private final SessionTracker sessionTracker = new SessionTracker();
	private final AtomicLong lastSuccessfulUpdateMs = new AtomicLong(0L);
	private Thread sdkThread;

	public void start() {
		if (!running.compareAndSet(false, true)) {
			return;
		}
		DiscordRichPresence.LOGGER.info("Starting Discord Rich Presence");
		sdkThread = new Thread(this::sdkLoop, "discordrichpresence-sdk");
		sdkThread.setDaemon(true);
		sdkThread.start();
	}

	public void shutdown() {
		running.set(false);
		if (sdkThread != null) {
			sdkThread.interrupt();
		}
	}

	private void sdkLoop() {
		while (running.get()) {
			CreateParams params = new CreateParams();
			params.setClientID(DiscordConstants.APPLICATION_ID);
			params.setFlags(CreateParams.Flags.NO_REQUIRE_DISCORD);

			try (Core core = new Core(params)) {
				if (!core.isDiscordRunning()) {
					DiscordRichPresence.LOGGER.info(
							"Discord desktop app not found, retrying in {} ms",
							DiscordConstants.RECONNECT_DELAY_MS
					);
					sleepWithCallbacks(null, DiscordConstants.RECONNECT_DELAY_MS);
					continue;
				}

				DiscordRichPresence.LOGGER.info("Connected to Discord");
				lastSuccessfulUpdateMs.set(0L);

				while (running.get()) {
					long now = System.currentTimeMillis();
					long lastSuccess = lastSuccessfulUpdateMs.get();
					if (now - lastSuccess >= DiscordConstants.UPDATE_INTERVAL_MS || lastSuccess == 0L) {
						Minecraft client = Minecraft.getInstance();
						GameSnapshot snapshot = RenderThreadSnapshots.collect(client, sessionTracker);
						if (snapshot == null) {
							DiscordRichPresence.LOGGER.warn("Could not collect game snapshot for Discord Rich Presence");
						} else {
							if (snapshot.playState() == PlayState.MAIN_MENU) {
								PresenceImages.clearServerIconSuppressions();
							}
							sendPresence(core, new Activity(), snapshot);
						}
					}

					sleepWithCallbacks(core, DiscordConstants.UPDATE_INTERVAL_MS);
				}
			} catch (Exception exception) {
				DiscordRichPresence.LOGGER.warn("Discord connection failed", exception);
			}

			lastSuccessfulUpdateMs.set(0L);

			if (!running.get()) {
				break;
			}

			sleepWithCallbacks(null, DiscordConstants.RECONNECT_DELAY_MS);
		}
	}

	private void sendPresence(Core core, Activity activity, GameSnapshot snapshot) {
		String largeImage = PresenceImages.largeImage(snapshot);
		try {
			PresenceComposer.apply(activity, snapshot);
			core.activityManager().updateActivity(activity, result -> {
				if (result == Result.OK) {
					lastSuccessfulUpdateMs.set(System.currentTimeMillis());
					DiscordRichPresence.LOGGER.info(
							"Discord Rich Presence updated: {} | {}",
							snapshot.playState(),
							summary(snapshot)
					);
					return;
				}
				DiscordRichPresence.LOGGER.warn(
						"Discord rejected Rich Presence update ({}): {}",
						result,
						summary(snapshot)
				);
				if (result == Result.INVALID_PAYLOAD
						&& !DiscordConstants.MINECRAFT_LOGO_ASSET_KEY.equals(largeImage)) {
					PresenceImages.suppressServerIcon(snapshot.serverAddress());
					retryPresenceWithDefaultLargeImage(core, snapshot);
				}
			});
		} catch (GameSDKException exception) {
			DiscordRichPresence.LOGGER.warn(
					"Failed to update Discord Rich Presence ({}): {}",
					exception.getResult(),
					summary(snapshot)
			);
		} catch (Exception exception) {
			DiscordRichPresence.LOGGER.warn("Failed to update Discord Rich Presence", exception);
		}
	}

	private void retryPresenceWithDefaultLargeImage(Core core, GameSnapshot snapshot) {
		try {
			Activity activity = new Activity();
			PresenceComposer.apply(activity, snapshot);
			core.activityManager().updateActivity(activity, result -> {
				if (result == Result.OK) {
					lastSuccessfulUpdateMs.set(System.currentTimeMillis());
				}
			});
		} catch (GameSDKException exception) {
			DiscordRichPresence.LOGGER.warn(
					"Failed to update Discord Rich Presence with default artwork ({})",
					exception.getResult()
			);
		}
	}

	private void sleepWithCallbacks(Core core, long durationMs) {
		long deadline = System.currentTimeMillis() + durationMs;
		while (running.get() && System.currentTimeMillis() < deadline) {
			if (core != null) {
				runCallbacks(core);
			}
			try {
				Thread.sleep(16L);
			} catch (InterruptedException exception) {
				Thread.currentThread().interrupt();
				return;
			}
		}
	}

	private void runCallbacks(Core core) {
		try {
			core.runCallbacks();
		} catch (GameSDKException exception) {
			DiscordRichPresence.LOGGER.warn("Discord callback failed ({})", exception.getResult());
		}
	}

	private static String summary(GameSnapshot snapshot) {
		return PresenceDetailCycler.currentDetail(snapshot);
	}
}
