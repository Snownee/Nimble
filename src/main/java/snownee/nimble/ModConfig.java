package snownee.nimble;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
@Config(modid = Nimble.MODID)
public class ModConfig
{
    public static boolean enable = true;
    public static boolean nimbleMounting = true;
    public static boolean nimbleElytra = true;
    public static boolean elytraRollScreen = true;
    public static int elytraTickDelay = 10;
    public static boolean frontKeyToggleMode = false;

    @SubscribeEvent
    public static void onConfigReload(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(Nimble.MODID))
        {
            ConfigManager.sync(Nimble.MODID, Config.Type.INSTANCE);
        }
    }
}
