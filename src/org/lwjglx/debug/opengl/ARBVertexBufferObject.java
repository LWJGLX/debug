package org.lwjglx.debug.opengl;

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

public class ARBVertexBufferObject {

    public static void glGenBuffersARB(IntBuffer buffers) {
        org.lwjgl.opengl.ARBVertexBufferObject.glGenBuffersARB(buffers);
        if (Properties.PROFILE.enabled) {
            Context ctx = Context.currentContext();
            int pos = buffers.position();
            for (int i = 0; i < buffers.remaining(); i++) {
                int buffer = buffers.get(pos + i);
                ctx.shareGroup.bufferObjects.put(buffer, new BufferObject());
            }
        }
    }

    public static void glGenBuffersARB(int[] buffers) {
        org.lwjgl.opengl.ARBVertexBufferObject.glGenBuffersARB(buffers);
        if (Properties.PROFILE.enabled) {
            Context ctx = Context.currentContext();
            for (int i = 0; i < buffers.length; i++) {
                int buffer = buffers[i];
                ctx.shareGroup.bufferObjects.put(buffer, new BufferObject());
            }
        }
    }

    public static int glGenBuffersARB() {
        int ret = org.lwjgl.opengl.ARBVertexBufferObject.glGenBuffersARB();
        if (Properties.PROFILE.enabled) {
            Context ctx = Context.currentContext();
            ctx.shareGroup.bufferObjects.put(ret, new BufferObject());
        }
        return ret;
    }

    public static void glBindBufferARB(int target, int buffer) {
        org.lwjgl.opengl.ARBVertexBufferObject.glBindBufferARB(target, buffer);
        if (Properties.PROFILE.enabled) {
            Context ctx = Context.currentContext();
            if (buffer != 0) {
                BufferObject bo = ctx.shareGroup.bufferObjects.get(buffer);
                ctx.bufferObjectBindings.put(target, bo);
            } else {
                ctx.bufferObjectBindings.remove(target);
            }
        }
    }

    public static void glDeleteBuffersARB(IntBuffer buffers) {
        org.lwjgl.opengl.ARBVertexBufferObject.glDeleteBuffersARB(buffers);
        if (Properties.PROFILE.enabled) {
            Context ctx = Context.currentContext();
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

    public static void glDeleteBuffersARB(int[] buffers) {
        org.lwjgl.opengl.ARBVertexBufferObject.glDeleteBuffersARB(buffers);
        if (Properties.PROFILE.enabled) {
            Context ctx = Context.currentContext();
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

    public static void glDeleteBuffersARB(int buffer) {
        org.lwjgl.opengl.ARBVertexBufferObject.glDeleteBuffersARB(buffer);
        if (Properties.PROFILE.enabled) {
            Context ctx = Context.currentContext();
            BufferObject bo = ctx.shareGroup.bufferObjects.remove(buffer);
            Iterator<Map.Entry<Integer, BufferObject>> it = ctx.bufferObjectBindings.entrySet().iterator();
            while (it.hasNext()) {
                if (it.next().getValue() == bo) {
                    it.remove();
                }
            }
        }
    }

    public static void glBufferDataARB(int target, long size, int usage) {
        org.lwjgl.opengl.ARBVertexBufferObject.glBufferDataARB(target, size, usage);
        if (Properties.PROFILE.enabled) {
            Context ctx = Context.currentContext();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = size;
            }
        }
    }

    public static void glBufferDataARB(int target, ByteBuffer data, int usage) {
        org.lwjgl.opengl.ARBVertexBufferObject.glBufferDataARB(target, data, usage);
        if (Properties.PROFILE.enabled) {
            Context ctx = Context.currentContext();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.remaining() : 0L;
            }
        }
    }

    public static void glBufferDataARB(int target, ShortBuffer data, int usage) {
        org.lwjgl.opengl.ARBVertexBufferObject.glBufferDataARB(target, data, usage);
        if (Properties.PROFILE.enabled) {
            Context ctx = Context.currentContext();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.remaining() << 1 : 0L;
            }
        }
    }

    public static void glBufferDataARB(int target, short[] data, int usage) {
        org.lwjgl.opengl.ARBVertexBufferObject.glBufferDataARB(target, data, usage);
        if (Properties.PROFILE.enabled) {
            Context ctx = Context.currentContext();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.length << 1 : 0L;
            }
        }
    }

    public static void glBufferDataARB(int target, IntBuffer data, int usage) {
        org.lwjgl.opengl.ARBVertexBufferObject.glBufferDataARB(target, data, usage);
        if (Properties.PROFILE.enabled) {
            Context ctx = Context.currentContext();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.remaining() << 2 : 0L;
            }
        }
    }

    public static void glBufferDataARB(int target, int[] data, int usage) {
        org.lwjgl.opengl.ARBVertexBufferObject.glBufferDataARB(target, data, usage);
        if (Properties.PROFILE.enabled) {
            Context ctx = Context.currentContext();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.length << 2 : 0L;
            }
        }
    }

    public static void glBufferDataARB(int target, FloatBuffer data, int usage) {
        org.lwjgl.opengl.ARBVertexBufferObject.glBufferDataARB(target, data, usage);
        if (Properties.PROFILE.enabled) {
            Context ctx = Context.currentContext();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.remaining() << 2 : 0L;
            }
        }
    }

    public static void glBufferDataARB(int target, float[] data, int usage) {
        org.lwjgl.opengl.ARBVertexBufferObject.glBufferDataARB(target, data, usage);
        if (Properties.PROFILE.enabled) {
            Context ctx = Context.currentContext();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.length << 2 : 0L;
            }
        }
    }

    public static void glBufferDataARB(int target, DoubleBuffer data, int usage) {
        org.lwjgl.opengl.ARBVertexBufferObject.glBufferDataARB(target, data, usage);
        if (Properties.PROFILE.enabled) {
            Context ctx = Context.currentContext();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.remaining() << 3 : 0L;
            }
        }
    }

    public static void glBufferDataARB(int target, double[] data, int usage) {
        org.lwjgl.opengl.ARBVertexBufferObject.glBufferDataARB(target, data, usage);
        if (Properties.PROFILE.enabled) {
            Context ctx = Context.currentContext();
            BufferObject bo = ctx.bufferObjectBindings.get(target);
            if (bo != null) {
                bo.size = data != null ? data.length << 3 : 0L;
            }
        }
    }

}
