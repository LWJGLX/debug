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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.lwjglx.debug.ClassMetadata.MethodInfo;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
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

    InterceptedCall(String receiverInternalName, String name, String desc) {
        this.index = counter.incrementAndGet();
        this.receiverInternalName = receiverInternalName;
        this.name = name;
        this.desc = desc;
    }
}

class Method {
    final String name;
    final String desc;

    Method(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + desc.hashCode();
        result = prime * result + name.hashCode();
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        Method other = (Method) obj;
        if (!desc.equals(other.desc))
            return false;
        if (!name.equals(other.name))
            return false;
        return true;
    }
}

class ClassKey {
    final ClassLoader cl;
    final String internalName;

    ClassKey(ClassLoader cl, String internalName) {
        this.cl = cl;
        this.internalName = internalName;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + cl.hashCode();
        result = prime * result + internalName.hashCode();
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        ClassKey other = (ClassKey) obj;
        if (cl != other.cl)
            return false;
        if (!internalName.equals(other.internalName))
            return false;
        return true;
    }
}

class InterceptClassGenerator implements Opcodes {

    private static final String MethodCall_InternalName = "org/lwjglx/debug/MethodCall";
    private static final String MethodCall_Desc = "L" + MethodCall_InternalName + ";";
    private static final String RT_InternalName = "org/lwjglx/debug/RT";

