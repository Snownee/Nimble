package snownee.nimble;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import snownee.nimble.compat.config.ClothNimbleConfig;

public class Nimble implements ClientModInitializer {
	public static final String MODID = "nimble";
	public static final String NAME = "Nimble";
	public static NimbleConfig CONFIG = new NimbleConfig();

	@Override
	public void onInitializeClient() {
		if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
			AutoConfig.register(ClothNimbleConfig.class, Toml4jConfigSerializer::new);
			CONFIG = AutoConfig.getConfigHolder(ClothNimbleConfig.class).getConfig();
		}
		NimbleHandler.preInit();
	}
}
