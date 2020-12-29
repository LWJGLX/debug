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
package org.lwjglx.debug.org.lwjgl.opengl;

import static org.lwjglx.debug.Log.*;
import static org.lwjglx.debug.Properties.*;
import static org.lwjglx.debug.RT.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjglx.debug.*;

public class GL20 {

    public static void glEnableVertexAttribArray(int index) {
        if (Properties.VALIDATE.enabled && index > -1) {
            Context.currentContext().currentVao.enabledVertexArrays[index] = true;
        }
        org.lwjgl.opengl.GL20.glEnableVertexAttribArray(index);
    }

    public static void glDisableVertexAttribArray(int index) {
        if (Properties.VALIDATE.enabled && index > -1) {
            Context.currentContext().currentVao.enabledVertexArrays[index] = false;
        }
        org.lwjgl.opengl.GL20.glDisableVertexAttribArray(index);
    }

    public static void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, FloatBuffer pointer) {
        if (Properties.VALIDATE.enabled && index > -1) {
            Context.currentContext().currentVao.initializedVertexArrays[index] = pointer != null;
        }
        org.lwjgl.opengl.GL20.glVertexAttribPointer(index, size, type, normalized, stride, pointer);
    }

    public static void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, ByteBuffer pointer) {
        if (Properties.VALIDATE.enabled && index > -1) {
            Context.currentContext().currentVao.initializedVertexArrays[index] = pointer != null;
        }
        org.lwjgl.opengl.GL20.glVertexAttribPointer(index, size, type, normalized, stride, pointer);
    }

    public static void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, IntBuffer pointer) {
        if (Properties.VALIDATE.enabled && index > -1) {
            Context.currentContext().currentVao.initializedVertexArrays[index] = pointer != null;
        }
        org.lwjgl.opengl.GL20.glVertexAttribPointer(index, size, type, normalized, stride, pointer);
    }

    public static void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, ShortBuffer pointer) {
        if (Properties.VALIDATE.enabled && index > -1) {
            Context.currentContext().currentVao.initializedVertexArrays[index] = pointer != null;
        }
        org.lwjgl.opengl.GL20.glVertexAttribPointer(index, size, type, normalized, stride, pointer);
    }

    public static void nglVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long pointer) {
        if (Properties.VALIDATE.enabled && index > -1) {
            int vbo = org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER_BINDING);
            if (vbo != 0) {
                Context.currentContext().currentVao.initializedVertexArrays[index] = true;
            } else if (isInvalidPointer(pointer)) {
        		throwIAEOrLogError("There is no GL_ARRAY_BUFFER bound and pointer argument [" + pointer + "] is invalid. "
        				+ "This will likely lead to a JVM crash in a draw call");
            }
        }
        org.lwjgl.opengl.GL20.nglVertexAttribPointer(index, size, type, normalized, stride, pointer);
    }

    public static void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long pointer) {
        if (Properties.VALIDATE.enabled && index > -1) {
            int vbo = org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER_BINDING);
            if (vbo != 0) {
                Context.currentContext().currentVao.initializedVertexArrays[index] = true;
            } else if (isInvalidPointer(pointer)) {
        		throwIAEOrLogError("There is no GL_ARRAY_BUFFER bound and pointer argument [" + pointer + "] is invalid. "
        				+ "This will likely lead to a JVM crash in a draw call");
            }
        }
        org.lwjgl.opengl.GL20.glVertexAttribPointer(index, size, type, normalized, stride, pointer);
    }

    public static void glCompileShader(int shader) {
        org.lwjgl.opengl.GL20.glCompileShader(shader);
        if (Properties.VALIDATE.enabled) {
            /* Check compile status */
            int status = org.lwjgl.opengl.GL20.glGetShaderi(shader, org.lwjgl.opengl.GL20.GL_COMPILE_STATUS);
            if (status != org.lwjgl.opengl.GL11.GL_TRUE) {
                String shaderLog = org.lwjgl.opengl.GL20.glGetShaderInfoLog(shader);
                error("Shader [" + shader + "] did not compile successfully:\n" + shaderLog);
            }
        }
    }

    public static void glLinkProgram(int program) {
        org.lwjgl.opengl.GL20.glLinkProgram(program);
        if (Properties.VALIDATE.enabled) {
            /* Check link status */
            int status = org.lwjgl.opengl.GL20.glGetProgrami(program, org.lwjgl.opengl.GL20.GL_LINK_STATUS);
            if (status != org.lwjgl.opengl.GL11.GL_TRUE) {
                String programLog = org.lwjgl.opengl.GL20.glGetProgramInfoLog(program);
                error("Program [" + program + "] did not link successfully:\n" + programLog);
            }
        }
    }

    public static void glShaderSource(int shader, org.lwjgl.PointerBuffer strings, IntBuffer length) {
        org.lwjgl.opengl.GL20.glShaderSource(shader, strings, length);
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
    }

    public static void glShaderSource(int shader, CharSequence... strings) {
        org.lwjgl.opengl.GL20.glShaderSource(shader, strings);
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
    }

    public static void glShaderSource(int shader, CharSequence string) {
        org.lwjgl.opengl.GL20.glShaderSource(shader, string);
        if (TRACE.enabled) {
            /* Log the shader source */
            trace("Shader source for shader [" + shader + "]:\n" + string);
        }
    }

    public static void glGetShaderi(int shader, int pname, int ret, MethodCall mc) {
        mc.param(shader);
        mc.paramEnum(GLmetadata._null_().get(pname));
        switch (pname) {
        case org.lwjgl.opengl.GL20.GL_COMPILE_STATUS:
            mc.returnValueEnum(GLmetadata.Boolean().get(ret));
            break;
        default:
            mc.param(pname);
            break;
        }
    }

    public static void glGetProgrami(int program, int pname, int ret, MethodCall mc) {
        mc.param(program);
        mc.paramEnum(GLmetadata._null_().get(pname));
        switch (pname) {
        case org.lwjgl.opengl.GL20.GL_LINK_STATUS:
            mc.returnValueEnum(GLmetadata.Boolean().get(ret));
            break;
        default:
            mc.param(pname);
            break;
        }
    }

}
