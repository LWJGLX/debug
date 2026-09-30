package org.lwjglx.debug.org.lwjgl.openal;

import java.util.function.IntFunction;

import org.lwjgl.PointerBuffer;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjglx.debug.openal.ALDevice;
import org.lwjglx.debug.openal.ALRT;

public class ALC {

	public static ALCCapabilities createCapabilities(long device) {
		ALDevice dev = ALDevice.get(device);
		ALRT.checkDevice(dev, device, "ALC.createCapabilities");
		return org.lwjgl.openal.ALC.createCapabilities(device);
	}

	public static ALCCapabilities createCapabilities(long device, IntFunction<PointerBuffer> bufferFactory) {
		ALDevice dev = ALDevice.get(device);
		ALRT.checkDevice(dev, device, "ALC.createCapabilities");
		return org.lwjgl.openal.ALC.createCapabilities(device, bufferFactory);
	}

	public static void destroy() {
		for (ALDevice dev : ALDevice.allDevices()) {
			if (dev.state == org.lwjglx.debug.ResourceState.ALIVE) {
				org.lwjglx.debug.RT.throwISEOrLogError("ALC.destroy: device 0x" + Long.toHexString(dev.handle) + " was never closed");
			}
		}
		org.lwjgl.openal.ALC.destroy();
	}
}
