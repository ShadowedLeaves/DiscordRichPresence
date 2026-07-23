package dev.shadowedleaves.discordrichpresence.game;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public final class DimensionFormatter {
	private DimensionFormatter() {
	}

	public static String format(ResourceKey<Level> dimensionKey) {
		return switch (dimensionKey.location().getPath()) {
			case "overworld" -> "Overworld";
			case "the_nether" -> "The Nether";
			case "the_end" -> "The End";
			default -> formatCustom(dimensionKey.location().getNamespace(), dimensionKey.location().getPath());
		};
	}

	private static String formatCustom(String namespace, String path) {
		if ("minecraft".equals(namespace)) {
			return capitalizeWords(path.replace('_', ' '));
		}
		return capitalizeWords(namespace.replace('_', ' ')) + ": " + capitalizeWords(path.replace('_', ' '));
	}

	private static String capitalizeWords(String value) {
		if (value.isEmpty()) {
			return value;
		}
		StringBuilder builder = new StringBuilder(value.length());
		boolean capitalizeNext = true;
		for (int index = 0; index < value.length(); index++) {
			char character = value.charAt(index);
			if (Character.isWhitespace(character)) {
				capitalizeNext = true;
				builder.append(character);
				continue;
			}
			if (capitalizeNext) {
				builder.append(Character.toUpperCase(character));
				capitalizeNext = false;
			} else {
				builder.append(character);
			}
		}
		return builder.toString();
	}
}
