package snownee.nimble.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.client.model.geom.ModelPart;
import snownee.nimble.NimbleHandler;

@Mixin(ModelPart.class)
public class ModelPartMixin {

	@ModifyArg(
			method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V", at = @At(
					value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;compile(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"
			), index = 7
	)
	private float nimble$render(float original) {
		return NimbleHandler.isAnimating() ? original * NimbleHandler.getAlphaFactor() : original;
	}

}
