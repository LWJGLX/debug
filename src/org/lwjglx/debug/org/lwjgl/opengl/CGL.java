package org.lwjglx.debug.org.lwjgl.opengl;

import static org.lwjglx.debug.org.lwjgl.opengl.Context.*;

public class CGL {

    public static int CGLSetCurrentContext(long context) {
        int result = org.lwjgl.opengl.CGL.CGLSetCurrentContext(context);
        if (result == org.lwjgl.opengl.CGL.kCGLNoError) {
            if (context == 0L) {
                CURRENT_CONTEXT.remove();
            } else {
                Context ctx = CONTEXTS.get(context);
                if (ctx == null) {
                    Context.create(context, 0L);
                    ctx = CONTEXTS.get(context);
                }
                CURRENT_CONTEXT.set(ctx);
            }
        }
        return result;
    }
}
