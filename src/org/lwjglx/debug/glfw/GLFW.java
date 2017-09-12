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
package org.lwjglx.debug.glfw;

import static org.lwjglx.debug.Context.*;
import static org.lwjglx.debug.Log.*;

import java.nio.ByteBuffer;
import java.util.Map;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.Platform;
import org.lwjglx.debug.Context;
import org.lwjglx.debug.MethodCall;
import org.lwjglx.debug.Properties;
import org.lwjglx.debug.RT;

public class GLFW {

    private static GLFWErrorCallback userCallback;
    private static GLFWErrorCallback errorCallback;

    public static boolean glfwInit() {
        boolean ret;
        if (Properties.VALIDATE.enabled) {
            debug("Registering GLFWErrorCallback");
            /* Set a GLFW error callback first and remember any possibly current callback to delegate to */
            errorCallback = new GLFWErrorCallback() {
                private final Map<Integer, String> ERROR_CODES = APIUtil.apiClassTokens((field, value) -> 0x10000 < value && value < 0x20000, null, org.lwjgl.glfw.GLFW.class);

                public void invoke(int error, long description) {
                    String msg = getDescription(description);
                    System.err.printf("[LWJGL] %s error\n", ERROR_CODES.get(error));
                    System.err.println("\tDescription : " + msg);
                    System.err.println("\tStacktrace  :");
                    StackTraceElement[] stack = Thread.currentThread().getStackTrace();
                    for (int i = 4; i < stack.length; i++) {
                        System.err.print("\t\t");
                        System.err.println(stack[i].toString());
                    }
                    if (userCallback != null) {
                        userCallback.invoke(error, description);
                    }
                }
            };
            userCallback = org.lwjgl.glfw.GLFW.glfwSetErrorCallback(errorCallback);
            ret = org.lwjgl.glfw.GLFW.glfwInit();
            if (!ret) {
                error("glfwInit returned false");
            }
        } else {
            ret = org.lwjgl.glfw.GLFW.glfwInit();
        }
        RT.glfwInitialized = ret;
        return ret;
    }

    public static void glfwTerminate() {
        if (Properties.VALIDATE.enabled) {
            // Reinstate any user-defined error callback
            // because the user might call glfwSetErrorCallback(null).free()
            org.lwjgl.glfw.GLFW.glfwSetErrorCallback(userCallback);
        }
        org.lwjgl.glfw.GLFW.glfwTerminate();
        if (Properties.VALIDATE.enabled) {
            debug("Freeing GLFWErrorCallback");
            if (errorCallback != null)
                errorCallback.free();
        }
        RT.glfwInitialized = false;
    }

    private static void printBoolean(MethodCall mc, int value) {
        switch (value) {
        case org.lwjgl.glfw.GLFW.GLFW_TRUE:
            mc.paramEnum("GLFW_TRUE");
            break;
        case org.lwjgl.glfw.GLFW.GLFW_FALSE:
            mc.paramEnum("GLFW_FALSE");
            break;
        default:
            mc.param(value);
            break;
        }
    }

    /*
     * Custom/manual trace method for glfwCreateStandardCursor
     */
    public static void glfwCreateStandardCursor(int shape, long ret, MethodCall mc) {
        switch (shape) {
        case org.lwjgl.glfw.GLFW.GLFW_ARROW_CURSOR:
            mc.paramEnum("GLFW_ARROW_CURSOR");
            break;
        case org.lwjgl.glfw.GLFW.GLFW_IBEAM_CURSOR:
            mc.paramEnum("GLFW_IBEAM_CURSOR");
            break;
        case org.lwjgl.glfw.GLFW.GLFW_CROSSHAIR_CURSOR:
            mc.paramEnum("GLFW_CROSSHAIR_CURSOR");
            break;
        case org.lwjgl.glfw.GLFW.GLFW_HAND_CURSOR:
            mc.paramEnum("GLFW_HAND_CURSOR");
            break;
        case org.lwjgl.glfw.GLFW.GLFW_HRESIZE_CURSOR:
            mc.paramEnum("GLFW_HRESIZE_CURSOR");
            break;
        case org.lwjgl.glfw.GLFW.GLFW_VRESIZE_CURSOR:
            mc.paramEnum("GLFW_VRESIZE_CURSOR");
            break;
        default:
            mc.param(shape);
            break;
        }
        mc.returnValue(ret);
    }

