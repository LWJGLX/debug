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
import java.util.Iterator;
import java.util.Map;

import org.lwjglx.debug.Context;
import org.lwjglx.debug.Context.TextureLayer;
import org.lwjglx.debug.Context.TextureObject;
import org.lwjglx.debug.GLmetadata;
import org.lwjglx.debug.MethodCall;
import org.lwjglx.debug.Properties;
import org.lwjglx.debug.RT;

public class GL11 {

    public static void glClear(int mask) {
        if (Properties.VALIDATE.enabled) {
            checkFramebufferCompleteness();
        }
        org.lwjgl.opengl.GL11.glClear(mask);
    }

    public static void glEnableClientState(int cap) {
        if (Properties.VALIDATE.enabled) {
            switch (cap) {
            case org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY:
                CURRENT_CONTEXT.get().currentVao.vertexArrayEnabled = true;
                break;
            case org.lwjgl.opengl.GL11.GL_NORMAL_ARRAY:
                CURRENT_CONTEXT.get().currentVao.normalArrayEnabled = true;
                break;
            case org.lwjgl.opengl.GL11.GL_COLOR_ARRAY:
                CURRENT_CONTEXT.get().currentVao.colorArrayEnabled = true;
                break;
            case org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY:
                CURRENT_CONTEXT.get().currentVao.texCoordArrayEnabled = true;
                break;
            }
        }
        org.lwjgl.opengl.GL11.glEnableClientState(cap);
    }

    public static void glDisableClientState(int cap) {
        if (Properties.VALIDATE.enabled) {
            switch (cap) {
            case org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY:
                CURRENT_CONTEXT.get().currentVao.vertexArrayEnabled = false;
                break;
            case org.lwjgl.opengl.GL11.GL_NORMAL_ARRAY:
                CURRENT_CONTEXT.get().currentVao.normalArrayEnabled = false;
                break;
            case org.lwjgl.opengl.GL11.GL_COLOR_ARRAY:
                CURRENT_CONTEXT.get().currentVao.colorArrayEnabled = false;
                break;
            case org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY:
                CURRENT_CONTEXT.get().currentVao.texCoordArrayEnabled = false;
                break;
            }
        }
        org.lwjgl.opengl.GL11.glDisableClientState(cap);
    }

    public static void glVertexPointer(int size, int type, int stride, ByteBuffer pointer) {
        if (Properties.VALIDATE.enabled) {
            CURRENT_CONTEXT.get().currentVao.vertexArrayInitialized = pointer != null;
        }
        org.lwjgl.opengl.GL11.glVertexPointer(size, type, stride, pointer);
    }

