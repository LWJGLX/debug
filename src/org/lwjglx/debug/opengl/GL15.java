package org.lwjglx.debug.opengl;

import static org.lwjglx.debug.Context.*;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Iterator;
import java.util.Map;

import org.lwjglx.debug.Context;
import org.lwjglx.debug.Context.BufferObject;
import org.lwjglx.debug.Properties;

public class GL15 {

    public static void glGenBuffers(IntBuffer buffers) {
        org.lwjgl.opengl.GL15.glGenBuffers(buffers);
        if (Properties.PROFILE.enabled) {
            Context ctx = CURRENT_CONTEXT.get();
            int pos = buffers.position();
            for (int i = 0; i < buffers.remaining(); i++) {
                int buffer = buffers.get(pos + i);
                ctx.shareGroup.bufferObjects.put(buffer, new BufferObject());
            }
        }
    }

    public static void glGenBuffers(int[] buffers) {
        org.lwjgl.opengl.GL15.glGenBuffers(buffers);
        if (Properties.PROFILE.enabled) {
            Context ctx = CURRENT_CONTEXT.get();
            for (int i = 0; i < buffers.length; i++) {
                int buffer = buffers[i];
                ctx.shareGroup.bufferObjects.put(buffer, new BufferObject());
            }
        }
    }

    public static int glGenBuffers() {
        int ret = org.lwjgl.opengl.GL15.glGenBuffers();
        if (Properties.PROFILE.enabled) {
            Context ctx = CURRENT_CONTEXT.get();
            ctx.shareGroup.bufferObjects.put(ret, new BufferObject());
        }
        return ret;
    }

    public static void glBindBuffer(int target, int buffer) {
        org.lwjgl.opengl.GL15.glBindBuffer(target, buffer);
        if (Properties.PROFILE.enabled) {
            Context ctx = CURRENT_CONTEXT.get();
            if (buffer != 0) {
                BufferObject bo = ctx.shareGroup.bufferObjects.get(buffer);
                ctx.bufferObjectBindings.put(target, bo);
            } else {
                ctx.bufferObjectBindings.remove(target);
            }
        }
    }

    public static void glDeleteBuffers(IntBuffer buffers) {
        org.lwjgl.opengl.GL15.glDeleteBuffers(buffers);
        if (Properties.PROFILE.enabled) {
            Context ctx = CURRENT_CONTEXT.get();
            int pos = buffers.position();
            for (int i = 0; i < buffers.remaining(); i++) {
                int buffer = buffers.get(pos + i);
                BufferObject bo = ctx.shareGroup.bufferObjects.remove(buffer);
                Iterator<Map.Entry<Integer, BufferObject>> it = ctx.bufferObjectBindings.entrySet().iterator();
                while (it.hasNext()) {
                    if (it.next().getValue() == bo) {
                        it.remove();
                    }
                }
            }
        }
    }

    public static void glDeleteBuffers(int[] buffers) {
        org.lwjgl.opengl.GL15.glDeleteBuffers(buffers);
        if (Properties.PROFILE.enabled) {
            Context ctx = CURRENT_CONTEXT.get();
            for (int i = 0; i < buffers.length; i++) {
                int buffer = buffers[i];
                BufferObject bo = ctx.shareGroup.bufferObjects.remove(buffer);
                Iterator<Map.Entry<Integer, BufferObject>> it = ctx.bufferObjectBindings.entrySet().iterator();
                while (it.hasNext()) {
                    if (it.next().getValue() == bo) {
                        it.remove();
                    }
                }
            }
        }
    }

    public static void glDeleteBuffers(int buffer) {
        org.lwjgl.opengl.GL15.glDeleteBuffers(buffer);
        if (Properties.PROFILE.enabled) {
            Context ctx = CURRENT_CONTEXT.get();
            BufferObject bo = ctx.shareGroup.bufferObjects.remove(buffer);
            Iterator<Map.Entry<Integer, BufferObject>> it = ctx.bufferObjectBindings.entrySet().iterator();
            while (it.hasNext()) {
                if (it.next().getValue() == bo) {
                    it.remove();
                }
            }
        }
    }

    public static void glBufferData(int target, long size, int usage) {
        org.lwjgl.opengl.GL15.glBufferData(target, size, usage);
        if (Properties.PROFILE.enabled) {
            Context ctx = CURRENT_CONTEXT.get();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = size;
            }
        }
    }

    public static void glBufferData(int target, ByteBuffer data, int usage) {
        org.lwjgl.opengl.GL15.glBufferData(target, data, usage);
        if (Properties.PROFILE.enabled) {
            Context ctx = CURRENT_CONTEXT.get();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.remaining() : 0L;
            }
        }
    }

    public static void glBufferData(int target, ShortBuffer data, int usage) {
        org.lwjgl.opengl.GL15.glBufferData(target, data, usage);
        if (Properties.PROFILE.enabled) {
            Context ctx = CURRENT_CONTEXT.get();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.remaining() << 1 : 0L;
            }
        }
    }

    public static void glBufferData(int target, short[] data, int usage) {
        org.lwjgl.opengl.GL15.glBufferData(target, data, usage);
        if (Properties.PROFILE.enabled) {
            Context ctx = CURRENT_CONTEXT.get();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.length << 1 : 0L;
            }
        }
    }

    public static void glBufferData(int target, IntBuffer data, int usage) {
        org.lwjgl.opengl.GL15.glBufferData(target, data, usage);
        if (Properties.PROFILE.enabled) {
            Context ctx = CURRENT_CONTEXT.get();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.remaining() << 2 : 0L;
            }
        }
    }

    public static void glBufferData(int target, int[] data, int usage) {
        org.lwjgl.opengl.GL15.glBufferData(target, data, usage);
        if (Properties.PROFILE.enabled) {
            Context ctx = CURRENT_CONTEXT.get();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.length << 2 : 0L;
            }
        }
    }

    public static void glBufferData(int target, FloatBuffer data, int usage) {
        org.lwjgl.opengl.GL15.glBufferData(target, data, usage);
        if (Properties.PROFILE.enabled) {
            Context ctx = CURRENT_CONTEXT.get();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.remaining() << 2 : 0L;
            }
        }
    }

    public static void glBufferData(int target, float[] data, int usage) {
        org.lwjgl.opengl.GL15.glBufferData(target, data, usage);
        if (Properties.PROFILE.enabled) {
            Context ctx = CURRENT_CONTEXT.get();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.length << 2 : 0L;
            }
        }
    }

    public static void glBufferData(int target, DoubleBuffer data, int usage) {
        org.lwjgl.opengl.GL15.glBufferData(target, data, usage);
        if (Properties.PROFILE.enabled) {
            Context ctx = CURRENT_CONTEXT.get();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.remaining() << 3 : 0L;
            }
        }
    }

    public static void glBufferData(int target, double[] data, int usage) {
        org.lwjgl.opengl.GL15.glBufferData(target, data, usage);
        if (Properties.PROFILE.enabled) {
            Context ctx = CURRENT_CONTEXT.get();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.length << 3 : 0L;
            }
        }
    }

}
