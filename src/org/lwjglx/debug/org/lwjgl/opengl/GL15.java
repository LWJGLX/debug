package org.lwjglx.debug.org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import org.lwjglx.debug.Properties;
import org.lwjglx.debug.RT;
import org.lwjglx.debug.org.lwjgl.opengl.Context.BufferObject;

public class GL15 {

	public static void glGenBuffers(IntBuffer buffers) {
		org.lwjgl.opengl.GL15.glGenBuffers(buffers);
		if (Properties.VALIDATE.enabled) {
			Context ctx = Context.currentContext();
			int pos = buffers.position();
			for (int i = 0; i < buffers.remaining(); i++) {
				int b = buffers.get(pos + i);
				if (b != 0) {
					BufferObject bo = new BufferObject();
					ctx.shareGroup.bufferObjects.create(b, bo);
				}
			}
		}
	}

	public static void glGenBuffers(int[] buffers) {
		org.lwjgl.opengl.GL15.glGenBuffers(buffers);
		if (Properties.VALIDATE.enabled) {
			Context ctx = Context.currentContext();
			for (int i = 0; i < buffers.length; i++) {
				int b = buffers[i];
				if (b != 0) {
					BufferObject bo = new BufferObject();
					ctx.shareGroup.bufferObjects.create(b, bo);
				}
			}
		}
	}

	public static int glGenBuffers() {
		int b = org.lwjgl.opengl.GL15.glGenBuffers();
		if (Properties.VALIDATE.enabled && b != 0) {
			Context ctx = Context.currentContext();
			BufferObject bo = new BufferObject();
			ctx.shareGroup.bufferObjects.create(b, bo);
		}
		return b;
	}

	public static void glDeleteBuffers(int buffer) {
		if (Properties.VALIDATE.enabled && buffer != 0) {
			Context ctx = Context.currentContext();
			ctx.shareGroup.bufferObjects.delete(buffer, "glDeleteBuffers");
			ctx.bufferObjectBindings.remove(buffer);
		}
		org.lwjgl.opengl.GL15.glDeleteBuffers(buffer);
	}

	public static void glDeleteBuffers(IntBuffer buffers) {
		if (Properties.VALIDATE.enabled) {
			Context ctx = Context.currentContext();
			int pos = buffers.position();
			for (int i = 0; i < buffers.remaining(); i++) {
				int b = buffers.get(pos + i);
				if (b != 0) {
					ctx.shareGroup.bufferObjects.delete(b, "glDeleteBuffers");
					ctx.bufferObjectBindings.remove(b);
				}
			}
		}
		org.lwjgl.opengl.GL15.glDeleteBuffers(buffers);
	}

	public static void glDeleteBuffers(int[] buffers) {
		if (Properties.VALIDATE.enabled) {
			Context ctx = Context.currentContext();
			for (int i = 0; i < buffers.length; i++) {
				int b = buffers[i];
				if (b != 0) {
					ctx.shareGroup.bufferObjects.delete(b, "glDeleteBuffers");
					ctx.bufferObjectBindings.remove(b);
				}
			}
		}
		org.lwjgl.opengl.GL15.glDeleteBuffers(buffers);
	}

	public static void glBindBuffer(int target, int buffer) {
		if (Properties.VALIDATE.enabled && buffer != 0) {
			Context ctx = Context.currentContext();
			ctx.shareGroup.bufferObjects.checkAlive(buffer, "glBindBuffer");
		}
		org.lwjgl.opengl.GL15.glBindBuffer(target, buffer);
	}

	public static boolean glIsBuffer(int buffer) {
		return org.lwjgl.opengl.GL15.glIsBuffer(buffer);
	}

	public static void glBufferData(int target, long size, int usage) {
		org.lwjgl.opengl.GL15.glBufferData(target, size, usage);
	}

	public static void glBufferData(int target, ByteBuffer data, int usage) {
		org.lwjgl.opengl.GL15.glBufferData(target, data, usage);
	}

	public static void glBufferData(int target, ShortBuffer data, int usage) {
		org.lwjgl.opengl.GL15.glBufferData(target, data, usage);
	}

	public static void glBufferData(int target, IntBuffer data, int usage) {
		org.lwjgl.opengl.GL15.glBufferData(target, data, usage);
	}

	public static void glBufferData(int target, LongBuffer data, int usage) {
		org.lwjgl.opengl.GL15.glBufferData(target, data, usage);
	}

	public static void glBufferData(int target, FloatBuffer data, int usage) {
		org.lwjgl.opengl.GL15.glBufferData(target, data, usage);
	}

	public static void glBufferData(int target, DoubleBuffer data, int usage) {
		org.lwjgl.opengl.GL15.glBufferData(target, data, usage);
	}

	public static void glBufferData(int target, short[] data, int usage) {
		org.lwjgl.opengl.GL15.glBufferData(target, data, usage);
	}

	public static void glBufferData(int target, int[] data, int usage) {
		org.lwjgl.opengl.GL15.glBufferData(target, data, usage);
	}

	public static void glBufferData(int target, long[] data, int usage) {
		org.lwjgl.opengl.GL15.glBufferData(target, data, usage);
	}

	public static void glBufferData(int target, float[] data, int usage) {
		org.lwjgl.opengl.GL15.glBufferData(target, data, usage);
	}

	public static void glBufferData(int target, double[] data, int usage) {
		org.lwjgl.opengl.GL15.glBufferData(target, data, usage);
	}
}
