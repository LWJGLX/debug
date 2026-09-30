package org.lwjglx.debug.openal;

import java.util.concurrent.ConcurrentHashMap;

import org.lwjglx.debug.Properties;

public class ALContext {

	private static final ConcurrentHashMap<Long, ALContext> CONTEXTS = new ConcurrentHashMap<>();
	private static volatile ALContext currentContext;

	public final long handle;
	public final ALDevice device;
	public volatile ResourceState state = ResourceState.ALIVE;
	public final ConcurrentHashMap<Integer, ALObjects.Source> sources = new ConcurrentHashMap<>();
	public final ConcurrentHashMap<Integer, ALObjects.Buffer> buffers = new ConcurrentHashMap<>();
	public final ConcurrentHashMap<Integer, ALObjects.Effect> effects = new ConcurrentHashMap<>();
	public final ConcurrentHashMap<Integer, ALObjects.Filter> filters = new ConcurrentHashMap<>();
	public final ConcurrentHashMap<Integer, ALObjects.AuxiliaryEffectSlot> auxSlots = new ConcurrentHashMap<>();
	public final Throwable creationSite;

	private ALContext(long handle, ALDevice device) {
		this.handle = handle;
		this.device = device;
		this.creationSite = Properties.STRICT.enabled ? new Throwable("Context created here") : null;
	}

	public static ALContext create(long handle, ALDevice device) {
		ALContext context = new ALContext(handle, device);
		CONTEXTS.put(handle, context);
		device.contexts.add(context);
		return context;
	}

	public static ALContext get(long handle) {
		return CONTEXTS.get(handle);
	}

	public static java.util.Collection<ALContext> allContexts() {
		return CONTEXTS.values();
	}

	public static ALContext currentContext() {
		return currentContext;
	}

	public static void makeCurrent(ALContext context) {
		currentContext = context;
	}
}
