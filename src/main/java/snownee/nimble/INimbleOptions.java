package snownee.nimble;

import net.minecraft.client.CameraType;

public interface INimbleOptions {

	CameraType nimble$getOriginalCameraType();

	void nimble$setOriginalCameraType(CameraType mode);

}