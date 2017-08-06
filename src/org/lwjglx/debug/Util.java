package org.lwjglx.debug;

class Util {

    static boolean isBuffer(String internalName) {
        return internalName.equals("java/nio/Buffer") || internalName.equals("java/nio/ByteBuffer") || internalName.equals("java/nio/CharBuffer") || internalName.equals("java/nio/ShortBuffer")
                || internalName.equals("java/nio/IntBuffer") || internalName.equals("java/nio/LongBuffer") || internalName.equals("java/nio/FloatBuffer")
                || internalName.equals("java/nio/DoubleBuffer");
    }

    static boolean isMultiByteWrite(String owner, String name) {
        if (owner.equals("java/nio/ByteBuffer")
                && (name.equals("putChar") || name.equals("putShort") || name.equals("putInt") || name.equals("putLong") || name.equals("putFloat") || name.equals("putDouble"))) {
            return true;
        } else if (owner.equals("java/nio/CharBuffer") && name.equals("put")) {
            return true;
        } else if (owner.equals("java/nio/ShortBuffer") && name.equals("put")) {
            return true;
        } else if (owner.equals("java/nio/IntBuffer") && name.equals("put")) {
            return true;
        } else if (owner.equals("java/nio/LongBuffer") && name.equals("put")) {
            return true;
        } else if (owner.equals("java/nio/FloatBuffer") && name.equals("put")) {
            return true;
        } else if (owner.equals("java/nio/DoubleBuffer") && name.equals("put")) {
            return true;
        }
        return false;
    }

    static boolean isTypedViewMethod(String name) {
        return name.equals("asCharBuffer") || name.equals("asShortBuffer") || name.equals("asIntBuffer") || name.equals("asLongBuffer") || name.equals("asFloatBuffer")
                || name.equals("asDoubleBuffer");
    }

}
