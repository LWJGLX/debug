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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjglx.debug.*;

public class EXTDirectStateAccess {

    private static void glMultiTexImage1DEXT_trace(int texunit, int target, int level, int internalformat, int width, int border, int format, int type, Buffer pixels, MethodCall mc) {
        mc.paramEnum(GLmetadata._null_().get(texunit));
        mc.paramEnum(GLmetadata.TextureTarget().get(target));
        mc.param(level);
        if (internalformat >= 1 && internalformat <= 4)
            mc.param(internalformat);
        else
            mc.paramEnum(GLmetadata.InternalFormat().get(internalformat));
        mc.param(width);
        mc.param(border);
        mc.paramEnum(GLmetadata.PixelFormat().get(format));
        mc.paramEnum(GLmetadata.PixelType().get(type));
        mc.param(pixels);
    }

    public static void glMultiTexImage1DEXT(int texunit, int target, int level, int internalformat, int width, int border, int format, int type, ByteBuffer pixels, Void ret, MethodCall mc) {
        glMultiTexImage1DEXT_trace(texunit, target, level, internalformat, width, border, format, type, pixels, mc);
    }

    public static void glMultiTexImage1DEXT(int texunit, int target, int level, int internalformat, int width, int border, int format, int type, ShortBuffer pixels, Void ret, MethodCall mc) {
        glMultiTexImage1DEXT_trace(texunit, target, level, internalformat, width, border, format, type, pixels, mc);
    }

    public static void glMultiTexImage1DEXT(int texunit, int target, int level, int internalformat, int width, int border, int format, int type, IntBuffer pixels, Void ret, MethodCall mc) {
        glMultiTexImage1DEXT_trace(texunit, target, level, internalformat, width, border, format, type, pixels, mc);
    }

    public static void glMultiTexImage1DEXT(int texunit, int target, int level, int internalformat, int width, int border, int format, int type, FloatBuffer pixels, Void ret, MethodCall mc) {
        glMultiTexImage1DEXT_trace(texunit, target, level, internalformat, width, border, format, type, pixels, mc);
    }

    public static void glMultiTexImage1DEXT(int texunit, int target, int level, int internalformat, int width, int border, int format, int type, DoubleBuffer pixels, Void ret, MethodCall mc) {
        glMultiTexImage1DEXT_trace(texunit, target, level, internalformat, width, border, format, type, pixels, mc);
    }

    private static void glMultiTexImage2DEXT_trace(int texunit, int target, int level, int internalformat, int width, int height, int border, int format, int type, Buffer pixels, MethodCall mc) {
        mc.paramEnum(GLmetadata._null_().get(texunit));
        mc.paramEnum(GLmetadata.TextureTarget().get(target));
        mc.param(level);
        if (internalformat >= 1 && internalformat <= 4)
            mc.param(internalformat);
        else
            mc.paramEnum(GLmetadata.InternalFormat().get(internalformat));
        mc.param(width);
        mc.param(height);
        mc.param(border);
        mc.paramEnum(GLmetadata.PixelFormat().get(format));
        mc.paramEnum(GLmetadata.PixelType().get(type));
        mc.param(pixels);
    }

    public static void glMultiTexImage2DEXT(int texunit, int target, int level, int internalformat, int width, int height, int border, int format, int type, ByteBuffer pixels, Void ret,
            MethodCall mc) {
        glMultiTexImage2DEXT_trace(texunit, target, level, internalformat, width, height, border, format, type, pixels, mc);
    }

    public static void glMultiTexImage2DEXT(int texunit, int target, int level, int internalformat, int width, int height, int border, int format, int type, ShortBuffer pixels, Void ret,
            MethodCall mc) {
        glMultiTexImage2DEXT_trace(texunit, target, level, internalformat, width, height, border, format, type, pixels, mc);
    }

    public static void glMultiTexImage2DEXT(int texunit, int target, int level, int internalformat, int width, int height, int border, int format, int type, IntBuffer pixels, Void ret,
            MethodCall mc) {
        glMultiTexImage2DEXT_trace(texunit, target, level, internalformat, width, height, border, format, type, pixels, mc);
    }

    public static void glMultiTexImage2DEXT(int texunit, int target, int level, int internalformat, int width, int height, int border, int format, int type, FloatBuffer pixels, Void ret,
            MethodCall mc) {
        glMultiTexImage2DEXT_trace(texunit, target, level, internalformat, width, height, border, format, type, pixels, mc);
    }

