package org.lwjglx.debug.org.lwjgl.opengl;

import org.lwjglx.debug.Properties;

public class GL43 {

    public static void glTexBufferRange(int target, int internalformat, int buffer, long offset, long size) {
        if (Properties.VALIDATE.enabled && buffer != 0) {
            Context ctx = Context.currentContext();
            ctx.shareGroup.bufferObjects.checkAlive(buffer, "glTexBufferRange");
        }
        org.lwjgl.opengl.GL43.glTexBufferRange(target, internalformat, buffer, offset, size);
    }

}
