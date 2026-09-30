package org.lwjglx.debug.org.lwjgl.openal;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjglx.debug.Properties;
import org.lwjglx.debug.RT;
import org.lwjglx.debug.openal.ALContext;
import org.lwjglx.debug.openal.ALObjects;
import org.lwjglx.debug.openal.ALRT;
import org.lwjglx.debug.ResourceState;

public class AL10 {

	/* --- Buffer Management --- */

	public static int alGenBuffers() {
		ALContext ctx = ALRT.checkContext("alGenBuffers");
		int buffer = org.lwjgl.openal.AL10.alGenBuffers();
		if (ctx != null && ctx.device != null && buffer != 0) {
			ctx.device.buffers.put(buffer, new ALObjects.Buffer(buffer, ctx.device));
		}
		return buffer;
	}

	public static void alGenBuffers(IntBuffer buffers) {
		ALContext ctx = ALRT.checkContext("alGenBuffers");
		int pos = buffers.position();
		org.lwjgl.openal.AL10.alGenBuffers(buffers);
		if (ctx != null && ctx.device != null) {
			for (int i = pos; i < buffers.limit(); i++) {
				int b = buffers.get(i);
				if (b != 0) {
					ctx.device.buffers.put(b, new ALObjects.Buffer(b, ctx.device));
				}
			}
		}
	}

	public static void alGenBuffers(int[] buffers) {
		ALContext ctx = ALRT.checkContext("alGenBuffers");
		org.lwjgl.openal.AL10.alGenBuffers(buffers);
		if (ctx != null && ctx.device != null) {
			for (int b : buffers) {
				if (b != 0) {
					ctx.device.buffers.put(b, new ALObjects.Buffer(b, ctx.device));
				}
			}
		}
	}

	public static void alDeleteBuffers(int buffer) {
		ALContext ctx = ALRT.checkContext("alDeleteBuffers");
		if (ctx != null && buffer != 0) {
			deleteBufferCheck(ctx, buffer);
		}
		org.lwjgl.openal.AL10.alDeleteBuffers(buffer);
	}

	public static void alDeleteBuffers(IntBuffer buffers) {
		ALContext ctx = ALRT.checkContext("alDeleteBuffers");
		if (ctx != null) {
			int pos = buffers.position();
			for (int i = pos; i < buffers.limit(); i++) {
				int b = buffers.get(i);
				if (b != 0) {
					deleteBufferCheck(ctx, b);
				}
			}
		}
		org.lwjgl.openal.AL10.alDeleteBuffers(buffers);
	}

	public static void alDeleteBuffers(int[] buffers) {
		ALContext ctx = ALRT.checkContext("alDeleteBuffers");
		if (ctx != null) {
			for (int b : buffers) {
				if (b != 0) {
					deleteBufferCheck(ctx, b);
				}
			}
		}
		org.lwjgl.openal.AL10.alDeleteBuffers(buffers);
	}

	private static void deleteBufferCheck(ALContext ctx, int buffer) {
		if (ctx.device == null) return;
		ALObjects.Buffer b = ctx.device.buffers.get(buffer);
		if (b == null) {
			RT.throwISEOrLogError("alDeleteBuffers: unknown buffer " + buffer);
			return;
		}
		if (b.state == ResourceState.DELETED) {
			RT.throwISEOrLogError("alDeleteBuffers: buffer " + buffer + " already deleted (double-free)"
					+ ALRT.deletionInfo(b.deletionSite) + ALRT.creationInfo(b.creationSite));
			return;
		}
		if (!b.attachedSources.isEmpty()) {
			RT.throwISEOrLogError("alDeleteBuffers: buffer " + buffer + " is still attached to "
					+ b.attachedSources.size() + " source(s)");
		}
		b.state = ResourceState.DELETED;
		if (Properties.STRICT.enabled) {
			b.deletionSite = new Throwable("Buffer deleted here");
		}
	}

