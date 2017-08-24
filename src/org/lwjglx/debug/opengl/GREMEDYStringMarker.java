package org.lwjglx.debug.opengl;

import static org.lwjgl.system.MemoryUtil.*;

import java.nio.ByteBuffer;

import org.lwjglx.debug.Properties;
import org.lwjglx.debug.RT;

public class GREMEDYStringMarker {

    public static void nglStringMarkerGREMEDY(int len, long string) {
        if (!Properties.PROFILE.enabled) {
            org.lwjgl.opengl.GREMEDYStringMarker.nglStringMarkerGREMEDY(len, string);
            return;
        }
        String str;
        if (len <= 0) {
            str = memASCII(string);
        } else {
            str = memASCII(memByteBuffer(string, len), len);
        }
        RT.stringMarker(str);
    }

    public static void glStringMarkerGREMEDY(ByteBuffer string) {
        if (!Properties.PROFILE.enabled) {
            org.lwjgl.opengl.GREMEDYStringMarker.glStringMarkerGREMEDY(string);
            return;
        }
        RT.stringMarker(memASCII(string, string.remaining()));
    }

    public static void glStringMarkerGREMEDY(CharSequence string) {
        if (!Properties.PROFILE.enabled) {
            org.lwjgl.opengl.GREMEDYStringMarker.glStringMarkerGREMEDY(string);
            return;
        }
        RT.stringMarker(string.toString());
    }

}
