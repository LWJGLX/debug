package org.lwjglx.debug.openal;

import static org.lwjglx.debug.Log.*;

import org.lwjglx.debug.Properties;
import org.lwjglx.debug.RT;
import org.lwjglx.debug.ResourceState;

/**
 * OpenAL runtime validation helpers.
 */
public class ALRT {

	public static ALContext checkContext(String methodName) {
		ALContext ctx = ALContext.currentContext();
		if (ctx == null) {
			RT.throwISEOrLogError("No OpenAL context is current when calling " + methodName);
		} else if (ctx.state == ResourceState.DELETED) {
			RT.throwISEOrLogError("Current OpenAL context has been destroyed when calling " + methodName);
		}
		return ctx;
	}

	public static void checkDevice(ALDevice device, long handle, String methodName) {
		if (device == null) {
			RT.throwISEOrLogError(methodName + ": unknown device handle 0x" + Long.toHexString(handle));
		} else if (device.state == ResourceState.DELETED) {
			RT.throwISEOrLogError(methodName + ": device has been closed" + deletionInfo(device.creationSite));
		}
	}

	public static ALObjects.Source checkSource(ALContext ctx, int source, String methodName) {
		if (ctx == null) return null;
		ALObjects.Source s = ctx.sources.get(source);
		if (s == null) {
			ALObjects.Source other = findSourceInAnyContext(source);
			if (other != null) {
				RT.throwISEOrLogError(methodName + ": source " + source + " belongs to context 0x"
						+ Long.toHexString(other.owningContext.handle) + ", not the current context 0x"
						+ Long.toHexString(ctx.handle) + " (cross-context use)");
				return null;
			}
			RT.throwISEOrLogError(methodName + ": unknown source name " + source);
			return null;
		}
		if (s.state == ResourceState.DELETED) {
			RT.throwISEOrLogError(methodName + ": source " + source + " has been deleted (use-after-free)"
					+ deletionInfo(s.deletionSite) + creationInfo(s.creationSite));
			return null;
		}
		return s;
	}

	public static ALObjects.Buffer checkBuffer(ALContext ctx, int buffer, String methodName) {
		if (ctx == null || buffer == 0) return null;
		ALDevice dev = ctx.device;
		ALObjects.Buffer b = dev != null ? dev.buffers.get(buffer) : null;
		if (b == null) {
			ALObjects.Buffer other = findBufferInAnyDevice(buffer);
			if (other != null) {
				RT.throwISEOrLogError(methodName + ": buffer " + buffer + " belongs to device 0x"
						+ Long.toHexString(other.owningDevice.handle) + ", not the current device 0x"
						+ Long.toHexString(dev != null ? dev.handle : 0) + " (cross-device use)");
				return null;
			}
			RT.throwISEOrLogError(methodName + ": unknown buffer name " + buffer);
			return null;
		}
		if (b.state == ResourceState.DELETED) {
			RT.throwISEOrLogError(methodName + ": buffer " + buffer + " has been deleted (use-after-free)"
					+ deletionInfo(b.deletionSite) + creationInfo(b.creationSite));
			return null;
		}
		return b;
	}

	public static ALObjects.Effect checkEffect(ALContext ctx, int effect, String methodName) {
		if (ctx == null || effect == 0) return null;
		ALObjects.Effect e = ctx.effects.get(effect);
		if (e == null) {
			ALObjects.Effect other = findEffectInAnyContext(effect);
			if (other != null) {
				RT.throwISEOrLogError(methodName + ": effect " + effect + " belongs to context 0x"
						+ Long.toHexString(other.owningContext.handle) + ", not the current context 0x"
						+ Long.toHexString(ctx.handle) + " (cross-context use)");
				return null;
			}
			RT.throwISEOrLogError(methodName + ": unknown effect name " + effect);
			return null;
		}
		if (e.state == ResourceState.DELETED) {
			RT.throwISEOrLogError(methodName + ": effect " + effect + " has been deleted (use-after-free)"
					+ deletionInfo(e.deletionSite) + creationInfo(e.creationSite));
			return null;
		}
		return e;
	}

	public static ALObjects.Filter checkFilter(ALContext ctx, int filter, String methodName) {
		if (ctx == null || filter == 0) return null;
		ALObjects.Filter f = ctx.filters.get(filter);
		if (f == null) {
			ALObjects.Filter other = findFilterInAnyContext(filter);
			if (other != null) {
				RT.throwISEOrLogError(methodName + ": filter " + filter + " belongs to context 0x"
						+ Long.toHexString(other.owningContext.handle) + ", not the current context 0x"
						+ Long.toHexString(ctx.handle) + " (cross-context use)");
				return null;
			}
			RT.throwISEOrLogError(methodName + ": unknown filter name " + filter);
			return null;
		}
		if (f.state == ResourceState.DELETED) {
			RT.throwISEOrLogError(methodName + ": filter " + filter + " has been deleted (use-after-free)"
					+ deletionInfo(f.deletionSite) + creationInfo(f.creationSite));
			return null;
		}
		return f;
	}

