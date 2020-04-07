package org.lwjglx.debug.opengl;

import static org.lwjglx.debug.Context.*;

import java.nio.*;

import org.lwjglx.debug.*;

public class GL45 {

    public static void glCreateVertexArrays(IntBuffer arrays) {
        org.lwjgl.opengl.GL45.glCreateVertexArrays(arrays);
        if (Properties.VALIDATE.enabled) {
            Context context = CURRENT_CONTEXT.get();
            int position = arrays.position();
            for (int i = 0; i < arrays.remaining(); i++) {
                VAO vao = new VAO(context.GL_MAX_VERTEX_ATTRIBS);
                CURRENT_CONTEXT.get().vaos.put(arrays.get(position + i), vao);
            }
        }
    }

    public static int glCreateVertexArrays() {
        int index = org.lwjgl.opengl.GL45.glCreateVertexArrays();
        if (Properties.VALIDATE.enabled) {
            Context context = CURRENT_CONTEXT.get();
            VAO vao = new VAO(context.GL_MAX_VERTEX_ATTRIBS);
            CURRENT_CONTEXT.get().vaos.put(index, vao);
        }
        return index;
    }

    public static void glDisableVertexArrayAttrib(int vaobj, int index) {
        org.lwjgl.opengl.GL45.glDisableVertexArrayAttrib(vaobj, index);
        if (Properties.VALIDATE.enabled) {
            Context context = CURRENT_CONTEXT.get();
            context.vaos.get(vaobj).enabledVertexArrays[index] = false;
        }
    }

    public static void glEnableVertexArrayAttrib(int vaobj, int index) {
        org.lwjgl.opengl.GL45.glEnableVertexArrayAttrib(vaobj, index);
        if (Properties.VALIDATE.enabled) {
            Context context = CURRENT_CONTEXT.get();
            context.vaos.get(vaobj).enabledVertexArrays[index] = true;
        }
    }

    public static int glCreateFramebuffers() {
        int handle = org.lwjgl.opengl.GL45.glCreateFramebuffers();
        if (Properties.VALIDATE.enabled) {
            FBO fbo = new FBO(handle);
            Context ctx = CURRENT_CONTEXT.get();
            ctx.fbos.put(handle, fbo);
        }
        return handle;
    }

    public static void glCreateFramebuffers(int[] framebuffers) {
        org.lwjgl.opengl.GL45.glCreateFramebuffers(framebuffers);
        if (Properties.VALIDATE.enabled) {
            Context ctx = CURRENT_CONTEXT.get();
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
            Context ctx = CURRENT_CONTEXT.get();
            int pos = framebuffers.position();
            for (int i = 0; i < framebuffers.remaining(); i++) {
                int handle = framebuffers.get(pos + i);
                FBO fbo = new FBO(handle);
                ctx.fbos.put(handle, fbo);
            }
        }
    }

}
