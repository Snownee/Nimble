package snownee.nimble;

import snownee.kiwi.config.KiwiConfig;
import snownee.kiwi.config.KiwiConfig.ConfigType;
import snownee.kiwi.config.KiwiConfig.Range;

@KiwiConfig(type = ConfigType.CLIENT)
public class NimbleConfig {
	public static boolean enable = true;
	@Range(min = 0.1, max = 10)
	public static float transitionSpeed = 1;
	public static boolean nimbleMounting = true;
	public static boolean nimbleElytra = true;
	public static boolean elytraRollScreen = true;
	@Range(min = 0, max = 1000)
	public static int elytraTickDelay = 10;
	public static boolean frontKeyToggleMode = false;
}
