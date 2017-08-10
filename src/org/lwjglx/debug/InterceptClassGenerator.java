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

import static org.lwjglx.debug.Log.*;
import static org.lwjglx.debug.Properties.*;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import org.lwjglx.debug.ClassMetadata.MethodInfo;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.TraceClassVisitor;

class InterceptedCall {
    private static final AtomicInteger counter = new AtomicInteger();

    int index;
    String receiverInternalName;
    String name;
    String glName;
    String desc;
    String generatedMethodName;

    InterceptedCall(String source, int line, String receiverInternalName, String name, String desc) {
        this.index = counter.incrementAndGet();
        this.receiverInternalName = receiverInternalName;
        this.name = name;
        this.desc = desc;
    }
}

class InterceptClassGenerator implements Opcodes {

    private static final String MethodCall_InternalName = "org/lwjglx/debug/MethodCall";
    private static final String MethodCall_Desc = "L" + MethodCall_InternalName + ";";
    private static final String RT_InternalName = "org/lwjglx/debug/RT";

    private static boolean isGLcall(InterceptedCall call) {
        return (call.name.startsWith("gl") || call.name.startsWith("ngl")) && call.receiverInternalName.startsWith("org/lwjgl/opengl/");
    }

    private static String glCall(InterceptedCall call) {
        if (!isGLcall(call))
            return null;
        String name = call.name;
        if (name.startsWith("ngl")) {
            name = name.substring(1);
        }
        try {
            org.lwjgl.opengl.GLCapabilities.class.getField(name);
            return name;
        } catch (Exception e) {
            /* Try with 'v' suffix */
            String nameV = name + "v";
            try {
                org.lwjgl.opengl.GLCapabilities.class.getField(nameV);
                return nameV;
            } catch (Exception e2) {
                if (DEBUG)
                    debug("Expected field GLCapabilities." + name + " to exist");
                return null;
            }
        }
    }

