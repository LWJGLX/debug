/*
 * (C) Copyright 2017 Kai Burjack

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

 */
package org.lwjglx.debug;

import static org.lwjglx.debug.Context.CONTEXTS;
import static org.lwjglx.debug.Context.CURRENT_CONTEXT;
import static org.lwjglx.debug.Log.error;
import static org.lwjglx.debug.Log.trace;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.lwjgl.PointerBuffer;

class Command {
    List<Param> params = new ArrayList<>();
    Map<Integer, String> returnGroup;
    Map<Integer, String> extension;

    void addParam(String name, int type, Map<Integer, String> group) {
        Param p = new Param();
        p.name = name;
        p.type = type;
        p.group = group;
        params.add(p);
    }
}

class Param {
    String name;
    int type;
    Map<Integer, String> group;
}

/**
 * Runtime support methods for generated classes and manual validation/trace methods.
 */
public class RT {

    private static final Map<Buffer, ByteOrder> bufferEndiannessWritten = Collections.synchronizedMap(new WeakIdentityHashMap<>());
    private static final Map<Buffer, Buffer> bufferViews = Collections.synchronizedMap(new WeakIdentityHashMap<>());

    private static void throwIfNotNativeEndianness(ByteOrder order) {
        if (order != null && order != ByteOrder.nativeOrder()) {
            throwIAEOrLogError("buffer contains values written using non-native endianness.");
        }
    }

    public static boolean checkNativeByteOrder(Buffer buf) {
        if (buf == null) {
            return true;
        }
        throwIfNotNativeEndianness(bufferEndiannessWritten.get(buf));
        Buffer viewedBuffer = bufferViews.get(buf);
        if (viewedBuffer != null) {
            throwIfNotNativeEndianness(bufferEndiannessWritten.get(viewedBuffer));
        }
        return true;
    }

    private static void writeByteBuffer(ByteBuffer buf) {
        ByteOrder order = bufferEndiannessWritten.get(buf);
        if (order == null) {
            Buffer viewedBuffer = bufferViews.get(buf);
            if (viewedBuffer != null) {
                bufferEndiannessWritten.put(viewedBuffer, buf.order());
            } else {
                bufferEndiannessWritten.put(buf, buf.order());
            }
        }
    }

    private static void writeCharBuffer(CharBuffer buf) {
        Buffer viewedBuffer = bufferViews.get(buf);
        if (viewedBuffer != null) {
            bufferEndiannessWritten.put(viewedBuffer, buf.order());
        } else {
            /* We don't know how that typed view was created. Just assume it is correct. */
        }
    }

    private static void writeShortBuffer(ShortBuffer buf) {
        Buffer viewedBuffer = bufferViews.get(buf);
        if (viewedBuffer != null) {
            bufferEndiannessWritten.put(viewedBuffer, buf.order());
        } else {
            /* We don't know how that typed view was created. Just assume it is correct. */
        }
    }

    private static void writeIntBuffer(IntBuffer buf) {
        Buffer viewedBuffer = bufferViews.get(buf);
        if (viewedBuffer != null) {
            bufferEndiannessWritten.put(viewedBuffer, buf.order());
        } else {
            /* We don't know how that typed view was created. Just assume it is correct. */
        }
    }

    private static void writeLongBuffer(LongBuffer buf) {
        Buffer viewedBuffer = bufferViews.get(buf);
        if (viewedBuffer != null) {
            bufferEndiannessWritten.put(viewedBuffer, buf.order());
        } else {
            /* We don't know how that typed view was created. Just assume it is correct. */
        }
    }

    private static void writeFloatBuffer(FloatBuffer buf) {
        Buffer viewedBuffer = bufferViews.get(buf);
        if (viewedBuffer != null) {
            bufferEndiannessWritten.put(viewedBuffer, buf.order());
        } else {
            /* We don't know how that typed view was created. Just assume it is correct. */
        }
    }

    private static void writeDoubleBuffer(DoubleBuffer buf) {
        Buffer viewedBuffer = bufferViews.get(buf);
        if (viewedBuffer != null) {
            bufferEndiannessWritten.put(viewedBuffer, buf.order());
        } else {
            throw new AssertionError();
        }
    }

