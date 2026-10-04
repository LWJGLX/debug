package org.lwjglx.debug.org.lwjgl.openal;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjglx.debug.Log;
import org.lwjglx.debug.Properties;
import org.lwjglx.debug.RT;
import org.lwjglx.debug.openal.ALContext;
import org.lwjglx.debug.openal.ALDevice;
import org.lwjglx.debug.openal.ALObjects;
import org.lwjglx.debug.openal.ALRT;
import org.lwjglx.debug.ResourceState;

public class ALC10 {

	public static long alcOpenDevice(ByteBuffer devicename) {
		long device = org.lwjgl.openal.ALC10.alcOpenDevice(devicename);
		if (device != 0L) {
			ALDevice.create(device);
		}
		return device;
	}

	public static long alcOpenDevice(CharSequence devicename) {
		long device = org.lwjgl.openal.ALC10.alcOpenDevice(devicename);
		if (device != 0L) {
			ALDevice.create(device);
		}
		return device;
	}

	public static boolean alcCloseDevice(long device) {
		if (device == 0L) {
			return false;
		}
		ALDevice dev = ALDevice.get(device);
		ALRT.checkDevice(dev, device, "alcCloseDevice");
		if (dev != null) {
			for (ALContext ctx : dev.contexts) {
				if (ctx.state == ResourceState.ALIVE) {
					RT.throwISEOrLogError("alcCloseDevice called on device 0x" + Long.toHexString(device)
							+ " while context 0x" + Long.toHexString(ctx.handle) + " is still alive");
				}
			}
			boolean hasLeaks = false;
			int liveBuffers = 0;
			for (ALObjects.Buffer b : dev.buffers.values()) {
				if (b.state == ResourceState.ALIVE) {
					liveBuffers++;
				}
			}
			if (liveBuffers > 0) {
				hasLeaks = true;
				Log.warn("OpenAL device 0x" + Long.toHexString(device) + " closed with " + liveBuffers + " un-deleted buffer(s)");
			}
			if (hasLeaks && Properties.FAIL_ON_LEAKS.enabled) {
				RT.throwISEOrLogError("OpenAL device 0x" + Long.toHexString(device) + " closed with leaked resources");
			}
		}
		boolean res = org.lwjgl.openal.ALC10.alcCloseDevice(device);
		if (res && dev != null) {
			dev.state = ResourceState.DELETED;
		}
		return res;
	}

	public static long alcCreateContext(long device, IntBuffer attrlist) {
		ALDevice dev = ALDevice.get(device);
		ALRT.checkDevice(dev, device, "alcCreateContext");
		long context = org.lwjgl.openal.ALC10.alcCreateContext(device, attrlist);
		if (context != 0L && dev != null) {
			ALContext.create(context, dev);
		}
		return context;
	}

	public static long alcCreateContext(long device, int[] attrlist) {
		ALDevice dev = ALDevice.get(device);
		ALRT.checkDevice(dev, device, "alcCreateContext");
		long context = org.lwjgl.openal.ALC10.alcCreateContext(device, attrlist);
		if (context != 0L && dev != null) {
			ALContext.create(context, dev);
		}
		return context;
	}

	public static boolean alcMakeContextCurrent(long context) {
		if (context == 0L) {
			ALContext.makeCurrent(null);
			return org.lwjgl.openal.ALC10.alcMakeContextCurrent(0L);
		}
		ALContext ctx = ALContext.get(context);
		if (ctx == null) {
			RT.throwISEOrLogError("alcMakeContextCurrent: unknown context handle 0x" + Long.toHexString(context));
		} else if (ctx.state == ResourceState.DELETED) {
			RT.throwISEOrLogError("alcMakeContextCurrent: context 0x" + Long.toHexString(context)
					+ " has already been destroyed (use-after-free)");
		}
		boolean res = org.lwjgl.openal.ALC10.alcMakeContextCurrent(context);
		if (res) {
			ALContext.makeCurrent(ctx);
		}
		return res;
	}

	public static void alcProcessContext(long context) {
		ALContext ctx = ALContext.get(context);
		if (ctx != null && ctx.state == ResourceState.DELETED) {
			RT.throwISEOrLogError("alcProcessContext: context has been destroyed (use-after-free)");
		}
		org.lwjgl.openal.ALC10.alcProcessContext(context);
	}

	public static void alcSuspendContext(long context) {
		ALContext ctx = ALContext.get(context);
		if (ctx != null && ctx.state == ResourceState.DELETED) {
			RT.throwISEOrLogError("alcSuspendContext: context has been destroyed (use-after-free)");
		}
		org.lwjgl.openal.ALC10.alcSuspendContext(context);
	}