	public static boolean alIsBuffer(int buffer) {
		ALRT.checkContext("alIsBuffer");
		return org.lwjgl.openal.AL10.alIsBuffer(buffer);
	}

	public static void alBufferData(int buffer, int format, ByteBuffer data, int frequency) {
		ALContext ctx = ALRT.checkContext("alBufferData");
		checkBufferData(ctx, buffer);
		org.lwjgl.openal.AL10.alBufferData(buffer, format, data, frequency);
	}

	public static void alBufferData(int buffer, int format, ShortBuffer data, int frequency) {
		ALContext ctx = ALRT.checkContext("alBufferData");
		checkBufferData(ctx, buffer);
		org.lwjgl.openal.AL10.alBufferData(buffer, format, data, frequency);
	}

	public static void alBufferData(int buffer, int format, IntBuffer data, int frequency) {
		ALContext ctx = ALRT.checkContext("alBufferData");
		checkBufferData(ctx, buffer);
		org.lwjgl.openal.AL10.alBufferData(buffer, format, data, frequency);
	}

	public static void alBufferData(int buffer, int format, FloatBuffer data, int frequency) {
		ALContext ctx = ALRT.checkContext("alBufferData");
		checkBufferData(ctx, buffer);
		org.lwjgl.openal.AL10.alBufferData(buffer, format, data, frequency);
	}

	public static void alBufferData(int buffer, int format, short[] data, int frequency) {
		ALContext ctx = ALRT.checkContext("alBufferData");
		checkBufferData(ctx, buffer);
		org.lwjgl.openal.AL10.alBufferData(buffer, format, data, frequency);
	}

	public static void alBufferData(int buffer, int format, int[] data, int frequency) {
		ALContext ctx = ALRT.checkContext("alBufferData");
		checkBufferData(ctx, buffer);
		org.lwjgl.openal.AL10.alBufferData(buffer, format, data, frequency);
	}

	public static void alBufferData(int buffer, int format, float[] data, int frequency) {
		ALContext ctx = ALRT.checkContext("alBufferData");
		checkBufferData(ctx, buffer);
		org.lwjgl.openal.AL10.alBufferData(buffer, format, data, frequency);
	}

	private static void checkBufferData(ALContext ctx, int buffer) {
		ALObjects.Buffer b = ALRT.checkBuffer(ctx, buffer, "alBufferData");
		if (b != null && !b.attachedSources.isEmpty()) {
			RT.throwISEOrLogError("alBufferData: modifying buffer " + buffer
					+ " while attached to source(s) is undefined behavior in OpenAL");
		}
	}

	public static float alGetBufferf(int buffer, int param) {
		ALContext ctx = ALRT.checkContext("alGetBufferf");
		ALRT.checkBuffer(ctx, buffer, "alGetBufferf");
		return org.lwjgl.openal.AL10.alGetBufferf(buffer, param);
	}

	public static void alGetBufferf(int buffer, int param, FloatBuffer values) {
		ALContext ctx = ALRT.checkContext("alGetBufferf");
		ALRT.checkBuffer(ctx, buffer, "alGetBufferf");
		org.lwjgl.openal.AL10.alGetBufferf(buffer, param, values);
	}

	public static void alGetBufferf(int buffer, int param, float[] values) {
		ALContext ctx = ALRT.checkContext("alGetBufferf");
		ALRT.checkBuffer(ctx, buffer, "alGetBufferf");
		org.lwjgl.openal.AL10.alGetBufferf(buffer, param, values);
	}

	public static int alGetBufferi(int buffer, int param) {
		ALContext ctx = ALRT.checkContext("alGetBufferi");
		ALRT.checkBuffer(ctx, buffer, "alGetBufferi");
		return org.lwjgl.openal.AL10.alGetBufferi(buffer, param);
	}

	public static void alGetBufferi(int buffer, int param, IntBuffer values) {
		ALContext ctx = ALRT.checkContext("alGetBufferi");
		ALRT.checkBuffer(ctx, buffer, "alGetBufferi");
		org.lwjgl.openal.AL10.alGetBufferi(buffer, param, values);
	}

