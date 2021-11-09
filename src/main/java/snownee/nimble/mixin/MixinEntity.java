package snownee.nimble.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.Entity;
import snownee.nimble.NimbleHandler;
import snownee.nimble.event.EntityMountEvent;

@Mixin(Entity.class)
public class MixinEntity {
	@Inject(at = @At("TAIL"), method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z")
	private void nimble$startRiding(Entity entity, boolean force, CallbackInfoReturnable<Boolean> ci) {
		NimbleHandler.mountEvent(new EntityMountEvent((Entity) (Object) this, true));
	}

	@Inject(at = @At("TAIL"), method = "removeVehicle()V")
	private void nimble$removeVehicle(CallbackInfo ci) {
		NimbleHandler.mountEvent(new EntityMountEvent((Entity) (Object) this, false));
	}
}
