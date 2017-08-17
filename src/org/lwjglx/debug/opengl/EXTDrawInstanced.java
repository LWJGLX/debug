package org.lwjglx.debug.opengl;

import static org.lwjglx.debug.Context.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjglx.debug.Properties;
import org.lwjglx.debug.RT;

public class EXTDrawInstanced {

    public static void glDrawArraysInstancedEXT(int mode, int start, int count, int primcount) {
        checkVertexAttributes();
        org.lwjgl.opengl.EXTDrawInstanced.glDrawArraysInstancedEXT(mode, start, count, primcount);
        if (Properties.PROFILE) {
            RT.draw(count * primcount);
        }
    }

    public static void glDrawElementsInstancedEXT(int mode, int count, int type, long indices, int primcount) {
        checkVertexAttributes();
        org.lwjgl.opengl.EXTDrawInstanced.glDrawElementsInstancedEXT(mode, count, type, indices, primcount);
        if (Properties.PROFILE) {
            RT.draw(count * primcount);
        }
    }

    public static void glDrawElementsInstancedEXT(int mode, int type, ByteBuffer indices, int primcount) {
        checkVertexAttributes();
        org.lwjgl.opengl.EXTDrawInstanced.glDrawElementsInstancedEXT(mode, type, indices, primcount);
        if (Properties.PROFILE) {
            RT.draw(indices.remaining() * primcount);
        }
    }

    public static void glDrawElementsInstancedEXT(int mode, ByteBuffer indices, int primcount) {
        checkVertexAttributes();
        org.lwjgl.opengl.EXTDrawInstanced.glDrawElementsInstancedEXT(mode, indices, primcount);
        if (Properties.PROFILE) {
            RT.draw(indices.remaining() * primcount);
        }
    }

    public static void glDrawElementsInstancedEXT(int mode, ShortBuffer indices, int primcount) {
        checkVertexAttributes();
        org.lwjgl.opengl.EXTDrawInstanced.glDrawElementsInstancedEXT(mode, indices, primcount);
        if (Properties.PROFILE) {
            RT.draw(indices.remaining() * primcount);
        }
    }

    public static void glDrawElementsInstancedEXT(int mode, IntBuffer indices, int primcount) {
        checkVertexAttributes();
        org.lwjgl.opengl.EXTDrawInstanced.glDrawElementsInstancedEXT(mode, indices, primcount);
        if (Properties.PROFILE) {
            RT.draw(indices.remaining() * primcount);
        }
    }

}
