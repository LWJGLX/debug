package org.lwjglx.debug.org.lwjgl.opengl;

import static org.lwjglx.debug.org.lwjgl.opengl.Context.*;

import java.nio.*;

import org.lwjglx.debug.*;

public class GL45 {

    public static void glCreateVertexArrays(IntBuffer arrays) {
        org.lwjgl.opengl.GL45.glCreateVertexArrays(arrays);
        if (Properties.VALIDATE.enabled) {
            Context context = Context.currentContext();
            int position = arrays.position();
            for (int i = 0; i < arrays.remaining(); i++) {
                int handle = arrays.get(position + i);
                VAO vao = new VAO(context.GL_MAX_VERTEX_ATTRIBS);
                context.vaos.put(handle, vao);
                context.vaoTracker.create(handle, vao);
            }
        }
    }

    public static int glCreateVertexArrays() {
        int index = org.lwjgl.opengl.GL45.glCreateVertexArrays();
        if (Properties.VALIDATE.enabled) {
            Context context = Context.currentContext();
            VAO vao = new VAO(context.GL_MAX_VERTEX_ATTRIBS);
            context.vaos.put(index, vao);
            context.vaoTracker.create(index, vao);
        }
        return index;
    }

    public static void glDisableVertexArrayAttrib(int vaobj, int index) {
        if (Properties.VALIDATE.enabled) {
            Context context = Context.currentContext();
            context.vaoTracker.checkAlive(vaobj, "glDisableVertexArrayAttrib");
            VAO vao = context.vaos.get(vaobj);
            if (vao != null && index > -1 && index < vao.enabledVertexArrays.length) {
                vao.enabledVertexArrays[index] = false;
            }
        }
        org.lwjgl.opengl.GL45.glDisableVertexArrayAttrib(vaobj, index);
    }

    public static void glEnableVertexArrayAttrib(int vaobj, int index) {
        if (Properties.VALIDATE.enabled) {
            Context context = Context.currentContext();
            context.vaoTracker.checkAlive(vaobj, "glEnableVertexArrayAttrib");
            VAO vao = context.vaos.get(vaobj);
            if (vao != null && index > -1 && index < vao.enabledVertexArrays.length) {
                vao.enabledVertexArrays[index] = true;
            }
        }
        org.lwjgl.opengl.GL45.glEnableVertexArrayAttrib(vaobj, index);
    }

    public static int glCreateFramebuffers() {
        int handle = org.lwjgl.opengl.GL45.glCreateFramebuffers();
        if (Properties.VALIDATE.enabled) {
            FBO fbo = new FBO(handle);
            Context ctx = Context.currentContext();
            ctx.fbos.put(handle, fbo);
            ctx.fboTracker.create(handle, fbo);
        }
        return handle;
    }

    public static void glCreateFramebuffers(int[] framebuffers) {
        org.lwjgl.opengl.GL45.glCreateFramebuffers(framebuffers);
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

    public static void glCreateFramebuffers(IntBuffer framebuffers) {
        org.lwjgl.opengl.GL45.glCreateFramebuffers(framebuffers);
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

    public static int glCreateBuffers() {
        int handle = org.lwjgl.opengl.GL45.glCreateBuffers();
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            ctx.shareGroup.bufferObjects.create(handle, new BufferObject());
        }
        return handle;
    }

    public static void glCreateBuffers(IntBuffer buffers) {
        org.lwjgl.opengl.GL45.glCreateBuffers(buffers);
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            int pos = buffers.position();
            for (int i = 0; i < buffers.remaining(); i++) {
                ctx.shareGroup.bufferObjects.create(buffers.get(pos + i), new BufferObject());
            }
        }
    }

