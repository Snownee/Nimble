package snownee.nimble.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.Camera;

@Mixin(Camera.class)
public interface CameraAccess {

	@Invoker
	void callMove(double d, double e, double f);

	@Invoker
	double callGetMaxZoom(double d);

	@Invoker
	void callSetPosition(double x, double y, double z);

	@Accessor
	float getEyeHeight();

	@Accessor
	float getEyeHeightOld();

}
