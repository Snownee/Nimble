package snownee.nimble.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.CameraType;
import net.minecraft.client.Options;
import snownee.nimble.INimbleOptions;
import snownee.nimble.NimbleHandler;

@Mixin(Options.class)
public class OptionsMixin implements INimbleOptions {

	@Shadow
	private CameraType cameraType;

	@Inject(method = "getCameraType", at = @At("HEAD"), cancellable = true)
	private void nimble$getCameraType(CallbackInfoReturnable<CameraType> ci) {
		ci.setReturnValue(NimbleHandler.getCameraType());
	}

	@Override
	public CameraType nimble$getOriginalCameraType() {
		return cameraType;
	}

	@Override
	public void nimble$setOriginalCameraType(CameraType mode) {
		cameraType = mode;
	}

}
