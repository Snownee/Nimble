package snownee.nimble.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import snownee.nimble.INimbleOptions;
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

	@Redirect(
			method = "handleKeybinds", at = @At(
					value = "INVOKE", target = "Lnet/minecraft/client/Options;getCameraType()Lnet/minecraft/client/CameraType;"
			)
	)
	private CameraType nimble$handleKeybinds(Options options) {
		CameraType cameraType = ((INimbleOptions) options).nimble$getOriginalCameraType();
		if (cameraType.cycle() == CameraType.THIRD_PERSON_FRONT) {
			cameraType = CameraType.THIRD_PERSON_FRONT;
		}
		return cameraType;
	}

}