    public static void glCreateBuffers(int[] buffers) {
        org.lwjgl.opengl.GL45.glCreateBuffers(buffers);
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            for (int i = 0; i < buffers.length; i++) {
                ctx.shareGroup.bufferObjects.create(buffers[i], new BufferObject());
            }
        }
    }

    public static int glCreateTextures(int target) {
        int handle = org.lwjgl.opengl.GL45.glCreateTextures(target);
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            ctx.shareGroup.textureObjects.create(handle, new TextureObject());
        }
        return handle;
    }

    public static void glCreateTextures(int target, IntBuffer textures) {
        org.lwjgl.opengl.GL45.glCreateTextures(target, textures);
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            int pos = textures.position();
            for (int i = 0; i < textures.remaining(); i++) {
                ctx.shareGroup.textureObjects.create(textures.get(pos + i), new TextureObject());
            }
        }
    }

    public static void glCreateTextures(int target, int[] textures) {
        org.lwjgl.opengl.GL45.glCreateTextures(target, textures);
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            for (int i = 0; i < textures.length; i++) {
                ctx.shareGroup.textureObjects.create(textures[i], new TextureObject());
            }
        }
    }

    public static int glCreateProgramPipelines() {
        int handle = org.lwjgl.opengl.GL45.glCreateProgramPipelines();
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            ProgramPipeline pp = new ProgramPipeline();
            ctx.programPipelines.put(handle, pp);
            ctx.pipelineTracker.create(handle, pp);
        }
        return handle;
    }

    public static void glCreateProgramPipelines(IntBuffer pipelines) {
        org.lwjgl.opengl.GL45.glCreateProgramPipelines(pipelines);
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            int pos = pipelines.position();
            for (int i = 0; i < pipelines.remaining(); i++) {
                int handle = pipelines.get(pos + i);
                ProgramPipeline pp = new ProgramPipeline();
                ctx.programPipelines.put(handle, pp);
                ctx.pipelineTracker.create(handle, pp);
            }
        }
    }

    public static void glCreateProgramPipelines(int[] pipelines) {
        org.lwjgl.opengl.GL45.glCreateProgramPipelines(pipelines);
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            for (int i = 0; i < pipelines.length; i++) {
                int handle = pipelines[i];
                ProgramPipeline pp = new ProgramPipeline();
                ctx.programPipelines.put(handle, pp);
                ctx.pipelineTracker.create(handle, pp);
            }
        }
    }

    public static int glCreateRenderbuffers() {
        int handle = org.lwjgl.opengl.GL45.glCreateRenderbuffers();
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            ctx.shareGroup.renderbufferObjects.create(handle, new Context.RenderbufferObject());
        }
        return handle;
    }

    public static void glCreateRenderbuffers(IntBuffer renderbuffers) {
        org.lwjgl.opengl.GL45.glCreateRenderbuffers(renderbuffers);
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            int pos = renderbuffers.position();
            for (int i = 0; i < renderbuffers.remaining(); i++) {
                int handle = renderbuffers.get(pos + i);
                ctx.shareGroup.renderbufferObjects.create(handle, new Context.RenderbufferObject());
            }
        }
    }

    public static void glCreateRenderbuffers(int[] renderbuffers) {
        org.lwjgl.opengl.GL45.glCreateRenderbuffers(renderbuffers);
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            for (int i = 0; i < renderbuffers.length; i++) {
                int handle = renderbuffers[i];
                ctx.shareGroup.renderbufferObjects.create(handle, new Context.RenderbufferObject());
            }
        }
    }

    public static void glNamedFramebufferTexture(int framebuffer, int attachment, int texture, int level) {
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            ctx.fboTracker.checkAlive(framebuffer, "glNamedFramebufferTexture");
            if (texture != 0) {
                ctx.shareGroup.textureObjects.checkAlive(texture, "glNamedFramebufferTexture");
            }
            Context.FBO fbo = ctx.fbos.get(framebuffer);
            if (fbo != null) {
                fbo.attachTexture(attachment, texture);
            }
        }
        org.lwjgl.opengl.GL45.glNamedFramebufferTexture(framebuffer, attachment, texture, level);
    }

    public static void glNamedFramebufferRenderbuffer(int framebuffer, int attachment, int renderbuffertarget, int renderbuffer) {
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            ctx.fboTracker.checkAlive(framebuffer, "glNamedFramebufferRenderbuffer");
            if (renderbuffer != 0) {
                ctx.shareGroup.renderbufferObjects.checkAlive(renderbuffer, "glNamedFramebufferRenderbuffer");
            }
            Context.FBO fbo = ctx.fbos.get(framebuffer);
            if (fbo != null) {
                fbo.attachRenderbuffer(attachment, renderbuffer);
            }
        }
        org.lwjgl.opengl.GL45.glNamedFramebufferRenderbuffer(framebuffer, attachment, renderbuffertarget, renderbuffer);
    }

    public static void glNamedFramebufferTextureLayer(int framebuffer, int attachment, int texture, int level, int layer) {
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            ctx.fboTracker.checkAlive(framebuffer, "glNamedFramebufferTextureLayer");
            if (texture != 0) {
                ctx.shareGroup.textureObjects.checkAlive(texture, "glNamedFramebufferTextureLayer");
            }
            Context.FBO fbo = ctx.fbos.get(framebuffer);
            if (fbo != null) {
                fbo.attachTexture(attachment, texture);
            }
        }
        org.lwjgl.opengl.GL45.glNamedFramebufferTextureLayer(framebuffer, attachment, texture, level, layer);
    }

    public static void glTextureBuffer(int texture, int internalformat, int buffer) {
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            if (texture != 0) {
                ctx.shareGroup.textureObjects.checkAlive(texture, "glTextureBuffer");
            }
            if (buffer != 0) {
                ctx.shareGroup.bufferObjects.checkAlive(buffer, "glTextureBuffer");
            }
        }
        org.lwjgl.opengl.GL45.glTextureBuffer(texture, internalformat, buffer);
    }

    public static void glTextureBufferRange(int texture, int internalformat, int buffer, long offset, long size) {
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            if (texture != 0) {
                ctx.shareGroup.textureObjects.checkAlive(texture, "glTextureBufferRange");
            }
            if (buffer != 0) {
                ctx.shareGroup.bufferObjects.checkAlive(buffer, "glTextureBufferRange");
            }
        }
        org.lwjgl.opengl.GL45.glTextureBufferRange(texture, internalformat, buffer, offset, size);
    }

}
