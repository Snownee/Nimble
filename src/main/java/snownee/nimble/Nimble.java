package snownee.nimble;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import snownee.kiwi.Mod;

@Mod(Nimble.ID)
public class Nimble implements ClientModInitializer {
	public static final String ID = "nimble";
	public static final String NAME = "Nimble";

	@Override
	public void onInitializeClient() {
		KeyBindingHelper.registerKeyBinding(NimbleHandler.kbFrontView);
		ClientTickEvents.END_CLIENT_TICK.register(NimbleHandler::tick);
	}
}
