package org.lwjglx.debug;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

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

    static void ldcI(MethodVisitor mv, int i) {
        /* Special opcodes for some integer constants */
        if (i >= -1 && i <= 5) {
            mv.visitInsn(Opcodes.ICONST_0 + i);
        } else {
            /* BIPUSH or SIPUSH if integer constant within certain limits */
            if (i >= Byte.MIN_VALUE && i <= Byte.MAX_VALUE) {
                mv.visitIntInsn(Opcodes.BIPUSH, i);
            } else if (i >= Short.MIN_VALUE && i <= Short.MAX_VALUE) {
                mv.visitIntInsn(Opcodes.SIPUSH, i);
            } else {
                /* Fallback to LDC */
                mv.visitLdcInsn(Integer.valueOf(i));
            }
        }
    }

}
