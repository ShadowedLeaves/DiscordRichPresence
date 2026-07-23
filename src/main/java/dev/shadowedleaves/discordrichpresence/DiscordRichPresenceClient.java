package dev.shadowedleaves.discordrichpresence;

import dev.shadowedleaves.discordrichpresence.discord.DiscordSdkManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class DiscordRichPresenceClient implements ClientModInitializer {
	private DiscordSdkManager sdkManager;

	@Override
	public void onInitializeClient() {
		DiscordRichPresence.LOGGER.info("Initializing Discord Rich Presence");
		sdkManager = new DiscordSdkManager();

		ClientLifecycleEvents.CLIENT_STARTED.register(client -> sdkManager.start());
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> sdkManager.shutdown());
	}
}
