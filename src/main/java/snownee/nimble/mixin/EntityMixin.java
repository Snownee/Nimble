package snownee.nimble.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.Entity;
import snownee.nimble.NimbleHandler;

@Mixin(Entity.class)
public class EntityMixin {
	@Inject(at = @At("TAIL"), method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z")
	private void nimble$startRiding(Entity entity, boolean force, CallbackInfoReturnable<Boolean> ci) {
		Entity self = (Entity) (Object) this;
		if (self.level.isClientSide)
			NimbleHandler.mountEvent(self, true);
	}

	@Inject(at = @At("HEAD"), method = "removeVehicle()V")
	private void nimble$removeVehicle(CallbackInfo ci) {
		Entity self = (Entity) (Object) this;
		if (self.level.isClientSide)
			NimbleHandler.mountEvent(self, false);
	}
}
