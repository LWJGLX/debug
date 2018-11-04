/*
 * (C) Copyright 2017 Kai Burjack

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

 */
package org.lwjglx.debug;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

class ClassMetadata implements Opcodes {

    private static final String NativeType_Desc = "Lorg/lwjgl/system/NativeType;";
    private static final String jsr305_Nullable_Desc = "Ljavax/annotation/Nullable;";

    static class MethodInfo {
        public String[] parameterNativeTypes;
        public String[] parameterNames;
        public boolean[] nullable;
        public String returnNativeType;
        public String name;
    }

    static final Map<String, ClassMetadata> meta = new HashMap<>();
    static boolean nullableSupportChecked;
    static boolean hasNullables;

    final Map<String, MethodInfo> methods = new HashMap<>();

    static ClassMetadata create(String internalName, ClassLoader cl) {
        /* Has Nullable support be checked already? */
        if (!nullableSupportChecked) {
            nullableSupportChecked = true;
            /* If not, scan a class which we know has Nullable annotations first */
            create("org/lwjgl/system/MemoryUtil", cl);
        }
        if (meta.containsKey(internalName))
            return meta.get(internalName);
        final ClassMetadata m = new ClassMetadata();
        InputStream is = cl.getResourceAsStream(internalName + ".class");
        ClassReader cr;
        try {
            cr = new ClassReader(is);
        } catch (IOException e) {
            return null;
        }
        cr.accept(new ClassVisitor(ASM7) {
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                boolean isPublic = (access & ACC_PUBLIC) != 0;
                boolean isStatic = (access & ACC_STATIC) != 0;
                if (!isPublic || !isStatic)
                    return null;
                final MethodInfo minfo = new MethodInfo();
                Type[] argumentTypes = Type.getArgumentTypes(desc);
                int numParamLocals = 0;
                for (Type t : argumentTypes)
                    numParamLocals += t.getSize();
                int[] localToParamIndex = new int[numParamLocals];
                for (int param = 0, paramLocal = 0; param < argumentTypes.length; paramLocal += argumentTypes[param].getSize(), param++)
                    localToParamIndex[paramLocal] = param;
                int numParameters = argumentTypes.length;
                minfo.parameterNativeTypes = new String[numParameters];
                minfo.nullable = new boolean[numParameters];
                minfo.parameterNames = new String[numParameters];
                minfo.name = name;
                m.methods.put(name + desc, minfo);
                return new MethodVisitor(ASM7) {
                    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end,
                            int index) {
                        if (index >= localToParamIndex.length)
                            return;
                        int param = localToParamIndex[index];
                        minfo.parameterNames[param] = name;
                    }

                    public AnnotationVisitor visitParameterAnnotation(int parameter, String annotDesc, boolean visible) {
                        if (jsr305_Nullable_Desc.equals(annotDesc)) {
                            minfo.nullable[parameter] = true;
                            hasNullables = true;
                        } else if (NativeType_Desc.equals(annotDesc)) {
                            return new AnnotationVisitor(ASM7) {
                                public void visit(String name, Object value) {
                                    if (!"value".equals(name) || !(value instanceof String))
                                        return;
                                    minfo.parameterNativeTypes[parameter] = (String) value;
                                }
                            };
                        }
                        return null;
                    }

                    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                        if (!desc.equals(NativeType_Desc))
                            return null;
                        return new AnnotationVisitor(ASM7) {
                            public void visit(String name, Object value) {
                                if (!"value".equals(name) || !(value instanceof String))
                                    return;
                                minfo.returnNativeType = (String) value;
                            }
                        };
                    }
                };
            }
        }, ClassReader.SKIP_FRAMES);
        meta.put(internalName, m);
        try {
            is.close();
        } catch (IOException e) {
        }
        return m;
    }

}