	public static void alGetBufferi(int buffer, int param, int[] values) {
		ALContext ctx = ALRT.checkContext("alGetBufferi");
		ALRT.checkBuffer(ctx, buffer, "alGetBufferi");
		org.lwjgl.openal.AL10.alGetBufferi(buffer, param, values);
	}

	/* --- Source Management --- */

	public static int alGenSources() {
		ALContext ctx = ALRT.checkContext("alGenSources");
		int source = org.lwjgl.openal.AL10.alGenSources();
		if (ctx != null && source != 0) {
			ctx.sources.put(source, new ALObjects.Source(source, ctx));
		}
		return source;
	}

	public static void alGenSources(IntBuffer sources) {
		ALContext ctx = ALRT.checkContext("alGenSources");
		int pos = sources.position();
		org.lwjgl.openal.AL10.alGenSources(sources);
		if (ctx != null) {
			for (int i = pos; i < sources.limit(); i++) {
				int s = sources.get(i);
				if (s != 0) {
					ctx.sources.put(s, new ALObjects.Source(s, ctx));
				}
			}
		}
	}

	public static void alGenSources(int[] sources) {
		ALContext ctx = ALRT.checkContext("alGenSources");
		org.lwjgl.openal.AL10.alGenSources(sources);
		if (ctx != null) {
			for (int s : sources) {
				if (s != 0) {
					ctx.sources.put(s, new ALObjects.Source(s, ctx));
				}
			}
		}
	}

	public static void alDeleteSources(int source) {
		ALContext ctx = ALRT.checkContext("alDeleteSources");
		if (ctx != null && source != 0) {
			deleteSourceCheck(ctx, source);
		}
		org.lwjgl.openal.AL10.alDeleteSources(source);
	}

	public static void alDeleteSources(IntBuffer sources) {
		ALContext ctx = ALRT.checkContext("alDeleteSources");
		if (ctx != null) {
			int pos = sources.position();
			for (int i = pos; i < sources.limit(); i++) {
				int s = sources.get(i);
				if (s != 0) {
					deleteSourceCheck(ctx, s);
				}
			}
		}
		org.lwjgl.openal.AL10.alDeleteSources(sources);
	}

	public static void alDeleteSources(int[] sources) {
		ALContext ctx = ALRT.checkContext("alDeleteSources");
		if (ctx != null) {
			for (int s : sources) {
				if (s != 0) {
					deleteSourceCheck(ctx, s);
				}
			}
		}
		org.lwjgl.openal.AL10.alDeleteSources(sources);
	}

	private static void deleteSourceCheck(ALContext ctx, int source) {
		ALObjects.Source s = ctx.sources.get(source);
		if (s == null) {
			RT.throwISEOrLogError("alDeleteSources: unknown source " + source);
			return;
		}
		if (s.state == ResourceState.DELETED) {
			RT.throwISEOrLogError("alDeleteSources: source " + source + " already deleted (double-free)"
					+ ALRT.deletionInfo(s.deletionSite) + ALRT.creationInfo(s.creationSite));
			return;
		}
		if (s.attachedBuffer != null) {
			s.attachedBuffer.attachedSources.remove(s);
			s.attachedBuffer = null;
		}
		s.clearQueuedBuffers();
		s.state = ResourceState.DELETED;
		if (Properties.STRICT.enabled) {
			s.deletionSite = new Throwable("Source deleted here");
		}
	}

	public static boolean alIsSource(int source) {
		ALRT.checkContext("alIsSource");
		return org.lwjgl.openal.AL10.alIsSource(source);
	}

