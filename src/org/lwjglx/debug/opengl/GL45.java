package org.lwjglx.debug.opengl;

import static org.lwjglx.debug.Context.*;

import java.nio.*;

import org.lwjglx.debug.*;

public class GL45 {

    public static void glCreateVertexArrays(IntBuffer arrays) {
        org.lwjgl.opengl.GL45.glCreateVertexArrays(arrays);
        if (Properties.VALIDATE.enabled) {
            Context context = Context.currentContext();
            int position = arrays.position();
            for (int i = 0; i < arrays.remaining(); i++) {
                VAO vao = new VAO(context.GL_MAX_VERTEX_ATTRIBS);
                context.vaos.put(arrays.get(position + i), vao);
            }
        }
    }

    public static int glCreateVertexArrays() {
        int index = org.lwjgl.opengl.GL45.glCreateVertexArrays();
        if (Properties.VALIDATE.enabled) {
            Context context = Context.currentContext();
            VAO vao = new VAO(context.GL_MAX_VERTEX_ATTRIBS);
            context.vaos.put(index, vao);
        }
        return index;
    }

    public static void glDisableVertexArrayAttrib(int vaobj, int index) {
        org.lwjgl.opengl.GL45.glDisableVertexArrayAttrib(vaobj, index);
        if (Properties.VALIDATE.enabled) {
            Context context = Context.currentContext();
            context.vaos.get(vaobj).enabledVertexArrays[index] = false;
        }
    }

    public static void glEnableVertexArrayAttrib(int vaobj, int index) {
        org.lwjgl.opengl.GL45.glEnableVertexArrayAttrib(vaobj, index);
        if (Properties.VALIDATE.enabled) {
            Context context = Context.currentContext();
            context.vaos.get(vaobj).enabledVertexArrays[index] = true;
        }
    }

    public static int glCreateFramebuffers() {
        int handle = org.lwjgl.opengl.GL45.glCreateFramebuffers();
        if (Properties.VALIDATE.enabled) {
            FBO fbo = new FBO(handle);
            Context ctx = Context.currentContext();
            ctx.fbos.put(handle, fbo);
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
            }
        }
    }

}
