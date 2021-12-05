package snownee.nimble;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Nimble.MODID)
public class Nimble {
	public static final String MODID = "nimble";
	public static final String NAME = "Nimble";
	public static NimbleConfig CONFIG = new NimbleConfig();

	public Nimble() {
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "anything. i don't care", (remoteversionstring, networkbool) -> networkbool));
		if (FMLEnvironment.dist.isClient()) {
			FMLJavaModLoadingContext.get().getModEventBus().addListener(NimbleHandler::preInit);

			Pair<Void, ForgeConfigSpec> configPair = new ForgeConfigSpec.Builder().configure(CONFIG::setup);
			ModLoadingContext.get().registerConfig(Type.CLIENT, configPair.getRight());
		}
	}
}