	public static void alSourcei(int source, int param, int value) {
		ALContext ctx = ALRT.checkContext("alSourcei");
		ALObjects.Source s = ALRT.checkSource(ctx, source, "alSourcei");
		if (param == org.lwjgl.openal.AL10.AL_BUFFER) {
			if (value != 0) {
				ALObjects.Buffer b = ALRT.checkBuffer(ctx, value, "alSourcei(AL_BUFFER)");
				if (s != null && b != null) {
					if (s.attachedBuffer != null) {
						s.attachedBuffer.attachedSources.remove(s);
					}
					s.clearQueuedBuffers();
					s.attachedBuffer = b;
					b.attachedSources.add(s);
				}
			} else if (s != null) {
				if (s.attachedBuffer != null) {
					s.attachedBuffer.attachedSources.remove(s);
					s.attachedBuffer = null;
				}
				s.clearQueuedBuffers();
			}
		} else if (param == org.lwjgl.openal.EXTEfx.AL_DIRECT_FILTER) {
			if (value != 0) {
				ALRT.checkFilter(ctx, value, "alSourcei(AL_DIRECT_FILTER)");
			}
		}
		org.lwjgl.openal.AL10.alSourcei(source, param, value);
	}

	public static void alSourcef(int source, int param, float value) {
		ALContext ctx = ALRT.checkContext("alSourcef");
		ALRT.checkSource(ctx, source, "alSourcef");
		org.lwjgl.openal.AL10.alSourcef(source, param, value);
	}

	public static void alSource3f(int source, int param, float v1, float v2, float v3) {
		ALContext ctx = ALRT.checkContext("alSource3f");
		ALRT.checkSource(ctx, source, "alSource3f");
		org.lwjgl.openal.AL10.alSource3f(source, param, v1, v2, v3);
	}

	public static void alSourcefv(int source, int param, FloatBuffer values) {
		ALContext ctx = ALRT.checkContext("alSourcefv");
		ALRT.checkSource(ctx, source, "alSourcefv");
		org.lwjgl.openal.AL10.alSourcefv(source, param, values);
	}

	public static void alSourcefv(int source, int param, float[] values) {
		ALContext ctx = ALRT.checkContext("alSourcefv");
		ALRT.checkSource(ctx, source, "alSourcefv");
		org.lwjgl.openal.AL10.alSourcefv(source, param, values);
	}

	public static float alGetSourcef(int source, int param) {
		ALContext ctx = ALRT.checkContext("alGetSourcef");
		ALRT.checkSource(ctx, source, "alGetSourcef");
		return org.lwjgl.openal.AL10.alGetSourcef(source, param);
	}

	public static void alGetSourcef(int source, int param, FloatBuffer values) {
		ALContext ctx = ALRT.checkContext("alGetSourcef");
		ALRT.checkSource(ctx, source, "alGetSourcef");
		org.lwjgl.openal.AL10.alGetSourcef(source, param, values);
	}

	public static void alGetSourcef(int source, int param, float[] values) {
		ALContext ctx = ALRT.checkContext("alGetSourcef");
		ALRT.checkSource(ctx, source, "alGetSourcef");
		org.lwjgl.openal.AL10.alGetSourcef(source, param, values);
	}

	public static void alGetSource3f(int source, int param, FloatBuffer v1, FloatBuffer v2, FloatBuffer v3) {
		ALContext ctx = ALRT.checkContext("alGetSource3f");
		ALRT.checkSource(ctx, source, "alGetSource3f");
		org.lwjgl.openal.AL10.alGetSource3f(source, param, v1, v2, v3);
	}

	public static void alGetSource3f(int source, int param, float[] v1, float[] v2, float[] v3) {
		ALContext ctx = ALRT.checkContext("alGetSource3f");
		ALRT.checkSource(ctx, source, "alGetSource3f");
		org.lwjgl.openal.AL10.alGetSource3f(source, param, v1, v2, v3);
	}

	public static void alGetSourcefv(int source, int param, FloatBuffer values) {
		ALContext ctx = ALRT.checkContext("alGetSourcefv");
		ALRT.checkSource(ctx, source, "alGetSourcefv");
		org.lwjgl.openal.AL10.alGetSourcefv(source, param, values);
	}