	public static void alcDestroyContext(long context) {
		if (context == 0L) {
			return;
		}
		ALContext ctx = ALContext.get(context);
		if (ctx == null) {
			RT.throwISEOrLogError("alcDestroyContext: unknown context handle 0x" + Long.toHexString(context));
			org.lwjgl.openal.ALC10.alcDestroyContext(context);
			return;
		}
		if (ctx.state == ResourceState.DELETED) {
			RT.throwISEOrLogError("alcDestroyContext: context 0x" + Long.toHexString(context)
					+ " has already been destroyed (double-free)");
			return;
		}

		try {
			boolean hasLeaks = false;

			int liveSources = 0;
			for (ALObjects.Source s : ctx.sources.values()) {
				if (s.state == ResourceState.ALIVE) {
					liveSources++;
				}
			}
			if (liveSources > 0) {
				hasLeaks = true;
				Log.warn("OpenAL context 0x" + Long.toHexString(context) + " destroyed with " + liveSources + " un-deleted source(s)");
			}

			int liveEffects = 0;
			for (ALObjects.Effect e : ctx.effects.values()) {
				if (e.state == ResourceState.ALIVE) {
					liveEffects++;
				}
			}
			if (liveEffects > 0) {
				hasLeaks = true;
				Log.warn("OpenAL context 0x" + Long.toHexString(context) + " destroyed with " + liveEffects + " un-deleted effect(s)");
			}

			int liveFilters = 0;
			for (ALObjects.Filter f : ctx.filters.values()) {
				if (f.state == ResourceState.ALIVE) {
					liveFilters++;
				}
			}
			if (liveFilters > 0) {
				hasLeaks = true;
				Log.warn("OpenAL context 0x" + Long.toHexString(context) + " destroyed with " + liveFilters + " un-deleted filter(s)");
			}

			int liveAuxSlots = 0;
			for (ALObjects.AuxiliaryEffectSlot a : ctx.auxSlots.values()) {
				if (a.state == ResourceState.ALIVE) {
					liveAuxSlots++;
				}
			}
			if (liveAuxSlots > 0) {
				hasLeaks = true;
				Log.warn("OpenAL context 0x" + Long.toHexString(context) + " destroyed with " + liveAuxSlots + " un-deleted auxiliary effect slot(s)");
			}

			if (hasLeaks && Properties.FAIL_ON_LEAKS.enabled) {
				RT.throwISEOrLogError("OpenAL context 0x" + Long.toHexString(context) + " destroyed with leaked resources");
			}
		} finally {
			org.lwjgl.openal.ALC10.alcDestroyContext(context);
			ctx.state = ResourceState.DELETED;
			if (ALContext.currentContext() == ctx) {
				ALContext.makeCurrent(null);
			}
			if (ctx.device != null) {
				ctx.device.contexts.remove(ctx);
			}
		}
	}

	public static long alcGetContextsDevice(long context) {
		ALContext ctx = ALContext.get(context);
		if (ctx != null && ctx.state == ResourceState.DELETED) {
			RT.throwISEOrLogError("alcGetContextsDevice: context has been destroyed (use-after-free)");
		}
		return org.lwjgl.openal.ALC10.alcGetContextsDevice(context);
	}

	public static boolean alcIsExtensionPresent(long device, ByteBuffer extName) {
		if (device != 0L) {
			ALDevice dev = ALDevice.get(device);
			ALRT.checkDevice(dev, device, "alcIsExtensionPresent");
		}
		return org.lwjgl.openal.ALC10.alcIsExtensionPresent(device, extName);
	}

	public static boolean alcIsExtensionPresent(long device, CharSequence extName) {
		if (device != 0L) {
			ALDevice dev = ALDevice.get(device);
			ALRT.checkDevice(dev, device, "alcIsExtensionPresent");
		}
		return org.lwjgl.openal.ALC10.alcIsExtensionPresent(device, extName);
	}

	public static long alcGetProcAddress(long device, ByteBuffer funcName) {
		if (device != 0L) {
			ALDevice dev = ALDevice.get(device);
			ALRT.checkDevice(dev, device, "alcGetProcAddress");
		}
		return org.lwjgl.openal.ALC10.alcGetProcAddress(device, funcName);
	}

	public static long alcGetProcAddress(long device, CharSequence funcName) {
		if (device != 0L) {
			ALDevice dev = ALDevice.get(device);
			ALRT.checkDevice(dev, device, "alcGetProcAddress");
		}
		return org.lwjgl.openal.ALC10.alcGetProcAddress(device, funcName);
	}

	public static int alcGetEnumValue(long device, ByteBuffer enumName) {
		if (device != 0L) {
			ALDevice dev = ALDevice.get(device);
			ALRT.checkDevice(dev, device, "alcGetEnumValue");
		}
		return org.lwjgl.openal.ALC10.alcGetEnumValue(device, enumName);
	}

	public static int alcGetEnumValue(long device, CharSequence enumName) {
		if (device != 0L) {
			ALDevice dev = ALDevice.get(device);
			ALRT.checkDevice(dev, device, "alcGetEnumValue");
		}
		return org.lwjgl.openal.ALC10.alcGetEnumValue(device, enumName);
	}

	public static int alcGetError(long device) {
		if (device != 0L) {
			ALDevice dev = ALDevice.get(device);
			ALRT.checkDevice(dev, device, "alcGetError");
		}
		return org.lwjgl.openal.ALC10.alcGetError(device);
	}

	public static String alcGetString(long device, int param) {
		if (device != 0L) {
			ALDevice dev = ALDevice.get(device);
			ALRT.checkDevice(dev, device, "alcGetString");
		}
		return org.lwjgl.openal.ALC10.alcGetString(device, param);
	}

	public static void alcGetIntegerv(long device, int param, IntBuffer size) {
		if (device != 0L) {
			ALDevice dev = ALDevice.get(device);
			ALRT.checkDevice(dev, device, "alcGetIntegerv");
		}
		org.lwjgl.openal.ALC10.alcGetIntegerv(device, param, size);
	}

	public static void alcGetIntegerv(long device, int param, int[] size) {
		if (device != 0L) {
			ALDevice dev = ALDevice.get(device);
			ALRT.checkDevice(dev, device, "alcGetIntegerv");
		}
		org.lwjgl.openal.ALC10.alcGetIntegerv(device, param, size);
	}

	public static int alcGetInteger(long device, int param) {
		if (device != 0L) {
			ALDevice dev = ALDevice.get(device);
			ALRT.checkDevice(dev, device, "alcGetInteger");
		}
		return org.lwjgl.openal.ALC10.alcGetInteger(device, param);
	}
}