    public static void glMultiTexImage2DEXT(int texunit, int target, int level, int internalformat, int width, int height, int border, int format, int type, DoubleBuffer pixels, Void ret,
            MethodCall mc) {
        glMultiTexImage2DEXT_trace(texunit, target, level, internalformat, width, height, border, format, type, pixels, mc);
    }

    private static void glMultiTexImage3DEXT_trace(int texunit, int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, Buffer pixels,
            MethodCall mc) {
        mc.paramEnum(GLmetadata._null_().get(texunit));
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

    public static void glMultiTexImage3DEXT(int texunit, int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, ByteBuffer pixels, Void ret,
            MethodCall mc) {
        glMultiTexImage3DEXT_trace(texunit, target, level, internalformat, width, height, depth, border, format, type, pixels, mc);
    }

    public static void glMultiTexImage3DEXT(int texunit, int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, ShortBuffer pixels, Void ret,
            MethodCall mc) {
        glMultiTexImage3DEXT_trace(texunit, target, level, internalformat, width, height, depth, border, format, type, pixels, mc);
    }

    public static void glMultiTexImage3DEXT(int texunit, int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, IntBuffer pixels, Void ret,
            MethodCall mc) {
        glMultiTexImage3DEXT_trace(texunit, target, level, internalformat, width, height, depth, border, format, type, pixels, mc);
    }

    public static void glMultiTexImage3DEXT(int texunit, int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, FloatBuffer pixels, Void ret,
            MethodCall mc) {
        glMultiTexImage3DEXT_trace(texunit, target, level, internalformat, width, height, depth, border, format, type, pixels, mc);
    }

    public static void glMultiTexImage3DEXT(int texunit, int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, DoubleBuffer pixels, Void ret,
            MethodCall mc) {
        glMultiTexImage3DEXT_trace(texunit, target, level, internalformat, width, height, depth, border, format, type, pixels, mc);
    }

    private static void glTextureImage1DEXT_trace(int texture, int target, int level, int internalformat, int width, int border, int format, int type, Buffer pixels, MethodCall mc) {
        mc.param(texture);
        mc.paramEnum(GLmetadata.TextureTarget().get(target));
        mc.param(level);
        if (internalformat >= 1 && internalformat <= 4)
            mc.param(internalformat);
        else
            mc.paramEnum(GLmetadata.InternalFormat().get(internalformat));
        mc.param(width);
        mc.param(border);
        mc.paramEnum(GLmetadata.PixelFormat().get(format));
        mc.paramEnum(GLmetadata.PixelType().get(type));
        mc.param(pixels);
    }

    public static void glTextureImage1DEXT(int texture, int target, int level, int internalformat, int width, int border, int format, int type, ByteBuffer pixels, Void ret, MethodCall mc) {
        glTextureImage1DEXT_trace(texture, target, level, internalformat, width, border, format, type, pixels, mc);
    }

    public static void glTextureImage1DEXT(int texture, int target, int level, int internalformat, int width, int border, int format, int type, ShortBuffer pixels, Void ret, MethodCall mc) {
        glTextureImage1DEXT_trace(texture, target, level, internalformat, width, border, format, type, pixels, mc);
    }

    public static void glTextureImage1DEXT(int texture, int target, int level, int internalformat, int width, int border, int format, int type, IntBuffer pixels, Void ret, MethodCall mc) {
        glTextureImage1DEXT_trace(texture, target, level, internalformat, width, border, format, type, pixels, mc);
    }

    public static void glTextureImage1DEXT(int texture, int target, int level, int internalformat, int width, int border, int format, int type, FloatBuffer pixels, Void ret, MethodCall mc) {
        glTextureImage1DEXT_trace(texture, target, level, internalformat, width, border, format, type, pixels, mc);
    }

    public static void glTextureImage1DEXT(int texture, int target, int level, int internalformat, int width, int border, int format, int type, DoubleBuffer pixels, Void ret, MethodCall mc) {
        glTextureImage1DEXT_trace(texture, target, level, internalformat, width, border, format, type, pixels, mc);
    }

    private static void glTextureImage2DEXT_trace(int texture, int target, int level, int internalformat, int width, int height, int border, int format, int type, Buffer pixels, MethodCall mc) {
        mc.param(texture);
        mc.paramEnum(GLmetadata.TextureTarget().get(target));
        mc.param(level);
        if (internalformat >= 1 && internalformat <= 4)
            mc.param(internalformat);
        else
            mc.paramEnum(GLmetadata.InternalFormat().get(internalformat));
        mc.param(width);
        mc.param(height);
        mc.param(border);
        mc.paramEnum(GLmetadata.PixelFormat().get(format));
        mc.paramEnum(GLmetadata.PixelType().get(type));
        mc.param(pixels);
    }

    public static void glTextureImage2DEXT(int texture, int target, int level, int internalformat, int width, int height, int border, int format, int type, ByteBuffer pixels, Void ret,
            MethodCall mc) {
        glTextureImage2DEXT_trace(texture, target, level, internalformat, width, height, border, format, type, pixels, mc);
    }

    public static void glTextureImage2DEXT(int texture, int target, int level, int internalformat, int width, int height, int border, int format, int type, ShortBuffer pixels, Void ret,
            MethodCall mc) {
        glTextureImage2DEXT_trace(texture, target, level, internalformat, width, height, border, format, type, pixels, mc);
    }

    public static void glTextureImage2DEXT(int texture, int target, int level, int internalformat, int width, int height, int border, int format, int type, IntBuffer pixels, Void ret, MethodCall mc) {
        glTextureImage2DEXT_trace(texture, target, level, internalformat, width, height, border, format, type, pixels, mc);
    }

    public static void glTextureImage2DEXT(int texture, int target, int level, int internalformat, int width, int height, int border, int format, int type, FloatBuffer pixels, Void ret,
            MethodCall mc) {
        glTextureImage2DEXT_trace(texture, target, level, internalformat, width, height, border, format, type, pixels, mc);
    }

    public static void glTextureImage2DEXT(int texture, int target, int level, int internalformat, int width, int height, int border, int format, int type, DoubleBuffer pixels, Void ret,
            MethodCall mc) {
        glTextureImage2DEXT_trace(texture, target, level, internalformat, width, height, border, format, type, pixels, mc);
    }

    private static void glTextureImage3DEXT_trace(int texture, int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, Buffer pixels,
            MethodCall mc) {
        mc.param(texture);
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

    public static void glTextureImage3DEXT(int texture, int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, ByteBuffer pixels, Void ret,
            MethodCall mc) {
        glTextureImage3DEXT_trace(texture, target, level, internalformat, width, height, depth, border, format, type, pixels, mc);
    }

    public static void glTextureImage3DEXT(int texture, int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, ShortBuffer pixels, Void ret,
            MethodCall mc) {
        glTextureImage3DEXT_trace(texture, target, level, internalformat, width, height, depth, border, format, type, pixels, mc);
    }

    public static void glTextureImage3DEXT(int texture, int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, IntBuffer pixels, Void ret,
            MethodCall mc) {
        glTextureImage3DEXT_trace(texture, target, level, internalformat, width, height, depth, border, format, type, pixels, mc);
    }

    public static void glTextureImage3DEXT(int texture, int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, FloatBuffer pixels, Void ret,
            MethodCall mc) {
        glTextureImage3DEXT_trace(texture, target, level, internalformat, width, height, depth, border, format, type, pixels, mc);
    }

    public static void glTextureImage3DEXT(int texture, int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, DoubleBuffer pixels, Void ret,
            MethodCall mc) {
        glTextureImage3DEXT_trace(texture, target, level, internalformat, width, height, depth, border, format, type, pixels, mc);
    }

    public static void glDisableVertexArrayAttribEXT(int vaobj, int index) {
        org.lwjgl.opengl.EXTDirectStateAccess.glDisableVertexArrayAttribEXT(vaobj, index);
        if (Properties.VALIDATE.enabled) {
            Context context = Context.currentContext();
            context.vaos.get(vaobj).enabledVertexArrays[index] = false;
        }
    }

    public static void glEnableVertexArrayAttribEXT(int vaobj, int index) {
        org.lwjgl.opengl.EXTDirectStateAccess.glDisableVertexArrayAttribEXT(vaobj, index);
        if (Properties.VALIDATE.enabled) {
            Context context = Context.currentContext();
            context.vaos.get(vaobj).enabledVertexArrays[index] = true;
        }
    }

}
