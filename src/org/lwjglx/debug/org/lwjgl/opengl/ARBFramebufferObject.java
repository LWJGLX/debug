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
package org.lwjglx.debug.org.lwjgl.opengl;

import static org.lwjglx.debug.RT.*;

import java.nio.IntBuffer;

import org.lwjglx.debug.Properties;
import org.lwjglx.debug.org.lwjgl.opengl.Context.FBO;

public class ARBFramebufferObject {

    public static void glGenFramebuffers(IntBuffer framebuffers) {
        org.lwjgl.opengl.ARBFramebufferObject.glGenFramebuffers(framebuffers);
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            int pos = framebuffers.position();
            for (int i = 0; i < framebuffers.remaining(); i++) {
                int handle = framebuffers.get(pos + i);
                FBO fbo = new FBO(handle);
                ctx.fbos.put(handle, fbo);
            }
        }
    }

    public static int glGenFramebuffers() {
        int handle = org.lwjgl.opengl.ARBFramebufferObject.glGenFramebuffers();
        if (Properties.VALIDATE.enabled) {
            FBO fbo = new FBO(handle);
            Context ctx = Context.currentContext();
            ctx.fbos.put(handle, fbo);
        }
        return handle;
    }

    public static void glGenFramebuffers(int[] framebuffers) {
        org.lwjgl.opengl.ARBFramebufferObject.glGenFramebuffers(framebuffers);
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            for (int i = 0; i < framebuffers.length; i++) {
                int handle = framebuffers[i];
                FBO fbo = new FBO(handle);
                ctx.fbos.put(handle, fbo);
            }
        }
    }

    public static void glBindFramebuffer(int target, int framebuffer) {
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            FBO fbo = ctx.fbos.get(framebuffer);
            if (fbo == null && ctx.shareGroup != null) {
                for (Context c : ctx.shareGroup.contexts) {
                    if (c.fbos.containsKey(framebuffer)) {
                        throwISEOrLogError("Trying to bind unknown FBO [" + framebuffer + "] from shared context [" + c.counter + "]");
                    }
                }
            }
            ctx.currentFbo = fbo;
        }
        org.lwjgl.opengl.ARBFramebufferObject.glBindFramebuffer(target, framebuffer);
    }

    public static void glDeleteFramebuffers(IntBuffer framebuffers) {
        org.lwjgl.opengl.ARBFramebufferObject.glDeleteFramebuffers(framebuffers);
        if (Properties.VALIDATE.enabled) {
            Context context = Context.currentContext();
            int pos = framebuffers.position();
            for (int i = 0; i < framebuffers.remaining(); i++) {
                int framebuffer = framebuffers.get(pos + i);
                if (framebuffer == 0)
                    continue;
                FBO fbo = context.fbos.get(framebuffer);
                if (fbo != null && fbo == context.currentFbo) {
                    context.currentFbo = context.defaultFbo;
                }
                context.fbos.remove(framebuffer);
            }
        }
    }

    public static void glDeleteFramebuffers(int framebuffer) {
        org.lwjgl.opengl.ARBFramebufferObject.glDeleteFramebuffers(framebuffer);
        if (Properties.VALIDATE.enabled) {
            if (framebuffer == 0)
                return;
            Context context = Context.currentContext();
            FBO fbo = context.fbos.get(framebuffer);
            if (fbo != null && fbo == context.currentFbo) {
                context.currentFbo = context.defaultFbo;
            }
            context.fbos.remove(framebuffer);
        }
    }

    public static void glDeleteFramebuffers(int[] framebuffers) {
        org.lwjgl.opengl.ARBFramebufferObject.glDeleteFramebuffers(framebuffers);
        if (Properties.VALIDATE.enabled) {
            Context context = Context.currentContext();
            for (int i = 0; i < framebuffers.length; i++) {
                int framebuffer = framebuffers[i];
                if (framebuffer == 0)
                    continue;
                FBO fbo = context.fbos.get(framebuffer);
                if (fbo != null && fbo == context.currentFbo) {
                    context.currentFbo = context.defaultFbo;
                }
                context.fbos.remove(framebuffer);
            }
        }
    }

}