	public static void alGetSourcefv(int source, int param, float[] values) {
		ALContext ctx = ALRT.checkContext("alGetSourcefv");
		ALRT.checkSource(ctx, source, "alGetSourcefv");
		org.lwjgl.openal.AL10.alGetSourcefv(source, param, values);
	}

	public static int alGetSourcei(int source, int param) {
		ALContext ctx = ALRT.checkContext("alGetSourcei");
		ALRT.checkSource(ctx, source, "alGetSourcei");
		return org.lwjgl.openal.AL10.alGetSourcei(source, param);
	}

	public static void alGetSourcei(int source, int param, IntBuffer values) {
		ALContext ctx = ALRT.checkContext("alGetSourcei");
		ALRT.checkSource(ctx, source, "alGetSourcei");
		org.lwjgl.openal.AL10.alGetSourcei(source, param, values);
	}

	public static void alGetSourcei(int source, int param, int[] values) {
		ALContext ctx = ALRT.checkContext("alGetSourcei");
		ALRT.checkSource(ctx, source, "alGetSourcei");
		org.lwjgl.openal.AL10.alGetSourcei(source, param, values);
	}

	public static void alGetSourceiv(int source, int param, IntBuffer values) {
		ALContext ctx = ALRT.checkContext("alGetSourceiv");
		ALRT.checkSource(ctx, source, "alGetSourceiv");
		org.lwjgl.openal.AL10.alGetSourceiv(source, param, values);
	}

	public static void alGetSourceiv(int source, int param, int[] values) {
		ALContext ctx = ALRT.checkContext("alGetSourceiv");
		ALRT.checkSource(ctx, source, "alGetSourceiv");
		org.lwjgl.openal.AL10.alGetSourceiv(source, param, values);
	}

	/* --- Source Playback Controls --- */

	public static void alSourcePlay(int source) {
		ALContext ctx = ALRT.checkContext("alSourcePlay");
		ALRT.checkSource(ctx, source, "alSourcePlay");
		org.lwjgl.openal.AL10.alSourcePlay(source);
	}

	public static void alSourcePause(int source) {
		ALContext ctx = ALRT.checkContext("alSourcePause");
		ALRT.checkSource(ctx, source, "alSourcePause");
		org.lwjgl.openal.AL10.alSourcePause(source);
	}

	public static void alSourceStop(int source) {
		ALContext ctx = ALRT.checkContext("alSourceStop");
		ALRT.checkSource(ctx, source, "alSourceStop");
		org.lwjgl.openal.AL10.alSourceStop(source);
	}

	public static void alSourceRewind(int source) {
		ALContext ctx = ALRT.checkContext("alSourceRewind");
		ALRT.checkSource(ctx, source, "alSourceRewind");
		org.lwjgl.openal.AL10.alSourceRewind(source);
	}

	public static void alSourcePlayv(IntBuffer sources) {
		ALContext ctx = ALRT.checkContext("alSourcePlayv");
		if (ctx != null) {
			int pos = sources.position();
			for (int i = pos; i < sources.limit(); i++) {
				ALRT.checkSource(ctx, sources.get(i), "alSourcePlayv");
			}
		}
		org.lwjgl.openal.AL10.alSourcePlayv(sources);
	}

	public static void alSourcePlayv(int[] sources) {
		ALContext ctx = ALRT.checkContext("alSourcePlayv");
		if (ctx != null) {
			for (int s : sources) {
				ALRT.checkSource(ctx, s, "alSourcePlayv");
			}
		}
		org.lwjgl.openal.AL10.alSourcePlayv(sources);
	}

	public static void alSourcePausev(IntBuffer sources) {
		ALContext ctx = ALRT.checkContext("alSourcePausev");
		if (ctx != null) {
			int pos = sources.position();
			for (int i = pos; i < sources.limit(); i++) {
				ALRT.checkSource(ctx, sources.get(i), "alSourcePausev");
			}
		}
		org.lwjgl.openal.AL10.alSourcePausev(sources);
	}

