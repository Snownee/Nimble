package snownee.nimble.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import snownee.nimble.NimbleHandler;

@Mixin(Minecraft.class)
public class MinecraftMixin {

	@Inject(
			at = @At(
					value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;render(FJZ)V"
			), method = "runTick(Z)V"
	)
	private void nimble$runTick(boolean bl, CallbackInfo ci) {
		NimbleHandler.onFrame((Minecraft) (Object) this);
	}

}
