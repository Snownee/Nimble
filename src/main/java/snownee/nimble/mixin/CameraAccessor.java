package snownee.nimble.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.Camera;

@Mixin(Camera.class)
public interface CameraAccessor {

	@Invoker("move")
	void _move(double d, double e, double f);

	@Invoker("getMaxZoom")
	double _getMaxZoom(double d);
}