    public static void glVertexPointer(int size, int type, int stride, long pointer) {
        if (Properties.VALIDATE.enabled) {
            int vbo = org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER_BINDING);
            if (vbo != 0) {
                CURRENT_CONTEXT.get().currentVao.vertexArrayInitialized = true;
            }
        }
        org.lwjgl.opengl.GL11.glVertexPointer(size, type, stride, pointer);
    }

    public static void glVertexPointer(int size, int type, int stride, ShortBuffer pointer) {
        if (Properties.VALIDATE.enabled) {
            CURRENT_CONTEXT.get().currentVao.vertexArrayInitialized = pointer != null;
        }
        org.lwjgl.opengl.GL11.glVertexPointer(size, type, stride, pointer);
    }

    public static void glVertexPointer(int size, int type, int stride, IntBuffer pointer) {
        if (Properties.VALIDATE.enabled) {
            CURRENT_CONTEXT.get().currentVao.vertexArrayInitialized = pointer != null;
        }
        org.lwjgl.opengl.GL11.glVertexPointer(size, type, stride, pointer);
    }

    public static void glVertexPointer(int size, int type, int stride, FloatBuffer pointer) {
        if (Properties.VALIDATE.enabled) {
            CURRENT_CONTEXT.get().currentVao.vertexArrayInitialized = pointer != null;
        }
        org.lwjgl.opengl.GL11.glVertexPointer(size, type, stride, pointer);
    }

    public static void glNormalPointer(int type, int stride, ByteBuffer pointer) {
        if (Properties.VALIDATE.enabled) {
            CURRENT_CONTEXT.get().currentVao.vertexArrayInitialized = pointer != null;
        }
        org.lwjgl.opengl.GL11.glNormalPointer(type, stride, pointer);
    }

    public static void glNormalPointer(int type, int stride, long pointer) {
        if (Properties.VALIDATE.enabled) {
            int vbo = org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER_BINDING);
            if (vbo != 0) {
                CURRENT_CONTEXT.get().currentVao.normalArrayInitialized = true;
            }
        }
        org.lwjgl.opengl.GL11.glNormalPointer(type, stride, pointer);
    }

    public static void glNormalPointer(int type, int stride, ShortBuffer pointer) {
        if (Properties.VALIDATE.enabled) {
            CURRENT_CONTEXT.get().currentVao.normalArrayInitialized = pointer != null;
        }
        org.lwjgl.opengl.GL11.glNormalPointer(type, stride, pointer);
    }

    public static void glNormalPointer(int type, int stride, IntBuffer pointer) {
        if (Properties.VALIDATE.enabled) {
            CURRENT_CONTEXT.get().currentVao.normalArrayInitialized = pointer != null;
        }
        org.lwjgl.opengl.GL11.glNormalPointer(type, stride, pointer);
    }

    public static void glNormalPointer(int type, int stride, FloatBuffer pointer) {
        if (Properties.VALIDATE.enabled) {
            CURRENT_CONTEXT.get().currentVao.normalArrayInitialized = pointer != null;
        }
        org.lwjgl.opengl.GL11.glNormalPointer(type, stride, pointer);
    }

    public static void glColorPointer(int size, int type, int stride, ByteBuffer pointer) {
        if (Properties.VALIDATE.enabled) {
            CURRENT_CONTEXT.get().currentVao.normalArrayInitialized = pointer != null;
        }
        org.lwjgl.opengl.GL11.glColorPointer(size, type, stride, pointer);
    }

    public static void glColorPointer(int size, int type, int stride, long pointer) {
        if (Properties.VALIDATE.enabled) {
            int vbo = org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER_BINDING);
            if (vbo != 0) {
                CURRENT_CONTEXT.get().currentVao.colorArrayInitialized = true;
            }
        }
        org.lwjgl.opengl.GL11.glColorPointer(size, type, stride, pointer);
    }

    public static void glColorPointer(int size, int type, int stride, ShortBuffer pointer) {
        if (Properties.VALIDATE.enabled) {
            CURRENT_CONTEXT.get().currentVao.colorArrayInitialized = pointer != null;
        }
        org.lwjgl.opengl.GL11.glColorPointer(size, type, stride, pointer);
    }

    public static void glColorPointer(int size, int type, int stride, IntBuffer pointer) {
        if (Properties.VALIDATE.enabled) {
            CURRENT_CONTEXT.get().currentVao.colorArrayInitialized = pointer != null;
        }
        org.lwjgl.opengl.GL11.glColorPointer(size, type, stride, pointer);
    }

    public static void glColorPointer(int size, int type, int stride, FloatBuffer pointer) {
        if (Properties.VALIDATE.enabled) {
            CURRENT_CONTEXT.get().currentVao.colorArrayInitialized = pointer != null;
        }
        org.lwjgl.opengl.GL11.glColorPointer(size, type, stride, pointer);
    }

    public static void glTexCoordPointer(int size, int type, int stride, ByteBuffer pointer) {
        if (Properties.VALIDATE.enabled) {
            CURRENT_CONTEXT.get().currentVao.texCoordArrayInitialized = pointer != null;
        }
        org.lwjgl.opengl.GL11.glTexCoordPointer(size, type, stride, pointer);
    }

    public static void glTexCoordPointer(int size, int type, int stride, long pointer) {
        if (Properties.VALIDATE.enabled) {
            int vbo = org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER_BINDING);
            if (vbo != 0) {
                CURRENT_CONTEXT.get().currentVao.texCoordArrayInitialized = true;
            }
        }
        org.lwjgl.opengl.GL11.glTexCoordPointer(size, type, stride, pointer);
    }

    public static void glTexCoordPointer(int size, int type, int stride, ShortBuffer pointer) {
        if (Properties.VALIDATE.enabled) {
            CURRENT_CONTEXT.get().currentVao.texCoordArrayInitialized = pointer != null;
        }
        org.lwjgl.opengl.GL11.glTexCoordPointer(size, type, stride, pointer);
    }

    public static void glTexCoordPointer(int size, int type, int stride, IntBuffer pointer) {
        if (Properties.VALIDATE.enabled) {
            CURRENT_CONTEXT.get().currentVao.texCoordArrayInitialized = pointer != null;
        }
        org.lwjgl.opengl.GL11.glTexCoordPointer(size, type, stride, pointer);
    }

    public static void glTexCoordPointer(int size, int type, int stride, FloatBuffer pointer) {
        if (Properties.VALIDATE.enabled) {
            CURRENT_CONTEXT.get().currentVao.texCoordArrayInitialized = pointer != null;
        }
        org.lwjgl.opengl.GL11.glTexCoordPointer(size, type, stride, pointer);
    }

    public static int glGetInteger(int pname) {
        if (Properties.PROFILE.enabled && pname == org.lwjgl.opengl.GL30.GL_NUM_EXTENSIONS) {
            int numExtensions = org.lwjgl.opengl.GL11.glGetInteger(pname);
            return numExtensions + 2; // <- for GREMEDY_string_marker and GREMEDY_frame_terminator
        }
        return org.lwjgl.opengl.GL11.glGetInteger(pname);
    }

    public static void glGetIntegerv(int pname, IntBuffer params) {
        if (Properties.PROFILE.enabled && pname == org.lwjgl.opengl.GL30.GL_NUM_EXTENSIONS) {
            int numExtensions = org.lwjgl.opengl.GL11.glGetInteger(pname);
            params.put(params.position(), numExtensions + 2); // <- for GREMEDY_string_marker and GREMEDY_frame_terminator
            return;
        }
        org.lwjgl.opengl.GL11.glGetIntegerv(pname, params);
    }

    public static void glGetIntegerv(int pname, int[] params) {
        if (Properties.PROFILE.enabled && pname == org.lwjgl.opengl.GL30.GL_NUM_EXTENSIONS) {
            int numExtensions = org.lwjgl.opengl.GL11.glGetInteger(pname);
            params[0] = numExtensions + 2; // <- for GREMEDY_string_marker and GREMEDY_frame_terminator
            return;
        }
        org.lwjgl.opengl.GL11.glGetIntegerv(pname, params);
    }

    public static String glGetString(int name) {
        String res = org.lwjgl.opengl.GL11.glGetString(name);
        if (name == org.lwjgl.opengl.GL11.GL_EXTENSIONS && Properties.PROFILE.enabled) {
            /* Also advertize GL_GREMEDY_string_marker and GL_GREMEDY_frame_terminator */
            if (res != null) {
                if (!res.endsWith(" "))
                    res += " ";
            } else {
                res = "";
            }
            res += "GL_GREMEDY_string_marker GL_GREMEDY_frame_terminator ";
        }
        return res;
    }

    public static void glVertex2f(float x, float y) {
        org.lwjgl.opengl.GL11.glVertex2f(x, y);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex2s(short x, short y) {
        org.lwjgl.opengl.GL11.glVertex2s(x, y);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex2i(int x, int y) {
        org.lwjgl.opengl.GL11.glVertex2i(x, y);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex2d(double x, double y) {
        org.lwjgl.opengl.GL11.glVertex2d(x, y);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex2fv(FloatBuffer coords) {
        org.lwjgl.opengl.GL11.glVertex2fv(coords);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex2fv(float[] coords) {
        org.lwjgl.opengl.GL11.glVertex2fv(coords);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex2fs(ShortBuffer coords) {
        org.lwjgl.opengl.GL11.glVertex2sv(coords);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex2fs(short[] coords) {
        org.lwjgl.opengl.GL11.glVertex2sv(coords);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex2fi(IntBuffer coords) {
        org.lwjgl.opengl.GL11.glVertex2iv(coords);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex2fi(int[] coords) {
        org.lwjgl.opengl.GL11.glVertex2iv(coords);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex2fd(DoubleBuffer coords) {
        org.lwjgl.opengl.GL11.glVertex2dv(coords);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex2fd(double[] coords) {
        org.lwjgl.opengl.GL11.glVertex2dv(coords);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex3f(float x, float y, float z) {
        org.lwjgl.opengl.GL11.glVertex3f(x, y, z);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex3s(short x, short y, short z) {
        org.lwjgl.opengl.GL11.glVertex3s(x, y, z);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex3i(int x, int y, int z) {
        org.lwjgl.opengl.GL11.glVertex3i(x, y, z);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex3d(double x, double y, double z) {
        org.lwjgl.opengl.GL11.glVertex3d(x, y, z);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex3fv(FloatBuffer coords) {
        org.lwjgl.opengl.GL11.glVertex3fv(coords);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex3fv(float[] coords) {
        org.lwjgl.opengl.GL11.glVertex3fv(coords);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex3fs(ShortBuffer coords) {
        org.lwjgl.opengl.GL11.glVertex3sv(coords);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex3fs(short[] coords) {
        org.lwjgl.opengl.GL11.glVertex3sv(coords);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex3fi(IntBuffer coords) {
        org.lwjgl.opengl.GL11.glVertex3iv(coords);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex3fi(int[] coords) {
        org.lwjgl.opengl.GL11.glVertex3iv(coords);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex3fd(DoubleBuffer coords) {
        org.lwjgl.opengl.GL11.glVertex3dv(coords);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex3fd(double[] coords) {
        org.lwjgl.opengl.GL11.glVertex3dv(coords);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex4f(float x, float y, float z, float w) {
        org.lwjgl.opengl.GL11.glVertex4f(x, y, z, w);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex4f(short x, short y, short z, short w) {
        org.lwjgl.opengl.GL11.glVertex4s(x, y, z, w);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex4f(int x, int y, int z, int w) {
        org.lwjgl.opengl.GL11.glVertex4i(x, y, z, w);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex4f(double x, double y, double z, double w) {
        org.lwjgl.opengl.GL11.glVertex4d(x, y, z, w);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex4fv(FloatBuffer coords) {
        org.lwjgl.opengl.GL11.glVertex4fv(coords);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex4fv(float[] coords) {
        org.lwjgl.opengl.GL11.glVertex4fv(coords);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex4fs(ShortBuffer coords) {
        org.lwjgl.opengl.GL11.glVertex4sv(coords);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex4fs(short[] coords) {
        org.lwjgl.opengl.GL11.glVertex4sv(coords);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex4fi(IntBuffer coords) {
        org.lwjgl.opengl.GL11.glVertex4iv(coords);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex4fi(int[] coords) {
        org.lwjgl.opengl.GL11.glVertex4iv(coords);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex4fd(DoubleBuffer coords) {
        org.lwjgl.opengl.GL11.glVertex4dv(coords);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glVertex4fd(double[] coords) {
        org.lwjgl.opengl.GL11.glVertex4dv(coords);
        if (Properties.PROFILE.enabled) {
            RT.vertex();
        }
    }

    public static void glDrawArrays(int mode, int first, int count) {
        if (Properties.VALIDATE.enabled) {
            checkBeforeDrawCall();
        }
        if (Properties.PROFILE.enabled) {
            RT.beforeDraw();
        }
        org.lwjgl.opengl.GL11.glDrawArrays(mode, first, count);
        RT.draw(count);
    }

    public static void glDrawElements(int mode, int count, int type, long indices) {
        if (Properties.VALIDATE.enabled) {
            int ibo = org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING);
            if (ibo == 0) {
                throwISEOrLogError("glDrawElements called with index offset but no ELEMENT_ARRAY_BUFFER bound");
            }
            checkBeforeDrawCall();
        }
        if (Properties.PROFILE.enabled) {
            RT.beforeDraw();
        }
        org.lwjgl.opengl.GL11.glDrawElements(mode, count, type, indices);
        RT.draw(count);
    }

    public static void glDrawElements(int mode, int type, ByteBuffer indices) {
        if (Properties.VALIDATE.enabled) {
            checkBeforeDrawCall();
        }
        if (Properties.PROFILE.enabled) {
            RT.beforeDraw();
        }
        org.lwjgl.opengl.GL11.glDrawElements(mode, type, indices);
        RT.draw(indices.remaining());
    }

    public static void glDrawElements(int mode, ByteBuffer indices) {
        if (Properties.VALIDATE.enabled) {
            checkBeforeDrawCall();
        }
        if (Properties.PROFILE.enabled) {
            RT.beforeDraw();
        }
        org.lwjgl.opengl.GL11.glDrawElements(mode, indices);
        RT.draw(indices.remaining());
    }

    public static void glDrawElements(int mode, ShortBuffer indices) {
        if (Properties.VALIDATE.enabled) {
            checkBeforeDrawCall();
        }
        if (Properties.PROFILE.enabled) {
            RT.beforeDraw();
        }
        org.lwjgl.opengl.GL11.glDrawElements(mode, indices);
        RT.draw(indices.remaining());
    }

    public static void glDrawElements(int mode, IntBuffer indices) {
        if (Properties.VALIDATE.enabled) {
            checkBeforeDrawCall();
        }
        if (Properties.PROFILE.enabled) {
            RT.beforeDraw();
        }
        org.lwjgl.opengl.GL11.glDrawElements(mode, indices);
        RT.draw(indices.remaining());
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

    public static void glGenTextures(IntBuffer textures) {
        org.lwjgl.opengl.GL11.glGenTextures(textures);
        Context ctx = CURRENT_CONTEXT.get();
        int pos = textures.position();
        for (int i = 0; i < textures.remaining(); i++) {
            int texture = textures.get(pos + i);
            ctx.textureObjects.put(texture, new TextureObject());
        }
    }

    public static void glGenTextures(int[] textures) {
        org.lwjgl.opengl.GL11.glGenTextures(textures);
        Context ctx = CURRENT_CONTEXT.get();
        for (int i = 0; i < textures.length; i++) {
            int texture = textures[i];
            ctx.textureObjects.put(texture, new TextureObject());
        }
    }

    public static int glGenTextures() {
        int tex = org.lwjgl.opengl.GL11.glGenTextures();
        Context ctx = CURRENT_CONTEXT.get();
        ctx.textureObjects.put(tex, new TextureObject());
        return tex;
    }

    private static void assignLayers(int target, TextureObject to) {
        if (to.layers == null) {
            switch (target) {
            case org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP:
                to.layers = new TextureLayer[6];
                break;
            default:
                to.layers = new TextureLayer[1];
                break;
            }
            for (int i = 0; i < to.layers.length; i++) {
                to.layers[i] = new TextureLayer();
            }
        }
    }

    public static void glBindTexture(int target, int texture) {
        org.lwjgl.opengl.GL11.glBindTexture(target, texture);
        if (Properties.PROFILE.enabled) {
            Context ctx = CURRENT_CONTEXT.get();
            if (texture != 0) {
                TextureObject to = ctx.textureObjects.get(texture);
                assignLayers(target, to);
                ctx.textureObjectBindings.put(target, to);
            } else {
                ctx.textureObjectBindings.remove(target);
            }
        }
    }

    public static void glDeleteTextures(IntBuffer textures) {
        org.lwjgl.opengl.GL11.glDeleteTextures(textures);
        if (Properties.PROFILE.enabled) {
            Context ctx = CURRENT_CONTEXT.get();
            int pos = textures.position();
            for (int i = 0; i < textures.remaining(); i++) {
                int buffer = textures.get(pos + i);
                TextureObject to = ctx.textureObjects.remove(buffer);
                Iterator<Map.Entry<Integer, TextureObject>> it = ctx.textureObjectBindings.entrySet().iterator();
                while (it.hasNext()) {
                    if (it.next().getValue() == to) {
                        it.remove();
                    }
                }
            }
        }
    }

    public static void glDeleteTextures(int[] textures) {
        org.lwjgl.opengl.GL11.glDeleteTextures(textures);
        if (Properties.PROFILE.enabled) {
            Context ctx = CURRENT_CONTEXT.get();
            for (int i = 0; i < textures.length; i++) {
                int buffer = textures[i];
                TextureObject to = ctx.textureObjects.remove(buffer);
                Iterator<Map.Entry<Integer, TextureObject>> it = ctx.textureObjectBindings.entrySet().iterator();
                while (it.hasNext()) {
                    if (it.next().getValue() == to) {
                        it.remove();
                    }
                }
            }
        }
    }

    public static void glDeleteTextures(int texture) {
        org.lwjgl.opengl.GL11.glDeleteTextures(texture);
        if (Properties.PROFILE.enabled) {
            Context ctx = CURRENT_CONTEXT.get();
            TextureObject to = ctx.textureObjects.remove(texture);
            Iterator<Map.Entry<Integer, TextureObject>> it = ctx.textureObjectBindings.entrySet().iterator();
            while (it.hasNext()) {
                if (it.next().getValue() == to) {
                    it.remove();
                }
            }
        }
    }

    private static void glTexImage2D_trace(int target, int level, int internalformat, int width, int height, int border, int format, int type, Object pixelsOrSize, MethodCall mc) {
        mc.paramEnum(GLmetadata.TextureTarget().get(target));
        mc.param(level);
        if (internalformat >= 1 && internalformat <= 4)
            mc.param(internalformat);
        else
            mc.paramEnum(RT.glEnumFor(internalformat, GLmetadata.InternalFormat()));
        mc.param(width);
        mc.param(height);
        mc.param(border);
        mc.paramEnum(RT.glEnumFor(format, GLmetadata.PixelFormat()));
        mc.paramEnum(RT.glEnumFor(type, GLmetadata.PixelType()));
        mc.param(pixelsOrSize);
    }

    public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, long size, Void ret, MethodCall mc) {
        glTexImage2D_trace(target, level, internalformat, width, height, border, format, type, size, mc);
    }

    public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ByteBuffer pixels, Void ret, MethodCall mc) {
        glTexImage2D_trace(target, level, internalformat, width, height, border, format, type, pixels, mc);
    }

    public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ShortBuffer pixels, Void ret, MethodCall mc) {
        glTexImage2D_trace(target, level, internalformat, width, height, border, format, type, pixels, mc);
    }

    public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, IntBuffer pixels, Void ret, MethodCall mc) {
        glTexImage2D_trace(target, level, internalformat, width, height, border, format, type, pixels, mc);
    }

    public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, FloatBuffer pixels, Void ret, MethodCall mc) {
        glTexImage2D_trace(target, level, internalformat, width, height, border, format, type, pixels, mc);
    }

    public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, DoubleBuffer pixels, Void ret, MethodCall mc) {
        glTexImage2D_trace(target, level, internalformat, width, height, border, format, type, pixels, mc);
    }

    private static void profileTexture2D(int target, int level, int internalformat, int width, int height) {
        Context ctx = CURRENT_CONTEXT.get();
        int boundTarget = target;
        if (target >= org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X && target <= org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z) {
            boundTarget = org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
        }
        TextureObject to = ctx.textureObjectBindings.get(boundTarget);
        if (to != null) {
            setTextureLayerSize(target, level, internalformat, width, height, to);
            /* See whether mipmap levels should be automatically generated */
            boolean generateMipmaps = ctx.caps.OpenGL14 && org.lwjgl.opengl.GL11.glGetTexParameteri(boundTarget, org.lwjgl.opengl.GL14.GL_GENERATE_MIPMAP) == 1;
            if (generateMipmaps) {
                /* Determine maximum mipmap level */
                int maxLevel = ctx.caps.OpenGL12 ? org.lwjgl.opengl.GL11.glGetTexParameteri(boundTarget, org.lwjgl.opengl.GL12.GL_TEXTURE_MAX_LEVEL) : 1000;
                /* Set the size of all mipmap levels */
                while (width > 1 || height > 1 && level < maxLevel) {
                    width >>>= 1;
                    height >>>= 1;
                    level++;
                    setTextureLayerSize(target, level, internalformat, width, height, to);
                }
            }
        }
    }

    public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, long size) {
        org.lwjgl.opengl.GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, size);
        if (Properties.PROFILE.enabled) {
            profileTexture2D(target, level, internalformat, width, height);
        }
    }

    public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ByteBuffer pixels) {
        org.lwjgl.opengl.GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
        if (Properties.PROFILE.enabled) {
            profileTexture2D(target, level, internalformat, width, height);
        }
    }

    public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ShortBuffer pixels) {
        org.lwjgl.opengl.GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
        if (Properties.PROFILE.enabled) {
            profileTexture2D(target, level, internalformat, width, height);
        }
    }

    public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, IntBuffer pixels) {
        org.lwjgl.opengl.GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
        if (Properties.PROFILE.enabled) {
            profileTexture2D(target, level, internalformat, width, height);
        }
    }

    public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, FloatBuffer pixels) {
        org.lwjgl.opengl.GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
        if (Properties.PROFILE.enabled) {
            profileTexture2D(target, level, internalformat, width, height);
        }
    }

    public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, DoubleBuffer pixels) {
        org.lwjgl.opengl.GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
        if (Properties.PROFILE.enabled) {
            profileTexture2D(target, level, internalformat, width, height);
        }
    }

    private static void glTexImage1D_trace(int target, int level, int internalformat, int width, int border, int format, int type, Buffer pixels, MethodCall mc) {
        mc.paramEnum(GLmetadata.TextureTarget().get(target));
        mc.param(level);
        if (internalformat >= 1 && internalformat <= 4)
            mc.param(internalformat);
        else
            mc.paramEnum(RT.glEnumFor(internalformat, GLmetadata.InternalFormat()));
        mc.param(width);
        mc.param(border);
        mc.paramEnum(RT.glEnumFor(format, GLmetadata.PixelFormat()));
        mc.paramEnum(RT.glEnumFor(type, GLmetadata.PixelType()));
        mc.param(pixels);
    }

    public static void glTexImage1D(int target, int level, int internalformat, int width, int border, int format, int type, ByteBuffer pixels, Void ret, MethodCall mc) {
        glTexImage1D_trace(target, level, internalformat, width, border, format, type, pixels, mc);
    }

    public static void glTexImage1D(int target, int level, int internalformat, int width, int border, int format, int type, ShortBuffer pixels, Void ret, MethodCall mc) {
        glTexImage1D_trace(target, level, internalformat, width, border, format, type, pixels, mc);
    }

    public static void glTexImage1D(int target, int level, int internalformat, int width, int border, int format, int type, IntBuffer pixels, Void ret, MethodCall mc) {
        glTexImage1D_trace(target, level, internalformat, width, border, format, type, pixels, mc);
    }

    public static void glTexImage1D(int target, int level, int internalformat, int width, int border, int format, int type, FloatBuffer pixels, Void ret, MethodCall mc) {
        glTexImage1D_trace(target, level, internalformat, width, border, format, type, pixels, mc);
    }

    public static void glTexImage1D(int target, int level, int internalformat, int width, int border, int format, int type, DoubleBuffer pixels, Void ret, MethodCall mc) {
        glTexImage1D_trace(target, level, internalformat, width, border, format, type, pixels, mc);
    }

    private static void glTexParameter_trace(int target, int pname, int param, MethodCall mc) {
        switch (pname) {
        case org.lwjgl.opengl.GL43.GL_DEPTH_STENCIL_TEXTURE_MODE:
            mc.paramEnum(RT.glEnumFor(param, GLmetadata.PixelFormat()));
            break;
        case org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER:
            mc.paramEnum(RT.glEnumFor(param, GLmetadata.TextureMinFilter()));
            break;
        case org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER:
            mc.paramEnum(RT.glEnumFor(param, GLmetadata.TextureMagFilter()));
            break;
        case org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_FUNC:
            mc.paramEnum(RT.glEnumFor(param, GLmetadata.AlphaFunction()));
            break;
        case org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_MODE:
            mc.paramEnum(GLmetadata._null_().get(param));
            break;
        case org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S:
            mc.paramEnum(RT.glEnumFor(param, GLmetadata.TextureWrapMode()));
            break;
        case org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T:
            mc.paramEnum(RT.glEnumFor(param, GLmetadata.TextureWrapMode()));
            break;
        case org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R:
            mc.paramEnum(RT.glEnumFor(param, GLmetadata.TextureWrapMode()));
            break;
        case org.lwjgl.opengl.GL14.GL_GENERATE_MIPMAP:
            mc.paramEnum(GLmetadata.Boolean().get(param));
            break;
        default:
            mc.param(param);
            break;
        }
    }

    public static void glTexParameteri(int target, int pname, int param, Void ret, MethodCall mc) {
        mc.paramEnum(RT.glEnumFor(target, GLmetadata.TextureTarget()));
        mc.paramEnum(RT.glEnumFor(pname, GLmetadata.TextureParameterName()));
        glTexParameter_trace(target, pname, param, mc);
    }

    public static void glBegin(int mode) {
        RT.beginImmediate();
        org.lwjgl.opengl.GL11.glBegin(mode);
    }

    public static void glEnd() {
        org.lwjgl.opengl.GL11.glEnd();
        RT.endImmediate();
    }

}
