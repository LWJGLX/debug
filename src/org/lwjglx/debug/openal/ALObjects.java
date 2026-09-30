package org.lwjglx.debug.openal;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjglx.debug.Properties;

public class ALObjects {

	public static class Buffer {
		public final int name;
		public final ALContext owningContext;
		public volatile ResourceState state = ResourceState.ALIVE;
		public final Set<Source> attachedSources = ConcurrentHashMap.newKeySet();
		public final Throwable creationSite;
		public volatile Throwable deletionSite;

		public Buffer(int name, ALContext owningContext) {
			this.name = name;
			this.owningContext = owningContext;
			this.creationSite = Properties.STRICT.enabled ? new Throwable("Buffer created here") : null;
		}
	}

	public static class Source {
		public final int name;
		public final ALContext owningContext;
		public volatile ResourceState state = ResourceState.ALIVE;
		public volatile Buffer attachedBuffer;
		public final Throwable creationSite;
		public volatile Throwable deletionSite;

		public Source(int name, ALContext owningContext) {
			this.name = name;
			this.owningContext = owningContext;
			this.creationSite = Properties.STRICT.enabled ? new Throwable("Source created here") : null;
		}
	}

	public static class Effect {
		public final int name;
		public final ALContext owningContext;
		public volatile ResourceState state = ResourceState.ALIVE;
		public final Throwable creationSite;
		public volatile Throwable deletionSite;

		public Effect(int name, ALContext owningContext) {
			this.name = name;
			this.owningContext = owningContext;
			this.creationSite = Properties.STRICT.enabled ? new Throwable("Effect created here") : null;
		}
	}

	public static class Filter {
		public final int name;
		public final ALContext owningContext;
		public volatile ResourceState state = ResourceState.ALIVE;
		public final Throwable creationSite;
		public volatile Throwable deletionSite;

		public Filter(int name, ALContext owningContext) {
			this.name = name;
			this.owningContext = owningContext;
			this.creationSite = Properties.STRICT.enabled ? new Throwable("Filter created here") : null;
		}
	}

	public static class AuxiliaryEffectSlot {
		public final int name;
		public final ALContext owningContext;
		public volatile ResourceState state = ResourceState.ALIVE;
		public final Throwable creationSite;
		public volatile Throwable deletionSite;

		public AuxiliaryEffectSlot(int name, ALContext owningContext) {
			this.name = name;
			this.owningContext = owningContext;
			this.creationSite = Properties.STRICT.enabled ? new Throwable("Auxiliary effect slot created here") : null;
		}
	}
}