	public static ALObjects.AuxiliaryEffectSlot checkAuxSlot(ALContext ctx, int slot, String methodName) {
		if (ctx == null || slot == 0) return null;
		ALObjects.AuxiliaryEffectSlot a = ctx.auxSlots.get(slot);
		if (a == null) {
			ALObjects.AuxiliaryEffectSlot other = findAuxSlotInAnyContext(slot);
			if (other != null) {
				RT.throwISEOrLogError(methodName + ": auxiliary effect slot " + slot + " belongs to context 0x"
						+ Long.toHexString(other.owningContext.handle) + ", not the current context 0x"
						+ Long.toHexString(ctx.handle) + " (cross-context use)");
				return null;
			}
			RT.throwISEOrLogError(methodName + ": unknown auxiliary effect slot name " + slot);
			return null;
		}
		if (a.state == ResourceState.DELETED) {
			RT.throwISEOrLogError(methodName + ": auxiliary effect slot " + slot + " has been deleted (use-after-free)"
					+ deletionInfo(a.deletionSite) + creationInfo(a.creationSite));
			return null;
		}
		return a;
	}

	private static ALObjects.Source findSourceInAnyContext(int source) {
		for (ALContext c : ALContext.allContexts()) {
			ALObjects.Source s = c.sources.get(source);
			if (s != null) return s;
		}
		return null;
	}

	private static ALObjects.Buffer findBufferInAnyDevice(int buffer) {
		for (ALDevice d : ALDevice.allDevices()) {
			ALObjects.Buffer b = d.buffers.get(buffer);
			if (b != null) return b;
		}
		return null;
	}

	private static ALObjects.Effect findEffectInAnyContext(int effect) {
		for (ALContext c : ALContext.allContexts()) {
			ALObjects.Effect e = c.effects.get(effect);
			if (e != null) return e;
		}
		return null;
	}

	private static ALObjects.Filter findFilterInAnyContext(int filter) {
		for (ALContext c : ALContext.allContexts()) {
			ALObjects.Filter f = c.filters.get(filter);
			if (f != null) return f;
		}
		return null;
	}

	private static ALObjects.AuxiliaryEffectSlot findAuxSlotInAnyContext(int slot) {
		for (ALContext c : ALContext.allContexts()) {
			ALObjects.AuxiliaryEffectSlot a = c.auxSlots.get(slot);
			if (a != null) return a;
		}
		return null;
	}

	public static void checkALError(String methodName) {
		if (!Properties.VALIDATE.enabled) {
			return;
		}
		ALContext ctx = ALContext.currentContext();
		if (ctx == null || ctx.state == ResourceState.DELETED) {
			return;
		}
		try {
			if (org.lwjgl.openal.AL.getCapabilities() == null) {
				return;
			}
		} catch (Throwable t) {
			return;
		}
		int err = org.lwjgl.openal.AL10.alGetError();
		if (err != 0) {
			RT.throwISEOrLogError(methodName + " produced error: " + ALMetadata.alErrorName(err));
		}
	}

	public static void checkALCError(long device, String methodName) {
		if (!Properties.VALIDATE.enabled) {
			return;
		}
		int err = org.lwjgl.openal.ALC10.alcGetError(device);
		if (err != 0) {
			RT.throwISEOrLogError(methodName + " produced error: " + ALMetadata.alcErrorName(err));
		}
	}

	public static String deletionInfo(Throwable deletionSite) {
		if (deletionSite != null) {
			return "\n  Deleted at: " + formatTrace(deletionSite);
		}
		return "";
	}

	public static String creationInfo(Throwable creationSite) {
		if (creationSite != null) {
			return "\n  Created at: " + formatTrace(creationSite);
		}
		return "";
	}

	private static String formatTrace(Throwable t) {
		StackTraceElement[] stack = t.getStackTrace();
		for (StackTraceElement ste : stack) {
			if (!ste.getClassName().startsWith("org.lwjglx.debug")) {
				return ste.toString();
			}
		}
		return stack.length > 0 ? stack[0].toString() : "(unknown)";
	}

	public static org.lwjglx.debug.MethodCall paramAlEnum(org.lwjglx.debug.MethodCall mc, int val) {
		mc.paramEnum(ALMetadata.enumName(val));
		return mc;
	}

	public static org.lwjglx.debug.MethodCall paramAlDevice(org.lwjglx.debug.MethodCall mc, long device) {
		if (device == 0L) {
			mc.paramEnum("device[NULL]");
		} else {
			mc.paramEnum("device[0x" + Long.toHexString(device) + "]");
		}
		return mc;
	}

	public static org.lwjglx.debug.MethodCall paramAlContext(org.lwjglx.debug.MethodCall mc, long context) {
		if (context == 0L) {
			mc.paramEnum("context[NULL]");
		} else {
			mc.paramEnum("context[0x" + Long.toHexString(context) + "]");
		}
		return mc;
	}

	public static int returnValueAlEnum(int val, org.lwjglx.debug.MethodCall mc) {
		mc.returnValueEnum(ALMetadata.enumName(val));
		return val;
	}
}
