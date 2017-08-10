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

import org.lwjglx.debug.Context;
import org.lwjglx.debug.GLmetadata;
import org.lwjglx.debug.MethodCall;

public class GL11 {

    public static void glDrawArrays(int mode, int first, int count) {
        checkVertexAttributes();
        org.lwjgl.opengl.GL11.glDrawArrays(mode, first, count);
    }

    public static void glDrawElements(int mode, int count, int type, long indices) {
        int ibo = org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING);
        if (ibo == 0) {
            throwISEOrLogError("glDrawElements called with index offset but no ELEMENT_ARRAY_BUFFER bound");
        }
        checkVertexAttributes();
        org.lwjgl.opengl.GL11.glDrawElements(mode, count, type, indices);
    }

    public static void glDrawElements(int mode, int type, ByteBuffer indices) {
        checkVertexAttributes();
        org.lwjgl.opengl.GL11.glDrawElements(mode, type, indices);
    }

    public static void glDrawElements(int mode, ByteBuffer indices) {
        checkVertexAttributes();
        org.lwjgl.opengl.GL11.glDrawElements(mode, indices);
    }

    public static void glDrawElements(int mode, ShortBuffer indices) {
        checkVertexAttributes();
        org.lwjgl.opengl.GL11.glDrawElements(mode, indices);
    }

    public static void glDrawElements(int mode, IntBuffer indices) {
        checkVertexAttributes();
        org.lwjgl.opengl.GL11.glDrawElements(mode, indices);
    }

    public static void glPopClientAttrib() {
        org.lwjgl.opengl.GL11.glPopClientAttrib();
        /* Refresh vertex array state */
        Context context = CURRENT_CONTEXT.get();
        for (int i = 0; i < context.GL_MAX_VERTEX_ATTRIBS; i++) {
            /* Read enable state and buffer bindings */
            if (context.caps.OpenGL20) {
                context.currentVao.enabledVertexArrays[i] = org.lwjgl.opengl.GL20.glGetVertexAttribi(0, org.lwjgl.opengl.GL20.GL_VERTEX_ATTRIB_ARRAY_ENABLED) == 1;
                context.currentVao.initializedVertexArrays[i] = org.lwjgl.opengl.GL20.glGetVertexAttribi(0, org.lwjgl.opengl.GL15.GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING) != 0;
            } else if (context.caps.GL_ARB_vertex_shader) {
                context.currentVao.enabledVertexArrays[i] = org.lwjgl.opengl.ARBVertexShader.glGetVertexAttribiARB(0, org.lwjgl.opengl.ARBVertexShader.GL_VERTEX_ATTRIB_ARRAY_ENABLED_ARB) == 1;
                context.currentVao.initializedVertexArrays[i] = org.lwjgl.opengl.GL20.glGetVertexAttribi(0, org.lwjgl.opengl.ARBVertexBufferObject.GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING_ARB) != 0;
            }
        }
    }

    private static void glTexImage2D_trace(int target, int level, int internalformat, int width, int height, int border, int format, int type, Buffer pixels, MethodCall mc) {
        mc.paramEnum(GLmetadata.TextureTarget.get(target));
        mc.param(level);
        if (internalformat >= 1 && internalformat <= 4)
            mc.param(internalformat);
        else
            mc.paramEnum(GLmetadata.InternalFormat.get(internalformat));
        mc.param(width);
        mc.param(height);
        mc.param(border);
        mc.paramEnum(GLmetadata.PixelFormat.get(format));
        mc.paramEnum(GLmetadata.PixelType.get(type));
        mc.param(pixels);
    }

    public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ByteBuffer pixels, MethodCall mc) {
        glTexImage2D_trace(target, level, internalformat, width, height, border, format, type, pixels, mc);
    }

    public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ShortBuffer pixels, MethodCall mc) {
        glTexImage2D_trace(target, level, internalformat, width, height, border, format, type, pixels, mc);
    }

    public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, IntBuffer pixels, MethodCall mc) {
        glTexImage2D_trace(target, level, internalformat, width, height, border, format, type, pixels, mc);
    }

    public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, FloatBuffer pixels, MethodCall mc) {
        glTexImage2D_trace(target, level, internalformat, width, height, border, format, type, pixels, mc);
    }

    public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, DoubleBuffer pixels, MethodCall mc) {
        glTexImage2D_trace(target, level, internalformat, width, height, border, format, type, pixels, mc);
    }

    private static void glTexImage1D_trace(int target, int level, int internalformat, int width, int border, int format, int type, Buffer pixels, MethodCall mc) {
        mc.paramEnum(GLmetadata.TextureTarget.get(target));
        mc.param(level);
        if (internalformat >= 1 && internalformat <= 4)
            mc.param(internalformat);
        else
            mc.paramEnum(GLmetadata.InternalFormat.get(internalformat));
        mc.param(width);
        mc.param(border);
        mc.paramEnum(GLmetadata.PixelFormat.get(format));
        mc.paramEnum(GLmetadata.PixelType.get(type));
        mc.param(pixels);
    }

    public static void glTexImage1D(int target, int level, int internalformat, int width, int border, int format, int type, ByteBuffer pixels, MethodCall mc) {
        glTexImage1D_trace(target, level, internalformat, width, border, format, type, pixels, mc);
    }

    public static void glTexImage1D(int target, int level, int internalformat, int width, int border, int format, int type, ShortBuffer pixels, MethodCall mc) {
        glTexImage1D_trace(target, level, internalformat, width, border, format, type, pixels, mc);
    }

    public static void glTexImage1D(int target, int level, int internalformat, int width, int border, int format, int type, IntBuffer pixels, MethodCall mc) {
        glTexImage1D_trace(target, level, internalformat, width, border, format, type, pixels, mc);
    }

    public static void glTexImage1D(int target, int level, int internalformat, int width, int border, int format, int type, FloatBuffer pixels, MethodCall mc) {
        glTexImage1D_trace(target, level, internalformat, width, border, format, type, pixels, mc);
    }

    public static void glTexImage1D(int target, int level, int internalformat, int width, int border, int format, int type, DoubleBuffer pixels, MethodCall mc) {
        glTexImage1D_trace(target, level, internalformat, width, border, format, type, pixels, mc);
    }

    private static void glTexParameter_trace(int target, int pname, int param, MethodCall mc) {
        switch (pname) {
        case org.lwjgl.opengl.GL43.GL_DEPTH_STENCIL_TEXTURE_MODE:
            mc.paramEnum(GLmetadata.PixelFormat.get(param));
            break;
        case org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER:
            mc.paramEnum(GLmetadata.TextureMinFilter.get(param));
            break;
        case org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER:
            mc.paramEnum(GLmetadata.TextureMagFilter.get(param));
            break;
        case org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_FUNC:
            mc.paramEnum(GLmetadata.AlphaFunction.get(param));
            break;
        case org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_MODE:
            mc.paramEnum(GLmetadata._null_.get(param));
            break;
        case org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S:
            mc.paramEnum(GLmetadata.TextureWrapMode.get(param));
            break;
        case org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T:
            mc.paramEnum(GLmetadata.TextureWrapMode.get(param));
            break;
        case org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R:
            mc.paramEnum(GLmetadata.TextureWrapMode.get(param));
            break;
        case org.lwjgl.opengl.GL14.GL_GENERATE_MIPMAP:
            mc.paramEnum(GLmetadata.Boolean.get(param));
            break;
        default:
            mc.param(param);
            break;
        }
    }

    public static void glTexParameteri(int target, int pname, int param, MethodCall mc) {
        mc.paramEnum(GLmetadata.TextureTarget.get(target));
        mc.paramEnum(GLmetadata.TextureParameterName.get(pname));
        glTexParameter_trace(target, pname, param, mc);
    }

    public static void glBegin(int mode) {
        org.lwjgl.opengl.GL11.glBegin(mode);
        Context ctx = CURRENT_CONTEXT.get();
        ctx.inImmediateMode = true;
    }

    public static void glEnd() {
        org.lwjgl.opengl.GL11.glEnd();
        Context ctx = CURRENT_CONTEXT.get();
        ctx.inImmediateMode = false;
    }

}
