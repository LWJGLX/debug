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

import static org.lwjglx.debug.Log.*;
import static org.lwjglx.debug.Properties.*;
import static org.lwjglx.debug.RT.*;
import static org.lwjglx.debug.opengl.Context.*;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjglx.debug.Properties;
import org.lwjglx.debug.opengl.Context.ProgramPipeline;

public class GL41 {

    public static void glVertexAttribLPointer(int index, int size, int type, int stride, ByteBuffer pointer) {
        if (Properties.VALIDATE.enabled && index > -1) {
            Context.currentContext().currentVao.initializedVertexArrays[index] = pointer != null;
        }
        org.lwjgl.opengl.GL41.glVertexAttribLPointer(index, size, type, stride, pointer);
    }

    public static void glVertexAttribLPointer(int index, int size, int type, int stride, long pointer) {
        if (Properties.VALIDATE.enabled && index > -1) {
            int vbo = org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER_BINDING);
            if (vbo != 0) {
                Context.currentContext().currentVao.initializedVertexArrays[index] = true;
            }
        }
        org.lwjgl.opengl.GL41.glVertexAttribLPointer(index, size, type, stride, pointer);
    }

    public static void glVertexAttribLPointer(int index, int size, int stride, DoubleBuffer pointer) {
        if (Properties.VALIDATE.enabled && index > -1) {
            Context.currentContext().currentVao.initializedVertexArrays[index] = pointer != null;
        }
        org.lwjgl.opengl.GL41.glVertexAttribLPointer(index, size, stride, pointer);
    }

    public static void glGenProgramPipelines(IntBuffer pipelines) {
        org.lwjgl.opengl.GL41.glGenProgramPipelines(pipelines);
        if (Properties.VALIDATE.enabled) {
            int position = pipelines.position();
            Context context = Context.currentContext();
            for (int i = 0; i < pipelines.remaining(); i++) {
                ProgramPipeline pp = new ProgramPipeline();
                context.programPipelines.put(pipelines.get(position + i), pp);
            }
        }
    }

    public static int glGenProgramPipelines() {
        int index = org.lwjgl.opengl.GL41.glGenProgramPipelines();
        if (Properties.VALIDATE.enabled) {
            Context context = Context.currentContext();
            ProgramPipeline pp = new ProgramPipeline();
            context.programPipelines.put(index, pp);
        }
        return index;
    }

    public static void glGenProgramPipelines(int[] pipelines) {
        org.lwjgl.opengl.GL41.glGenProgramPipelines(pipelines);
        if (Properties.VALIDATE.enabled) {
            Context context = Context.currentContext();
            for (int i = 0; i < pipelines.length; i++) {
                ProgramPipeline pp = new ProgramPipeline();
                context.programPipelines.put(pipelines[i], pp);
            }
        }
    }

    public static void glBindProgramPipeline(int pipeline) {
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            ProgramPipeline pp = ctx.programPipelines.get(pipeline);
            if (pp == null && ctx.shareGroup != null) {
                for (Context c : ctx.shareGroup.contexts) {
                    if (c.programPipelines.containsKey(pipeline)) {
                        throwISEOrLogError("Trying to bind unknown Program Pipeline [" + pipeline + "] from shared context [" + c.counter + "]");
                    }
                }
            }
            ctx.currentProgramPipeline = pp;
        }
        org.lwjgl.opengl.GL41.glBindProgramPipeline(pipeline);
    }

    public static void glDeleteProgramPipelines(IntBuffer pipelines) {
        org.lwjgl.opengl.GL41.glDeleteProgramPipelines(pipelines);
        if (Properties.VALIDATE.enabled) {
            deletePipelines(pipelines);
        }
    }

    public static void glDeleteProgramPipelines(int pipeline) {
        org.lwjgl.opengl.GL41.glDeleteProgramPipelines(pipeline);
        if (Properties.VALIDATE.enabled) {
            deletePipeline(pipeline);
        }
    }

    public static void glDeleteProgramPipelines(int[] pipelines) {
        org.lwjgl.opengl.GL41.glDeleteProgramPipelines(pipelines);
        if (Properties.VALIDATE.enabled) {
            deletePipelines(pipelines);
        }
    }

    public static int glCreateShaderProgramv(int type, PointerBuffer strings) {
        int shader = org.lwjgl.opengl.GL41.glCreateShaderProgramv(type, strings);
        if (TRACE.enabled) {
            /* Log the shader source */
            StringBuilder sb = new StringBuilder();
            if (strings != null) {
                int stringsPos = strings.position();
                for (int i = 0; i < strings.remaining(); i++) {
                    String source = org.lwjgl.system.MemoryUtil.memASCII(strings.get(stringsPos + i));
                    sb.append(source);
                }
            }
            trace("Shader source for shader [" + shader + "]:\n" + sb.toString());
        }
        return shader;
    }

    public static int glCreateShaderProgramv(int type, CharSequence... strings) {
        int shader = org.lwjgl.opengl.GL41.glCreateShaderProgramv(type, strings);
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
        return shader;
    }

    public static int glCreateShaderProgramv(int type, CharSequence string) {
        int shader = org.lwjgl.opengl.GL41.glCreateShaderProgramv(type, string);
        if (TRACE.enabled) {
            /* Log the shader source */
            trace("Shader source for shader [" + shader + "]:\n" + string);
        }
        return shader;
    }

}
