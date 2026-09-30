package org.lwjglx.debug.org.lwjgl.opengl;

import org.lwjglx.debug.Properties;

public class ARBTextureBufferRange {

    public static void glTexBufferRange(int target, int internalformat, int buffer, long offset, long size) {
        if (Properties.VALIDATE.enabled && buffer != 0) {
            Context ctx = Context.currentContext();
            ctx.shareGroup.bufferObjects.checkAlive(buffer, "glTexBufferRange");
        }
        org.lwjgl.opengl.ARBTextureBufferRange.glTexBufferRange(target, internalformat, buffer, offset, size);
    }

    public static void glTextureBufferRangeEXT(int texture, int target, int internalformat, int buffer, long offset, long size) {
        if (Properties.VALIDATE.enabled) {
            Context ctx = Context.currentContext();
            if (texture != 0) {
                ctx.shareGroup.textureObjects.checkAlive(texture, "glTextureBufferRangeEXT");
            }
            if (buffer != 0) {
                ctx.shareGroup.bufferObjects.checkAlive(buffer, "glTextureBufferRangeEXT");
            }
        }
        org.lwjgl.opengl.ARBTextureBufferRange.glTextureBufferRangeEXT(texture, target, internalformat, buffer, offset, size);
    }

}
