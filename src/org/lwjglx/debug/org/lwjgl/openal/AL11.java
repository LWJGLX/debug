package org.lwjglx.debug.org.lwjgl.openal;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjglx.debug.openal.ALContext;
import org.lwjglx.debug.openal.ALRT;

public class AL11 {

	/* --- Listener Parameters --- */

	public static void alListener3i(int param, int v1, int v2, int v3) {
		ALRT.checkContext("alListener3i");
		org.lwjgl.openal.AL11.alListener3i(param, v1, v2, v3);
	}

	public static void alGetListener3i(int param, IntBuffer v1, IntBuffer v2, IntBuffer v3) {
		ALRT.checkContext("alGetListener3i");
		org.lwjgl.openal.AL11.alGetListener3i(param, v1, v2, v3);
	}

	public static void alGetListener3i(int param, int[] v1, int[] v2, int[] v3) {
		ALRT.checkContext("alGetListener3i");
		org.lwjgl.openal.AL11.alGetListener3i(param, v1, v2, v3);
	}

	public static void alListeneriv(int param, IntBuffer values) {
		ALRT.checkContext("alListeneriv");
		org.lwjgl.openal.AL11.alListeneriv(param, values);
	}

	public static void alListeneriv(int param, int[] values) {
		ALRT.checkContext("alListeneriv");
		org.lwjgl.openal.AL11.alListeneriv(param, values);
	}

	public static void alGetListeneriv(int param, IntBuffer values) {
		ALRT.checkContext("alGetListeneriv");
		org.lwjgl.openal.AL11.alGetListeneriv(param, values);
	}

	public static void alGetListeneriv(int param, int[] values) {
		ALRT.checkContext("alGetListeneriv");
		org.lwjgl.openal.AL11.alGetListeneriv(param, values);
	}

	/* --- Source Parameters --- */

	public static void alSource3i(int source, int param, int v1, int v2, int v3) {
		ALContext ctx = ALRT.checkContext("alSource3i");
		ALRT.checkSource(ctx, source, "alSource3i");
		org.lwjgl.openal.AL11.alSource3i(source, param, v1, v2, v3);
	}

	public static void alGetSource3i(int source, int param, IntBuffer v1, IntBuffer v2, IntBuffer v3) {
		ALContext ctx = ALRT.checkContext("alGetSource3i");
		ALRT.checkSource(ctx, source, "alGetSource3i");
		org.lwjgl.openal.AL11.alGetSource3i(source, param, v1, v2, v3);
	}

	public static void alGetSource3i(int source, int param, int[] v1, int[] v2, int[] v3) {
		ALContext ctx = ALRT.checkContext("alGetSource3i");
		ALRT.checkSource(ctx, source, "alGetSource3i");
		org.lwjgl.openal.AL11.alGetSource3i(source, param, v1, v2, v3);
	}

	public static void alSourceiv(int source, int param, IntBuffer values) {
		ALContext ctx = ALRT.checkContext("alSourceiv");
		ALRT.checkSource(ctx, source, "alSourceiv");
		org.lwjgl.openal.AL11.alSourceiv(source, param, values);
	}

	public static void alSourceiv(int source, int param, int[] values) {
		ALContext ctx = ALRT.checkContext("alSourceiv");
		ALRT.checkSource(ctx, source, "alSourceiv");
		org.lwjgl.openal.AL11.alSourceiv(source, param, values);
	}

	/* --- Buffer Parameters --- */

	public static void alBufferf(int buffer, int param, float value) {
		ALContext ctx = ALRT.checkContext("alBufferf");
		ALRT.checkBuffer(ctx, buffer, "alBufferf");
		org.lwjgl.openal.AL11.alBufferf(buffer, param, value);
	}

	public static void alBuffer3f(int buffer, int param, float v1, float v2, float v3) {
		ALContext ctx = ALRT.checkContext("alBuffer3f");
		ALRT.checkBuffer(ctx, buffer, "alBuffer3f");
		org.lwjgl.openal.AL11.alBuffer3f(buffer, param, v1, v2, v3);
	}

	public static void alBufferfv(int buffer, int param, FloatBuffer values) {
		ALContext ctx = ALRT.checkContext("alBufferfv");
		ALRT.checkBuffer(ctx, buffer, "alBufferfv");
		org.lwjgl.openal.AL11.alBufferfv(buffer, param, values);
	}

