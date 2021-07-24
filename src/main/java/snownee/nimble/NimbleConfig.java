package snownee.nimble;

import org.apache.commons.lang3.tuple.Pair;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = Nimble.MODID, value = Dist.CLIENT, bus = Bus.MOD)
final class NimbleConfig {
	static boolean enable = true;
	static float transitionSpeed = 1;
	static boolean nimbleMounting = true;
	static boolean nimbleElytra = true;
	static boolean elytraRollScreen = true;
	static int elytraTickDelay = 10;
	static boolean frontKeyToggleMode = false;

	private static BooleanValue enableValue;
	private static DoubleValue transitionSpeedValue;
	private static BooleanValue nimbleMountingValue;
	private static BooleanValue nimbleElytraValue;
	private static BooleanValue elytraRollScreenValue;
	private static IntValue elytraTickDelayValue;
	private static BooleanValue frontKeyToggleModeValue;

	static {
		Pair<Void, ForgeConfigSpec> configPair = new ForgeConfigSpec.Builder().configure(NimbleConfig::setup);
		ModLoadingContext.get().registerConfig(Type.CLIENT, configPair.getRight());
	}

	private static void refresh() {
		enable = enableValue.get();
		transitionSpeed = transitionSpeedValue.get().floatValue();
		nimbleMounting = nimbleMountingValue.get();
		nimbleElytra = nimbleElytraValue.get();
		elytraRollScreen = elytraRollScreenValue.get();
		elytraTickDelay = elytraTickDelayValue.get();
		frontKeyToggleMode = frontKeyToggleModeValue.get();
	}

	private static Void setup(ForgeConfigSpec.Builder spec) {
		enableValue = spec.define("enable", enable);
		transitionSpeedValue = spec.defineInRange("transitionSpeed", transitionSpeed, 0.1, 10);
		nimbleMountingValue = spec.define("nimbleMounting", nimbleMounting);
		nimbleElytraValue = spec.define("nimbleElytra", nimbleElytra);
		elytraRollScreenValue = spec.define("elytraRollScreen", elytraRollScreen);
		elytraTickDelayValue = spec.defineInRange("elytraTickDelay", elytraTickDelay, 0, 1000);
		frontKeyToggleModeValue = spec.define("frontKeyToggleMode", frontKeyToggleMode);
		return null;
	}

	@SubscribeEvent
	public static void onFileChange(ModConfigEvent.Reloading event) {
		if (event.getConfig().getModId().equals(Nimble.MODID)) {
			((CommentedFileConfig) event.getConfig().getConfigData()).load();
			refresh();
		}
	}

	@SubscribeEvent
	public static void preInit(FMLClientSetupEvent event) {
		refresh();
	}

	private NimbleConfig() {
	}
}
