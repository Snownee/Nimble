package snownee.nimble;

import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.Environment;

@Mod(Nimble.MODID)
public class Nimble {
	public static final String MODID = "nimble";
	public static final String NAME = "Nimble";

	public Nimble() {
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "anything. i don't care", (remoteversionstring, networkbool) -> networkbool));
		if (Environment.get().getDist().isClient()) {
			FMLJavaModLoadingContext.get().getModEventBus().addListener(NimbleHandler::preInit);
		}
	}
}
