package snownee.nimble.mixin;

import java.util.List;

import net.minecraft.world.entity.WalkAnimationState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import snownee.nimble.NimbleHandler;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {

	@Shadow
	protected List<RenderLayer<T, M>> layers;

	protected LivingEntityRendererMixin(Context context) {
		super(context);
	}

	@Inject(
			method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(
			"HEAD"
	), cancellable = true
	)
	private void nimble$render(T entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
		if (entity != Minecraft.getInstance().player || NimbleHandler.getCameraType() != CameraType.THIRD_PERSON_BACK) {
			return;
		}
		float k;
		Direction direction;
		matrixStack.pushPose();
		getModel().attackTime = this.getAttackAnim(entity, partialTicks);
		getModel().riding = entity.isPassenger();
		getModel().young = entity.isBaby();
		float f = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
		float g = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
		float h = g - f;
		if (entity.isPassenger() && entity.getVehicle() instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity) entity.getVehicle();
			f = Mth.rotLerp(partialTicks, livingEntity.yBodyRotO, livingEntity.yBodyRot);
			h = g - f;
			float i = Mth.wrapDegrees(h);
			if (i < -85.0f) {
				i = -85.0f;
			}
			if (i >= 85.0f) {
				i = 85.0f;
			}
			f = g - i;
			if (i * i > 2500.0f) {
				f += i * 0.2f;
			}
			h = g - f;
		}
		float j = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
		if (LivingEntityRenderer.isEntityUpsideDown(entity)) {
			j *= -1.0f;
			h *= -1.0f;
		}
		if (entity.hasPose(Pose.SLEEPING) && (direction = entity.getBedOrientation()) != null) {
			k = entity.getEyeHeight(Pose.STANDING) - 0.1f;
			matrixStack.translate((-direction.getStepX()) * k, 0.0f, (-direction.getStepZ()) * k);
		}
		float i = this.getBob(entity, partialTicks);
		this.setupRotations(entity, matrixStack, i, f, partialTicks);
		matrixStack.scale(-1.0f, -1.0f, 1.0f);
		this.scale(entity, matrixStack, partialTicks);
		matrixStack.translate(0.0f, -1.501f, 0.0f);
		k = 0.0f;
		float l = 0.0f;
		if (!entity.isPassenger() && entity.isAlive()) {
			WalkAnimationState animState = entity.walkAnimation;
			k = animState.speed(partialTicks);
			l = animState.position(partialTicks);
			if (entity.isBaby()) {
				l *= 3.0f;
			}
			if (k > 1.0f) {
				k = 1.0f;
			}
		}
		getModel().prepareMobModel(entity, l, k, partialTicks);
		getModel().setupAnim(entity, l, k, i, h, j);
		Minecraft minecraft = Minecraft.getInstance();
		boolean bl = this.isBodyVisible(entity);
		boolean bl2 = !bl && !entity.isInvisibleTo(minecraft.player);
		boolean bl3 = minecraft.shouldEntityAppearGlowing(entity);
		RenderType renderType = this.getRenderType(entity, bl, bl2, bl3);
		NimbleHandler.applyAlphaFactor();
		if (renderType != null) {
			VertexConsumer vertexConsumer = buffer.getBuffer(renderType);
			int m = LivingEntityRenderer.getOverlayCoords(entity, this.getWhiteOverlayProgress(entity, partialTicks));
			getModel().renderToBuffer(matrixStack, vertexConsumer, packedLight, m, 1.0f, 1.0f, 1.0f, bl2 ? 0.15f : 1.0f);
		}
		if (!entity.isSpectator()) {
			for (RenderLayer<T, M> renderLayer : this.layers) {
				renderLayer.render(matrixStack, buffer, packedLight, entity, l, k, partialTicks, i, h, j);
			}
		}
		matrixStack.popPose();
		super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
		NimbleHandler.clearAlphaFactor();
		ci.cancel();
	}

	@Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
	private void nimble$getRenderType(T entity, boolean bl, boolean bl2, boolean bl3, CallbackInfoReturnable<RenderType> ci) {
		if (entity != Minecraft.getInstance().player || !NimbleHandler.isAnimating()) {
			return;
		}
		if (!bl2 && bl) {
			ResourceLocation resourceLocation = this.getTextureLocation(entity);
			ci.setReturnValue(RenderType.entityTranslucentCull(resourceLocation));
		}
	}

	@Shadow
	protected abstract float getAttackAnim(T livingBase, float partialTickTime);

	@Shadow
	protected abstract float getBob(T livingBase, float partialTicks);

	@Shadow
	protected abstract void setupRotations(T entityLiving, PoseStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks);

	@Shadow
	protected abstract void scale(T livingEntity, PoseStack matrixStack, float partialTickTime);

	@Shadow
	protected abstract boolean isBodyVisible(T livingEntity);

	@Shadow
	protected abstract RenderType getRenderType(T livingEntity, boolean bl, boolean bl2, boolean bl3);

	@Shadow
	protected abstract float getWhiteOverlayProgress(T livingEntity, float partialTicks);
}
