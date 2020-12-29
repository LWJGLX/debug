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

import static org.lwjglx.debug.RT.*;
import static org.lwjglx.debug.opengl.Context.*;

import java.nio.IntBuffer;

import org.lwjglx.debug.Properties;
import org.lwjglx.debug.opengl.Context.VAO;

public class ARBVertexArrayObject {

    public static void glGenVertexArrays(IntBuffer arrays) {
        org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays(arrays);
        if (Properties.VALIDATE.enabled) {
            Context context = Context.currentContext();
            int position = arrays.position();
            for (int i = 0; i < arrays.remaining(); i++) {
                VAO vao = new VAO(context.GL_MAX_VERTEX_ATTRIBS);
                context.vaos.put(arrays.get(position + i), vao);
            }
        }
    }

    public static int glGenVertexArrays() {
        int index = org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays();
        if (Properties.VALIDATE.enabled) {
            Context context = Context.currentContext();
            VAO vao = new VAO(context.GL_MAX_VERTEX_ATTRIBS);
            context.vaos.put(index, vao);
        }
        return index;
    }

    public static void glGenVertexArrays(int[] arrays) {
        org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays(arrays);
        if (Properties.VALIDATE.enabled) {
            Context context = Context.currentContext();
            for (int i = 0; i < arrays.length; i++) {
                VAO vao = new VAO(context.GL_MAX_VERTEX_ATTRIBS);
                context.vaos.put(arrays[i], vao);
            }
        }
    }

    public static void glBindVertexArray(int index) {
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            VAO vao = ctx.vaos.get(index);
            if (vao == null && ctx.shareGroup != null) {
                for (Context c : ctx.shareGroup.contexts) {
                    if (c.vaos.containsKey(index)) {
                        throwISEOrLogError("Trying to bind unknown VAO [" + index + "] from shared context [" + c.counter + "]");
                    }
                }
            }
            ctx.currentVao = vao;
        }
        org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray(index);
    }

    public static void glDeleteVertexArrays(int index) {
        org.lwjgl.opengl.ARBVertexArrayObject.glDeleteVertexArrays(index);
        if (Properties.VALIDATE.enabled) {
            deleteVertexArray(index);
        }
    }

    public static void glDeleteVertexArrays(IntBuffer indices) {
        org.lwjgl.opengl.ARBVertexArrayObject.glDeleteVertexArrays(indices);
        if (Properties.VALIDATE.enabled) {
            deleteVertexArrays(indices);
        }
    }

    public static void glDeleteVertexArrays(int[] indices) {
        org.lwjgl.opengl.ARBVertexArrayObject.glDeleteVertexArrays(indices);
        if (Properties.VALIDATE.enabled) {
            deleteVertexArrays(indices);
        }
    }

}
