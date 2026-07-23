package dev.shadowedleaves.discordrichpresence.game;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.WorldVersion;

public final class MinecraftVersionFormatter {
	private MinecraftVersionFormatter() {
	}

	public static String formattedVersion() {
		return FabricLoader.getInstance()
				.getModContainer("minecraft")
				.map(container -> "Minecraft " + container.getMetadata().getVersion().getFriendlyString())
				.orElseGet(MinecraftVersionFormatter::fromSharedConstants);
	}

	private static String fromSharedConstants() {
		WorldVersion version = SharedConstants.getCurrentVersion();
		try {
			return "Minecraft " + version.getId();
		} catch (Throwable ignored) {
		}
		try {
			return "Minecraft " + version.getName();
		} catch (Throwable ignored) {
		}
		return "Minecraft";
	}
}
