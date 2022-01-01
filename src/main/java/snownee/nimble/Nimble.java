package snownee.nimble;

import net.fabricmc.api.ClientModInitializer;
import snownee.kiwi.Mod;

@Mod(Nimble.MODID)
public class Nimble implements ClientModInitializer {
	public static final String MODID = "nimble";
	public static final String NAME = "Nimble";

	@Override
	public void onInitializeClient() {
		NimbleHandler.preInit();
	}
}
