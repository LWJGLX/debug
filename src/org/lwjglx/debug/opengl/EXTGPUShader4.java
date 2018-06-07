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

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjglx.debug.Properties;

public class EXTGPUShader4 {

    public static void glVertexAttribIPointerEXT(int index, int size, int type, int stride, ByteBuffer pointer) {
        if (Properties.VALIDATE.enabled && index > -1) {
            CURRENT_CONTEXT.get().currentVao.initializedVertexArrays[index] = pointer != null;
        }
        org.lwjgl.opengl.EXTGPUShader4.glVertexAttribIPointerEXT(index, size, type, stride, pointer);
    }

    public static void glVertexAttribIPointerEXT(int index, int size, int type, int stride, long pointer) {
        if (Properties.VALIDATE.enabled && index > -1) {
            int vbo = org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER_BINDING);
            if (vbo != 0) {
                CURRENT_CONTEXT.get().currentVao.initializedVertexArrays[index] = true;
            }
        }
        org.lwjgl.opengl.EXTGPUShader4.glVertexAttribIPointerEXT(index, size, type, stride, pointer);
    }

    public static void glVertexAttribIPointerEXT(int index, int size, int type, int stride, ShortBuffer pointer) {
        if (Properties.VALIDATE.enabled && index > -1) {
            CURRENT_CONTEXT.get().currentVao.initializedVertexArrays[index] = pointer != null;
        }
        org.lwjgl.opengl.EXTGPUShader4.glVertexAttribIPointerEXT(index, size, type, stride, pointer);
    }

    public static void glVertexAttribIPointerEXT(int index, int size, int type, int stride, IntBuffer pointer) {
        if (Properties.VALIDATE.enabled && index > -1) {
            CURRENT_CONTEXT.get().currentVao.initializedVertexArrays[index] = pointer != null;
        }
        org.lwjgl.opengl.EXTGPUShader4.glVertexAttribIPointerEXT(index, size, type, stride, pointer);
    }

}
