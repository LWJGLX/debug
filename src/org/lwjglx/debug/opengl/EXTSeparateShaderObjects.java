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

import static org.lwjglx.debug.Log.trace;
import static org.lwjglx.debug.Properties.*;

import java.nio.ByteBuffer;

public class EXTSeparateShaderObjects {

    public static int glCreateShaderProgramEXT(int type, ByteBuffer string) {
        int shader = org.lwjgl.opengl.EXTSeparateShaderObjects.glCreateShaderProgramEXT(type, string);
        if (TRACE.enabled) {
            /* Log the shader source */
            StringBuilder sb = new StringBuilder();
            if (string != null) {
                String source = org.lwjgl.system.MemoryUtil.memASCII(string);
                sb.append(source);
            }
            trace("Shader source for shader [" + shader + "]:\n" + sb.toString());
        }
        return shader;
    }

    public static int glCreateShaderProgramEXT(int type, CharSequence string) {
        int shader = org.lwjgl.opengl.EXTSeparateShaderObjects.glCreateShaderProgramEXT(type, string);
        if (TRACE.enabled) {
            /* Log the shader source */
            StringBuilder sb = new StringBuilder();
            if (string != null) {
                sb.append(string);
            }
            trace("Shader source for shader [" + shader + "]:\n" + sb.toString());
        }
        return shader;
    }

}