	public static void alSourcePausev(int[] sources) {
		ALContext ctx = ALRT.checkContext("alSourcePausev");
		if (ctx != null) {
			for (int s : sources) {
				ALRT.checkSource(ctx, s, "alSourcePausev");
			}
		}
		org.lwjgl.openal.AL10.alSourcePausev(sources);
	}

	public static void alSourceStopv(IntBuffer sources) {
		ALContext ctx = ALRT.checkContext("alSourceStopv");
		if (ctx != null) {
			int pos = sources.position();
			for (int i = pos; i < sources.limit(); i++) {
				ALRT.checkSource(ctx, sources.get(i), "alSourceStopv");
			}
		}
		org.lwjgl.openal.AL10.alSourceStopv(sources);
	}

	public static void alSourceStopv(int[] sources) {
		ALContext ctx = ALRT.checkContext("alSourceStopv");
		if (ctx != null) {
			for (int s : sources) {
				ALRT.checkSource(ctx, s, "alSourceStopv");
			}
		}
		org.lwjgl.openal.AL10.alSourceStopv(sources);
	}

	public static void alSourceRewindv(IntBuffer sources) {
		ALContext ctx = ALRT.checkContext("alSourceRewindv");
		if (ctx != null) {
			int pos = sources.position();
			for (int i = pos; i < sources.limit(); i++) {
				ALRT.checkSource(ctx, sources.get(i), "alSourceRewindv");
			}
		}
		org.lwjgl.openal.AL10.alSourceRewindv(sources);
	}

	public static void alSourceRewindv(int[] sources) {
		ALContext ctx = ALRT.checkContext("alSourceRewindv");
		if (ctx != null) {
			for (int s : sources) {
				ALRT.checkSource(ctx, s, "alSourceRewindv");
			}
		}
		org.lwjgl.openal.AL10.alSourceRewindv(sources);
	}

	/* --- Buffer Queuing --- */

	public static void alSourceQueueBuffers(int source, int buffer) {
		ALContext ctx = ALRT.checkContext("alSourceQueueBuffers");
		ALObjects.Source s = ALRT.checkSource(ctx, source, "alSourceQueueBuffers");
		ALObjects.Buffer b = ALRT.checkBuffer(ctx, buffer, "alSourceQueueBuffers");
		if (s != null && b != null) {
			s.queueBuffer(b);
		}
		org.lwjgl.openal.AL10.alSourceQueueBuffers(source, buffer);
	}

	public static void alSourceQueueBuffers(int source, IntBuffer buffers) {
		ALContext ctx = ALRT.checkContext("alSourceQueueBuffers");
		ALObjects.Source s = ALRT.checkSource(ctx, source, "alSourceQueueBuffers");
		if (ctx != null && s != null) {
			int pos = buffers.position();
			for (int i = pos; i < buffers.limit(); i++) {
				ALObjects.Buffer b = ALRT.checkBuffer(ctx, buffers.get(i), "alSourceQueueBuffers");
				if (b != null) {
					s.queueBuffer(b);
				}
			}
		}
		org.lwjgl.openal.AL10.alSourceQueueBuffers(source, buffers);
	}

	public static void alSourceQueueBuffers(int source, int[] buffers) {
		ALContext ctx = ALRT.checkContext("alSourceQueueBuffers");
		ALObjects.Source s = ALRT.checkSource(ctx, source, "alSourceQueueBuffers");
		if (ctx != null && s != null) {
			for (int bufId : buffers) {
				ALObjects.Buffer b = ALRT.checkBuffer(ctx, bufId, "alSourceQueueBuffers");
				if (b != null) {
					s.queueBuffer(b);
				}
			}
		}
		org.lwjgl.openal.AL10.alSourceQueueBuffers(source, buffers);
	}

