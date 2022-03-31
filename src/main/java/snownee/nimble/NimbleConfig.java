package snownee.nimble;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = Nimble.MODID, value = Dist.CLIENT, bus = Bus.MOD)
final class NimbleConfig {
	public boolean enable = true;
	public float transitionSpeed = 1;
	public boolean nimbleMounting = true;
	public boolean nimbleElytra = true;
	public boolean elytraRollScreen = true;
	public int elytraTickDelay = 10;
	public int elytraRollStrength = 20;
	public boolean frontKeyToggleMode = false;

	private BooleanValue enableValue;
	private DoubleValue transitionSpeedValue;
	private BooleanValue nimbleMountingValue;
	private BooleanValue nimbleElytraValue;
	private BooleanValue elytraRollScreenValue;
	private IntValue elytraTickDelayValue;
	private IntValue elytraRollStrengthValue;
	private BooleanValue frontKeyToggleModeValue;

	private void refresh() {
		enable = enableValue.get();
		transitionSpeed = transitionSpeedValue.get().floatValue();
		nimbleMounting = nimbleMountingValue.get();
		nimbleElytra = nimbleElytraValue.get();
		elytraRollScreen = elytraRollScreenValue.get();
		elytraTickDelay = elytraTickDelayValue.get();
		elytraRollStrength = elytraRollStrengthValue.get();
		frontKeyToggleMode = frontKeyToggleModeValue.get();
	}

	Void setup(ForgeConfigSpec.Builder spec) {
		enableValue = spec.define("enable", enable);
		transitionSpeedValue = spec.defineInRange("transitionSpeed", transitionSpeed, 0.1, 10);
		nimbleMountingValue = spec.define("nimbleMounting", nimbleMounting);
		nimbleElytraValue = spec.define("nimbleElytra", nimbleElytra);
		elytraRollScreenValue = spec.define("elytraRollScreen", elytraRollScreen);
		elytraTickDelayValue = spec.defineInRange("elytraTickDelay", elytraTickDelay, 0, 1000);
		elytraRollStrengthValue = spec.defineInRange("elytraRollStrength", elytraRollStrength, 0, 100);
		frontKeyToggleModeValue = spec.define("frontKeyToggleMode", frontKeyToggleMode);
		return null;
	}

	@SubscribeEvent
	public static void onFileChange(ModConfigEvent.Reloading event) {
		if (event.getConfig().getModId().equals(Nimble.MODID)) {
			((CommentedFileConfig) event.getConfig().getConfigData()).load();
			Nimble.CONFIG.refresh();
		}
	}

	@SubscribeEvent
	public static void preInit(FMLClientSetupEvent event) {
		Nimble.CONFIG.refresh();
	}

}