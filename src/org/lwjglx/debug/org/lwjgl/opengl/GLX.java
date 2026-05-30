package org.lwjglx.debug.org.lwjgl.opengl;

import static org.lwjglx.debug.org.lwjgl.opengl.Context.*;

public class GLX {

    public static boolean glXMakeCurrent(long display, long drawable, long ctx) {
        boolean result = org.lwjgl.opengl.GLX.glXMakeCurrent(display, drawable, ctx);
        if (result) {
            if (ctx == 0L) {
                CURRENT_CONTEXT.remove();
            } else {
                Context context = CONTEXTS.get(ctx);
                if (context == null) {
                    Context.create(ctx, 0L);
                    context = CONTEXTS.get(ctx);
                }
                CURRENT_CONTEXT.set(context);
            }
        }
        return result;
    }
}
