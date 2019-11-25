package snownee.nimble;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.GlStateManager;

@EventBusSubscriber(Dist.CLIENT)
@Mod(Nimble.MODID)
public class Nimble
{
    public static final String MODID = "nimble";
    public static final String NAME = "Nimble";

    private static final Logger LOGGER = LogManager.getLogger(MODID);
    private static final KeyBinding kbFrontView = new KeyBinding(Nimble.MODID + ".keybind.frontView", GLFW.GLFW_KEY_F4, Nimble.MODID + ".gui.keygroup");
    private static boolean useFront = false;
    private static boolean flag = false;

    public Nimble()
    {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
    }

    @SubscribeEvent
    public void preInit(FMLClientSetupEvent event)
    {
        ClientRegistry.registerKeyBinding(kbFrontView);
    }

    static int actualCameraMode = 0;
    static float distance = 0;
    static boolean elytraFlying = false;
    
    // Called by fermion transformer's code
    public static CameraSetup fireCameraSetupEvent(ActiveRenderInfo info, float partial, float pitch, float yaw) {
        CameraSetup event = new CameraSetup(Minecraft.getInstance().gameRenderer, info, partial, yaw, pitch, 0);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    @SubscribeEvent // Todo fix this, event removed
    public static void cameraSetup(CameraSetup event)
    {
        if (!ModConfig.enable)
            return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.isGamePaused())
            return;
        if (mc.player == null)
            return;

        if (ModConfig.nimbleElytra || ModConfig.elytraRollScreen)
        {
            if (mc.player.isElytraFlying())
            {
                if (ModConfig.elytraRollScreen)
                {
                    Vec3d look = mc.player.getLookVec();
                    look = new Vec3d(look.x, 0, look.z);
                    Vec3d motion = mc.player.getMotion();
                    Vec3d move = new Vec3d(motion.x, 0, motion.z).normalize();
                    event.setRoll((float) look.crossProduct(move).y * 10);
                }

                if (ModConfig.nimbleElytra && mc.player.getTicksElytraFlying() == ModConfig.elytraTickDelay)
                {
                    elytraFlying = true;
                    setCameraMode(1);
                    actualCameraMode = 1;
                }
            }
            else if (ModConfig.nimbleElytra && elytraFlying)
            {
                actualCameraMode = 0;
                elytraFlying = false;
            }
        }

        if (kbFrontView.isKeyDown())
        {
            return;
        }

        if (getCameraMode() == 1)
        {
            float ptick = mc.getRenderPartialTicks();
            float delta = 0.05F + (float) Math.sin(distance / 3 * Math.PI) * 0.15F * ptick;
            distance += actualCameraMode == 1 ? delta : -delta;
        }
        else
        {
            distance = 0;
            return;
        }
        if (distance < 0)
        {
            setCameraMode(0);
        }
        distance = Math.min(distance, 3);
        if (distance < 3)
        {
            GlStateManager.translatef(0, 0, 3 - distance);
            resetView();
        }
    }

    @SubscribeEvent
    public static void onFrame(TickEvent.RenderTickEvent event)
    {
        if (event.phase != TickEvent.Phase.START)
            return;
        if (!ModConfig.enable)
            return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.isGamePaused())
            return;
        if (mc.player == null)
            return;

        if (!ModConfig.frontKeyToggleMode && kbFrontView.isKeyDown())
        {
            setCameraMode(2);
            return;
        }
        if (ModConfig.frontKeyToggleMode && kbFrontView.isPressed())
        {
            useFront = !useFront;
            if (useFront)
            {
                setCameraMode(2);
            }
        }
        if (getCameraMode() == 2 && !useFront)
        {
            setCameraMode(0);
        }

        if (getCameraMode() == 0)
        {
            actualCameraMode = 0;
            if (distance > 0)
            {
                setCameraMode(1);
            }
        }
        else if (distance == 0)
        {
            actualCameraMode = 1;
        }
    }

    @SubscribeEvent
    public static void mountEvent(EntityMountEvent event)
    {
        if (ModConfig.nimbleMounting)
        {
            Minecraft mc = Minecraft.getInstance();
            if (event.getEntity() == mc.player)
            {
                setCameraMode(event.isMounting() ? 1 : 0);
            }
        }
    }

    private static void setCameraMode(int mode)
    {
        Minecraft.getInstance().gameSettings.thirdPersonView = mode;
        resetView();
    }

    private static void resetView()
    {
        // horrible hack to let global render reset states todo 1.14?
        flag = !flag;
        Minecraft.getInstance().player.rotationPitch += flag ? 0.000001 : -0.000001;
    }

    private static int getCameraMode()
    {
        return Minecraft.getInstance().gameSettings.thirdPersonView;
    }
}