    public static ByteBuffer slice(ByteBuffer buf) {
        ByteBuffer buffer = buf.slice();
        Buffer viewedBuffer = bufferViews.get(buf);
        if (viewedBuffer != null) {
            bufferViews.put(buffer, viewedBuffer);
        } else {
            bufferViews.put(buffer, buf);
        }
        return buffer;
    }

    public static CharBuffer slice(CharBuffer buf) {
        CharBuffer buffer = buf.slice();
        Buffer viewedBuffer = bufferViews.get(buf);
        if (viewedBuffer != null) {
            bufferViews.put(buffer, viewedBuffer);
        }
        return buffer;
    }

    public static ShortBuffer slice(ShortBuffer buf) {
        ShortBuffer buffer = buf.slice();
        Buffer viewedBuffer = bufferViews.get(buf);
        if (viewedBuffer != null) {
            bufferViews.put(buffer, viewedBuffer);
        }
        return buffer;
    }

    public static IntBuffer slice(IntBuffer buf) {
        IntBuffer buffer = buf.slice();
        Buffer viewedBuffer = bufferViews.get(buf);
        if (viewedBuffer != null) {
            bufferViews.put(buffer, viewedBuffer);
        }
        return buffer;
    }

    public static LongBuffer slice(LongBuffer buf) {
        LongBuffer buffer = buf.slice();
        Buffer viewedBuffer = bufferViews.get(buf);
        if (viewedBuffer != null) {
            bufferViews.put(buffer, viewedBuffer);
        }
        return buffer;
    }

    public static FloatBuffer slice(FloatBuffer buf) {
        FloatBuffer buffer = buf.slice();
        Buffer viewedBuffer = bufferViews.get(buf);
        if (viewedBuffer != null) {
            bufferViews.put(buffer, viewedBuffer);
        }
        return buffer;
    }

    public static DoubleBuffer slice(DoubleBuffer buf) {
        DoubleBuffer buffer = buf.slice();
        Buffer viewedBuffer = bufferViews.get(buf);
        if (viewedBuffer != null) {
            bufferViews.put(buffer, viewedBuffer);
        }
        return buffer;
    }

    public static CharBuffer asCharBuffer(ByteBuffer buf) {
        CharBuffer buffer = buf.asCharBuffer();
        Buffer viewedBuffer = bufferViews.get(buf);
        if (viewedBuffer != null) {
            bufferViews.put(buffer, viewedBuffer);
        } else {
            bufferViews.put(buffer, buf);
        }
        return buffer;
    }

    public static ShortBuffer asShortBuffer(ByteBuffer buf) {
        ShortBuffer buffer = buf.asShortBuffer();
        Buffer viewedBuffer = bufferViews.get(buf);
        if (viewedBuffer != null) {
            bufferViews.put(buffer, viewedBuffer);
        } else {
            bufferViews.put(buffer, buf);
        }
        return buffer;
    }

    public static IntBuffer asIntBuffer(ByteBuffer buf) {
        IntBuffer buffer = buf.asIntBuffer();
        Buffer viewedBuffer = bufferViews.get(buf);
        if (viewedBuffer != null) {
            bufferViews.put(buffer, viewedBuffer);
        } else {
            bufferViews.put(buffer, buf);
        }
        return buffer;
    }

    public static LongBuffer asLongBuffer(ByteBuffer buf) {
        LongBuffer buffer = buf.asLongBuffer();
        Buffer viewedBuffer = bufferViews.get(buf);
        if (viewedBuffer != null) {
            bufferViews.put(buffer, viewedBuffer);
        } else {
            bufferViews.put(buffer, buf);
        }
        return buffer;
    }

    public static FloatBuffer asFloatBuffer(ByteBuffer buf) {
        FloatBuffer buffer = buf.asFloatBuffer();
        Buffer viewedBuffer = bufferViews.get(buf);
        if (viewedBuffer != null) {
            bufferViews.put(buffer, viewedBuffer);
        } else {
            bufferViews.put(buffer, buf);
        }
        return buffer;
    }