	public static int alSourceUnqueueBuffers(int source) {
		ALContext ctx = ALRT.checkContext("alSourceUnqueueBuffers");
		ALObjects.Source s = ALRT.checkSource(ctx, source, "alSourceUnqueueBuffers");
		int buffer = org.lwjgl.openal.AL10.alSourceUnqueueBuffers(source);
		if (ctx != null && ctx.device != null && s != null && buffer != 0) {
			ALObjects.Buffer b = ctx.device.buffers.get(buffer);
			if (b != null) {
				s.unqueueBuffer(b);
			}
		}
		return buffer;
	}

	public static void alSourceUnqueueBuffers(int source, IntBuffer buffers) {
		ALContext ctx = ALRT.checkContext("alSourceUnqueueBuffers");
		ALObjects.Source s = ALRT.checkSource(ctx, source, "alSourceUnqueueBuffers");
		int pos = buffers.position();
		org.lwjgl.openal.AL10.alSourceUnqueueBuffers(source, buffers);
		if (ctx != null && ctx.device != null && s != null) {
			for (int i = pos; i < buffers.limit(); i++) {
				int bufId = buffers.get(i);
				ALObjects.Buffer b = ctx.device.buffers.get(bufId);
				if (b != null) {
					s.unqueueBuffer(b);
				}
			}
		}
	}

	public static void alSourceUnqueueBuffers(int source, int[] buffers) {
		ALContext ctx = ALRT.checkContext("alSourceUnqueueBuffers");
		ALObjects.Source s = ALRT.checkSource(ctx, source, "alSourceUnqueueBuffers");
		org.lwjgl.openal.AL10.alSourceUnqueueBuffers(source, buffers);
		if (ctx != null && ctx.device != null && s != null) {
			for (int bufId : buffers) {
				ALObjects.Buffer b = ctx.device.buffers.get(bufId);
				if (b != null) {
					s.unqueueBuffer(b);
				}
			}
		}
	}

	/* --- Listener Parameters --- */

	public static void alListenerf(int param, float value) {
		ALRT.checkContext("alListenerf");
		org.lwjgl.openal.AL10.alListenerf(param, value);
	}

	public static void alListener3f(int param, float v1, float v2, float v3) {
		ALRT.checkContext("alListener3f");
		org.lwjgl.openal.AL10.alListener3f(param, v1, v2, v3);
	}

	public static void alListenerfv(int param, FloatBuffer values) {
		ALRT.checkContext("alListenerfv");
		org.lwjgl.openal.AL10.alListenerfv(param, values);
	}

	public static void alListenerfv(int param, float[] values) {
		ALRT.checkContext("alListenerfv");
		org.lwjgl.openal.AL10.alListenerfv(param, values);
	}

	public static void alListeneri(int param, int value) {
		ALRT.checkContext("alListeneri");
		org.lwjgl.openal.AL10.alListeneri(param, value);
	}

	public static float alGetListenerf(int param) {
		ALRT.checkContext("alGetListenerf");
		return org.lwjgl.openal.AL10.alGetListenerf(param);
	}

	public static void alGetListenerf(int param, FloatBuffer values) {
		ALRT.checkContext("alGetListenerf");
		org.lwjgl.openal.AL10.alGetListenerf(param, values);
	}

	public static void alGetListenerf(int param, float[] values) {
		ALRT.checkContext("alGetListenerf");
		org.lwjgl.openal.AL10.alGetListenerf(param, values);
	}

	public static void alGetListener3f(int param, FloatBuffer v1, FloatBuffer v2, FloatBuffer v3) {
		ALRT.checkContext("alGetListener3f");
		org.lwjgl.openal.AL10.alGetListener3f(param, v1, v2, v3);
	}

	public static void alGetListener3f(int param, float[] v1, float[] v2, float[] v3) {
		ALRT.checkContext("alGetListener3f");
		org.lwjgl.openal.AL10.alGetListener3f(param, v1, v2, v3);
	}

	public static void alGetListenerfv(int param, FloatBuffer values) {
		ALRT.checkContext("alGetListenerfv");
		org.lwjgl.openal.AL10.alGetListenerfv(param, values);
	}

