package snownee.nimble.compat.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.minecraft.util.Mth;
import snownee.nimble.Nimble;
import snownee.nimble.NimbleConfig;

@Config(name = Nimble.MODID + "-client")
public final class ClothNimbleConfig extends NimbleConfig implements ConfigData {

	@Override
	public void validatePostLoad() throws ValidationException {
		transitionSpeed = Mth.clamp(transitionSpeed, 0.1F, 10);
		elytraTickDelay = Mth.clamp(elytraTickDelay, 0, 1000);
	}

}
