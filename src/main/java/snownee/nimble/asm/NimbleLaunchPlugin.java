package snownee.nimble.asm;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;

import net.thesilkminer.mc.fermion.asm.api.LaunchPlugin;
import net.thesilkminer.mc.fermion.asm.api.MappingUtilities;
import net.thesilkminer.mc.fermion.asm.api.PluginMetadata.Builder;
import net.thesilkminer.mc.fermion.asm.api.descriptor.ClassDescriptor;
import net.thesilkminer.mc.fermion.asm.api.descriptor.MethodDescriptor;
import net.thesilkminer.mc.fermion.asm.api.transformer.TransformerData;
import net.thesilkminer.mc.fermion.asm.prefab.AbstractLaunchPlugin;
import net.thesilkminer.mc.fermion.asm.prefab.transformer.SingleTargetMethodTransformer;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public final class NimbleLaunchPlugin extends AbstractLaunchPlugin
{
    private static final Set<String> ROOT_PACKAGES = Collections.singleton("snownee.nimble.asm");

    public NimbleLaunchPlugin()
    {
        super("nimble");
        registerTransformer(new CameraSetupTransformer(this));
    }

    @Override
    protected void populateMetadata(@Nonnull Builder builder)
    {
        builder.setName("Nimble")
                .addAuthor("Snownee")
                .setVersion("0.0.2")
                .setDescription("Nimble is a mod by Snownee.");
    }

    @Nonnull
    @Override
    public Set<String> getRootPackages()
    {
        return ROOT_PACKAGES;
    }

    static final class CameraSetupTransformer extends SingleTargetMethodTransformer
    {

        CameraSetupTransformer(@Nonnull LaunchPlugin plugin)
        {
            super(TransformerData.Builder
                            .create()
                            .setOwningPlugin(plugin)
                            .setName("Camera Setup Event Transformer")
                            .setDescription("Post camera setup events")
                            .build(),
                    ClassDescriptor.of("net.minecraft.client.renderer.ActiveRenderInfo"),
                    MethodDescriptor.of("func_216772_a", Arrays.asList(
                            ClassDescriptor.of("net.minecraft.world.IBlockReader"),
                            ClassDescriptor.of("net.minecraft.entity.Entity"),
                            ClassDescriptor.of(boolean.class),
                            ClassDescriptor.of(boolean.class),
                            ClassDescriptor.of(float.class)
                    ), ClassDescriptor.of(void.class))
            );
        }

        @Nonnull @Override protected BiFunction<Integer, MethodVisitor, MethodVisitor> getMethodVisitorCreator()
        {
            return (api, parent) -> new MethodVisitor(api, parent)
            {
                boolean shouldInject = true;
                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface)
                {
                    // Lcom/mojang/blaze3d/platform/GlStateManager;rotatef(FFFF)V
                    if (shouldInject && opcode == Opcodes.INVOKESTATIC && owner.equals("com/mojang/blaze3d/platform/GlStateManager") && name.equals("rotatef") && descriptor.equals("(FFFF)V")) {
                        // inject right here!
                        injectCode();
                        shouldInject = false;
                    }
                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                }

                private void injectCode()
                {
                    final String pitchFieldName = MappingUtilities.INSTANCE.mapField("field_216797_i");
                    final String yawFieldName = MappingUtilities.INSTANCE.mapField("field_216798_j");

                    // this
                    super.visitVarInsn(Opcodes.ALOAD, 0);
                    // partial
                    super.visitVarInsn(Opcodes.FLOAD, 5);
                    // this.pitch
                    super.visitVarInsn(Opcodes.ALOAD, 0);
                    super.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/renderer/ActiveRenderInfo", pitchFieldName, "F");
                    // this.yaw
                    super.visitVarInsn(Opcodes.ALOAD, 0);
                    super.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/renderer/ActiveRenderInfo", yawFieldName, "F");
                    // Nimble.fireCameraSetupEvent; use "copy mixin target reference" in minecraft dev intellij plugin
                    // Lsnownee/nimble/Nimble;fireCameraSetupEvent(Lnet/minecraft/client/renderer/ActiveRenderInfo;FFF)Lnet/minecraftforge/client/event/EntityViewRenderEvent$CameraSetup;
                    super.visitMethodInsn(Opcodes.INVOKESTATIC, "snownee/nimble/Nimble", "fireCameraSetupEvent",
                            "(Lnet/minecraft/client/renderer/ActiveRenderInfo;FFF)Lnet/minecraftforge/client/event/EntityViewRenderEvent$CameraSetup;",
                            false);

                    final int eventVarIndex = 6;
                    // dup
                    super.visitVarInsn(Opcodes.ASTORE, eventVarIndex);

                    super.visitVarInsn(Opcodes.ALOAD, 0);
                    // Lnet/minecraftforge/client/event/EntityViewRenderEvent$CameraSetup;getYaw()F
                    super.visitVarInsn(Opcodes.ALOAD, eventVarIndex);
                    super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/client/event/EntityViewRenderEvent$CameraSetup", "getYaw",
                            "()F", false);
                    // this.yaw =
                    super.visitFieldInsn(Opcodes.PUTFIELD, "net/minecraft/client/renderer/ActiveRenderInfo", yawFieldName, "F");

                    super.visitVarInsn(Opcodes.ALOAD, 0);
                    // Lnet/minecraftforge/client/event/EntityViewRenderEvent$CameraSetup;getPitch()F
                    super.visitVarInsn(Opcodes.ALOAD, eventVarIndex);
                    super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/client/event/EntityViewRenderEvent$CameraSetup", "getPitch",
                            "()F", false);
                    // this.pitch =
                    super.visitFieldInsn(Opcodes.PUTFIELD, "net/minecraft/client/renderer/ActiveRenderInfo", pitchFieldName, "F");

                    //GlStateManager.rotatef(roll, 0, 0, 1);
                    // Lnet/minecraftforge/client/event/EntityViewRenderEvent$CameraSetup;getRoll()F
                    super.visitVarInsn(Opcodes.ALOAD, eventVarIndex);
                    super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/client/event/EntityViewRenderEvent$CameraSetup", "getRoll",
                            "()F", false);
                    super.visitInsn(Opcodes.FCONST_0);
                    super.visitInsn(Opcodes.FCONST_0);
                    super.visitInsn(Opcodes.FCONST_1);
                    // Lcom/mojang/blaze3d/platform/GlStateManager;rotatef(FFFF)V
                    super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/mojang/blaze3d/platform/GlStateManager", "rotatef", "(FFFF)V", false);
                }
            };
        }
    }
}
