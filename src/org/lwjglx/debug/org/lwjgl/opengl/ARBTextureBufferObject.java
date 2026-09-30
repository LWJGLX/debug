package org.lwjglx.debug.org.lwjgl.opengl;

import org.lwjglx.debug.Properties;

public class ARBTextureBufferObject {

    public static void glTexBufferARB(int target, int internalformat, int buffer) {
        if (Properties.VALIDATE.enabled && buffer != 0) {
            Context ctx = Context.currentContext();
            ctx.shareGroup.bufferObjects.checkAlive(buffer, "glTexBufferARB");
        }
        org.lwjgl.opengl.ARBTextureBufferObject.glTexBufferARB(target, internalformat, buffer);
    }

}
