package snownee.nimble;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.commons.lang3.tuple.Pair;
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
    private static ModConfig config;

    public Nimble()
    {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        Pair<ModConfig, ForgeConfigSpec> configPair = new ForgeConfigSpec.Builder().configure(ModConfig::new);
        ModLoadingContext.get().registerConfig(Type.CLIENT, configPair.getRight());
        config = configPair.getLeft();
    }

    @SubscribeEvent
    public void preInit(FMLClientSetupEvent event)
    {
        ClientRegistry.registerKeyBinding(kbFrontView);
    }

    static int actualCameraMode = 0;
    static float distance = 0;
    static boolean elytraFlying = false;

    @SubscribeEvent
    public static void cameraSetup(CameraSetup event)
    {
        if (!config.enable.get())
            return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.isGamePaused())
            return;
        if (mc.player == null)
            return;

        if (config.nimbleElytra.get() || config.elytraRollScreen.get())
        {
            if (mc.player.isElytraFlying())
            {
                if (config.elytraRollScreen.get())
                {
                    Vec3d look = mc.player.getLookVec();
                    look = new Vec3d(look.x, 0, look.z);
                    Vec3d motion = mc.player.getMotion();
                    Vec3d move = new Vec3d(motion.x, 0, motion.z).normalize();
                    event.setRoll((float) look.crossProduct(move).y * 10);
                }

                if (config.nimbleElytra.get() && mc.player.getTicksElytraFlying() == config.elytraTickDelay.get())
                {
                    elytraFlying = true;
                    setCameraMode(1);
                    actualCameraMode = 1;
                }
            }
            else if (config.nimbleElytra.get() && elytraFlying)
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
        if (!config.enable.get())
            return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.isGamePaused())
            return;
        if (mc.player == null)
            return;

        if (!config.frontKeyToggleMode.get() && kbFrontView.isKeyDown())
        {
            setCameraMode(2);
            return;
        }
        if (config.frontKeyToggleMode.get() && kbFrontView.isPressed())
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
        if (config.nimbleMounting.get())
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
        // horrible hack to let global render reset states
        flag = !flag;
        Minecraft.getInstance().player.rotationPitch += flag ? 0.000001 : -0.000001;
    }

    private static int getCameraMode()
    {
        return Minecraft.getInstance().gameSettings.thirdPersonView;
    }
}