    public static DoubleBuffer asDoubleBuffer(ByteBuffer buf) {
        DoubleBuffer buffer = buf.asDoubleBuffer();
        Buffer viewedBuffer = bufferViews.get(buf);
        if (viewedBuffer != null) {
            bufferViews.put(buffer, viewedBuffer);
        } else {
            bufferViews.put(buffer, buf);
        }
        return buffer;
    }

    public static ByteBuffer putChar(ByteBuffer buf, char value) {
        buf.putChar(value);
        writeByteBuffer(buf);
        return buf;
    }

    public static ByteBuffer putChar(ByteBuffer buf, int index, char value) {
        buf.putChar(index, value);
        writeByteBuffer(buf);
        return buf;
    }

    public static ByteBuffer putShort(ByteBuffer buf, short value) {
        buf.putShort(value);
        writeByteBuffer(buf);
        return buf;
    }

    public static ByteBuffer putShort(ByteBuffer buf, int index, short value) {
        buf.putShort(index, value);
        writeByteBuffer(buf);
        return buf;
    }

    public static ByteBuffer putInt(ByteBuffer buf, int value) {
        buf.putInt(value);
        writeByteBuffer(buf);
        return buf;
    }

    public static ByteBuffer putInt(ByteBuffer buf, int index, int value) {
        buf.putInt(index, value);
        writeByteBuffer(buf);
        return buf;
    }

    public static ByteBuffer putLong(ByteBuffer buf, long value) {
        buf.putLong(value);
        writeByteBuffer(buf);
        return buf;
    }

    public static ByteBuffer putLong(ByteBuffer buf, int index, long value) {
        buf.putLong(index, value);
        writeByteBuffer(buf);
        return buf;
    }

    public static ByteBuffer putFloat(ByteBuffer buf, float value) {
        buf.putFloat(value);
        writeByteBuffer(buf);
        return buf;
    }

    public static ByteBuffer putFloat(ByteBuffer buf, int index, float value) {
        buf.putFloat(index, value);
        writeByteBuffer(buf);
        return buf;
    }

    public static ByteBuffer putDouble(ByteBuffer buf, double value) {
        buf.putDouble(value);
        writeByteBuffer(buf);
        return buf;
    }

    public static ByteBuffer putDouble(ByteBuffer buf, int index, double value) {
        buf.putDouble(index, value);
        writeByteBuffer(buf);
        return buf;
    }

    public static CharBuffer put(CharBuffer buf, char s) {
        buf.put(s);
        writeCharBuffer(buf);
        return buf;
    }

    public static CharBuffer put(CharBuffer buf, int index, char s) {
        buf.put(index, s);
        writeCharBuffer(buf);
        return buf;
    }

    public static CharBuffer put(CharBuffer buf, CharBuffer src) {
        buf.put(src);
        writeCharBuffer(buf);
        return buf;
    }

    public static CharBuffer put(CharBuffer buf, char[] src, int offset, int length) {
        buf.put(src, offset, length);
        writeCharBuffer(buf);
        return buf;
    }

    public static CharBuffer put(CharBuffer buf, char[] src) {
        buf.put(src);
        writeCharBuffer(buf);
        return buf;
    }

    public static ShortBuffer put(ShortBuffer buf, short s) {
        buf.put(s);
        writeShortBuffer(buf);
        return buf;
    }

    public static ShortBuffer put(ShortBuffer buf, int index, short s) {
        buf.put(index, s);
        writeShortBuffer(buf);
        return buf;
    }

    public static ShortBuffer put(ShortBuffer buf, ShortBuffer src) {
        buf.put(src);
        writeShortBuffer(buf);
        return buf;
    }

    public static ShortBuffer put(ShortBuffer buf, short[] src, int offset, int length) {
        buf.put(src, offset, length);
        writeShortBuffer(buf);
        return buf;
    }

    public static ShortBuffer put(ShortBuffer buf, short[] src) {
        buf.put(src);
        writeShortBuffer(buf);
        return buf;
    }

    public static IntBuffer put(IntBuffer buf, int s) {
        buf.put(s);
        writeIntBuffer(buf);
        return buf;
    }

