package org.lwjglx.debug.org.lwjgl.openal;

import java.util.function.IntFunction;

import org.lwjgl.PointerBuffer;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjglx.debug.openal.ALRT;

public class AL {

	public static ALCapabilities createCapabilities(ALCCapabilities alcCaps) {
		ALRT.checkContext("AL.createCapabilities");
		return org.lwjgl.openal.AL.createCapabilities(alcCaps);
	}

	public static ALCapabilities createCapabilities(ALCCapabilities alcCaps, IntFunction<PointerBuffer> bufferFactory) {
		ALRT.checkContext("AL.createCapabilities");
		return org.lwjgl.openal.AL.createCapabilities(alcCaps, bufferFactory);
	}
}
