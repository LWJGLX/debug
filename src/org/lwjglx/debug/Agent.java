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
import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class Agent implements ClassFileTransformer, Opcodes {

    private static final String RT_InternalName = "org/lwjglx/debug/RT";

    private static final AtomicInteger counter = new AtomicInteger();

    private final Set<Pattern> excludes;

    private Agent(Set<Pattern> excludes) {
        this.excludes = excludes;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            return transform_(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IllegalClassFormatException(t.getMessage());
        }
    }

    private boolean excluded(String owner, String name) {
        for (Pattern p : excludes) {
            if (p.matcher(owner + "." + name).find())
                return true;
        }
        return false;
    }

    private String resolveOwner(String methodOwner, String methodName, String methodDesc) {
        StringBuilder resolvedOwner = new StringBuilder(); // <- just a container for a String
        try {
            ClassReader cr;
            String nextOwner = methodOwner;
            do {
                cr = new ClassReader(nextOwner.replace('/', '.'));
                if (cr.getSuperName() == null || cr.getSuperName().equals("java/lang/Object")) {
                    resolvedOwner.append(nextOwner);
                    break;
                }
                final ClassReader fcr = cr;
                cr.accept(new ClassVisitor(ASM8) {
                    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
                            String[] exceptions) {
                        if ((access & ACC_STATIC) != 0 && name.equals(methodName) && descriptor.equals(methodDesc)) {
                            resolvedOwner.append(fcr.getClassName());
                        }
                        return null;
                    }
                }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                nextOwner = cr.getSuperName();
            } while (resolvedOwner.length() == 0);
        } catch (IOException e) {
            throw new AssertionError("Could not load class " + methodOwner);
        }
        return resolvedOwner.length() == 0 ? null : resolvedOwner.toString();
    }

    private static final Map<String, Set<String>> mustInstrumentMethods = new HashMap<>();
    {
        mustInstrumentMethods.put("org/lwjgl/glfw/GLFWErrorCallback", Collections.singleton("set()Lorg/lwjgl/glfw/GLFWErrorCallback;"));
    }

    private byte[] transform_(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if ((className == null || className.startsWith("java/") || className.startsWith("com/sun/") || className.startsWith("sun/") || className.startsWith("jdk/internal/")
                || className.startsWith("org/lwjgl/") || className.startsWith("org/joml/") || className.startsWith("org/lwjglx/debug/")) && !mustInstrumentMethods.containsKey(className)) {
            return null;
        }
        boolean forcedInstrumentation = mustInstrumentMethods.containsKey(className);
        Set<String> forceInstrumentationMethods = forcedInstrumentation ? mustInstrumentMethods.get(className) : null;
        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        class Modifications {
            boolean modified;
            boolean needsProxyClass;
            String sourceFile;
        }
        final Modifications modifications = new Modifications();
        Map<String, InterceptedCall> calls = new LinkedHashMap<String, InterceptedCall>();
        String callerName = className.replace('.', '/');
        String proxyName = "org/lwjglx/debug/$Proxy$" + counter.incrementAndGet();
        ClassVisitor cv = new ClassVisitor(ASM8, cw) {
            public void visitSource(String source, String debug) {
                super.visitSource(source, debug);
                modifications.sourceFile = source;
                Log.maxSourceLength = Math.max(Log.maxSourceLength, source.length());
            }

            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                if (forcedInstrumentation && !forceInstrumentationMethods.contains(name + desc))
                    return mv;
                return new MethodVisitor(ASM8, mv) {
                    private int lastLineNumber = -1;

                    public void visitCode() {
                        // Check main method
                        if ((access & (ACC_PUBLIC | ACC_STATIC)) == access && "main".equals(name) && "([Ljava/lang/String;)V".equals(desc)) {
                            mv.visitVarInsn(ALOAD, 0);
                            mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, "checkMainMethod", "([Ljava/lang/String;)V", false);
                            modifications.modified = true;
                        }
                        super.visitCode();
                    }

                    public void visitLineNumber(int line, Label start) {
                        super.visitLineNumber(line, start);
                        lastLineNumber = line;
                    }

                    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                        /* Intercept certain GLCapabilities field reads when profiling */
                        if (PROFILE.enabled && opcode == GETFIELD && owner.equals("org/lwjgl/opengl/GLCapabilities") && desc.equals("Z")) {
                            if (name.equals("GL_GREMEDY_string_marker") || name.equals("GL_GREMEDY_frame_terminator")) {
                                mv.visitInsn(POP); // <- pop off GLCapabilities
                                mv.visitInsn(ICONST_1); // <- true
                                modifications.modified = true;
                                return;
                            }
                        }
                        super.visitFieldInsn(opcode, owner, name, desc);
                    }

                    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                        if (opcode == INVOKESTATIC && owner.startsWith("org/lwjgl/") && !excluded(owner, name)) {
                            String key = owner + "." + name + desc;
                            InterceptedCall call = calls.get(key);
                            if (call == null) {
                                /* Resolve declaring class */
                                String resolvedOwner = resolveOwner(owner, name, desc);
                                /* Rewrite a GLnnC call to GLnn to be able to intercept the call */
                                if (resolvedOwner.matches(".*/GL(\\d\\d)C$")) {
                                    resolvedOwner = resolvedOwner.substring(0, resolvedOwner.length() - 1);
                                }
                                call = new InterceptedCall(owner, resolvedOwner, name, desc);
                                String methodName;
                                methodName = name + call.index;
                                call.generatedMethodName = methodName;
                                calls.put(key, call);
                            }
                            Log.maxLineNumberLength = Math.max(Log.maxLineNumberLength, (int) (Math.log10(lastLineNumber) + 1));
                            String proxyDesc = call.desc;
                            if (TRACE.enabled) {
                                Util.ldcI(mv, lastLineNumber);
                                proxyDesc = call.desc.substring(0, call.desc.lastIndexOf(')')) + "I" + call.desc.substring(call.desc.lastIndexOf(')'));
                            }
                            mv.visitMethodInsn(INVOKESTATIC, proxyName, call.generatedMethodName, proxyDesc, itf);
                            modifications.needsProxyClass = true;
                        } else if (opcode == INVOKEVIRTUAL && Util.isBuffer(owner) && Util.isMultiByteWrite(owner, name)) {
                            mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, name, "(L" + owner + ";" + desc.substring(1), itf);
                            modifications.needsProxyClass = true;
                        } else if (opcode == INVOKEVIRTUAL && owner.equals("java/nio/ByteBuffer") && Util.isTypedViewMethod(name)) {
                            mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, name, "(L" + owner + ";" + desc.substring(1), itf);
                            modifications.needsProxyClass = true;
                        } else if (opcode == INVOKEVIRTUAL && Util.isBuffer(owner) && name.equals("slice")) {
                            mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, name, "(L" + owner + ";" + desc.substring(1), itf);
                            modifications.needsProxyClass = true;
                        } else if (opcode == INVOKEVIRTUAL && Util.isBuffer(owner) && name.equals("flip")) {
                            mv.visitInsn(DUP);
                            mv.visitMethodInsn(INVOKESTATIC, RT_InternalName, "checkFlipBufferAtPosition0", "(Ljava/nio/Buffer;)V", false);
                            super.visitMethodInsn(opcode, owner, name, desc, itf);
                            modifications.needsProxyClass = true;
                        } else {
                            super.visitMethodInsn(opcode, owner, name, desc, itf);
                        }
                    }
                };
            }
        };
        cr.accept(cv, 0);
        if (!modifications.needsProxyClass && !modifications.modified) {
            if (DEBUG.enabled)
                debug("Did not modify: " + className);
            return null;
        }
        if (DEBUG.enabled) {
            debug("Modified [" + className + "] (" + calls.size() + " calls into LWJGL)");
        }
        if (modifications.needsProxyClass) {
            /* Generate proxy class */
            InterceptClassGenerator.generate(loader, proxyName, callerName, calls.values(), modifications.sourceFile);
        }
        byte[] arr = cw.toByteArray();
        if (DEBUG.enabled) {
            TraceClassVisitor tcv = new TraceClassVisitor(new PrintWriter(System.err));
            ClassReader tcr = new ClassReader(arr);
            tcr.accept(tcv, 0);
        }
        return arr;
    }

    /**
     * Based on: https://stackoverflow.com/questions/1247772/is-there-an-equivalent-of-java-util-regex-for-glob-type-patterns#answer-1248627
     */
    private static String convertGlobToRegEx(String line) {
        line = line.trim();
        int strLen = line.length();
        StringBuilder sb = new StringBuilder(strLen);
        if (!line.startsWith("*")) {
            sb.append("^");
        }
        if (line.endsWith("*")) {
            line = line.substring(0, strLen - 1);
            strLen--;
        }
        boolean escaping = false;
        int inCurlies = 0;
        for (char currentChar : line.toCharArray()) {
            switch (currentChar) {
            case '*':
                if (escaping)
                    sb.append("\\*");
                else
                    sb.append(".*");
                escaping = false;
                break;
            case '?':
                if (escaping)
                    sb.append("\\?");
                else
                    sb.append('.');
                escaping = false;
                break;
            case '.':
            case '(':
            case ')':
            case '+':
            case '|':
            case '^':
            case '$':
            case '@':
            case '%':
                sb.append('\\');
                sb.append(currentChar);
                escaping = false;
                break;
            case '\\':
                if (escaping) {
                    sb.append("\\\\");
                    escaping = false;
                } else
                    escaping = true;
                break;
            case '{':
                if (escaping) {
                    sb.append("\\{");
                } else {
                    sb.append('(');
                    inCurlies++;
                }
                escaping = false;
                break;
            case '}':
                if (inCurlies > 0 && !escaping) {
                    sb.append(')');
                    inCurlies--;
                } else if (escaping)
                    sb.append("\\}");
                else
                    sb.append("}");
                escaping = false;
                break;
            case ',':
                if (inCurlies > 0 && !escaping) {
                    sb.append('|');
                } else if (escaping)
                    sb.append("\\,");
                else
                    sb.append(",");
                break;
            default:
                escaping = false;
                sb.append(currentChar);
            }
        }
        return sb.toString();
    }

    public static void premain(String agentArguments, Instrumentation instrumentation) {
        Set<Pattern> excludes = new HashSet<Pattern>();
        /*
         * Exclude MemoryStack.stackPush/stackPop to avoid getting "Asymmetric pop detected" messages from the DebugMemoryStack
         * due to the method calling stackPush() being the generated proxy class method and the method calling AutoClosable.close()
         * being the user method.
         */
        excludes.add(Pattern.compile("org/lwjgl/system/MemoryStack\\.stack(Push|Pop)"));
        if (agentArguments != null) {
            /* Parse command line arguments */
            String[] args = agentArguments.split(";");
            for (int i = 0; i < args.length; i++) {
                if (!args[i].startsWith("-")) {
                    args[i] = "-" + args[i];
                }
            }
            OptionParser parser = new OptionParser();
            OptionSpec<String> path = parser.accepts("exclude").withRequiredArg().ofType(String.class).withValuesSeparatedBy(",");
            parser.accepts("debug");
            parser.accepts("trace");
            OptionSpec<String> profile = parser.accepts("profile").withOptionalArg().ofType(String.class);
            parser.accepts("nothrow");
            OptionSpec<String> validate = parser.accepts("validate").withOptionalArg().ofType(String.class);
            OptionSpec<Long> sleep = parser.accepts("sleep").withRequiredArg().ofType(Long.class);
            OptionSpec<String> output = parser.accepts("output").withRequiredArg().ofType(String.class);
            OptionSet options = parser.parse(args);
            if (options.has("exclude")) {
                List<String> excluded = options.valuesOf(path);
                for (String e : excluded) {
                    excludes.add(Pattern.compile(convertGlobToRegEx(e)));
                }
            }
            if (options.has("debug"))
                Properties.DEBUG.enable();
            if (options.has("trace"))
                Properties.TRACE.enable();
            if (options.has("profile")) {
                Properties.PROFILE.enable();
                Properties.VALIDATE.disableIfByDefault();
                /* Parse sub-properties of "profile" */
                String profileArgsString = options.valueOf(profile);
                if (profileArgsString != null) {
                    String[] profileArgsStrings = profileArgsString.split(";");
                    for (int i = 0; i < profileArgsStrings.length; i++) {
                        if (!profileArgsStrings[i].startsWith("-")) {
                            profileArgsStrings[i] = "-" + profileArgsStrings[i];
                        }
                    }
                    OptionParser profileArgParser = new OptionParser();
                    profileArgParser.accepts("suspend");
                    OptionSet profileArgs = profileArgParser.parse(profileArgsStrings);
                    if (profileArgs.has("suspend")) {
                        Properties.PROFILE_SUSPEND.enable();
                    }
                }
            }
            if (options.has("validate")) {
                Properties.VALIDATE.enable();
                String validateArgsString = options.valueOf(validate);
                if ("s".equals(validateArgsString)) {
                    Properties.STRICT.enable();
                }
            }
            if (options.has("nothrow"))
                Properties.NO_THROW_ON_ERROR.enable();
            if (options.has("sleep"))
                Properties.SLEEP = options.valueOf(sleep);
            if (options.has("output"))
                Properties.OUTPUT = options.valueOf(output);
        }
        if (Properties.PROFILE.enabled) {
            try {
                Profiling.startServer();
            } catch (ServletException e) {
                throw new AssertionError("Could not start profiling server", e);
            }
        }
        LWJGLInit.init();
        Agent t = new Agent(excludes);
        instrumentation.addTransformer(t);
        RT.mainThread = Thread.currentThread();

        if (Properties.PROFILE_SUSPEND.enabled) {
            /* Suspend execution until profile frontend connected */
            try {
                info("Waiting for first profile frontend to connect...");
                Profiling.frontendConnected.await();
                info("Profile frontend connected. Resuming application startup.");
            } catch (InterruptedException e) {
            }
        }
    }

}