    private static void checkFunctionSupported(MethodVisitor mv, String name) {
        mv.visitFieldInsn(GETFIELD, "org/lwjgl/opengl/GLCapabilities", name, "J");
        mv.visitLdcInsn(name);
        mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, "checkFunction", "(JLjava/lang/String;)V", false);
    }

    private static String validationMethod(ClassLoader cl, InterceptedCall call) {
        String interceptClassName = call.receiverInternalName.replace("org/lwjgl/", "org/lwjglx/debug/");
        Class<?> clazz;
        try {
            clazz = cl.loadClass(interceptClassName.replace('/', '.'));
        } catch (ClassNotFoundException e) {
            /* That's okay: We don't yet have a manual interceptor class */
            return null;
        }
        Method[] methods = clazz.getDeclaredMethods();
        for (Method m : methods) {
            boolean isStatic = Modifier.isStatic(m.getModifiers());
            boolean isPublic = Modifier.isPublic(m.getModifiers());
            if (!isStatic || !isPublic) {
                continue;
            }
            if (m.getName().equals(call.name) && Type.getMethodDescriptor(m).equals(call.desc)) {
                return interceptClassName;
            }
        }
        return null;
    }

    private static String traceMethod(ClassLoader cl, String traceMethodDesc, InterceptedCall call) {
        String traceClassName = call.receiverInternalName.replace("org/lwjgl/", "org/lwjglx/debug/");
        Class<?> clazz;
        try {
            clazz = cl.loadClass(traceClassName.replace('/', '.'));
        } catch (ClassNotFoundException e) {
            /* That's okay: We don't yet have a manual trace class */
            return null;
        }
        Method[] methods = clazz.getDeclaredMethods();
        for (Method m : methods) {
            boolean isStatic = Modifier.isStatic(m.getModifiers());
            boolean isPublic = Modifier.isPublic(m.getModifiers());
            if (!isStatic || !isPublic) {
                continue;
            }
            if (m.getName().equals(call.name) && Type.getMethodDescriptor(m).equals(traceMethodDesc)) {
                return traceClassName;
            }
        }
        return null;
    }

    public static Class<?> generate(ClassLoader classLoader, String proxyInternalName, String callerName, Collection<InterceptedCall> calls) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(V1_6, ACC_PUBLIC | ACC_SUPER | ACC_SYNTHETIC, proxyInternalName, null, "java/lang/Object", null);
        MethodVisitor ctor = cw.visitMethod(ACC_PRIVATE | ACC_SYNTHETIC, "<init>", "()V", null, null);
        ctor.visitCode();
        ctor.visitVarInsn(ALOAD, 0);
        ctor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        ctor.visitInsn(RETURN);
        ctor.visitMaxs(-1, -1);
        ctor.visitEnd();
        /* Generate a new method for each intercepted call */
        for (InterceptedCall call : calls) {
            String effectiveDesc = call.desc;
            if (TRACE) {
                effectiveDesc = call.desc.substring(0, call.desc.lastIndexOf(')')) + "Ljava/lang/String;I" + call.desc.substring(call.desc.lastIndexOf(')'));
            }
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC | ACC_SYNTHETIC, call.generatedMethodName, effectiveDesc, null, null);
            mv.visitCode();
            {
                /* Validate buffer arguments and also load all arguments onto stack */
                Type[] paramTypes = Type.getArgumentTypes(call.desc);
                Type retType = Type.getReturnType(call.desc);
                int var = 0; // <- counts the used local variables
                for (int i = 0; i < paramTypes.length; i++) {
                    Type paramType = paramTypes[i];
                    mv.visitVarInsn(paramType.getOpcode(ILOAD), var);
                    if (paramType.getSort() == Type.OBJECT && Util.isBuffer(paramType.getInternalName())) {
                        mv.visitInsn(DUP);
                        mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, "checkBuffer", "(" + paramType.getDescriptor() + ")V", false);
                    }
                    var += paramType.getSize();
                }
                /* Allocate locals for the source/line parameters (only available when TRACE) */
                int sourceVar = var++;
                int lineVar = var++;
                /* check if GL call */
                call.glName = glCall(call);
                if (call.glName != null) {
                    /* if GL call, then check whether GLCapabilities have been set */
                    mv.visitMethodInsn(INVOKESTATIC, "org/lwjgl/opengl/GL", "getCapabilities", "()Lorg/lwjgl/opengl/GLCapabilities;", false);
                    /* and whether the function is supported */
                    checkFunctionSupported(mv, call.glName);
                }
                /* Optionally delay the call */
                if (Properties.DELAY > 0L) {
                    mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, "delay", "()V", false);
                }
                /* Do we want to output a call trace? */
                if (Properties.TRACE) {
                    /* What is the expected descriptor of the trace method? */
                    String traceMethodDesc = call.desc.substring(0, call.desc.lastIndexOf(')'));
                    if (retType.getSort() != Type.VOID) {
                        traceMethodDesc += retType.getDescriptor();
                    } else {
                        traceMethodDesc += "Ljava/lang/Void;";
                    }
                    traceMethodDesc += "Lorg/lwjglx/debug/MethodCall;";
                    traceMethodDesc += ")V";
                    /* push a new MethodCall object on the stack */
                    mv.visitVarInsn(ALOAD, sourceVar);
                    mv.visitVarInsn(ILOAD, lineVar);
                    mv.visitLdcInsn(call.name);
                    mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, "methodCall", "(Ljava/lang/String;ILjava/lang/String;)Lorg/lwjglx/debug/MethodCall;", false);
                    int methodCallVar = var++;
                    /* check if we have a user-provided trace method */
                    String traceMethodOwnerName = traceMethod(classLoader, traceMethodDesc, call);
                    if (traceMethodOwnerName != null) {
                        mv.visitVarInsn(ASTORE, methodCallVar); // <- store in local
                        /* Check if we have a user-provided validation method */
                        String validationMethodOwnerName = validationMethod(classLoader, call);
                        if (validationMethodOwnerName != null) {
                            /* we have, so call it... */
                            mv.visitMethodInsn(INVOKESTATIC, validationMethodOwnerName, call.name, call.desc, false);
                        } else {
                            /* we don't have a user-defined validation method yet, so just call the target method directly */
                            mv.visitMethodInsn(INVOKESTATIC, call.receiverInternalName, call.name, call.desc, false);
                        }
                        /* Check GL error if it was a GL call */
                        if (call.glName != null && !call.glName.equals("glGetError")) {
                            mv.visitLdcInsn(call.name);
                            mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, "checkError", "(Ljava/lang/String;)V", false);
                        }
                        /* Store the return value in a local */
                        int retVar = var++;
                        if (retType.getSort() != Type.VOID) {
                            mv.visitVarInsn(retType.getOpcode(ISTORE), retVar);
                        }
                        /* Repeat the arguments onto stack */
                        var = 0;
                        for (int i = 0; i < paramTypes.length; i++) {
                            Type paramType = paramTypes[i];
                            mv.visitVarInsn(paramType.getOpcode(ILOAD), var);
                            var += paramType.getSize();
                        }
                        if (retType.getSort() != Type.VOID) {
                            mv.visitVarInsn(retType.getOpcode(ILOAD), retVar);
                        } else {
                            mv.visitInsn(ACONST_NULL);
                        }
                        /* Call the trace method */
                        mv.visitVarInsn(ALOAD, methodCallVar);
                        mv.visitMethodInsn(INVOKESTATIC, traceMethodOwnerName, call.name, traceMethodDesc, false);
                        mv.visitVarInsn(ALOAD, methodCallVar);
                        mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, "methodCall", "(Lorg/lwjglx/debug/MethodCall;)V", false);
                        if (retType.getSort() != Type.VOID) {
                            /* Load return value on stack for final return from proxy method */
                            mv.visitVarInsn(retType.getOpcode(ILOAD), retVar);
                        }
                    } else {
                        mv.visitInsn(DUP);
                        mv.visitVarInsn(ASTORE, methodCallVar); // <- store in local
                        /* No user-provided trace method -> generate default trace prolog */
                        ClassMetadata classMetadata = ClassMetadata.create(call.receiverInternalName, classLoader);
                        MethodInfo minfo = classMetadata.methods.get(call.name + call.desc);
                        generateDefaultTraceBefore(classLoader, minfo, mv, paramTypes, call);
                        /* Check if we have a user-provided validation method */
                        String validationMethodOwnerName = validationMethod(classLoader, call);
                        if (validationMethodOwnerName != null) {
                            /* we have, so call it... */
                            mv.visitMethodInsn(INVOKESTATIC, validationMethodOwnerName, call.name, call.desc, false);
                        } else {
                            /* we don't have a user-defined validation method yet, so just call the target method directly */
                            mv.visitMethodInsn(INVOKESTATIC, call.receiverInternalName, call.name, call.desc, false);
                        }
                        /* Check GL error if it was a GL call */
                        if (call.glName != null && !call.glName.equals("glGetError")) {
                            mv.visitLdcInsn(call.name);
                            mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, "checkError", "(Ljava/lang/String;)V", false);
                        }
                        /* Generate default trace epilog */
                        generateDefaultTraceAfter(call, mv, methodCallVar, retType, minfo);
                    }
                } else {
                    /* -- NO TRACE -- */
                    /* check if we have a user-provided validation method */
                    String validationMethodOwnerName = validationMethod(classLoader, call);
                    if (validationMethodOwnerName != null) {
                        /* we have, so call it... */
                        mv.visitMethodInsn(INVOKESTATIC, validationMethodOwnerName, call.name, call.desc, false);
                    } else {
                        /* we don't have a user-defined validation method yet, so just call the target method directly */
                        mv.visitMethodInsn(INVOKESTATIC, call.receiverInternalName, call.name, call.desc, false);
                    }
                    /* Check GL error if it was a GL call */
                    if (call.glName != null && !call.glName.equals("glGetError")) {
                        mv.visitLdcInsn(call.name);
                        mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, "checkError", "(Ljava/lang/String;)V", false);
                    }
                }
                /* and finally return the return value */
                mv.visitInsn(retType.getOpcode(IRETURN));
            }
            mv.visitMaxs(-1, -1);
            mv.visitEnd();
        }
        cw.visitEnd();
        byte[] arr = cw.toByteArray();
        if (DEBUG) {
            debug("Created proxy class for [" + callerName + "] (" + String.format("%,d", arr.length) + " bytes)");
            TraceClassVisitor tcv = new TraceClassVisitor(new PrintWriter(System.err));
            ClassReader tcr = new ClassReader(arr);
            tcr.accept(tcv, 0);
        }
        Class<?> generatedClass = ClassUtils.defineClass(classLoader, proxyInternalName, arr);
        return generatedClass;
    }

    private static int loadGLenum(String name, String helperMethod, MethodVisitor mv, int var, int glEnumIndex) {
        String fieldName = name;
        try {
            GLmetadata.class.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            fieldName += "v";
        }
        mv.visitFieldInsn(GETSTATIC, "org/lwjglx/debug/GLmetadata", fieldName, "Lorg/lwjglx/debug/Command;");
        ldcI(mv, glEnumIndex);
        mv.visitVarInsn(ILOAD, var);
        mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, helperMethod, "(Lorg/lwjglx/debug/Command;II)Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, MethodCall_InternalName, "paramEnum", "(Ljava/lang/String;)" + MethodCall_Desc, false);
        glEnumIndex++;
        return glEnumIndex;
    }

    private static void loadGLenumReturn(String name, String helperMethod, MethodVisitor mv) {
        String fieldName = name;
        try {
            GLmetadata.class.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            fieldName += "v";
        }
        mv.visitFieldInsn(GETSTATIC, "org/lwjglx/debug/GLmetadata", fieldName, "Lorg/lwjglx/debug/Command;");
        mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, helperMethod, "(I" + MethodCall_Desc + "Lorg/lwjglx/debug/Command;)I", false);
    }

    private static void generateDefaultTraceBefore(ClassLoader cl, MethodInfo minfo, MethodVisitor mv, Type[] paramTypes, InterceptedCall call) {
        int var = 0;
        int glEnumIndex = 0;
        for (int i = 0; i < paramTypes.length; i++) {
            Type paramType = paramTypes[i];
            String nativeType = minfo.parameterNativeTypes[i];
            if ("GLenum".equals(nativeType) || "GLboolean".equals(nativeType)) {
                glEnumIndex = loadGLenum(call.glName, "glEnumFor", mv, var, glEnumIndex);
            } else if ("GLbitfield".equals(nativeType)) {
                glEnumIndex = loadGLenum(call.glName, "decodeBitField", mv, var, glEnumIndex);
            } else if ("GLFWwindow *".equals(nativeType)) {
                mv.visitVarInsn(paramType.getOpcode(ILOAD), var);
                mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, "paramGlfwWindow", "(" + MethodCall_Desc + paramType.getDescriptor() + ")" + MethodCall_Desc, false);
            } else if ("GLFWmonitor *".equals(nativeType)) {
                mv.visitVarInsn(paramType.getOpcode(ILOAD), var);
                mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, "paramGlfwMonitor", "(" + MethodCall_Desc + paramType.getDescriptor() + ")" + MethodCall_Desc, false);
            } else {
                mv.visitVarInsn(paramType.getOpcode(ILOAD), var);
                if (paramType.getSort() == Type.ARRAY || paramType.getSort() == Type.OBJECT) {
                    mv.visitMethodInsn(INVOKEVIRTUAL, MethodCall_InternalName, "param", "(Ljava/lang/Object;)" + MethodCall_Desc, false);
                } else {
                    mv.visitMethodInsn(INVOKEVIRTUAL, MethodCall_InternalName, "param", "(" + paramType.getDescriptor() + ")" + MethodCall_Desc, false);
                }
            }
            var += paramType.getSize();
        }
        mv.visitInsn(POP);
    }

    private static void generateDefaultTraceAfter(InterceptedCall call, MethodVisitor mv, int mcvar, Type retType, MethodInfo minfo) {
        if (retType.getSort() == Type.VOID) {
            // Do nothing
        } else if (retType.getSort() == Type.ARRAY || retType.getSort() == Type.OBJECT) {
            mv.visitVarInsn(ALOAD, mcvar);
            mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, "returnValue", "(Ljava/lang/Object;" + MethodCall_Desc + ")Ljava/lang/Object;", false);
            if (!"java/lang/Object".equals(retType.getInternalName()))
                mv.visitTypeInsn(CHECKCAST, retType.getInternalName());
        } else {
            mv.visitVarInsn(ALOAD, mcvar);
            String returnNativeType = minfo.returnNativeType;
            if ("GLenum".equals(returnNativeType) || "GLboolean".equals(returnNativeType)) {
                loadGLenumReturn(call.glName, "glEnumReturn", mv);
            } else if ("GLFWwindow *".equals(returnNativeType)) {
                mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, "returnValueGlfwWindow", "(" + retType.getDescriptor() + MethodCall_Desc + ")" + retType.getDescriptor(), false);
            } else if ("GLFWmonitor *".equals(returnNativeType)) {
                mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, "returnValueGlfwMonitor", "(" + retType.getDescriptor() + MethodCall_Desc + ")" + retType.getDescriptor(), false);
            } else {
                mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, "returnValue", "(" + retType.getDescriptor() + MethodCall_Desc + ")" + retType.getDescriptor(), false);
            }
        }
        mv.visitVarInsn(ALOAD, mcvar);
        mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, "methodCall", "(Lorg/lwjglx/debug/MethodCall;)V", false);
    }

    private static void ldcI(MethodVisitor mv, int i) {
        /* Special opcodes for some integer constants */
        if (i >= -1 && i <= 5) {
            mv.visitInsn(ICONST_0 + i);
        } else {
            /* BIPUSH or SIPUSH if integer constant within certain limits */
            if (i >= Byte.MIN_VALUE && i <= Byte.MAX_VALUE) {
                mv.visitIntInsn(BIPUSH, i);
            } else if (i >= Short.MIN_VALUE && i <= Short.MAX_VALUE) {
                mv.visitIntInsn(SIPUSH, i);
            } else {
                /* Fallback to LDC */
                mv.visitLdcInsn(Integer.valueOf(i));
            }
        }
    }

}
