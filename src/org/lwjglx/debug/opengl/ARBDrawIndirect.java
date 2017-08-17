package org.lwjglx.debug.opengl;

import static org.lwjglx.debug.Context.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.NativeType;

public class ARBDrawIndirect {

    public static void glDrawArraysIndirect(@NativeType("GLenum") int mode, @NativeType("const void *") ByteBuffer indirect) {
        checkVertexAttributes();
        org.lwjgl.opengl.ARBDrawIndirect.glDrawArraysIndirect(mode, indirect);
    }

    public static void glDrawArraysIndirect(@NativeType("GLenum") int mode, @NativeType("const void *") long indirect) {
        checkVertexAttributes();
        org.lwjgl.opengl.ARBDrawIndirect.glDrawArraysIndirect(mode, indirect);
    }

    public static void glDrawArraysIndirect(@NativeType("GLenum") int mode, @NativeType("const void *") IntBuffer indirect) {
        checkVertexAttributes();
        org.lwjgl.opengl.ARBDrawIndirect.glDrawArraysIndirect(mode, indirect);
    }

    public static void glDrawElementsIndirect(@NativeType("GLenum") int mode, @NativeType("GLenum") int type, @NativeType("const void *") ByteBuffer indirect) {
        checkVertexAttributes();
        org.lwjgl.opengl.ARBDrawIndirect.glDrawElementsIndirect(mode, type, indirect);
    }

    public static void glDrawElementsIndirect(@NativeType("GLenum") int mode, @NativeType("GLenum") int type, @NativeType("const void *") long indirect) {
        checkVertexAttributes();
        org.lwjgl.opengl.ARBDrawIndirect.glDrawElementsIndirect(mode, type, indirect);
    }

    public static void glDrawElementsIndirect(@NativeType("GLenum") int mode, @NativeType("GLenum") int type, @NativeType("const void *") IntBuffer indirect) {
        checkVertexAttributes();
        org.lwjgl.opengl.ARBDrawIndirect.glDrawElementsIndirect(mode, type, indirect);
    }

    public static void glDrawArraysIndirect(@NativeType("GLenum") int mode, @NativeType("const void *") int[] indirect) {
        checkVertexAttributes();
        org.lwjgl.opengl.ARBDrawIndirect.glDrawArraysIndirect(mode, indirect);
    }

    public static void glDrawElementsIndirect(@NativeType("GLenum") int mode, @NativeType("GLenum") int type, @NativeType("const void *") int[] indirect) {
        checkVertexAttributes();
        org.lwjgl.opengl.ARBDrawIndirect.glDrawElementsIndirect(mode, type, indirect);
    }

}
