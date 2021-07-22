package snownee.nimble;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(Dist.CLIENT)
public class NimbleHandler {

    private static final KeyBinding kbFrontView = new KeyBinding(Nimble.MODID + ".keybind.frontView", GLFW.GLFW_KEY_F4, Nimble.MODID + ".gui.keygroup");
    private static boolean useFront = false;

    public static void preInit(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(kbFrontView);
    }

    static PointOfView actualCameraMode = PointOfView.FIRST_PERSON;
    static float distance = 0;
    static boolean elytraFlying = false;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void cameraSetup(CameraSetup event) {
        if (!shouldWork())
            return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.isGamePaused())
            return;
        if (mc.player == null || mc.player.isSleeping())
            return;

        if (NimbleConfig.nimbleElytra || NimbleConfig.elytraRollScreen) {
            if (mc.player.isElytraFlying()) {
                if (NimbleConfig.elytraRollScreen) {
                    Vector3d look = mc.player.getLookVec();
                    look = new Vector3d(look.x, 0, look.z);
                    Vector3d motion = mc.player.getMotion();
                    Vector3d move = new Vector3d(motion.x, 0, motion.z).normalize();
                    //event.getMatrix().rotate(Vector3f.ZP.rotationDegrees((float) (look.crossProduct(move).y * 10)));
                    event.setRoll((float) look.crossProduct(move).y * 10);
                }

                // sometimes if the game is too laggy, the specific tick may be skipped
                if (NimbleConfig.nimbleElytra && mc.player.getTicksElytraFlying() >= NimbleConfig.elytraTickDelay) {
                    elytraFlying = true;
                    setPointOfView(actualCameraMode = PointOfView.THIRD_PERSON_BACK);
                }
            } else if (NimbleConfig.nimbleElytra && elytraFlying) {
                actualCameraMode = PointOfView.FIRST_PERSON;
                elytraFlying = false;
            }
        }

        if (useFront) {
            return;
        }

        if (getPointOfView() == PointOfView.THIRD_PERSON_BACK) {
            float ptick = mc.getTickLength();
            distance += NimbleConfig.transitionSpeed * (actualCameraMode == PointOfView.THIRD_PERSON_BACK ? ptick * 0.1F : -ptick * 0.15F);
        } else {
            distance = 0;
            return;
        }
        if (distance < 0) {
            setPointOfView(PointOfView.FIRST_PERSON);
        }
        distance = MathHelper.clamp(distance, 0, 1);
        if (distance < 1) {
            float f = MathHelper.sin((float) (distance * Math.PI) / 2);
            ActiveRenderInfo info = event.getInfo();
            info.movePosition(-info.calcCameraDistance((f - 1) * 3), 0, 0);
            //event.getMatrix().translate(0, 0, );
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onFrame(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.START)
            return;
        if (!shouldWork())
            return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.isGamePaused())
            return;
        if (mc.player == null)
            return;

        PointOfView mode = getPointOfView();
        if (!useFront && mode == PointOfView.THIRD_PERSON_FRONT) {
            setPointOfView(mode = PointOfView.FIRST_PERSON);
        }

        if (!NimbleConfig.frontKeyToggleMode) {
            useFront = kbFrontView.isKeyDown();
        } else if (kbFrontView.isPressed()) {
            useFront = !useFront;
        }

        if (useFront) {
            setPointOfView(PointOfView.THIRD_PERSON_FRONT);
            return;
        } else if (mode == PointOfView.THIRD_PERSON_FRONT) {
            setPointOfView(mode = actualCameraMode);
        }

        if (mode == PointOfView.FIRST_PERSON) {
            actualCameraMode = PointOfView.FIRST_PERSON;
            if (distance > 0) {
                setPointOfView(PointOfView.THIRD_PERSON_BACK);
            }
        } else if (distance == 0) {
            actualCameraMode = PointOfView.THIRD_PERSON_BACK;
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void mountEvent(EntityMountEvent event) {
        if (shouldWork()) {
            Minecraft mc = Minecraft.getInstance();
            if (event.getEntity() == mc.player) {
                setPointOfView(event.isMounting() ? PointOfView.THIRD_PERSON_BACK : PointOfView.FIRST_PERSON);
            }
        }
    }

    private static void setPointOfView(PointOfView mode) {
        Minecraft.getInstance().gameSettings.func_243229_a(mode);
    }

    private static PointOfView getPointOfView() {
        return Minecraft.getInstance().gameSettings.func_243230_g();
    }

    private static boolean shouldWork() {
        return NimbleConfig.enable && getPointOfView().ordinal() < 3;
    }
}
