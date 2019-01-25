package snownee.nimble;

import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(Side.CLIENT)
@Mod(modid = Nimble.MODID, name = Nimble.NAME, version = "@VERSION_INJECT@", clientSideOnly = true)
public class Nimble
{
    public static final String MODID = "nimble";
    public static final String NAME = "Nimble";

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }

    static int actualCameraMode = 0;
    static float distance = 0;
    static boolean elytraFlying = false;

    @SubscribeEvent
    public static void cameraSetup(CameraSetup event)
    {
        if (!ModConfig.enable)
            return;
        Minecraft mc = Minecraft.getMinecraft();
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
                    Vec3d move = new Vec3d(mc.player.motionX, 0, mc.player.motionZ).normalize();
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
            GlStateManager.translate(0, 0, 3 - distance);
        }
    }

    @SubscribeEvent
    public static void onFrame(TickEvent.RenderTickEvent event)
    {
        if (event.phase != TickEvent.Phase.START)
            return;
        if (!ModConfig.enable)
            return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.isGamePaused())
            return;
        if (mc.player == null)
            return;

        if (getCameraMode() == 2)
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
            Minecraft mc = Minecraft.getMinecraft();
            if (event.getEntity() == mc.player)
            {
                setCameraMode(event.isMounting() ? 1 : 0);
            }
        }
    }

    private static void setCameraMode(int mode)
    {
        Minecraft.getMinecraft().gameSettings.thirdPersonView = mode;
    }

    private static int getCameraMode()
    {
        return Minecraft.getMinecraft().gameSettings.thirdPersonView;
    }
}
