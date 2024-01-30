package com.dre.brewery.utility;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {

	private static final Map<String, String> colorCache = new HashMap<>();


	public static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
	public static String color(String msg) {
		if (msg == null) return null;
        return getOrCache(msg, () -> cacheMsg(msg));
    }

	private static String parseColor(String msg) {
		Matcher matcher = HEX_PATTERN.matcher(msg);
		StringBuffer buffer = new StringBuffer();

		while(matcher.find()) {
			matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString());
		}

		return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
	}

	private static String getOrCache(String key, Supplier<String> other) {
		if (colorCache.containsKey(key)) return colorCache.get(key);
		return other.get();
	}

	private static String cacheMsg(String key) {
		String value = parseColor(key);
		colorCache.put(key, value);
		return value;
	}

	/**
	 * Creates a weighted mix between the two given colours
	 * <p>where the weight is calculated from the distance of the currentPos to the prev and next
	 *
	 * @param prevColor Previous Color
	 * @param prevPos Position of the Previous Color
	 * @param currentPos Current Position
	 * @param nextColor Next Color
	 * @param nextPos Position of the Next Color
	 * @return Mixed Color
	 */
	public static Color weightedMixColor(Color prevColor, int prevPos, int currentPos, Color nextColor, int nextPos) {
		float diffPrev = currentPos - prevPos;
		float diffNext = nextPos - currentPos;
		float total = diffNext + diffPrev;
		float percentNext = diffPrev / total;
		float percentPrev = diffNext / total;

			/*5 #8# 15
			8-5 = 3 -> 3/10
			15-8 = 7 -> 7/10*/

		return Color.fromRGB(
			Math.min(255, (int) ((nextColor.getRed() * percentNext) + (prevColor.getRed() * percentPrev))),
			Math.min(255, (int) ((nextColor.getGreen() * percentNext) + (prevColor.getGreen() * percentPrev))),
			Math.min(255, (int) ((nextColor.getBlue() * percentNext) + (prevColor.getBlue() * percentPrev)))
		);
	}
}
