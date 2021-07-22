package snownee.nimble;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.Environment;

@Mod(Nimble.MODID)
public class Nimble {
	public static final String MODID = "nimble";
	public static final String NAME = "Nimble";

	public Nimble() {
		if (Environment.get().getDist().isClient()) {
			FMLJavaModLoadingContext.get().getModEventBus().addListener(NimbleHandler::preInit);
		}
	}
}
