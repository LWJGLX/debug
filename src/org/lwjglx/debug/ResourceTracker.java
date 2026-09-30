package org.lwjglx.debug;

import java.util.concurrent.ConcurrentHashMap;

public class ResourceTracker<T> {

	public static class Entry<T> {
		public final int handle;
		public final T object;
		public volatile ResourceState state = ResourceState.ALIVE;
		public final Throwable creationSite;
		public volatile Throwable deletionSite;

		public Entry(int handle, T object) {
			this.handle = handle;
			this.object = object;
			this.creationSite = Properties.STRICT.enabled ? new Throwable("Resource [" + handle + "] created here") : null;
		}
	}

	private final String resourceTypeName;
	private final ConcurrentHashMap<Integer, Entry<T>> entries = new ConcurrentHashMap<>();

	public ResourceTracker(String resourceTypeName) {
		this.resourceTypeName = resourceTypeName;
	}

	public Entry<T> create(int handle, T object) {
		Entry<T> entry = new Entry<>(handle, object);
		entries.put(handle, entry);
		return entry;
	}

	public Entry<T> get(int handle) {
		return entries.get(handle);
	}

	public Entry<T> delete(int handle, String callerMethod) {
		Entry<T> entry = entries.get(handle);
		if (entry == null) {
			RT.throwISEOrLogError(callerMethod + ": unknown " + resourceTypeName + " handle " + handle);
			return null;
		}
		if (entry.state == ResourceState.DELETED) {
			RT.throwISEOrLogError(callerMethod + ": " + resourceTypeName + " " + handle + " already deleted (double-free)"
					+ deletionInfo(entry.deletionSite) + creationInfo(entry.creationSite));
			return null;
		}
		entry.state = ResourceState.DELETED;
		if (Properties.STRICT.enabled) {
			entry.deletionSite = new Throwable("Resource [" + handle + "] deleted here");
		}
		return entry;
	}

	public Entry<T> checkAlive(int handle, String callerMethod) {
		if (handle == 0) return null;
		Entry<T> entry = entries.get(handle);
		if (entry == null) {
			RT.throwISEOrLogError(callerMethod + ": unknown " + resourceTypeName + " handle " + handle);
			return null;
		}
		if (entry.state == ResourceState.DELETED) {
			RT.throwISEOrLogError(callerMethod + ": " + resourceTypeName + " " + handle + " has been deleted (use-after-free)"
					+ deletionInfo(entry.deletionSite) + creationInfo(entry.creationSite));
			return null;
		}
		return entry;
	}

	public int liveCount() {
		int count = 0;
		for (Entry<T> entry : entries.values()) {
			if (entry.state == ResourceState.ALIVE) {
				count++;
			}
		}
		return count;
	}

	public int liveCountExcludingDefault() {
		int count = 0;
		for (Entry<T> entry : entries.values()) {
			if (entry.handle != 0 && entry.state == ResourceState.ALIVE) {
				count++;
			}
		}
		return count;
	}

	public java.util.List<Entry<T>> liveEntriesExcludingDefault() {
		java.util.List<Entry<T>> result = new java.util.ArrayList<>();
		for (Entry<T> entry : entries.values()) {
			if (entry.handle != 0 && entry.state == ResourceState.ALIVE) {
				result.add(entry);
			}
		}
		return result;
	}

	public ConcurrentHashMap<Integer, Entry<T>> entries() {
		return entries;
	}

	private static String deletionInfo(Throwable deletionSite) {
		if (deletionSite != null) {
			return "\n  Deleted at: " + formatTrace(deletionSite);
		}
		return "";
	}

	private static String creationInfo(Throwable creationSite) {
		if (creationSite != null) {
			return "\n  Created at: " + formatTrace(creationSite);
		}
		return "";
	}

	public static String formatTrace(Throwable t) {
		StackTraceElement[] stack = t.getStackTrace();
		for (StackTraceElement ste : stack) {
			if (!ste.getClassName().startsWith("org.lwjglx.debug")) {
				return ste.toString();
			}
		}
		return stack.length > 0 ? stack[0].toString() : "(unknown)";
	}
}
