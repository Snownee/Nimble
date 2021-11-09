package snownee.nimble.event;

import net.minecraft.client.Camera;

public class CameraSetup {

	private final Camera camera;
	private float roll;

	public CameraSetup(Camera camera) {
		this.camera = camera;
	}

	public Camera getInfo() {
		return camera;
	}

	public void setRoll(float roll) {
		this.roll = roll;
	}

	public float getRoll() {
		return roll;
	}

}
