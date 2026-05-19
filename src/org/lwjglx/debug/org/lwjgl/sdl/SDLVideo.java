/*
 * (C) Copyright 2026 Kai Burjack
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lwjglx.debug.org.lwjgl.sdl;

import org.lwjglx.debug.Properties;
import org.lwjglx.debug.RT;
import org.lwjglx.debug.org.lwjgl.opengl.Context;

import java.nio.ByteBuffer;
import java.util.Map;

import static org.lwjglx.debug.Log.error;
import static org.lwjglx.debug.org.lwjgl.opengl.Context.CONTEXTS;
import static org.lwjglx.debug.org.lwjgl.opengl.Context.CURRENT_CONTEXT;

public class SDLVideo {

    public static long SDL_CreateWindow(CharSequence title, int w, int h, long flags) {
        if (Properties.VALIDATE.enabled) {
            org.lwjgl.sdl.SDLVideo.SDL_GL_SetAttribute(
                org.lwjgl.sdl.SDLVideo.SDL_GL_CONTEXT_FLAGS,
                org.lwjgl.sdl.SDLVideo.SDL_GL_CONTEXT_DEBUG_FLAG
            );
        }
        long window = org.lwjgl.sdl.SDLVideo.SDL_CreateWindow(title, w, h, flags);
        if (window != 0L) {
            RT.registerSdlWindow(window);
        } else {
            error("Failed to create SDL window");
        }
        return window;
    }

    public static long SDL_CreateWindow(ByteBuffer title, int w, int h, long flags) {
        if (Properties.VALIDATE.enabled) {
            org.lwjgl.sdl.SDLVideo.SDL_GL_SetAttribute(
                org.lwjgl.sdl.SDLVideo.SDL_GL_CONTEXT_FLAGS,
                org.lwjgl.sdl.SDLVideo.SDL_GL_CONTEXT_DEBUG_FLAG
            );
        }
        long window = org.lwjgl.sdl.SDLVideo.SDL_CreateWindow(title, w, h, flags);
        if (window != 0L) {
            RT.registerSdlWindow(window);
        } else {
            error("Failed to create SDL window");
        }
        return window;
    }

    public static void SDL_DestroyWindow(long window) {
        org.lwjgl.sdl.SDLVideo.SDL_DestroyWindow(window);
        if (window == 0L)
            return;
        RT.destroySdlWindow(window);
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

    public static long SDL_GL_CreateContext(long window) {
        if (Properties.VALIDATE.enabled) {
            RT.checkSdlWindow(window);
        }
        long context = org.lwjgl.sdl.SDLVideo.SDL_GL_CreateContext(window);
        if (context != 0L) {
            long shareContextHandle = 0L;
            Context current = CURRENT_CONTEXT.get();
            if (current != null) {
                for (Map.Entry<Long, Context> entry : CONTEXTS.entrySet()) {
                    if (entry.getValue() == current) {
                        shareContextHandle = entry.getKey();
                        break;
                    }
                }
            }
            Context.create(context, shareContextHandle);
            Context ctx = CONTEXTS.get(context);
            if (ctx != null) {
                ctx.window = window;
            }
        }
        return context;
    }

    public static boolean SDL_GL_MakeCurrent(long window, long context) {
        if (Properties.VALIDATE.enabled) {
            if (window != 0L) {
                RT.checkSdlWindow(window);
            }
            if (context != 0L) {
                RT.checkSdlGlContext(context);
                Context ctx = CONTEXTS.get(context);
                if (ctx != null && ctx.currentInThread != null && ctx.currentInThread != Thread.currentThread()) {
                    RT.throwISEOrLogError("Context of context[" + ctx.counter + "] is current in another thread ["
                                    + ctx.currentInThread + "]");
                }
            }
        }
        boolean ret = org.lwjgl.sdl.SDLVideo.SDL_GL_MakeCurrent(window, context);
        if (ret) {
            if (context == 0L) {
                Context ctx = CURRENT_CONTEXT.get();
                CURRENT_CONTEXT.remove();
                if (ctx != null)
                    ctx.currentInThread = null;
            } else {
                Context ctx = CONTEXTS.get(context);
                if (ctx != null) {
                    ctx.window = window;
                    CURRENT_CONTEXT.set(ctx);
                    ctx.currentInThread = Thread.currentThread();
                }
            }
        }
        return ret;
    }

    public static boolean SDL_GL_DestroyContext(long context) {
        if (context == 0L) {
            return org.lwjgl.sdl.SDLVideo.SDL_GL_DestroyContext(context);
        }
        Context currentContext = CURRENT_CONTEXT.get();
        Context ctx = CONTEXTS.get(context);
        boolean ret = org.lwjgl.sdl.SDLVideo.SDL_GL_DestroyContext(context);
        if (ret) {
            if (currentContext != null && currentContext == ctx) {
                CURRENT_CONTEXT.remove();
            }
            if (ctx != null) {
                ctx.destroy();
            }
            CONTEXTS.remove(context);
        }
        return ret;
    }

    public static boolean SDL_GL_SwapWindow(long window) {
        if (Properties.VALIDATE.enabled) {
            RT.checkSdlWindow(window);
        }
        boolean ret = org.lwjgl.sdl.SDLVideo.SDL_GL_SwapWindow(window);
        if (ret) {
            RT.frame();
        }
        return ret;
    }
}
