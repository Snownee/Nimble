package snownee.nimble.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import snownee.nimble.NimbleHandler;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {

	private float nimble$factor = 1;

	@Inject(method = "<init>*", at = @At("TAIL"))
	private void nimble$init(CallbackInfo ci) {
		NimbleHandler.modelFading = true;
	}

	@Redirect(
			method = "renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IZLnet/minecraft/client/model/Model;FFFLnet/minecraft/resources/ResourceLocation;)V", at = @At(
			value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;armorCutoutNoCull(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"
	)
	)
	private RenderType nimble$armorCutoutNoCull(ResourceLocation location) {
		return nimble$factor == 1 ? RenderType.armorCutoutNoCull(location) : RenderType.entityTranslucentCull(location);
	}

	@Inject(
			method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At(
			value = "HEAD"
	)
	)
	private void nimble$render(PoseStack matrixStack, MultiBufferSource buffer, int packedLight, LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
		if (entity == Minecraft.getInstance().player) {
			nimble$factor = NimbleHandler.getAlphaFactor();
		}
	}

}
