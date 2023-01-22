package snownee.nimble.mixin;

import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.GameRenderer;
import snownee.nimble.NimbleHandler;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Inject(
			at = @At(
					value = "INVOKE", target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V", shift = Shift.AFTER
			), method = "renderLevel(FJLcom/mojang/blaze3d/vertex/PoseStack;)V"
	)
	private void nimble$renderLevel(float pPartialTicks, long pFinishTimeNano, PoseStack pMatrixStack, CallbackInfo ci) {
		MutableFloat roll = new MutableFloat();
		NimbleHandler.cameraSetup(((GameRenderer) (Object) this).getMainCamera(), roll::setValue);
		if (roll.floatValue() != 0)
			pMatrixStack.mulPose(Axis.ZP.rotationDegrees(roll.floatValue()));
	}
}
