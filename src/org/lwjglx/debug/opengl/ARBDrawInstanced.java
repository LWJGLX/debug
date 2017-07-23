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

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class ARBDrawInstanced {

    public static void glDrawArraysInstancedARB(int mode, int first, int count, int primcount) {
        checkVertexAttributes();
        org.lwjgl.opengl.ARBDrawInstanced.glDrawArraysInstancedARB(mode, first, count, primcount);
    }

    public static void glDrawElementsInstancedARB(int mode, int count, int type, long indices, int primcount) {
        int ibo = org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING);
        if (ibo == 0) {
            throwISEOrLogError("glDrawElementsInstancedARB called with index offset but no ELEMENT_ARRAY_BUFFER bound");
        }
        checkVertexAttributes();
        org.lwjgl.opengl.ARBDrawInstanced.glDrawElementsInstancedARB(mode, count, type, indices, primcount);
    }

    public static void glDrawElementsInstancedARB(int mode, int type, ByteBuffer indices, int primcount) {
        checkVertexAttributes();
        org.lwjgl.opengl.ARBDrawInstanced.glDrawElementsInstancedARB(mode, type, indices, primcount);
    }

    public static void glDrawElementsInstancedARB(int mode, ByteBuffer indices, int primcount) {
        checkVertexAttributes();
        org.lwjgl.opengl.ARBDrawInstanced.glDrawElementsInstancedARB(mode, indices, primcount);
    }

    public static void glDrawElementsInstancedARB(int mode, ShortBuffer indices, int primcount) {
        checkVertexAttributes();
        org.lwjgl.opengl.ARBDrawInstanced.glDrawElementsInstancedARB(mode, indices, primcount);
    }

    public static void glDrawElementsInstancedARB(int mode, IntBuffer indices, int primcount) {
        checkVertexAttributes();
        org.lwjgl.opengl.ARBDrawInstanced.glDrawElementsInstancedARB(mode, indices, primcount);
    }

}
