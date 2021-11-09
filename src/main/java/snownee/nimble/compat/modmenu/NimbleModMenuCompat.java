package snownee.nimble.compat.modmenu;

import com.terraformersmc.modmenu.api.ModMenuApi;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class NimbleModMenuCompat implements ModMenuApi {
	//	@Override
	//	public ConfigScreenFactory<?> getModConfigScreenFactory() {
	//		return parent -> AutoConfig.getConfigScreen(ClothNimbleConfig.class, parent).get();
	//	}
}
