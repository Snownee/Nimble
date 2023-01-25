package snownee.nimble;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Nimble.ID)
public class Nimble {
	public static final String ID = "nimble";
	public static final String NAME = "Nimble";

	public Nimble() {
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "anything. i don't care", (remoteversionstring, networkbool) -> networkbool));
		if (FMLEnvironment.dist.isClient()) {
			FMLJavaModLoadingContext.get().getModEventBus().addListener(Nimble::registerKeybind);
			MinecraftForge.EVENT_BUS.addListener(Nimble::tick);
		}
	}

	private static void registerKeybind(RegisterKeyMappingsEvent event) {
		event.register(NimbleHandler.kbFrontView);
	}

	private static void tick(ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			NimbleHandler.tick(Minecraft.getInstance());
		}
	}
}
