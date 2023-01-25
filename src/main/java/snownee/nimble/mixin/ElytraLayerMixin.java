package snownee.nimble.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import snownee.nimble.NimbleHandler;

@Mixin(ElytraLayer.class)
public class ElytraLayerMixin {

	@Redirect(
			method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At(
					value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;armorCutoutNoCull(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"
			)
	)
	private RenderType nimble$armorCutoutNoCull(ResourceLocation location, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, LivingEntity entity) {
		if (entity == Minecraft.getInstance().player && NimbleHandler.isAnimating()) {
			return RenderType.entityTranslucentCull(location);
		}
		return RenderType.armorCutoutNoCull(location);
	}

}
