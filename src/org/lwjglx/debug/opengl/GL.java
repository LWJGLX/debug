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

import org.lwjglx.debug.Context;
import org.lwjglx.debug.Properties;

public class GL {

    public static org.lwjgl.opengl.GLCapabilities createCapabilities() {
        org.lwjgl.opengl.GLCapabilities caps = org.lwjgl.opengl.GL.createCapabilities();
        org.lwjgl.system.Callback callback = null;
        if (Properties.VALIDATE.enabled) {
            callback = GLUtil.setupDebugMessageCallback();
        }
        Context context = CURRENT_CONTEXT.get();
        context.caps = caps;
        context.debugCallback = callback;
        int GL_MAX_VERTEX_ATTRIBS = 16;
        if (caps.OpenGL20) {
            GL_MAX_VERTEX_ATTRIBS = org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL20.GL_MAX_VERTEX_ATTRIBS);
        } else if (caps.GL_ARB_vertex_shader) {
            GL_MAX_VERTEX_ATTRIBS = org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.ARBVertexShader.GL_MAX_VERTEX_ATTRIBS_ARB);
        }
        context.init(GL_MAX_VERTEX_ATTRIBS);
        return caps;
    }

    public static void setCapabilities(org.lwjgl.opengl.GLCapabilities caps) {
        org.lwjgl.opengl.GL.setCapabilities(caps);
        Context context = CURRENT_CONTEXT.get();
        if (context != null) {
            /* Can happen when calling setCapabilities(null) after glfwDestroyWindow()/glfwMakeContextCurrent(0L) */
            context.caps = caps;
        }
    }

}
