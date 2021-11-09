package snownee.nimble.event;

import net.minecraft.world.entity.Entity;

public class EntityMountEvent {

	private final Entity entity;
	private final boolean isMounting;

	public EntityMountEvent(Entity entity, boolean isMounting) {
		this.entity = entity;
		this.isMounting = isMounting;
	}

	public Entity getEntity() {
		return entity;
	}

	public boolean isMounting() {
		return isMounting;
	}
}
