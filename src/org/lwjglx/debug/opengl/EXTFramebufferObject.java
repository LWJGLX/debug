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
package org.lwjglx.debug.opengl;

import static org.lwjglx.debug.Context.*;
import static org.lwjglx.debug.Log.*;
import static org.lwjglx.debug.RT.*;

import java.nio.IntBuffer;

import org.lwjglx.debug.Context;
import org.lwjglx.debug.Properties;
import org.lwjglx.debug.Context.FBO;

public class EXTFramebufferObject {

    public static void glGenerateMipmapEXT(int target) {
        org.lwjgl.opengl.EXTFramebufferObject.glGenerateMipmapEXT(target);
        if (Properties.PROFILE.enabled) {
            generateMipmap(target);
        }
    }

    public static void glGenFramebuffersEXT(IntBuffer framebuffers) {
        org.lwjgl.opengl.EXTFramebufferObject.glGenFramebuffersEXT(framebuffers);
        if (Properties.VALIDATE.enabled) {
            Context ctx = CURRENT_CONTEXT.get();
            int pos = framebuffers.position();
            for (int i = 0; i < framebuffers.remaining(); i++) {
                int handle = framebuffers.get(pos + i);
                FBO fbo = new FBO();
                ctx.fbos.put(handle, fbo);
            }
        }
    }

    public static int glGenFramebuffersEXT() {
        int handle = org.lwjgl.opengl.EXTFramebufferObject.glGenFramebuffersEXT();
        if (Properties.VALIDATE.enabled) {
            FBO fbo = new FBO();
            Context ctx = CURRENT_CONTEXT.get();
            ctx.fbos.put(handle, fbo);
        }
        return handle;
    }

    public static void glGenFramebuffersEXT(int[] framebuffers) {
        org.lwjgl.opengl.EXTFramebufferObject.glGenFramebuffersEXT(framebuffers);
        if (Properties.VALIDATE.enabled) {
            Context ctx = CURRENT_CONTEXT.get();
            for (int i = 0; i < framebuffers.length; i++) {
                int handle = framebuffers[i];
                FBO fbo = new FBO();
                ctx.fbos.put(handle, fbo);
            }
        }
    }

    public static void glBindFramebufferEXT(int target, int framebuffer) {
        if (Properties.VALIDATE.enabled) {
            Context ctx = CURRENT_CONTEXT.get();
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
        org.lwjgl.opengl.EXTFramebufferObject.glBindFramebufferEXT(target, framebuffer);
        if (Properties.VALIDATE.enabled) {
            /* Check framebuffer status */
            int status = org.lwjgl.opengl.EXTFramebufferObject.glCheckFramebufferStatusEXT(target);
            if (status != org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT) {
                error("Framebuffer [" + framebuffer + "] is not complete: " + status);
            }
        }
    }

    public static void glDeleteFramebuffersEXT(IntBuffer framebuffers) {
        org.lwjgl.opengl.EXTFramebufferObject.glDeleteFramebuffersEXT(framebuffers);
        if (Properties.VALIDATE.enabled) {
            Context context = CURRENT_CONTEXT.get();
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

    public static void glDeleteFramebuffersEXT(int framebuffer) {
        org.lwjgl.opengl.EXTFramebufferObject.glDeleteFramebuffersEXT(framebuffer);
        if (Properties.VALIDATE.enabled) {
            if (framebuffer == 0)
                return;
            Context context = CURRENT_CONTEXT.get();
            FBO fbo = context.fbos.get(framebuffer);
            if (fbo != null && fbo == context.currentFbo) {
                context.currentFbo = context.defaultFbo;
            }
            context.fbos.remove(framebuffer);
        }
    }

    public static void glDeleteFramebuffersEXT(int[] framebuffers) {
        org.lwjgl.opengl.EXTFramebufferObject.glDeleteFramebuffersEXT(framebuffers);
        if (Properties.VALIDATE.enabled) {
            Context context = CURRENT_CONTEXT.get();
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
