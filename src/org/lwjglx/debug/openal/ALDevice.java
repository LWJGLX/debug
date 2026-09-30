package org.lwjglx.debug.openal;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjglx.debug.Properties;
import org.lwjglx.debug.ResourceState;

public class ALDevice {

	private static final ConcurrentHashMap<Long, ALDevice> DEVICES = new ConcurrentHashMap<>();

	public final long handle;
	public volatile ResourceState state = ResourceState.ALIVE;
	public final Set<ALContext> contexts = ConcurrentHashMap.newKeySet();
	public final ConcurrentHashMap<Integer, ALObjects.Buffer> buffers = new ConcurrentHashMap<>();
	public final Throwable creationSite;

	private ALDevice(long handle) {
		this.handle = handle;
		this.creationSite = Properties.STRICT.enabled ? new Throwable("Device opened here") : null;
	}

	public static ALDevice create(long handle) {
		ALDevice device = new ALDevice(handle);
		DEVICES.put(handle, device);
		return device;
	}

	public static ALDevice get(long handle) {
		return DEVICES.get(handle);
	}

	public static java.util.Collection<ALDevice> allDevices() {
		return DEVICES.values();
	}

	public static ALDevice remove(long handle) {
		return DEVICES.remove(handle);
	}
}