    /*
     * Custom/manual trace method for glfwWindowHint
     */
    public static void glfwWindowHint(int hint, int value, Void ret, MethodCall mc) {
        switch (hint) {
        case org.lwjgl.glfw.GLFW.GLFW_RESIZABLE:
            mc.paramEnum("GLFW_RESIZABLE");
            printBoolean(mc, value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_VISIBLE:
            mc.paramEnum("GLFW_VISIBLE");
            printBoolean(mc, value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_DECORATED:
            mc.paramEnum("GLFW_DECORATED");
            printBoolean(mc, value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_FOCUSED:
            mc.paramEnum("GLFW_FOCUSED");
            printBoolean(mc, value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_AUTO_ICONIFY:
            mc.paramEnum("GLFW_AUTO_ICONIFY");
            printBoolean(mc, value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_FLOATING:
            mc.paramEnum("GLFW_AUTO_FLOATING");
            printBoolean(mc, value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED:
            mc.paramEnum("GLFW_AUTO_MAXIMIZED");
            printBoolean(mc, value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_CENTER_CURSOR:
            mc.paramEnum("GLFW_CENTER_CURSOR");
            printBoolean(mc, value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_RED_BITS:
            mc.paramEnum("GLFW_RED_BITS");
            mc.param(value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_GREEN_BITS:
            mc.paramEnum("GLFW_GREEN_BITS");
            mc.param(value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_BLUE_BITS:
            mc.paramEnum("GLFW_BLUE_BITS");
            mc.param(value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_ALPHA_BITS:
            mc.paramEnum("GLFW_ALPHA_BITS");
            mc.param(value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_DEPTH_BITS:
            mc.paramEnum("GLFW_DEPTH_BITS");
            mc.param(value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_STENCIL_BITS:
            mc.paramEnum("GLFW_STENCIL_BITS");
            mc.param(value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_ACCUM_RED_BITS:
            mc.paramEnum("GLFW_ACCUM_RED_BITS");
            mc.param(value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_ACCUM_GREEN_BITS:
            mc.paramEnum("GLFW_ACCUM_GREEN_BITS");
            mc.param(value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_ACCUM_BLUE_BITS:
            mc.paramEnum("GLFW_ACCUM_BLUE_BITS");
            mc.param(value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_ACCUM_ALPHA_BITS:
            mc.paramEnum("GLFW_ACCUM_ALPHA_BITS");
            mc.param(value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_AUX_BUFFERS:
            mc.paramEnum("GLFW_AUX_BUFFERS");
            mc.param(value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_SAMPLES:
            mc.paramEnum("GLFW_SAMPLES");
            mc.param(value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_REFRESH_RATE:
            mc.paramEnum("GLFW_REFRESH_RATE");
            mc.param(value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_STEREO:
            mc.paramEnum("GLFW_STEREO");
            printBoolean(mc, value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_SRGB_CAPABLE:
            mc.paramEnum("GLFW_SRGB_CAPABLE");
            printBoolean(mc, value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_DOUBLEBUFFER:
            mc.paramEnum("GLFW_DOUBLEBUFFER");
            printBoolean(mc, value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_CLIENT_API:
            mc.paramEnum("GLFW_CLIENT_API");
            switch (value) {
            case org.lwjgl.glfw.GLFW.GLFW_NO_API:
                mc.paramEnum("GLFW_NO_API");
                break;
            case org.lwjgl.glfw.GLFW.GLFW_OPENGL_API:
                mc.paramEnum("GLFW_OPENGL_API");
                break;
            case org.lwjgl.glfw.GLFW.GLFW_OPENGL_ES_API:
                mc.paramEnum("GLFW_OPENGL_ES_API");
                break;
            default:
                mc.param(value);
                break;
            }
            break;
        case org.lwjgl.glfw.GLFW.GLFW_CONTEXT_CREATION_API:
            mc.paramEnum("GLFW_CONTEXT_CREATION_API");
            switch (value) {
            case org.lwjgl.glfw.GLFW.GLFW_NATIVE_CONTEXT_API:
                mc.paramEnum("GLFW_NATIVE_CONTEXT_API");
                break;
            case org.lwjgl.glfw.GLFW.GLFW_EGL_CONTEXT_API:
                mc.paramEnum("GLFW_EGL_CONTEXT_API");
                break;
            case org.lwjgl.glfw.GLFW.GLFW_OSMESA_CONTEXT_API:
                mc.paramEnum("GLFW_OSMESA_CONTEXT_API");
                break;
            default:
                mc.param(value);
                break;
            }
            break;
        case org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR:
            mc.paramEnum("GLFW_CONTEXT_VERSION_MAJOR");
            mc.param(value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR:
            mc.paramEnum("GLFW_CONTEXT_VERSION_MINOR");
            mc.param(value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_CONTEXT_ROBUSTNESS:
            mc.paramEnum("GLFW_CONTEXT_ROBUSTNESS");
            switch (value) {
            case org.lwjgl.glfw.GLFW.GLFW_NO_ROBUSTNESS:
                mc.paramEnum("GLFW_NO_ROBUSTNESS");
                break;
            case org.lwjgl.glfw.GLFW.GLFW_NO_RESET_NOTIFICATION:
                mc.paramEnum("NO_RESET_NOTIFICATION");
                break;
            case org.lwjgl.glfw.GLFW.GLFW_LOSE_CONTEXT_ON_RESET:
                mc.paramEnum("GLFW_LOSE_CONTEXT_ON_RESET");
                break;
            default:
                mc.param(value);
                break;
            }
            break;
        case org.lwjgl.glfw.GLFW.GLFW_CONTEXT_RELEASE_BEHAVIOR:
            mc.paramEnum("GLFW_CONTEXT_RELEASE_BEHAVIOR");
            switch (value) {
            case org.lwjgl.glfw.GLFW.GLFW_ANY_RELEASE_BEHAVIOR:
                mc.paramEnum("GLFW_ANY_RELEASE_BEHAVIOR");
                break;
            case org.lwjgl.glfw.GLFW.GLFW_RELEASE_BEHAVIOR_FLUSH:
                mc.paramEnum("GLFW_RELEASE_BEHAVIOR_FLUSH");
                break;
            case org.lwjgl.glfw.GLFW.GLFW_RELEASE_BEHAVIOR_NONE:
                mc.paramEnum("GLFW_RELEASE_BEHAVIOR_NONE");
                break;
            default:
                mc.param(value);
                break;
            }
            break;
        case org.lwjgl.glfw.GLFW.GLFW_CONTEXT_NO_ERROR:
            mc.paramEnum("GLFW_CONTEXT_NO_ERROR");
            printBoolean(mc, value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT:
            mc.paramEnum("GLFW_OPENGL_FORWARD_COMPAT");
            printBoolean(mc, value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_OPENGL_DEBUG_CONTEXT:
            mc.paramEnum("GLFW_OPENGL_DEBUG_CONTEXT");
            printBoolean(mc, value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE:
            mc.paramEnum("GLFW_OPENGL_PROFILE");
            switch (value) {
            case org.lwjgl.glfw.GLFW.GLFW_OPENGL_ANY_PROFILE:
                mc.paramEnum("GLFW_OPENGL_ANY_PROFILE");
                break;
            case org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE:
                mc.paramEnum("GLFW_OPENGL_CORE_PROFILE");
                break;
            case org.lwjgl.glfw.GLFW.GLFW_OPENGL_COMPAT_PROFILE:
                mc.paramEnum("GLFW_OPENGL_COMPAT_PROFILE");
                break;
            default:
                mc.param(value);
                break;
            }
            break;
        case org.lwjgl.glfw.GLFW.GLFW_COCOA_RETINA_FRAMEBUFFER:
            mc.paramEnum("GLFW_COCOA_RETINA_FRAMEBUFFER");
            printBoolean(mc, value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_COCOA_FRAME_AUTOSAVE:
            mc.paramEnum("GLFW_COCOA_FRAME_AUTOSAVE");
            printBoolean(mc, value);
            break;
        case org.lwjgl.glfw.GLFW.GLFW_COCOA_GRAPHICS_SWITCHING:
            mc.paramEnum("GLFW_COCOA_GRAPHICS_SWITCHING");
            printBoolean(mc, value);
            break;
        default:
            mc.param(hint);
            mc.param(value);
            break;
        }
    }

    public static long glfwCreateWindow(int width, int height, ByteBuffer title, long monitor, long share) {
        if (Properties.VALIDATE.enabled) {
            RT.checkGlfwMonitor(monitor);
            RT.checkGlfwWindow(share);
            org.lwjgl.glfw.GLFW.glfwWindowHint(org.lwjgl.glfw.GLFW.GLFW_OPENGL_DEBUG_CONTEXT, org.lwjgl.glfw.GLFW.GLFW_TRUE);
            if (Platform.get() == Platform.WINDOWS || Properties.STRICT.enabled) {
                Context ctx = CONTEXTS.get(share);
                if (ctx != null && ctx.currentInThread != null && ctx.currentInThread != Thread.currentThread()) {
                    RT.throwISEOrLogError("Context of share window[" + ctx.counter + "] is current in another thread [" + ctx.currentInThread + "]");
                }
            }
        }
        long window = org.lwjgl.glfw.GLFW.glfwCreateWindow(width, height, title, monitor, share);
        createWindow(window, share);
        return window;
    }

    public static long glfwCreateWindow(int width, int height, CharSequence title, long monitor, long share) {
        if (Properties.VALIDATE.enabled) {
            RT.checkGlfwMonitor(monitor);
            RT.checkGlfwWindow(share);
            org.lwjgl.glfw.GLFW.glfwWindowHint(org.lwjgl.glfw.GLFW.GLFW_OPENGL_DEBUG_CONTEXT, org.lwjgl.glfw.GLFW.GLFW_TRUE);
            if (Platform.get() == Platform.WINDOWS || Properties.STRICT.enabled) {
                Context ctx = CONTEXTS.get(share);
                if (ctx != null && ctx.currentInThread != null && ctx.currentInThread != Thread.currentThread()) {
                    RT.throwISEOrLogError("Context of share window[" + ctx.counter + "] is current in another thread [" + ctx.currentInThread + "]");
                }
            }
        }
        long window = org.lwjgl.glfw.GLFW.glfwCreateWindow(width, height, title, monitor, share);
        createWindow(window, share);
        return window;
    }

    public static void glfwMakeContextCurrent(long window) {
        if (window != 0L && Properties.VALIDATE.enabled) {
            Context ctx = CONTEXTS.get(window);
            if (ctx != null && ctx.currentInThread != null && ctx.currentInThread != Thread.currentThread()) {
                RT.throwISEOrLogError("Context of window[" + ctx.counter + "] is current in another thread [" + Thread.currentThread() + "]");
            }
        }
        org.lwjgl.glfw.GLFW.glfwMakeContextCurrent(window);
        if (window == 0L) {
            Context ctx = CURRENT_CONTEXT.get();
            CURRENT_CONTEXT.remove();
            if (ctx != null)
                ctx.currentInThread = null;
        } else {
            Context ctx = CONTEXTS.get(window);
            CURRENT_CONTEXT.set(ctx);
            if (ctx != null)
                ctx.currentInThread = Thread.currentThread();
        }
    }

    public static void glfwDestroyWindow(long window) {
        org.lwjgl.glfw.GLFW.glfwDestroyWindow(window);
        if (window == 0L)
            return;
        Context currentContext = CURRENT_CONTEXT.get();
        if (currentContext != null && currentContext.window == window) {
            CURRENT_CONTEXT.remove();
        }
        Context context = CONTEXTS.get(window);
        if (context != null) {
            context.destroy();
        }
        CONTEXTS.remove(window);
    }

    private static void createWindow(long window, long share) {
        if (window != 0L) {
            Context.create(window, share);
        } else {
            error("Failed to create GLFW window");
        }
    }

    public static void glfwSwapInterval(int interval, Void ret, MethodCall mc) {
        Context ctx = CURRENT_CONTEXT.get();
        if (ctx != null && interval != 0) { // != 0 for EXT_swap_control_tear
            long monitor = org.lwjgl.glfw.GLFW.glfwGetWindowMonitor(ctx.window);
            if (monitor == 0L) {
                monitor = org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor();
            }
            GLFWVidMode mode = org.lwjgl.glfw.GLFW.glfwGetVideoMode(monitor);
            int refreshRate = mode.refreshRate();
            mc.comment(refreshRate / Math.abs(interval) + " Hz");
        }
        mc.param(interval);
    }

    public static void glfwSwapBuffers(long window) {
        org.lwjgl.glfw.GLFW.glfwSwapBuffers(window);
        if (Properties.PROFILE.enabled) {
            RT.frame();
        }
    }

}