	public static void alBufferfv(int buffer, int param, float[] values) {
		ALContext ctx = ALRT.checkContext("alBufferfv");
		ALRT.checkBuffer(ctx, buffer, "alBufferfv");
		org.lwjgl.openal.AL11.alBufferfv(buffer, param, values);
	}

	public static void alBufferi(int buffer, int param, int value) {
		ALContext ctx = ALRT.checkContext("alBufferi");
		ALRT.checkBuffer(ctx, buffer, "alBufferi");
		org.lwjgl.openal.AL11.alBufferi(buffer, param, value);
	}

	public static void alBuffer3i(int buffer, int param, int v1, int v2, int v3) {
		ALContext ctx = ALRT.checkContext("alBuffer3i");
		ALRT.checkBuffer(ctx, buffer, "alBuffer3i");
		org.lwjgl.openal.AL11.alBuffer3i(buffer, param, v1, v2, v3);
	}

	public static void alBufferiv(int buffer, int param, IntBuffer values) {
		ALContext ctx = ALRT.checkContext("alBufferiv");
		ALRT.checkBuffer(ctx, buffer, "alBufferiv");
		org.lwjgl.openal.AL11.alBufferiv(buffer, param, values);
	}

	public static void alBufferiv(int buffer, int param, int[] values) {
		ALContext ctx = ALRT.checkContext("alBufferiv");
		ALRT.checkBuffer(ctx, buffer, "alBufferiv");
		org.lwjgl.openal.AL11.alBufferiv(buffer, param, values);
	}

	public static void alGetBuffer3i(int buffer, int param, IntBuffer v1, IntBuffer v2, IntBuffer v3) {
		ALContext ctx = ALRT.checkContext("alGetBuffer3i");
		ALRT.checkBuffer(ctx, buffer, "alGetBuffer3i");
		org.lwjgl.openal.AL11.alGetBuffer3i(buffer, param, v1, v2, v3);
	}

	public static void alGetBuffer3i(int buffer, int param, int[] v1, int[] v2, int[] v3) {
		ALContext ctx = ALRT.checkContext("alGetBuffer3i");
		ALRT.checkBuffer(ctx, buffer, "alGetBuffer3i");
		org.lwjgl.openal.AL11.alGetBuffer3i(buffer, param, v1, v2, v3);
	}

	public static void alGetBufferiv(int buffer, int param, IntBuffer values) {
		ALContext ctx = ALRT.checkContext("alGetBufferiv");
		ALRT.checkBuffer(ctx, buffer, "alGetBufferiv");
		org.lwjgl.openal.AL11.alGetBufferiv(buffer, param, values);
	}

	public static void alGetBufferiv(int buffer, int param, int[] values) {
		ALContext ctx = ALRT.checkContext("alGetBufferiv");
		ALRT.checkBuffer(ctx, buffer, "alGetBufferiv");
		org.lwjgl.openal.AL11.alGetBufferiv(buffer, param, values);
	}

	public static void alGetBuffer3f(int buffer, int param, FloatBuffer v1, FloatBuffer v2, FloatBuffer v3) {
		ALContext ctx = ALRT.checkContext("alGetBuffer3f");
		ALRT.checkBuffer(ctx, buffer, "alGetBuffer3f");
		org.lwjgl.openal.AL11.alGetBuffer3f(buffer, param, v1, v2, v3);
	}

	public static void alGetBuffer3f(int buffer, int param, float[] v1, float[] v2, float[] v3) {
		ALContext ctx = ALRT.checkContext("alGetBuffer3f");
		ALRT.checkBuffer(ctx, buffer, "alGetBuffer3f");
		org.lwjgl.openal.AL11.alGetBuffer3f(buffer, param, v1, v2, v3);
	}

	public static void alGetBufferfv(int buffer, int param, FloatBuffer values) {
		ALContext ctx = ALRT.checkContext("alGetBufferfv");
		ALRT.checkBuffer(ctx, buffer, "alGetBufferfv");
		org.lwjgl.openal.AL11.alGetBufferfv(buffer, param, values);
	}

	public static void alGetBufferfv(int buffer, int param, float[] values) {
		ALContext ctx = ALRT.checkContext("alGetBufferfv");
		ALRT.checkBuffer(ctx, buffer, "alGetBufferfv");
		org.lwjgl.openal.AL11.alGetBufferfv(buffer, param, values);
	}

	/* --- Global Queries --- */

	public static void alSpeedOfSound(float value) {
		ALRT.checkContext("alSpeedOfSound");
		org.lwjgl.openal.AL11.alSpeedOfSound(value);
	}
}
