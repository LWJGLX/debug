package org.lwjglx.debug.opengl;

import static org.lwjglx.debug.opengl.Context.*;

import org.lwjglx.debug.*;

public class NVDrawTexture {
    public static void glDrawTextureNV(int texture, int sampler, float x0, float y0, float x1, float y1, float z,
            float s0, float t0, float s1, float t1) {
        if (Properties.VALIDATE.enabled) {
            checkFramebufferCompleteness();
        }
        org.lwjgl.opengl.NVDrawTexture.glDrawTextureNV(texture, sampler, x0, y0, x1, y1, z, s0, t0, s1, t1);
        RT.draw(3);
    }
}