    public static IntBuffer put(IntBuffer buf, int index, int s) {
        buf.put(index, s);
        writeIntBuffer(buf);
        return buf;
    }

    public static IntBuffer put(IntBuffer buf, IntBuffer src) {
        buf.put(src);
        writeIntBuffer(buf);
        return buf;
    }

    public static IntBuffer put(IntBuffer buf, int[] src, int offset, int length) {
        buf.put(src, offset, length);
        writeIntBuffer(buf);
        return buf;
    }

    public static IntBuffer put(IntBuffer buf, int[] src) {
        buf.put(src);
        writeIntBuffer(buf);
        return buf;
    }

    public static LongBuffer put(LongBuffer buf, long s) {
        buf.put(s);
        writeLongBuffer(buf);
        return buf;
    }

    public static LongBuffer put(LongBuffer buf, int index, long s) {
        buf.put(index, s);
        writeLongBuffer(buf);
        return buf;
    }

    public static LongBuffer put(LongBuffer buf, LongBuffer src) {
        buf.put(src);
        writeLongBuffer(buf);
        return buf;
    }

    public static LongBuffer put(LongBuffer buf, long[] src, int offset, int length) {
        buf.put(src, offset, length);
        writeLongBuffer(buf);
        return buf;
    }

    public static LongBuffer put(LongBuffer buf, long[] src) {
        buf.put(src);
        writeLongBuffer(buf);
        return buf;
    }

    public static FloatBuffer put(FloatBuffer buf, float s) {
        buf.put(s);
        writeFloatBuffer(buf);
        return buf;
    }

    public static FloatBuffer put(FloatBuffer buf, int index, float s) {
        buf.put(index, s);
        writeFloatBuffer(buf);
        return buf;
    }

    public static FloatBuffer put(FloatBuffer buf, FloatBuffer src) {
        buf.put(src);
        writeFloatBuffer(buf);
        return buf;
    }

    public static FloatBuffer put(FloatBuffer buf, float[] src, int offset, int length) {
        buf.put(src, offset, length);
        writeFloatBuffer(buf);
        return buf;
    }

    public static FloatBuffer put(FloatBuffer buf, float[] src) {
        buf.put(src);
        writeFloatBuffer(buf);
        return buf;
    }

    public static DoubleBuffer put(DoubleBuffer buf, double s) {
        buf.put(s);
        writeDoubleBuffer(buf);
        return buf;
    }

    public static DoubleBuffer put(DoubleBuffer buf, int index, double s) {
        buf.put(index, s);
        writeDoubleBuffer(buf);
        return buf;
    }

    public static DoubleBuffer put(DoubleBuffer buf, DoubleBuffer src) {
        buf.put(src);
        writeDoubleBuffer(buf);
        return buf;
    }

    public static DoubleBuffer put(DoubleBuffer buf, double[] src, int offset, int length) {
        buf.put(src, offset, length);
        writeDoubleBuffer(buf);
        return buf;
    }

    public static DoubleBuffer put(DoubleBuffer buf, double[] src) {
        buf.put(src);
        writeDoubleBuffer(buf);
        return buf;
    }

    public static MethodCall methodCall(String source, int line, String name) {
        return new MethodCall(source, line, name);
    }

    public static void methodCall(MethodCall mc) {
        trace(mc.toString());
    }

    public static MethodCall paramGlfwWindow(MethodCall mc, long window) {
        Context ctx = CONTEXTS.get(window);
        if (ctx == null) {
            mc.param(window);
        } else {
            mc.paramEnum("window[" + ctx.counter + "]");
        }
        return mc;
    }

    public static MethodCall paramGlfwMonitor(MethodCall mc, long monitor) {
        PointerBuffer monitors = org.lwjgl.glfw.GLFW.glfwGetMonitors();
        for (int i = 0; i < monitors.remaining(); i++) {
            long m = monitors.get(i);
            if (m == monitor) {
                mc.paramEnum("monitor[" + i + "]");
                return mc;
            }
        }
        mc.param(monitor);
        return mc;
    }

    public static int returnValue(int val, MethodCall mc) {
        return mc.returnValue(val);
    }