	public static void alGetListenerfv(int param, float[] values) {
		ALRT.checkContext("alGetListenerfv");
		org.lwjgl.openal.AL10.alGetListenerfv(param, values);
	}

	public static int alGetListeneri(int param) {
		ALRT.checkContext("alGetListeneri");
		return org.lwjgl.openal.AL10.alGetListeneri(param);
	}

	public static void alGetListeneri(int param, IntBuffer values) {
		ALRT.checkContext("alGetListeneri");
		org.lwjgl.openal.AL10.alGetListeneri(param, values);
	}

	public static void alGetListeneri(int param, int[] values) {
		ALRT.checkContext("alGetListeneri");
		org.lwjgl.openal.AL10.alGetListeneri(param, values);
	}

	/* --- Global Queries & State --- */

	public static void alEnable(int capability) {
		ALRT.checkContext("alEnable");
		org.lwjgl.openal.AL10.alEnable(capability);
	}

	public static void alDisable(int capability) {
		ALRT.checkContext("alDisable");
		org.lwjgl.openal.AL10.alDisable(capability);
	}

	public static boolean alIsEnabled(int capability) {
		ALRT.checkContext("alIsEnabled");
		return org.lwjgl.openal.AL10.alIsEnabled(capability);
	}

	public static boolean alGetBoolean(int param) {
		ALRT.checkContext("alGetBoolean");
		return org.lwjgl.openal.AL10.alGetBoolean(param);
	}

	public static void alGetBooleanv(int param, ByteBuffer data) {
		ALRT.checkContext("alGetBooleanv");
		org.lwjgl.openal.AL10.alGetBooleanv(param, data);
	}

	public static int alGetInteger(int param) {
		ALRT.checkContext("alGetInteger");
		return org.lwjgl.openal.AL10.alGetInteger(param);
	}

	public static void alGetIntegerv(int param, IntBuffer data) {
		ALRT.checkContext("alGetIntegerv");
		org.lwjgl.openal.AL10.alGetIntegerv(param, data);
	}

	public static void alGetIntegerv(int param, int[] data) {
		ALRT.checkContext("alGetIntegerv");
		org.lwjgl.openal.AL10.alGetIntegerv(param, data);
	}

	public static float alGetFloat(int param) {
		ALRT.checkContext("alGetFloat");
		return org.lwjgl.openal.AL10.alGetFloat(param);
	}

	public static void alGetFloatv(int param, FloatBuffer data) {
		ALRT.checkContext("alGetFloatv");
		org.lwjgl.openal.AL10.alGetFloatv(param, data);
	}

	public static void alGetFloatv(int param, float[] data) {
		ALRT.checkContext("alGetFloatv");
		org.lwjgl.openal.AL10.alGetFloatv(param, data);
	}

	public static double alGetDouble(int param) {
		ALRT.checkContext("alGetDouble");
		return org.lwjgl.openal.AL10.alGetDouble(param);
	}

	public static void alGetDoublev(int param, DoubleBuffer data) {
		ALRT.checkContext("alGetDoublev");
		org.lwjgl.openal.AL10.alGetDoublev(param, data);
	}

	public static void alGetDoublev(int param, double[] data) {
		ALRT.checkContext("alGetDoublev");
		org.lwjgl.openal.AL10.alGetDoublev(param, data);
	}

	public static String alGetString(int param) {
		ALRT.checkContext("alGetString");
		return org.lwjgl.openal.AL10.alGetString(param);
	}

	public static void alDistanceModel(int value) {
		ALRT.checkContext("alDistanceModel");
		org.lwjgl.openal.AL10.alDistanceModel(value);
	}

	public static void alDopplerFactor(float value) {
		ALRT.checkContext("alDopplerFactor");
		org.lwjgl.openal.AL10.alDopplerFactor(value);
	}

	public static void alDopplerVelocity(float value) {
		ALRT.checkContext("alDopplerVelocity");
		org.lwjgl.openal.AL10.alDopplerVelocity(value);
	}
}
