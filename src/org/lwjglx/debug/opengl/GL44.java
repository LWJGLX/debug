package org.lwjglx.debug.opengl;

import static org.lwjglx.debug.Context.*;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjglx.debug.Context;
import org.lwjglx.debug.Properties;
import org.lwjglx.debug.Context.BufferObject;

public class GL44 {

    public static void glBufferStorage(int target, long size, int flags) {
        org.lwjgl.opengl.GL44.glBufferStorage(target, size, flags);
        if (Properties.PROFILE) {
            Context ctx = CURRENT_CONTEXT.get();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = size;
            }
        }
    }

    public static void glBufferStorage(int target, ByteBuffer data, int flags) {
        org.lwjgl.opengl.GL44.glBufferStorage(target, data, flags);
        if (Properties.PROFILE) {
            Context ctx = CURRENT_CONTEXT.get();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.remaining() : 0L;
            }
        }
    }

    public static void glBufferStorage(int target, ShortBuffer data, int flags) {
        org.lwjgl.opengl.GL44.glBufferStorage(target, data, flags);
        if (Properties.PROFILE) {
            Context ctx = CURRENT_CONTEXT.get();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.remaining() << 1 : 0L;
            }
        }
    }

    public static void glBufferStorage(int target, short[] data, int flags) {
        org.lwjgl.opengl.GL44.glBufferStorage(target, data, flags);
        if (Properties.PROFILE) {
            Context ctx = CURRENT_CONTEXT.get();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.length << 1 : 0L;
            }
        }
    }

    public static void glBufferStorage(int target, IntBuffer data, int flags) {
        org.lwjgl.opengl.GL44.glBufferStorage(target, data, flags);
        if (Properties.PROFILE) {
            Context ctx = CURRENT_CONTEXT.get();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.remaining() << 2 : 0L;
            }
        }
    }

    public static void glBufferStorage(int target, int[] data, int flags) {
        org.lwjgl.opengl.GL44.glBufferStorage(target, data, flags);
        if (Properties.PROFILE) {
            Context ctx = CURRENT_CONTEXT.get();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.length << 2 : 0L;
            }
        }
    }

    public static void glBufferStorage(int target, FloatBuffer data, int flags) {
        org.lwjgl.opengl.GL44.glBufferStorage(target, data, flags);
        if (Properties.PROFILE) {
            Context ctx = CURRENT_CONTEXT.get();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.remaining() << 2 : 0L;
            }
        }
    }

    public static void glBufferStorage(int target, float[] data, int flags) {
        org.lwjgl.opengl.GL44.glBufferStorage(target, data, flags);
        if (Properties.PROFILE) {
            Context ctx = CURRENT_CONTEXT.get();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.length << 2 : 0L;
            }
        }
    }

    public static void glBufferStorage(int target, DoubleBuffer data, int flags) {
        org.lwjgl.opengl.GL44.glBufferStorage(target, data, flags);
        if (Properties.PROFILE) {
            Context ctx = CURRENT_CONTEXT.get();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.remaining() << 3 : 0L;
            }
        }
    }

    public static void glBufferStorage(int target, double[] data, int flags) {
        org.lwjgl.opengl.GL44.glBufferStorage(target, data, flags);
        if (Properties.PROFILE) {
            Context ctx = CURRENT_CONTEXT.get();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.length << 3 : 0L;
            }
        }
    }

}
