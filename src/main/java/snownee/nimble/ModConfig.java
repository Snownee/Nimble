package snownee.nimble;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

final class ModConfig
{
    final BooleanValue enable;
    final BooleanValue nimbleMounting;
    final BooleanValue nimbleElytra;
    final BooleanValue elytraRollScreen;
    final IntValue elytraTickDelay; 
    final BooleanValue frontKeyToggleMode;

    ModConfig(ForgeConfigSpec.Builder spec)
    {
        enable = spec.define("enable", true);
        nimbleMounting = spec.define("nimbleMounting", true);
        nimbleElytra = spec.define("nimbleElytra", true);
        elytraRollScreen = spec.define("elytraRollScreen", true);
        elytraTickDelay = spec.defineInRange("elytraTickDelay", 10, 0, 1000);
        frontKeyToggleMode = spec.define("frontKeyToggleMode", false);
    }
}
