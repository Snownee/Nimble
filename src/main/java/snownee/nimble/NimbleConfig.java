package snownee.nimble;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import snownee.kiwi.config.ConfigUI;
import snownee.kiwi.config.KiwiConfig;
import snownee.kiwi.config.KiwiConfig.ConfigType;
import snownee.kiwi.config.KiwiConfig.Range;

@KiwiConfig(type = ConfigType.CLIENT)
public class NimbleConfig {
	public static boolean enable = true;
	@Range(min = 0.1, max = 10)
	public static float transitionSpeed = 1;
	public static boolean nimbleMounting = true;
	@ConfigUI.ItemType(String.class)
	public static List<String> mountingEntityBlocklist = List.of();
	public static boolean nimbleElytra = true;
	public static boolean elytraRollScreen = true;
	@Range(min = 0, max = 100)
	public static int elytraRollStrength = 20;
	@Range(min = 0, max = 1000)
	public static int elytraTickDelay = 10;
	public static boolean frontKeyToggleMode = false;
	@Range(min = 1, max = 30)
	public static float expectedThirdPersonDistance = 4;

	private static final Set<EntityType<?>> entityBlocklist = Sets.newHashSet();

	public static void onChanged(String path) {
		if ("mountingEntityBlocklist".equals(path)) {
			entityBlocklist.clear();
			for (String id : mountingEntityBlocklist) {
				//				if (id.startsWith("#")) {
				//					if (id.length() == 1)
				//						continue;
				//					id = id.substring(1);
				//					ResourceLocation rl = ResourceLocation.tryParse(id);
				//					if (rl == null)
				//						continue;
				//					TagKey<EntityType<?>> tag = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, rl);
				//				} else {
				EntityType.byString(id).ifPresent(entityBlocklist::add);
				//				}
			}
		}
	}

	public static boolean doMountSwitch(Entity entity) {
		return entity == null || !entityBlocklist.contains(entity.getType());
	}
}
