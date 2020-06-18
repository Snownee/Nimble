package snownee.nimble;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = Nimble.MODID, value = Dist.CLIENT)
public class NimbleHandler {

    private static final KeyBinding kbFrontView = new KeyBinding(Nimble.MODID + ".keybind.frontView", GLFW.GLFW_KEY_F4, Nimble.MODID + ".gui.keygroup");
    private static boolean useFront = false;

    @SubscribeEvent
    public static void preInit(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(kbFrontView);
    }

    static int actualCameraMode = 0;
    static float distance = 0;
    static boolean elytraFlying = false;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void cameraSetup(CameraSetup event) {
        if (!NimbleConfig.enable)
            return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.isGamePaused())
            return;
        if (mc.player == null || mc.player.isSleeping())
            return;

        if (NimbleConfig.nimbleElytra || NimbleConfig.elytraRollScreen) {
            if (mc.player.isElytraFlying()) {
                if (NimbleConfig.elytraRollScreen) {
                    Vec3d look = mc.player.getLookVec();
                    look = new Vec3d(look.x, 0, look.z);
                    Vec3d motion = mc.player.getMotion();
                    Vec3d move = new Vec3d(motion.x, 0, motion.z).normalize();
                    //event.getMatrix().rotate(Vector3f.ZP.rotationDegrees((float) (look.crossProduct(move).y * 10)));
                    event.setRoll((float) look.crossProduct(move).y * 10);
                }

                // sometimes if the game is too laggy, the specific tick may be skipped
                if (NimbleConfig.nimbleElytra && mc.player.getTicksElytraFlying() >= NimbleConfig.elytraTickDelay) {
                    elytraFlying = true;
                    setCameraMode(1);
                    actualCameraMode = 1;
                }
            } else if (NimbleConfig.nimbleElytra && elytraFlying) {
                actualCameraMode = 0;
                elytraFlying = false;
            }
        }

        if (useFront) {
            return;
        }

        if (getCameraMode() == 1) {
            float ptick = mc.getRenderPartialTicks();
            float delta = 0.05F + (float) Math.sin(distance / 3 * Math.PI) * 0.15F * ptick;
            distance += actualCameraMode == 1 ? delta : -delta;
        } else {
            distance = 0;
            return;
        }
        if (distance < 0) {
            setCameraMode(0);
        }
        distance = Math.min(distance, 3);
        if (distance < 3) {
            ActiveRenderInfo info = event.getInfo();
            info.movePosition(-info.calcCameraDistance(distance - 3), 0, 0);
            //event.getMatrix().translate(0, 0, );
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onFrame(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.START)
            return;
        if (!NimbleConfig.enable)
            return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.isGamePaused())
            return;
        if (mc.player == null)
            return;

        int mode = getCameraMode();
        if (!useFront && mode == 2) {
            setCameraMode(mode = 0);
        }

        if (!NimbleConfig.frontKeyToggleMode) {
            useFront = kbFrontView.isKeyDown();
        } else if (kbFrontView.isPressed()) {
            useFront = !useFront;
        }

        if (useFront) {
            setCameraMode(2);
            return;
        } else if (mode == 2) {
            setCameraMode(mode = actualCameraMode);
        }

        if (mode == 0) {
            actualCameraMode = 0;
            if (distance > 0) {
                setCameraMode(1);
            }
        } else if (distance == 0) {
            actualCameraMode = 1;
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void mountEvent(EntityMountEvent event) {
        if (NimbleConfig.nimbleMounting) {
            Minecraft mc = Minecraft.getInstance();
            if (event.getEntity() == mc.player) {
                setCameraMode(event.isMounting() ? 1 : 0);
            }
        }
    }

    private static void setCameraMode(int mode) {
        Minecraft.getInstance().gameSettings.thirdPersonView = mode;
    }

    private static int getCameraMode() {
        return Minecraft.getInstance().gameSettings.thirdPersonView;
    }
}
