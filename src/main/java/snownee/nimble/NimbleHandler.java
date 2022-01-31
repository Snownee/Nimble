package snownee.nimble;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.phys.Vec3;
import snownee.nimble.event.CameraSetup;
import snownee.nimble.event.EntityMountEvent;
import snownee.nimble.mixin.CameraAccess;

@Environment(EnvType.CLIENT)
public class NimbleHandler {

	private static final KeyMapping kbFrontView = new KeyMapping(Nimble.MODID + ".keybind.frontView", GLFW.GLFW_KEY_F4, Nimble.MODID + ".gui.keygroup");
	private static boolean useFront = false;

	public static void preInit() {
		KeyBindingHelper.registerKeyBinding(kbFrontView);
		ClientTickEvents.END_CLIENT_TICK.register(NimbleHandler::tick);
	}

	static CameraType actualCameraMode = CameraType.FIRST_PERSON;
	static float distance = 0;
	static boolean elytraFlying = false;

	public static void cameraSetup(CameraSetup event) {
		if (!shouldWork())
			return;
		Minecraft mc = Minecraft.getInstance();
		if (mc.isPaused())
			return;
		if (mc.player == null || mc.player.isSleeping())
			return;

		if (NimbleConfig.nimbleElytra || NimbleConfig.elytraRollScreen) {
			if (mc.player.isFallFlying()) {
				if (NimbleConfig.elytraRollScreen) {
					Vec3 look = mc.player.getLookAngle();
					look = new Vec3(look.x, 0, look.z);
					Vec3 motion = mc.player.getDeltaMovement();
					Vec3 move = new Vec3(motion.x, 0, motion.z).normalize();
					//event.getMatrix().rotate(Vector3f.ZP.rotationDegrees((float) (look.crossProduct(move).y * 10)));
					event.setRoll((float) look.cross(move).y * 10);
				}

				// sometimes if the game is too laggy, the specific tick may be skipped
				if (NimbleConfig.nimbleElytra && mc.player.getFallFlyingTicks() >= NimbleConfig.elytraTickDelay) {
					elytraFlying = true;
					setCameraType(actualCameraMode = CameraType.THIRD_PERSON_BACK);
				}
			} else if (NimbleConfig.nimbleElytra && elytraFlying) {
				actualCameraMode = CameraType.FIRST_PERSON;
				elytraFlying = false;
			}
		}

		if (useFront) {
			return;
		}

		if (getCameraType() == CameraType.THIRD_PERSON_BACK) {
			float ptick = mc.getDeltaFrameTime();
			distance += NimbleConfig.transitionSpeed * (actualCameraMode == CameraType.THIRD_PERSON_BACK ? ptick * 0.1F : -ptick * 0.15F);
		} else {
			distance = 0;
			return;
		}
		if (distance < 0) {
			setCameraType(CameraType.FIRST_PERSON);
		}
		distance = Mth.clamp(distance, 0, 1);
		if (distance < 1) {
			float f = Mth.sin((float) (distance * Math.PI) / 2);
			CameraAccess info = (CameraAccess) event.getInfo();
			info.callMove(info.callGetMaxZoom((1 - f) * 3), 0, 0);
		}
	}

	public static void onFrame(Minecraft mc) {
		if (!shouldWork())
			return;
		if (mc.isPaused())
			return;
		if (mc.player == null)
			return;

		CameraType mode = getCameraType();
		if (!useFront && mode == CameraType.THIRD_PERSON_FRONT) {
			setCameraType(mode = CameraType.FIRST_PERSON);
		}

		if (!NimbleConfig.frontKeyToggleMode) {
			useFront = kbFrontView.isDown();
		}

		if (useFront) {
			setCameraType(CameraType.THIRD_PERSON_FRONT);
			return;
		} else if (mode == CameraType.THIRD_PERSON_FRONT) {
			setCameraType(mode = actualCameraMode);
		}

		if (mode == CameraType.FIRST_PERSON) {
			actualCameraMode = CameraType.FIRST_PERSON;
			if (distance > 0) {
				setCameraType(CameraType.THIRD_PERSON_BACK);
			}
		} else if (distance == 0) {
			actualCameraMode = CameraType.THIRD_PERSON_BACK;
		}
	}

	public static void mountEvent(EntityMountEvent event) {
		if (shouldWork()) {
			Minecraft mc = Minecraft.getInstance();
			if (event.getEntity() == mc.player) {
				Entity vehicle = mc.player.getVehicle();
				if (vehicle instanceof AbstractHorse && !((AbstractHorse) vehicle).isSaddled()) {
					return;
				}
				setCameraType(event.isMounting() ? CameraType.THIRD_PERSON_BACK : CameraType.FIRST_PERSON);
			}
		}
	}

	private static void setCameraType(CameraType mode) {
		Minecraft.getInstance().options.setCameraType(mode);
	}

	private static CameraType getCameraType() {
		return Minecraft.getInstance().options.getCameraType();
	}

	private static boolean shouldWork() {
		return NimbleConfig.enable && getCameraType().ordinal() < 3;
	}

	private static void tick(Minecraft mc) {
		if (kbFrontView.consumeClick()) {
			useFront = !useFront;
		}
	}
}
