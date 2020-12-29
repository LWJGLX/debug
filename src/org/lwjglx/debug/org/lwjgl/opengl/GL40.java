package org.lwjglx.debug.org.lwjgl.opengl;

import static org.lwjglx.debug.org.lwjgl.opengl.Context.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.NativeType;
import org.lwjglx.debug.Properties;

public class GL40 {

    public static void glDrawArraysIndirect(@NativeType("GLenum") int mode, @NativeType("const void *") ByteBuffer indirect) {
        if (Properties.VALIDATE.enabled) {
            checkBeforeDrawCall();
        }
        org.lwjgl.opengl.GL40.glDrawArraysIndirect(mode, indirect);
    }

    public static void glDrawArraysIndirect(@NativeType("GLenum") int mode, @NativeType("const void *") long indirect) {
        if (Properties.VALIDATE.enabled) {
            checkBeforeDrawCall();
        }
        org.lwjgl.opengl.GL40.glDrawArraysIndirect(mode, indirect);
    }

    public static void glDrawArraysIndirect(@NativeType("GLenum") int mode, @NativeType("const void *") IntBuffer indirect) {
        if (Properties.VALIDATE.enabled) {
            checkBeforeDrawCall();
        }
        org.lwjgl.opengl.GL40.glDrawArraysIndirect(mode, indirect);
    }

    public static void glDrawElementsIndirect(@NativeType("GLenum") int mode, @NativeType("GLenum") int type, @NativeType("const void *") ByteBuffer indirect) {
        if (Properties.VALIDATE.enabled) {
            checkBeforeDrawCall();
        }
        org.lwjgl.opengl.GL40.glDrawElementsIndirect(mode, type, indirect);
    }

    public static void glDrawElementsIndirect(@NativeType("GLenum") int mode, @NativeType("GLenum") int type, @NativeType("const void *") long indirect) {
        if (Properties.VALIDATE.enabled) {
            checkBeforeDrawCall();
        }
        org.lwjgl.opengl.GL40.glDrawElementsIndirect(mode, type, indirect);
    }

    public static void glDrawElementsIndirect(@NativeType("GLenum") int mode, @NativeType("GLenum") int type, @NativeType("const void *") IntBuffer indirect) {
        if (Properties.VALIDATE.enabled) {
            checkBeforeDrawCall();
        }
        org.lwjgl.opengl.GL40.glDrawElementsIndirect(mode, type, indirect);
    }

    public static void glDrawArraysIndirect(@NativeType("GLenum") int mode, @NativeType("const void *") int[] indirect) {
        if (Properties.VALIDATE.enabled) {
            checkBeforeDrawCall();
        }
        org.lwjgl.opengl.GL40.glDrawArraysIndirect(mode, indirect);
    }

    public static void glDrawElementsIndirect(@NativeType("GLenum") int mode, @NativeType("GLenum") int type, @NativeType("const void *") int[] indirect) {
        if (Properties.VALIDATE.enabled) {
            checkBeforeDrawCall();
        }
        org.lwjgl.opengl.GL40.glDrawElementsIndirect(mode, type, indirect);
    }

}