    public static short returnValue(short val, MethodCall mc) {
        return mc.returnValue(val);
    }

    public static long returnValue(long val, MethodCall mc) {
        return mc.returnValue(val);
    }

    public static boolean returnValue(boolean val, MethodCall mc) {
        return mc.returnValue(val);
    }

    public static char returnValue(char val, MethodCall mc) {
        return mc.returnValue(val);
    }

    public static Object returnValue(Object val, MethodCall mc) {
        return mc.returnValue(val);
    }

    public static long returnValueGlfwWindow(long window, MethodCall mc) {
        Context ctx = CONTEXTS.get(window);
        if (ctx == null) {
            mc.returnValue(window);
        } else {
            mc.returnValueEnum("window[" + ctx.counter + "]");
        }
        return window;
    }

    public static long returnValueGlfwMonitor(long monitor, MethodCall mc) {
        PointerBuffer monitors = org.lwjgl.glfw.GLFW.glfwGetMonitors();
        for (int i = 0; i < monitors.remaining(); i++) {
            long m = monitors.get(i);
            if (m == monitor) {
                mc.returnValueEnum("monitor[" + i + "]");
                return monitor;
            }
        }
        mc.returnValue(monitor);
        return monitor;
    }

    public static void checkFunction(long addr, String glCall) {
        if (addr == 0L)
            throw new IllegalStateException(glCall + " is not supported in the current profile");
    }

    public static void checkError(String glCall) {
        Context context = CURRENT_CONTEXT.get();
        if (context != null && context.debugCallback == null && !context.inImmediateMode) {
            // No OpenGL debugging callback available, we have to resort to glGetError()
            int err = org.lwjgl.opengl.GL11.glGetError();
            if (err != 0) {
                throwISEOrLogError(glCall + " produced error: " + err + " (" + glErrorToString(err) + ")");
            }
        }
    }

    public static String glErrorToString(int err) {
        switch (err) {
        case 0x0500:
            return "GL_INVALID_ENUM";
        case 0x0501:
            return "GL_INVALID_VALUE";
        case 0x0502:
            return "GL_INVALID_OPERATION";
        case 0x0503:
            return "GL_STACK_OVERFLOW";
        case 0x0504:
            return "GL_STACK_UNDERFLOW";
        case 0x0505:
            return "GL_OUT_OF_MEMORY";
        case 0x0506:
            return "GL_INVALID_FRAMEBUFFER_OPERATION";
        case 0x0507:
            return "GL_CONTEXT_LOST";
        case 0x8031:
            return "GL_TABLE_TOO_LARGE";
        default:
            return "<UNKNOWN>";
        }
    }

    public static <T extends Throwable> T filterStackTrace(T t) {
        StackTraceElement[] elems = t.getStackTrace();
        int i = 0;
        for (; i < elems.length; i++) {
            String className = elems[i].getClassName();
            if (className == null) {
                className = "";
            }
            if (className.startsWith("org.lwjglx.") || className.startsWith("org.lwjgl.")) {
                continue;
            }
            break;
        }
        if (i > 0) {
            StackTraceElement[] newElems = new StackTraceElement[elems.length - i];
            System.arraycopy(elems, i, newElems, 0, elems.length - i);
            t.setStackTrace(newElems);
        }
        return t;
    }

    public static void throwISEOrLogError(String message) {
        throwISEOrLogError(message, true);
    }

    public static void throwISEOrLogError(String message, boolean stacktrace) {
        IllegalStateException e = filterStackTrace(new IllegalStateException(message));
        if (!Properties.NO_THROW_ON_ERROR) {
            throw e;
        } else {
            error(message, stacktrace ? e : null);
        }
    }

    public static void throwIAEOrLogError(String message) {
        throwIAEOrLogError(message, true);
    }

    public static void throwIAEOrLogError(String message, boolean stacktrace) {
        IllegalArgumentException e = filterStackTrace(new IllegalArgumentException(message));
        if (!Properties.NO_THROW_ON_ERROR) {
            throw e;
        } else {
            error(message, stacktrace ? e : null);
        }
    }

