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
                ctx.fboTracker.create(handle, fbo);
            }
        }
    }

    public static int glGenFramebuffers() {
        int handle = org.lwjgl.opengl.ARBFramebufferObject.glGenFramebuffers();
        if (Properties.VALIDATE.enabled) {
            FBO fbo = new FBO(handle);
            Context ctx = Context.currentContext();
            ctx.fbos.put(handle, fbo);
            ctx.fboTracker.create(handle, fbo);
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
                ctx.fboTracker.create(handle, fbo);
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
            if (framebuffer != 0) {
                ctx.fboTracker.checkAlive(framebuffer, "glBindFramebuffer");
            }
            if (target == org.lwjgl.opengl.ARBFramebufferObject.GL_DRAW_FRAMEBUFFER) {
                ctx.currentDrawFbo = fbo;
                ctx.currentFbo = fbo;
            } else if (target == org.lwjgl.opengl.ARBFramebufferObject.GL_READ_FRAMEBUFFER) {
                ctx.currentReadFbo = fbo;
            } else {
                ctx.currentDrawFbo = fbo;
                ctx.currentReadFbo = fbo;
                ctx.currentFbo = fbo;
            }
        }
        org.lwjgl.opengl.ARBFramebufferObject.glBindFramebuffer(target, framebuffer);
    }

    public static void glDeleteFramebuffers(IntBuffer framebuffers) {
        org.lwjgl.opengl.ARBFramebufferObject.glDeleteFramebuffers(framebuffers);
        if (Properties.VALIDATE.enabled) {
            Context.deleteFramebuffers(framebuffers);
        }
    }

    public static void glDeleteFramebuffers(int framebuffer) {
        org.lwjgl.opengl.ARBFramebufferObject.glDeleteFramebuffers(framebuffer);
        if (Properties.VALIDATE.enabled) {
            Context.deleteFramebuffer(framebuffer);
        }
    }

    public static void glDeleteFramebuffers(int[] framebuffers) {
        org.lwjgl.opengl.ARBFramebufferObject.glDeleteFramebuffers(framebuffers);
        if (Properties.VALIDATE.enabled) {
            Context.deleteFramebuffers(framebuffers);
        }
    }

    public static void glGenRenderbuffers(IntBuffer renderbuffers) {
        org.lwjgl.opengl.ARBFramebufferObject.glGenRenderbuffers(renderbuffers);
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            int pos = renderbuffers.position();
            for (int i = 0; i < renderbuffers.remaining(); i++) {
                int handle = renderbuffers.get(pos + i);
                ctx.shareGroup.renderbufferObjects.create(handle, new Context.RenderbufferObject());
            }
        }
    }

    public static int glGenRenderbuffers() {
        int handle = org.lwjgl.opengl.ARBFramebufferObject.glGenRenderbuffers();
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            ctx.shareGroup.renderbufferObjects.create(handle, new Context.RenderbufferObject());
        }
        return handle;
    }

    public static void glGenRenderbuffers(int[] renderbuffers) {
        org.lwjgl.opengl.ARBFramebufferObject.glGenRenderbuffers(renderbuffers);
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            for (int i = 0; i < renderbuffers.length; i++) {
                int handle = renderbuffers[i];
                ctx.shareGroup.renderbufferObjects.create(handle, new Context.RenderbufferObject());
            }
        }
    }

    public static void glBindRenderbuffer(int target, int renderbuffer) {
        if (Properties.VALIDATE.enabled && renderbuffer != 0) {
            Context ctx = Context.currentContext();
            ctx.shareGroup.renderbufferObjects.checkAlive(renderbuffer, "glBindRenderbuffer");
        }
        org.lwjgl.opengl.ARBFramebufferObject.glBindRenderbuffer(target, renderbuffer);
    }

    public static void glDeleteRenderbuffers(IntBuffer renderbuffers) {
        org.lwjgl.opengl.ARBFramebufferObject.glDeleteRenderbuffers(renderbuffers);
        if (Properties.VALIDATE.enabled) {
            Context.deleteRenderbuffers(renderbuffers);
        }
    }

    public static void glDeleteRenderbuffers(int renderbuffer) {
        org.lwjgl.opengl.ARBFramebufferObject.glDeleteRenderbuffers(renderbuffer);
        if (Properties.VALIDATE.enabled) {
            Context.deleteRenderbuffer(renderbuffer);
        }
    }

    public static void glDeleteRenderbuffers(int[] renderbuffers) {
        org.lwjgl.opengl.ARBFramebufferObject.glDeleteRenderbuffers(renderbuffers);
        if (Properties.VALIDATE.enabled) {
            Context.deleteRenderbuffers(renderbuffers);
        }
    }

    public static void glFramebufferTexture1D(int target, int attachment, int textarget, int texture, int level) {
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            if (texture != 0) {
                ctx.shareGroup.textureObjects.checkAlive(texture, "glFramebufferTexture1D");
            }
            FBO fbo = (target == org.lwjgl.opengl.ARBFramebufferObject.GL_READ_FRAMEBUFFER) ? ctx.currentReadFbo : ctx.currentDrawFbo;
            if (fbo != null) {
                fbo.attachTexture(attachment, texture);
            }
        }
        org.lwjgl.opengl.ARBFramebufferObject.glFramebufferTexture1D(target, attachment, textarget, texture, level);
    }

    public static void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            if (texture != 0) {
                ctx.shareGroup.textureObjects.checkAlive(texture, "glFramebufferTexture2D");
            }
            FBO fbo = (target == org.lwjgl.opengl.ARBFramebufferObject.GL_READ_FRAMEBUFFER) ? ctx.currentReadFbo : ctx.currentDrawFbo;
            if (fbo != null) {
                fbo.attachTexture(attachment, texture);
            }
        }
        org.lwjgl.opengl.ARBFramebufferObject.glFramebufferTexture2D(target, attachment, textarget, texture, level);
    }

    public static void glFramebufferTexture3D(int target, int attachment, int textarget, int texture, int level, int zoffset) {
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            if (texture != 0) {
                ctx.shareGroup.textureObjects.checkAlive(texture, "glFramebufferTexture3D");
            }
            FBO fbo = (target == org.lwjgl.opengl.ARBFramebufferObject.GL_READ_FRAMEBUFFER) ? ctx.currentReadFbo : ctx.currentDrawFbo;
            if (fbo != null) {
                fbo.attachTexture(attachment, texture);
            }
        }
        org.lwjgl.opengl.ARBFramebufferObject.glFramebufferTexture3D(target, attachment, textarget, texture, level, zoffset);
    }

    public static void glFramebufferTextureLayer(int target, int attachment, int texture, int level, int layer) {
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            if (texture != 0) {
                ctx.shareGroup.textureObjects.checkAlive(texture, "glFramebufferTextureLayer");
            }
            FBO fbo = (target == org.lwjgl.opengl.ARBFramebufferObject.GL_READ_FRAMEBUFFER) ? ctx.currentReadFbo : ctx.currentDrawFbo;
            if (fbo != null) {
                fbo.attachTexture(attachment, texture);
            }
        }
        org.lwjgl.opengl.ARBFramebufferObject.glFramebufferTextureLayer(target, attachment, texture, level, layer);
    }

    public static void glFramebufferRenderbuffer(int target, int attachment, int renderbuffertarget, int renderbuffer) {
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            if (renderbuffer != 0) {
                ctx.shareGroup.renderbufferObjects.checkAlive(renderbuffer, "glFramebufferRenderbuffer");
            }
            FBO fbo = (target == org.lwjgl.opengl.ARBFramebufferObject.GL_READ_FRAMEBUFFER) ? ctx.currentReadFbo : ctx.currentDrawFbo;
            if (fbo != null) {
                fbo.attachRenderbuffer(attachment, renderbuffer);
            }
        }
        org.lwjgl.opengl.ARBFramebufferObject.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
    }

    public static void glBlitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
        if (Properties.VALIDATE.enabled) {
            Context context = Context.currentContext();
            if (context.currentReadFbo != null) {
                for (int tex : context.currentReadFbo.attachedTextures.values()) {
                    context.shareGroup.textureObjects.checkAlive(tex, "glBlitFramebuffer");
                }
                for (int rb : context.currentReadFbo.attachedRenderbuffers.values()) {
                    context.shareGroup.renderbufferObjects.checkAlive(rb, "glBlitFramebuffer");
                }
            }
            if (context.currentDrawFbo != null) {
                for (int tex : context.currentDrawFbo.attachedTextures.values()) {
                    context.shareGroup.textureObjects.checkAlive(tex, "glBlitFramebuffer");
                }
                for (int rb : context.currentDrawFbo.attachedRenderbuffers.values()) {
                    context.shareGroup.renderbufferObjects.checkAlive(rb, "glBlitFramebuffer");
                }
            }
        }
        org.lwjgl.opengl.ARBFramebufferObject.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
    }

    public static int glCheckFramebufferStatus(int target) {
        if (Properties.VALIDATE.enabled) {
            Context context = Context.currentContext();
            FBO fbo = (target == org.lwjgl.opengl.ARBFramebufferObject.GL_READ_FRAMEBUFFER) ? context.currentReadFbo : context.currentDrawFbo;
            if (fbo != null) {
                for (int tex : fbo.attachedTextures.values()) {
                    context.shareGroup.textureObjects.checkAlive(tex, "glCheckFramebufferStatus");
                }
                for (int rb : fbo.attachedRenderbuffers.values()) {
                    context.shareGroup.renderbufferObjects.checkAlive(rb, "glCheckFramebufferStatus");
                }
            }
        }
        return org.lwjgl.opengl.ARBFramebufferObject.glCheckFramebufferStatus(target);
    }

}
