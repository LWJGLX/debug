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

import static org.lwjglx.debug.Log.error;
import static org.lwjglx.debug.Log.trace;
import static org.lwjglx.debug.Properties.TRACE;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjglx.debug.GLmetadata;
import org.lwjglx.debug.MethodCall;
import org.lwjglx.debug.Properties;

public class ARBShaderObjects {

    public static void glCompileShaderARB(int shader) {
        org.lwjgl.opengl.ARBShaderObjects.glCompileShaderARB(shader);
        if (Properties.VALIDATE.enabled) {
            /* Check compile status */
            int status = org.lwjgl.opengl.ARBShaderObjects.glGetObjectParameteriARB(shader, org.lwjgl.opengl.ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB);
            if (status != org.lwjgl.opengl.GL11.GL_TRUE) {
                String shaderLog = org.lwjgl.opengl.ARBShaderObjects.glGetInfoLogARB(shader);
                error("Shader [" + shader + "] did not compile successfully:\n" + shaderLog);
            }
        }
    }

    public static void glLinkProgramARB(int program) {
        org.lwjgl.opengl.ARBShaderObjects.glLinkProgramARB(program);
        if (Properties.VALIDATE.enabled) {
            /* Check link status */
            int status = org.lwjgl.opengl.ARBShaderObjects.glGetObjectParameteriARB(program, org.lwjgl.opengl.ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB);
            if (status != org.lwjgl.opengl.GL11.GL_TRUE) {
                String programLog = org.lwjgl.opengl.ARBShaderObjects.glGetInfoLogARB(program);
                error("Program [" + program + "] did not link successfully:\n" + programLog);
            }
        }
    }

    public static void glShaderSource(int shader, org.lwjgl.PointerBuffer strings, IntBuffer length) {
        if (TRACE.enabled) {
            /* Log the shader source */
            StringBuilder sb = new StringBuilder();
            if (strings != null && length != null) {
                int stringsPos = strings.position();
                int lengthPos = length.position();
                for (int i = 0; i < strings.remaining(); i++) {
                    int len = length.get(lengthPos + i);
                    ByteBuffer string = org.lwjgl.system.MemoryUtil.memByteBuffer(strings.get(stringsPos + i), len);
                    String source = org.lwjgl.system.MemoryUtil.memASCII(string, len);
                    sb.append(source);
                }
            }
            trace("Shader source for shader [" + shader + "]:\n" + sb.toString());
        }
        org.lwjgl.opengl.ARBShaderObjects.glShaderSourceARB(shader, strings, length);
    }

    public static void glShaderSource(int shader, CharSequence... strings) {
        if (TRACE.enabled) {
            /* Log the shader source */
            StringBuilder sb = new StringBuilder();
            if (strings != null) {
                for (int i = 0; i < strings.length; i++) {
                    sb.append(strings[i]);
                }
            }
            trace("Shader source for shader [" + shader + "]:\n" + sb.toString());
        }
        org.lwjgl.opengl.ARBShaderObjects.glShaderSourceARB(shader, strings);
    }

    public static void glShaderSource(int shader, CharSequence string) {
        if (TRACE.enabled) {
            /* Log the shader source */
            trace("Shader source for shader [" + shader + "]:\n" + string);
        }
        org.lwjgl.opengl.ARBShaderObjects.glShaderSourceARB(shader, string);
    }

    public static void glGetObjectParameteriARB(int shader, int pname, int ret, MethodCall mc) {
        mc.param(shader);
        mc.paramEnum(GLmetadata._null_().get(pname));
        switch (pname) {
        case org.lwjgl.opengl.ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB:
            mc.returnValueEnum(GLmetadata.Boolean().get(ret));
            break;
        case org.lwjgl.opengl.ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB:
            mc.returnValueEnum(GLmetadata.Boolean().get(ret));
            break;
        default:
            mc.param(pname);
            break;
        }
    }

}