    private static void checkBufferDirect(Buffer buffer, String type) {
        if (buffer == null) {
            return;
        }
        if (!buffer.isDirect()) {
            throwIAEOrLogError("buffer is not direct. Buffers created via " + type + ".allocate() or " + type + ".wrap() are not supported. " + "Use BufferUtils.create" + type + "() instead.");
        } else if (buffer.remaining() == 0) {
            throwIAEOrLogError("buffer has no remaining elements. Did you forget to flip()/rewind() it?");
        }
    }

    public static void checkBuffer(ByteBuffer buffer) {
        checkBufferDirect(buffer, "ByteBuffer");
        checkNativeByteOrder(buffer);
    }

    public static void checkBuffer(ShortBuffer buffer) {
        checkBufferDirect(buffer, "ShortBuffer");
        checkNativeByteOrder(buffer);
    }

    public static void checkBuffer(FloatBuffer buffer) {
        checkBufferDirect(buffer, "FloatBuffer");
        checkNativeByteOrder(buffer);
    }

    public static void checkBuffer(IntBuffer buffer) {
        checkBufferDirect(buffer, "IntBuffer");
        checkNativeByteOrder(buffer);
    }

    public static void checkBuffer(DoubleBuffer buffer) {
        checkBufferDirect(buffer, "DoubleBuffer");
        checkNativeByteOrder(buffer);
    }

    public static void checkBuffer(LongBuffer buffer) {
        checkBufferDirect(buffer, "LongBuffer");
        checkNativeByteOrder(buffer);
    }

    public static String glEnumFor(Command cmd, int paramIndex, int value) {
        Param param = cmd.params.get(paramIndex);
        /* Try extension first */
        String glEnum = null;
        if (cmd.extension != null) {
            glEnum = cmd.extension.get(value);
        }
        /* Next, try the group of the GLenum */
        if (glEnum == null) {
            glEnum = param.group.get(value);
        }
        if (glEnum == null && param.group != GLmetadata._null_) {
            // try the null-group
            /*
             * Reason: Take, for example, glEnable/glDisable. Its parameter is a GLenum in the group EnableCap. However, that group only holds all enum values that were valid in like OpenGL 1.1. Over
             * the time, new enum values became valid, such as GL_TEXTURE_CUBE_MAP_SEAMLESS in OpenGL 3.2. However, those enum values were not added to the EnableCap enum group. They were instead
             * added in the overlap-free "unnamed" group, which we call the null-group. So we will look in that unnamed group, too.
             */
            glEnum = GLmetadata._null_.get(value);
        }
        return glEnum;
    }

    public static int glEnumReturn(int value, MethodCall mc, Command cmd) {
        /* Try extension first */
        String glEnum = null;
        if (cmd.extension != null) {
            glEnum = cmd.extension.get(value);
        }
        /* Next, try the group of the GLenum */
        if (glEnum == null) {
            glEnum = cmd.returnGroup.get(value);
        }
        if (glEnum == null && cmd.returnGroup != GLmetadata._null_) {
            // try the null-group
            /*
             * Reason: Take, for example, glEnable/glDisable. Its parameter is a GLenum in the group EnableCap. However, that group only holds all enum values that were valid in like OpenGL 1.1. Over
             * the time, new enum values became valid, such as GL_TEXTURE_CUBE_MAP_SEAMLESS in OpenGL 3.2. However, those enum values were not added to the EnableCap enum group. They were instead
             * added in the overlap-free "unnamed" group, which we call the null-group. So we will look in that unnamed group, too.
             */
            glEnum = GLmetadata._null_.get(value);
        }
        mc.returnValueEnum(glEnum);
        return value;
    }

    public static String decodeBitField(Command cmd, int paramIndex, int value) {
        Param param = cmd.params.get(paramIndex);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, String> e : param.group.entrySet()) {
            int bitmask = e.getKey();
            int v = value & bitmask;
            if (v != 0) {
                if (sb.length() == 0) {
                    sb.append(e.getValue());
                } else {
                    sb.append(" | ").append(e.getValue());
                }
            }
        }
        return sb.toString();
    }

    public static void delay() {
        try {
            Thread.sleep(Properties.DELAY);
        } catch (InterruptedException e) {
        }
    }

}
