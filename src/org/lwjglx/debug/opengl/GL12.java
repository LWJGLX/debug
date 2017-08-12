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
import static org.lwjglx.debug.RT.*;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjglx.debug.GLmetadata;
import org.lwjglx.debug.MethodCall;

public class GL12 {

    public static void glDrawRangeElements(int mode, int start, int end, int count, int type, long indices) {
        int ibo = org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING);
        if (ibo == 0) {
            throwISEOrLogError("glDrawRangeElements called with index offset but no ELEMENT_ARRAY_BUFFER bound");
        }
        checkVertexAttributes();
        org.lwjgl.opengl.GL12.glDrawRangeElements(mode, start, end, count, type, indices);
    }

    public static void glDrawRangeElements(int mode, int start, int end, int type, ByteBuffer indices) {
        checkVertexAttributes();
        org.lwjgl.opengl.GL12.glDrawRangeElements(mode, start, end, type, indices);
    }

    public static void glDrawRangeElements(int mode, int start, int end, ByteBuffer indices) {
        checkVertexAttributes();
        org.lwjgl.opengl.GL12.glDrawRangeElements(mode, start, end, indices);
    }

    public static void glDrawRangeElements(int mode, int start, int end, ShortBuffer indices) {
        checkVertexAttributes();
        org.lwjgl.opengl.GL12.glDrawRangeElements(mode, start, end, indices);
    }

    public static void glDrawRangeElements(int mode, int start, int end, IntBuffer indices) {
        checkVertexAttributes();
        org.lwjgl.opengl.GL12.glDrawRangeElements(mode, start, end, indices);
    }

    private static void glTexImage3D_trace(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, Buffer pixels, MethodCall mc) {
        mc.paramEnum(GLmetadata.TextureTarget().get(target));
        mc.param(level);
        if (internalformat >= 1 && internalformat <= 4)
            mc.param(internalformat);
        else
            mc.paramEnum(GLmetadata.InternalFormat().get(internalformat));
        mc.param(width);
        mc.param(height);
        mc.param(depth);
        mc.param(border);
        mc.paramEnum(GLmetadata.PixelFormat().get(format));
        mc.paramEnum(GLmetadata.PixelType().get(type));
        mc.param(pixels);
    }

    public static void glTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, ByteBuffer pixels, Void ret, MethodCall mc) {
        glTexImage3D_trace(target, level, internalformat, width, height, depth, border, format, type, pixels, mc);
    }

    public static void glTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, ShortBuffer pixels, Void ret, MethodCall mc) {
        glTexImage3D_trace(target, level, internalformat, width, height, depth, border, format, type, pixels, mc);
    }

    public static void glTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, IntBuffer pixels, Void ret, MethodCall mc) {
        glTexImage3D_trace(target, level, internalformat, width, height, depth, border, format, type, pixels, mc);
    }

    public static void glTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, FloatBuffer pixels, Void ret, MethodCall mc) {
        glTexImage3D_trace(target, level, internalformat, width, height, depth, border, format, type, pixels, mc);
    }

    public static void glTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, DoubleBuffer pixels, Void ret, MethodCall mc) {
        glTexImage3D_trace(target, level, internalformat, width, height, depth, border, format, type, pixels, mc);
    }

}
