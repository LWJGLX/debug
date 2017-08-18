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

import org.lwjglx.debug.Properties;
import org.lwjglx.debug.RT;

public class GL32 {

    public static void glDrawElementsBaseVertex(int mode, int count, int type, long indices, int basevertex) {
        if (Properties.VALIDATE.enabled) {
            int ibo = org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING);
            if (ibo == 0) {
                throwISEOrLogError("glDrawElementsBaseVertex called with index offset but no ELEMENT_ARRAY_BUFFER bound");
            }
            checkVertexAttributes();
        }
        org.lwjgl.opengl.GL32.glDrawElementsBaseVertex(mode, count, type, indices, basevertex);
        if (Properties.PROFILE.enabled) {
            RT.draw(count);
        }
    }

    public static void glDrawElementsBaseVertex(int mode, int type, ByteBuffer indices, int basevertex) {
        if (Properties.VALIDATE.enabled) {
            checkVertexAttributes();
        }
        org.lwjgl.opengl.GL32.glDrawElementsBaseVertex(mode, type, indices, basevertex);
        if (Properties.PROFILE.enabled) {
            RT.draw(indices.remaining());
        }
    }

    public static void glDrawElementsBaseVertex(int mode, ByteBuffer indices, int basevertex) {
        if (Properties.VALIDATE.enabled) {
            checkVertexAttributes();
        }
        org.lwjgl.opengl.GL32.glDrawElementsBaseVertex(mode, indices, basevertex);
        if (Properties.PROFILE.enabled) {
            RT.draw(indices.remaining());
        }
    }

    public static void glDrawElementsBaseVertex(int mode, ShortBuffer indices, int basevertex) {
        if (Properties.VALIDATE.enabled) {
            checkVertexAttributes();
        }
        org.lwjgl.opengl.GL32.glDrawElementsBaseVertex(mode, indices, basevertex);
        if (Properties.PROFILE.enabled) {
            RT.draw(indices.remaining());
        }
    }

    public static void glDrawElementsBaseVertex(int mode, IntBuffer indices, int basevertex) {
        if (Properties.VALIDATE.enabled) {
            checkVertexAttributes();
        }
        org.lwjgl.opengl.GL32.glDrawElementsBaseVertex(mode, indices, basevertex);
        if (Properties.PROFILE.enabled) {
            RT.draw(indices.remaining());
        }
    }

    public static void glDrawRangeElementsBaseVertex(int mode, int start, int end, int count, int type, long indices, int basevertex) {
        if (Properties.VALIDATE.enabled) {
            int ibo = org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING);
            if (ibo == 0) {
                throwISEOrLogError("glDrawRangeElementsBaseVertex called with index offset but no ELEMENT_ARRAY_BUFFER bound");
            }
            checkVertexAttributes();
        }
        org.lwjgl.opengl.GL32.glDrawRangeElementsBaseVertex(mode, start, end, count, type, indices, basevertex);
        if (Properties.PROFILE.enabled) {
            RT.draw(count);
        }
    }

    public static void glDrawRangeElementsBaseVertex(int mode, int start, int end, int type, ByteBuffer indices, int basevertex) {
        if (Properties.VALIDATE.enabled) {
            checkVertexAttributes();
        }
        org.lwjgl.opengl.GL32.glDrawRangeElementsBaseVertex(mode, start, end, type, indices, basevertex);
        if (Properties.PROFILE.enabled) {
            RT.draw(indices.remaining());
        }
    }

    public static void glDrawRangeElementsBaseVertex(int mode, int start, int end, ByteBuffer indices, int basevertex) {
        if (Properties.VALIDATE.enabled) {
            checkVertexAttributes();
        }
        org.lwjgl.opengl.GL32.glDrawRangeElementsBaseVertex(mode, start, end, indices, basevertex);
        if (Properties.PROFILE.enabled) {
            RT.draw(indices.remaining());
        }
    }

    public static void glDrawRangeElementsBaseVertex(int mode, int start, int end, ShortBuffer indices, int basevertex) {
        if (Properties.VALIDATE.enabled) {
            checkVertexAttributes();
        }
        org.lwjgl.opengl.GL32.glDrawRangeElementsBaseVertex(mode, start, end, indices, basevertex);
        if (Properties.PROFILE.enabled) {
            RT.draw(indices.remaining());
        }
    }

    public static void glDrawRangeElementsBaseVertex(int mode, int start, int end, IntBuffer indices, int basevertex) {
        if (Properties.VALIDATE.enabled) {
            checkVertexAttributes();
        }
        org.lwjgl.opengl.GL32.glDrawRangeElementsBaseVertex(mode, start, end, indices, basevertex);
        if (Properties.PROFILE.enabled) {
            RT.draw(indices.remaining());
        }
    }

    public static void glDrawElementsInstancedBaseVertex(int mode, int count, int type, long indices, int primcount, int basevertex) {
        if (Properties.VALIDATE.enabled) {
            int ibo = org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING);
            if (ibo == 0) {
                throwISEOrLogError("glDrawElementsInstancedBaseVertex called with index offset but no ELEMENT_ARRAY_BUFFER bound");
            }
            checkVertexAttributes();
        }
        org.lwjgl.opengl.GL32.glDrawElementsInstancedBaseVertex(mode, count, type, indices, primcount, basevertex);
        if (Properties.PROFILE.enabled) {
            RT.draw(count * primcount);
        }
    }

    public static void glDrawElementsInstancedBaseVertex(int mode, int type, ByteBuffer indices, int primcount, int basevertex) {
        if (Properties.VALIDATE.enabled) {
            checkVertexAttributes();
        }
        org.lwjgl.opengl.GL32.glDrawElementsInstancedBaseVertex(mode, type, indices, primcount, basevertex);
        if (Properties.PROFILE.enabled) {
            RT.draw(indices.remaining() * primcount);
        }
    }

    public static void glDrawElementsInstancedBaseVertex(int mode, ByteBuffer indices, int primcount, int basevertex) {
        if (Properties.VALIDATE.enabled) {
            checkVertexAttributes();
        }
        org.lwjgl.opengl.GL32.glDrawElementsInstancedBaseVertex(mode, indices, primcount, basevertex);
        if (Properties.PROFILE.enabled) {
            RT.draw(indices.remaining() * primcount);
        }
    }

    public static void glDrawElementsInstancedBaseVertex(int mode, ShortBuffer indices, int primcount, int basevertex) {
        if (Properties.VALIDATE.enabled) {
            checkVertexAttributes();
        }
        org.lwjgl.opengl.GL32.glDrawElementsInstancedBaseVertex(mode, indices, primcount, basevertex);
        if (Properties.PROFILE.enabled) {
            RT.draw(indices.remaining() * primcount);
        }
    }

    public static void glDrawElementsInstancedBaseVertex(int mode, IntBuffer indices, int primcount, int basevertex) {
        if (Properties.VALIDATE.enabled) {
            checkVertexAttributes();
        }
        org.lwjgl.opengl.GL32.glDrawElementsInstancedBaseVertex(mode, indices, primcount, basevertex);
        if (Properties.PROFILE.enabled) {
            RT.draw(indices.remaining() * primcount);
        }
    }

}