    private static final Map<ClassKey, HashSet<Method>> declaredMethods = new ConcurrentHashMap<>();

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
                if (DEBUG.enabled)
                    debug("Expected field GLCapabilities." + name + " to exist");
                return null;
            }
        }
    }

    private static boolean isMainThreadMethod(InterceptedCall call) {
        if (call.receiverInternalName.equals("org/lwjgl/glfw/GLFW")) {
            return Arrays
                    .asList("glfwCreateWindow", "glfwDefaultWindowHints", "glfwDestroyWindow", "glfwFocusWindow", "glfwGetFramebufferSize", "glfwGetWindowAttrib", "glfwGetWindowFrameSize",
                            "glfwGetWindowMonitor", "glfwGetWindowPos", "glfwGetWindowSize", "glfwHideWindow", "glfwIconifyWindow", "glfwMaximizeWindow", "glfwPollEvents", "glfwRestoreWindow",
                            "glfwSetFramebufferSizeCallback", "glfwSetWindowAspectRatio", "glfwSetWindowCloseCallback", "glfwSetWindowFocusCallback", "glfwSetWindowIcon",
                            "glfwSetWindowIconifyCallback", "glfwSetWindowMonitor", "glfwSetWindowPos", "glfwSetWindowPosCallback", "glfwSetWindowRefreshCallback", "glfwSetWindowSize",
                            "glfwSetWindowSizeCallback", "glfwSetWindowSizeLimits", "glfwSetWindowTitle", "glfwShowWindow", "glfwWaitEvents", "glfwWaitEventsTimeout", "glfwWindowHint")
                    .contains(call.name);
        }
        return false;
    }

    private static boolean requiresGlfwInit(InterceptedCall call) {
        if (call.receiverInternalName.equals("org/lwjgl/glfw/GLFW")) {
            return call.name.startsWith("glfw") && !call.name.equals("glfwSetErrorCallback") && !call.name.equals("glfwInit");
        }
        return false;
    }

    private static void checkFunctionSupported(MethodVisitor mv, String name) {
        mv.visitFieldInsn(GETFIELD, "org/lwjgl/opengl/GLCapabilities", name, "J");
        mv.visitLdcInsn(name);
        mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, "checkFunction", "(JLjava/lang/String;)V", false);
    }

    private static String getClassForMethod(ClassLoader cl, String desc, InterceptedCall call) {
        String className = call.receiverInternalName.replace("org/lwjgl/", "org/lwjglx/debug/");
        ClassKey key = new ClassKey(cl, className);
        HashSet<Method> dmethods = declaredMethods.get(key);
        if (dmethods == null) {
            dmethods = new HashSet<>();
            declaredMethods.put(key, dmethods);
            InputStream is = cl.getResourceAsStream(className + ".class");
            if (is == null) {
                return null;
            }
            ClassReader cr;
            try {
                cr = new ClassReader(is);
            } catch (IOException e) {
                return null;
            }
            final HashSet<Method> methods = dmethods;
            cr.accept(new ClassVisitor(ASM5) {
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    boolean isStatic = (access & ACC_STATIC) != 0;
                    boolean isPublic = (access & ACC_PUBLIC) != 0;
                    if (!isStatic || !isPublic)
                        return null;
                    methods.add(new Method(name, desc));
                    return null;
                }
            }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        }
        Method searched = new Method(call.name, desc);
        if (dmethods.contains(searched))
            return className;
        return null;
    }

    public static Class<?> generate(ClassLoader classLoader, String proxyInternalName, String callerName, Collection<InterceptedCall> calls, String source) {
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
            if (TRACE.enabled) {
                effectiveDesc = call.desc.substring(0, call.desc.lastIndexOf(')')) + "I" + call.desc.substring(call.desc.lastIndexOf(')'));
            }
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC | ACC_SYNTHETIC, call.generatedMethodName, effectiveDesc, null, null);
            mv.visitCode();
            {
                /* Check if the method may only be called from the main thread */
                if (VALIDATE.enabled) {
                    if (isMainThreadMethod(call)) {
                        mv.visitLdcInsn(call.name);
                        mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, "checkMainThread", "(Ljava/lang/String;)V", false);
                    }
                    if (requiresGlfwInit(call)) {
                        mv.visitLdcInsn(call.name);
                        mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, "checkGlfwInitialized", "(Ljava/lang/String;)V", false);
                    }
                }
                /* Validate buffer arguments and also load all arguments onto stack */
                Type[] paramTypes = Type.getArgumentTypes(call.desc);
                Type retType = Type.getReturnType(call.desc);
                int var = loadArgumentsAndValidateBuffers(mv, paramTypes);
                /* Allocate locals for the source/line parameters (only available when TRACE) */
                int lineVar = var++;
                /* check if GL call */
                call.glName = glCall(call);
                if (call.glName != null) {
                    if (VALIDATE.enabled) {
                        /* if GL call, then check whether GLCapabilities have been set */
                        mv.visitMethodInsn(INVOKESTATIC, "org/lwjgl/opengl/GL", "getCapabilities", "()Lorg/lwjgl/opengl/GLCapabilities;", false);
                        /* and whether the function is supported */
                        checkFunctionSupported(mv, call.glName);
                    }
                    /* also increment GL call profiling counter */
                    if (PROFILE.enabled) {
                        mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, "glCall", "()V", false);
                    }
                }
                /* Optionally delay the call */
                sleep(mv);
                /* Do we want to output a call trace? */
                if (TRACE.enabled) {
                    /* What is the expected descriptor of the trace method? */
                    String traceMethodDesc = buildTraceMethodDesc(call, retType);
                    /* push a new MethodCall object on the stack */
                    if (source != null)
                        mv.visitLdcInsn(source);
                    else
                        mv.visitInsn(ACONST_NULL);
                    mv.visitVarInsn(ILOAD, lineVar);
                    mv.visitLdcInsn(call.name);
                    mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, "methodCall", "(Ljava/lang/String;ILjava/lang/String;)" + MethodCall_Desc, false);
                    int methodCallVar = var++; // <- local to hold the created MethodCall
                    /* check if we have a user-provided trace method */
                    String traceMethodOwnerName = getClassForMethod(classLoader, traceMethodDesc, call);
                    if (traceMethodOwnerName != null) {
                        mv.visitVarInsn(ASTORE, methodCallVar); // <- store in local
                        /* Call a user-provided intercept method or the target method */
                        callUserMethodOrDirect(classLoader, call, mv);
                        /* Store the return value in a local */
                        int retVar = var++;
                        if (retType.getSort() != Type.VOID) {
                            mv.visitVarInsn(retType.getOpcode(ISTORE), retVar);
                        }
                        /* Repeat the arguments onto stack */
                        loadArguments(mv, paramTypes);
                        /* and load the return value (if any) */
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
                        mv.visitInsn(DUP); // <- duplicate MethodCall to be reused in generateDefaultTraceBefore()
                        mv.visitVarInsn(ASTORE, methodCallVar); // <- store in local
                        /* No user-provided trace method -> generate default trace prolog */
                        ClassMetadata classMetadata = ClassMetadata.create(call.receiverInternalName, classLoader);
                        MethodInfo minfo = classMetadata.methods.get(call.name + call.desc);
                        /* Generate trace prolog */
                        generateDefaultTraceBefore(call, mv, paramTypes, minfo);
                        /* Call a user-provided intercept method or the target method */
                        callUserMethodOrDirect(classLoader, call, mv);
                        /* Generate default trace epilog */
                        generateDefaultTraceAfter(call, mv, methodCallVar, retType, minfo);
                    }
                } else {
                    /* Call a user-provided intercept method or the target method */
                    callUserMethodOrDirect(classLoader, call, mv);
                }
                /* and finally return the return value */
                mv.visitInsn(retType.getOpcode(IRETURN));
            }
            mv.visitMaxs(-1, -1);
            mv.visitEnd();
        }
        cw.visitEnd();
        byte[] arr = cw.toByteArray();
        if (DEBUG.enabled) {
            debug("Created proxy class for [" + callerName + "] (" + String.format("%,d", arr.length) + " bytes)");
            TraceClassVisitor tcv = new TraceClassVisitor(new PrintWriter(System.err));
            ClassReader tcr = new ClassReader(arr);
            tcr.accept(tcv, 0);
        }
        Class<?> generatedClass = ClassUtils.defineClass(classLoader, proxyInternalName, arr);
        return generatedClass;
    }

    private static String buildTraceMethodDesc(InterceptedCall call, Type retType) {
        String traceMethodDesc = call.desc.substring(0, call.desc.lastIndexOf(')'));
        if (retType.getSort() != Type.VOID) {
            traceMethodDesc += retType.getDescriptor();
        } else {
            traceMethodDesc += "Ljava/lang/Void;";
        }
        traceMethodDesc += "Lorg/lwjglx/debug/MethodCall;";
        traceMethodDesc += ")V";
        return traceMethodDesc;
    }

    private static void sleep(MethodVisitor mv) {
        if (Properties.SLEEP > 0L) {
            mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, "delay", "()V", false);
        }
    }

    private static int loadArgumentsAndValidateBuffers(MethodVisitor mv, Type[] paramTypes) {
        int var = 0; // <- counts the used local variables
        for (int i = 0; i < paramTypes.length; i++) {
            Type paramType = paramTypes[i];
            mv.visitVarInsn(paramType.getOpcode(ILOAD), var);
            if (VALIDATE.enabled) {
                if (paramType.getSort() == Type.OBJECT && Util.isBuffer(paramType.getInternalName())) {
                    mv.visitInsn(DUP);
                    mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, "checkBuffer", "(" + paramType.getDescriptor() + ")V", false);
                }
            }
            var += paramType.getSize();
        }
        return var;
    }

    private static void loadArguments(MethodVisitor mv, Type[] paramTypes) {
        int var = 0;
        for (int i = 0; i < paramTypes.length; i++) {
            Type paramType = paramTypes[i];
            mv.visitVarInsn(paramType.getOpcode(ILOAD), var);
            var += paramType.getSize();
        }
    }

    private static void callUserMethodOrDirect(ClassLoader classLoader, InterceptedCall call, MethodVisitor mv) {
        /* Check if we have a user-provided validation method */
        String validationMethodOwnerName = getClassForMethod(classLoader, call.desc, call);
        if (validationMethodOwnerName != null) {
            /* we have, so call it... */
            mv.visitMethodInsn(INVOKESTATIC, validationMethodOwnerName, call.name, call.desc, false);
        } else {
            /* we don't have a user-defined validation method yet, so just call the target method directly */
            mv.visitMethodInsn(INVOKESTATIC, call.receiverInternalName, call.name, call.desc, false);
        }
        /* Check GL error if it was a GL call */
        if (VALIDATE.enabled && call.glName != null && !call.glName.equals("glGetError")) {
            mv.visitLdcInsn(call.name);
            mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, "checkError", "(Ljava/lang/String;)V", false);
        }
    }

    private static int loadGLenum(String name, String helperMethod, MethodVisitor mv, int var, int glEnumIndex) {
        String fieldName = name;
        try {
            GLmetadata.class.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            fieldName += "v";
        }
        mv.visitMethodInsn(INVOKESTATIC, "org/lwjglx/debug/GLmetadata", fieldName, "()Lorg/lwjglx/debug/Command;", false);
        Util.ldcI(mv, glEnumIndex);
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
        mv.visitMethodInsn(INVOKESTATIC, "org/lwjglx/debug/GLmetadata", fieldName, "()Lorg/lwjglx/debug/Command;", false);
        mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, helperMethod, "(I" + MethodCall_Desc + "Lorg/lwjglx/debug/Command;)I", false);
    }

    private static void generateDefaultTraceBefore(InterceptedCall call, MethodVisitor mv, Type[] paramTypes, MethodInfo minfo) {
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

}
