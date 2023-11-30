package snownee.nimble;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import it.unimi.dsi.fastutil.floats.FloatConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.phys.Vec3;
import snownee.nimble.mixin.CameraAccess;

public class NimbleHandler {

	public static final KeyMapping kbFrontView = new KeyMapping(Nimble.ID + ".keybind.frontView", GLFW.GLFW_KEY_F4, Nimble.ID + ".gui.keygroup");

	private static boolean useFront;
	private static CameraType oMode;
	private static CameraType mode;
	private static CameraType targetMode;
	private static float distance;
	private static boolean elytraFlying;
	private static boolean nimbleMounting;
	private static float roll;
	public static boolean modelFading;

	public static void tick(Minecraft mc) {
		if (!shouldWork() || mc.isPaused() || mc.player == null) {
			return;
		}
		if (NimbleConfig.frontKeyToggleMode && kbFrontView.consumeClick()) {
			useFront = !useFront;
			if (useFront) {
				setCameraType(CameraType.THIRD_PERSON_FRONT);
			}
		}
		if (NimbleConfig.nimbleElytra) {
			if (mc.player.isFallFlying()) {
				if (mc.player.getFallFlyingTicks() == NimbleConfig.elytraTickDelay && getOriginalCameraType() == CameraType.FIRST_PERSON) {
					elytraFlying = true;
					targetMode = CameraType.THIRD_PERSON_BACK;
				}
			} else {
				elytraFlying = false;
			}
		}
	}

	public static void onFrame(Minecraft mc) {
		if (!shouldWork() || mc.isPaused() || mc.player == null || NimbleConfig.frontKeyToggleMode) {
			return;
		}
		useFront = kbFrontView.isDown();
		if (useFront) {
			setCameraType(CameraType.THIRD_PERSON_FRONT);
		}
	}

	public static void mountEvent(@NotNull Entity entity, boolean mount) {
		if (!shouldWork() || !NimbleConfig.nimbleMounting)
			return;
		Minecraft mc = Minecraft.getInstance();
		if (entity == mc.player) {
			Entity vehicle = mc.player.getVehicle();
			if (!NimbleConfig.doMountSwitch(vehicle)) {
				return;
			}
			if (vehicle instanceof AbstractHorse horse && !horse.isSaddled()) {
				return;
			}
			if (mount && getOriginalCameraType() == CameraType.FIRST_PERSON) {
				nimbleMounting = true;
				targetMode = CameraType.THIRD_PERSON_BACK;
			} else {
				nimbleMounting = false;
			}
		}
	}

	public static void cameraSetup(Camera camera, FloatConsumer rollSetter) {
		if (!shouldWork())
			return;
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.player.isSleeping())
			return;

		oMode = getCameraType(); // update mode
		CameraType originalMode = getOriginalCameraType();
		if (elytraFlying || nimbleMounting) {
			if (originalMode == CameraType.THIRD_PERSON_BACK) {
				setOriginalCameraType(targetMode = originalMode = CameraType.FIRST_PERSON);
				elytraFlying = nimbleMounting = false;
			}
		} else {
			targetMode = originalMode;
		}

		if (!useFront && mode == CameraType.THIRD_PERSON_FRONT) {
			setCameraType(originalMode);
		}

		float pTicks = mc.getFrameTime();
		if (NimbleConfig.elytraRollScreen && mc.player.isFallFlying()) {
			Vec3 look = mc.player.getViewVector(pTicks);
			look = new Vec3(look.x, 0, look.z);
			Vec3 motion = mc.player.getDeltaMovement();
			Vec3 move = new Vec3(motion.x, 0, motion.z).normalize();
			float nRoll = (float) look.cross(move).y * NimbleConfig.elytraRollStrength;
			roll = Mth.lerp(pTicks, roll, nRoll);
			rollSetter.accept(roll);
		}

		if (useFront) {
			return;
		}

		float deltaTime = mc.getDeltaFrameTime();
		distance += NimbleConfig.transitionSpeed * (targetMode == CameraType.THIRD_PERSON_BACK ? deltaTime * 0.1F : -deltaTime * 0.15F);
		distance = Mth.clamp(distance, 0, 1);
		boolean animating = oMode != CameraType.FIRST_PERSON;
		setCameraType(distance == 0 ? CameraType.FIRST_PERSON : CameraType.THIRD_PERSON_BACK);
		if (animating) {
			CameraAccess info = (CameraAccess) camera;
			Entity entity = camera.getEntity();
			double x = Mth.lerp(pTicks, entity.xo, entity.getX());
			double y = Mth.lerp(pTicks, entity.yo, entity.getY()) + Mth.lerp(pTicks, info.getEyeHeightOld(), info.getEyeHeight());
			double z = Mth.lerp(pTicks, entity.zo, entity.getZ());
			info.callSetPosition(x, y, z);

			float f = Mth.sin((float) (distance * Math.PI) / 2);
			float expectDistance = NimbleConfig.expectedThirdPersonDistance;
			if (modelFading) {
				f *= expectDistance;
			} else {
				f = 1 + f * (expectDistance - 1);
			}
			info.callMove(-info.callGetMaxZoom(f), 0, 0);

			mc.gameRenderer.itemInHandRenderer.itemUsed(InteractionHand.MAIN_HAND);
			mc.gameRenderer.itemInHandRenderer.itemUsed(InteractionHand.OFF_HAND);
		}
	}

	public static boolean shouldWork() {
		return NimbleConfig.enable && getOriginalCameraType().ordinal() < 3;
	}

	public static CameraType getOriginalCameraType() {
		INimbleOptions options = (INimbleOptions) Minecraft.getInstance().options;
		CameraType type = options.nimble$getOriginalCameraType();
		if (type == CameraType.THIRD_PERSON_FRONT) {
			setOriginalCameraType(type = CameraType.FIRST_PERSON);
		}
		return type;
	}

	public static void setOriginalCameraType(CameraType mode) {
		INimbleOptions options = (INimbleOptions) Minecraft.getInstance().options;
		options.nimble$setOriginalCameraType(mode);
	}

	public static CameraType getCameraType() {
		if (mode == null) {
			mode = getOriginalCameraType();
		}
		return mode;
	}

	public static void setCameraType(CameraType mode) {
		if (getCameraType() != mode) {
			boolean checkPostEffect = getCameraType().isFirstPerson() != mode.isFirstPerson();
			NimbleHandler.mode = mode;
			Minecraft mc = Minecraft.getInstance();
			if (checkPostEffect) {
				mc.gameRenderer.checkEntityPostEffect(mc.options.getCameraType().isFirstPerson() ? mc.getCameraEntity() : null);
			}
			mc.levelRenderer.needsUpdate();
		}
	}

	private static final ThreadLocal<Float> alphaFactor = ThreadLocal.withInitial(() -> 1F);

	public static float getAlphaFactor() {
		float f = alphaFactor.get();
		return f == 1 ? 0 : f; // don't know why but it works
	}

	public static void applyAlphaFactor() {
		alphaFactor.set(distance);
	}

	public static void clearAlphaFactor() {
		alphaFactor.remove();
	}

	public static boolean isAnimating() {
		return oMode == CameraType.THIRD_PERSON_BACK && distance < 1;
	}
}
