package org.lwjglx.debug.opengl;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;
import org.lwjglx.debug.RT;

public class GREMEDYStringMarker {

    public static void glStringMarkerGREMEDY(ByteBuffer string) {
        RT.stringMarker(MemoryUtil.memASCII(string));
    }

    public static void glStringMarkerGREMEDY(CharSequence string) {
        RT.stringMarker(string.toString());
    }

}
