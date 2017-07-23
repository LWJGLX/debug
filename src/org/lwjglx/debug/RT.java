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

import static org.lwjglx.debug.Context.*;
import static org.lwjglx.debug.Log.*;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
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
        if (context != null && context.debugCallback == null) {
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
        if (buffer != null && buffer.order() != ByteOrder.nativeOrder()) {
            throwIAEOrLogError("buffer has wrong ByteOrder. Call buffer.order(ByteOrder.nativeOrder()) to fix it.");
        }
    }

    public static void checkBuffer(ShortBuffer buffer) {
        checkBufferDirect(buffer, "ShortBuffer");
        if (buffer != null && buffer.order() != ByteOrder.nativeOrder()) {
            throwIAEOrLogError("buffer has wrong ByteOrder. Call buffer.order(ByteOrder.nativeOrder()) to fix it.");
        }
    }

    public static void checkBuffer(FloatBuffer buffer) {
        checkBufferDirect(buffer, "FloatBuffer");
        if (buffer != null && buffer.order() != ByteOrder.nativeOrder()) {
            throwIAEOrLogError("buffer has wrong ByteOrder. Call buffer.order(ByteOrder.nativeOrder()) to fix it.");
        }
    }

    public static void checkBuffer(IntBuffer buffer) {
        checkBufferDirect(buffer, "IntBuffer");
        if (buffer != null && buffer.order() != ByteOrder.nativeOrder()) {
            throwIAEOrLogError("buffer has wrong ByteOrder. Call buffer.order(ByteOrder.nativeOrder()) to fix it.");
        }
    }

    public static void checkBuffer(DoubleBuffer buffer) {
        checkBufferDirect(buffer, "DoubleBuffer");
        if (buffer != null && buffer.order() != ByteOrder.nativeOrder()) {
            throwIAEOrLogError("buffer has wrong ByteOrder. Call buffer.order(ByteOrder.nativeOrder()) to fix it.");
        }
    }

    public static void checkBuffer(LongBuffer buffer) {
        checkBufferDirect(buffer, "LongBuffer");
        if (buffer.order() != ByteOrder.nativeOrder()) {
            throwIAEOrLogError("buffer has wrong ByteOrder. Call buffer.order(ByteOrder.nativeOrder()) to fix it.");
        }
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
