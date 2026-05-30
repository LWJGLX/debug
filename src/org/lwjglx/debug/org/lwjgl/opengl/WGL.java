package org.lwjglx.debug.org.lwjgl.opengl;

import static org.lwjglx.debug.org.lwjgl.opengl.Context.*;

public class WGL {

    public static boolean wglMakeCurrent(java.nio.IntBuffer buffer, long hdc, long hglrc) {
        boolean result = org.lwjgl.opengl.WGL.wglMakeCurrent(buffer, hdc, hglrc);
        if (result) {
            if (hglrc == 0L) {
                CURRENT_CONTEXT.remove();
            } else {
                Context context = CONTEXTS.get(hglrc);
                if (context == null) {
                    Context.create(hglrc, 0L);
                    context = CONTEXTS.get(hglrc);
                }
                CURRENT_CONTEXT.set(context);
            }
        }
        return result;
    }
}
