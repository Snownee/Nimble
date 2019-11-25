package snownee.nimble.asm;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;

import net.thesilkminer.mc.fermion.asm.api.LaunchPlugin;
import net.thesilkminer.mc.fermion.asm.api.PluginMetadata.Builder;
import net.thesilkminer.mc.fermion.asm.api.descriptor.ClassDescriptor;
import net.thesilkminer.mc.fermion.asm.api.transformer.TransformerData;
import net.thesilkminer.mc.fermion.asm.prefab.AbstractLaunchPlugin;
import net.thesilkminer.mc.fermion.asm.prefab.AbstractTransformer;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
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

    static final class CameraSetupTransformer extends AbstractTransformer
    {

        CameraSetupTransformer(@Nonnull LaunchPlugin plugin)
        {
            super(TransformerData.Builder.create()
                            .setOwningPlugin(plugin)
                            .setName("Camera Setup Event Transformer")
                            .setDescription("Why the heck do I need a description")
                            .build(),
                    ClassDescriptor.of("net.minecraft.client.renderer.ActiveRenderInfo")
            );
        }

        @Nonnull
        @Override
        public BiFunction<Integer, ClassVisitor, ClassVisitor> getClassVisitorCreator()
        {
            return (version, parent) -> new ClassVisitor(version, parent)
            {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions)
                {
                    MethodVisitor parent = super.visitMethod(access, name, descriptor, signature, exceptions);
                    boolean mcp = name.equals("update");
                    if (mcp || name.equals("func_216772_a"))
                    {
                        return new MethodVisitor(api, parent)
                        {
                            boolean line57Visited = false;
                            @Override 
                            public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack)
                            {
                                super.visitFrame(type, nLocal, local, nStack, stack);
                                if (line57Visited && type == Opcodes.F_CHOP) {
                                    injectCode();
                                }
                            }

                            @Override
                            public void visitMaxs(int maxStack, int maxLocals)
                            {
                                super.visitMaxs(Math.max(maxStack, 4), Math.max(maxLocals, 6));
                            }

                            @Override
                            public void visitLineNumber(int line, Label start)
                            {
                                super.visitLineNumber(line, start);
                                if (line == 57)
                                {
                                    line57Visited = true; // maybe use a different logic in the future
                                }
                            }

                            private void injectCode()
                            {
                                final String pitchFieldName = mcp ? "pitch" : "field_216797_i";
                                final String yawFieldName = mcp ? "yaw" : "field_216798_j";

                                // this
                                super.visitVarInsn(Opcodes.ALOAD, 0);
                                // partial
                                super.visitVarInsn(Opcodes.AALOAD, 5);
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

                                // Lnet/minecraftforge/client/event/EntityViewRenderEvent$CameraSetup;getYaw()F
                                super.visitVarInsn(Opcodes.ALOAD, eventVarIndex);
                                super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/client/event/EntityViewRenderEvent$CameraSetup", "getYaw",
                                        "()F", false);
                                // this.yaw = 
                                super.visitFieldInsn(Opcodes.PUTFIELD, "net/minecraft/client/renderer/ActiveRenderInfo", yawFieldName, "F");

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
                                super.visitInsn(Opcodes.DCONST_0);
                                super.visitInsn(Opcodes.DCONST_0);
                                super.visitInsn(Opcodes.DCONST_1);
                                // Lcom/mojang/blaze3d/platform/GlStateManager;rotatef(FFFF)V
                                super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/mojang/blaze3d/platform/GlStateManager", "rotatef", "(FFFF)V", false);
                            }
                        };
                    }
                    return parent;
                }
            };
        }
    }
}
