/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjglx.debug.opengl;

import static org.lwjglx.debug.RT.*;
import static org.lwjglx.debug.opengl.Context.*;

import org.lwjgl.opengl.AMDDebugOutput;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLDebugMessageAMDCallback;
import org.lwjgl.opengl.GLDebugMessageARBCallback;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.opengl.KHRDebug;
import org.lwjgl.system.*;

import java.io.*;
import java.util.function.Consumer;

import static org.lwjgl.opengl.AMDDebugOutput.*;
import static org.lwjgl.opengl.ARBDebugOutput.*;
import static org.lwjgl.system.APIUtil.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * This class was taken from LWJGL3's original <a href="https://github.com/LWJGL/lwjgl3/blob/master/modules/core/src/main/java/org/lwjgl/opengl/GLUtil.java">GLUtil</a> and modified to throw inside of
 * the debug message callbacks.
 */
public class GLUtil {

    private static final String EXCEPTION_MESSAGE = "OpenGL function call raised an error (see stderr output)";

    public static Callback setupDebugMessageCallback() {
        return setupDebugMessageCallback(APIUtil.DEBUG_STREAM);
    }

    private static void trace(Consumer<String> output) {
        /* We can not just use a fixed stacktrace element offset, because
         * some methods are intercepted and some are not. So, check the package name.
         */
        StackTraceElement[] elems = filterStackTrace(new Throwable(), 4).getStackTrace();
        for (int i = 0; i < elems.length; i++) {
            StackTraceElement ste = elems[i];
            output.accept(ste.toString());
        }
    }

    private static void printTrace(PrintStream stream) {
        trace(new Consumer<String>() {
            boolean first = true;
            public void accept(String str) {
                if (first) {
                    printDetail(stream, "Stacktrace", str);
                    first = false;
                } else {
                    printDetailLine(stream, "Stacktrace", str);
                }
            }
        });
    }

    public static Callback setupDebugMessageCallback(PrintStream stream) {
        GLCapabilities caps = org.lwjgl.opengl.GL.getCapabilities();

        if (caps.OpenGL43) {
            apiLog("[GL] Using OpenGL 4.3 for error logging.");
            GLDebugMessageCallback proc = GLDebugMessageCallback.create((source, type, id, severity, length, message, userParam) -> {
                Context context = CURRENT_CONTEXT.get();
                String level;
                if (severity == org.lwjgl.opengl.GL43.GL_DEBUG_SEVERITY_NOTIFICATION || severity == org.lwjgl.opengl.GL43.GL_DEBUG_SEVERITY_LOW)
                    level = "info ";
                else if (severity == org.lwjgl.opengl.GL43.GL_DEBUG_SEVERITY_MEDIUM)
                    level = "warn ";
                else
                    level = "error";
                if (context != null) {
                    stream.println("[" + level + "][" + context.counter + "] OpenGL debug message");
                } else {
                    stream.println("[" + level + "] OpenGL debug message");
                }
                printDetail(stream, "ID", String.format("0x%X", id));
                printDetail(stream, "Source", getDebugSource(source));
                printDetail(stream, "Type", getDebugType(type));
                printDetail(stream, "Severity", getDebugSeverity(severity));
                printDetail(stream, "Message", GLDebugMessageCallback.getMessage(length, message));
                printTrace(stream);
                if (type == org.lwjgl.opengl.GL43.GL_DEBUG_TYPE_ERROR) {
                    throwISEOrLogError(EXCEPTION_MESSAGE, false, 3);
                }
            });
            org.lwjgl.opengl.GL43.glDebugMessageCallback(proc, NULL);
            if ((org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL43.GL_CONTEXT_FLAGS) & org.lwjgl.opengl.GL43.GL_CONTEXT_FLAG_DEBUG_BIT) == 0) {
                apiLog("[GL] Warning: A non-debug context may not produce any debug output.");
                org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL43.GL_DEBUG_OUTPUT);
            }
            org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL43.GL_DEBUG_OUTPUT_SYNCHRONOUS);
            return proc;
        }

        if (caps.GL_KHR_debug) {
            apiLog("[GL] Using KHR_debug for error logging.");
            GLDebugMessageCallback proc = GLDebugMessageCallback.create((source, type, id, severity, length, message, userParam) -> {
                Context context = CURRENT_CONTEXT.get();
                String level;
                if (severity == org.lwjgl.opengl.GL43.GL_DEBUG_SEVERITY_NOTIFICATION || severity == org.lwjgl.opengl.GL43.GL_DEBUG_SEVERITY_LOW)
                    level = "info ";
                else if (severity == org.lwjgl.opengl.GL43.GL_DEBUG_SEVERITY_MEDIUM)
                    level = "warn ";
                else
                    level = "error";
                if (context != null) {
                    stream.println("[" + level + "][" + context.counter + "] OpenGL debug message");
                } else {
                    stream.println("[" + level + "] OpenGL debug message");
                }
                printDetail(stream, "ID", String.format("0x%X", id));
                printDetail(stream, "Source", getDebugSource(source));
                printDetail(stream, "Type", getDebugType(type));
                printDetail(stream, "Severity", getDebugSeverity(severity));
                printDetail(stream, "Message", GLDebugMessageCallback.getMessage(length, message));
                printTrace(stream);
                if (type == KHRDebug.GL_DEBUG_TYPE_ERROR) {
                    throwISEOrLogError(EXCEPTION_MESSAGE, false, 3);
                }
            });
            KHRDebug.glDebugMessageCallback(proc, NULL);
            if (caps.OpenGL30 && (org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL43.GL_CONTEXT_FLAGS) & org.lwjgl.opengl.GL43.GL_CONTEXT_FLAG_DEBUG_BIT) == 0) {
                apiLog("[GL] Warning: A non-debug context may not produce any debug output.");
                org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL43.GL_DEBUG_OUTPUT);
            }
            org.lwjgl.opengl.GL11.glEnable(KHRDebug.GL_DEBUG_OUTPUT_SYNCHRONOUS);
            return proc;
        }

        if (caps.GL_ARB_debug_output) {
            apiLog("[GL] Using ARB_debug_output for error logging.");
            GLDebugMessageARBCallback proc = GLDebugMessageARBCallback.create((source, type, id, severity, length, message, userParam) -> {
                Context context = CURRENT_CONTEXT.get();
                String level;
                if (severity == GL_DEBUG_SEVERITY_LOW_ARB)
                    level = "info ";
                else if (severity == GL_DEBUG_SEVERITY_MEDIUM_ARB)
                    level = "warn ";
                else
                    level = "error";
                if (context != null) {
                    stream.println("[" + level + "][" + context.counter + "] OpenGL debug message");
                } else {
                    stream.println("[" + level + "] OpenGL debug message");
                }
                printDetail(stream, "ID", String.format("0x%X", id));
                printDetail(stream, "Source", getSourceARB(source));
                printDetail(stream, "Type", getTypeARB(type));
                printDetail(stream, "Severity", getSeverityARB(severity));
                printDetail(stream, "Message", GLDebugMessageARBCallback.getMessage(length, message));
                printTrace(stream);
                if (type == ARBDebugOutput.GL_DEBUG_TYPE_ERROR_ARB) {
                    throwISEOrLogError(EXCEPTION_MESSAGE, false, 3);
                }
            });
            glDebugMessageCallbackARB(proc, NULL);
            org.lwjgl.opengl.GL11.glEnable(ARBDebugOutput.GL_DEBUG_OUTPUT_SYNCHRONOUS_ARB);
            return proc;
        }

        if (caps.GL_AMD_debug_output) {
            apiLog("[GL] Using AMD_debug_output for error logging.");
            GLDebugMessageAMDCallback proc = GLDebugMessageAMDCallback.create((id, category, severity, length, message, userParam) -> {
                Context context = CURRENT_CONTEXT.get();
                String level;
                if (severity == GL_DEBUG_SEVERITY_LOW_AMD)
                    level = "info ";
                else if (severity == GL_DEBUG_SEVERITY_MEDIUM_AMD)
                    level = "warn ";
                else
                    level = "error";
                if (context != null) {
                    stream.println("[" + level + "][" + context.counter + "] OpenGL debug message");
                } else {
                    stream.println("[" + level + "] OpenGL debug message");
                }
                printDetail(stream, "ID", String.format("0x%X", id));
                printDetail(stream, "Category", getCategoryAMD(category));
                printDetail(stream, "Severity", getSeverityAMD(severity));
                printDetail(stream, "Message", GLDebugMessageAMDCallback.getMessage(length, message));
                printTrace(stream);
                if (severity == AMDDebugOutput.GL_DEBUG_SEVERITY_HIGH_AMD) {
                    throwISEOrLogError(EXCEPTION_MESSAGE, false, 3);
                }
            });
            glDebugMessageCallbackAMD(proc, NULL);
            return proc;
        }

        apiLog("[GL] No debug output implementation is available.");
        return null;
    }

    private static void printDetail(PrintStream stream, String type, String message) {
        stream.printf("  %s: %s\n", type, message);
    }

    private static void printDetailLine(PrintStream stream, String type, String message) {
        stream.append("    ");
        for (int i = 0; i < type.length(); i++) {
            stream.append(" ");
        }
        stream.append(message + "\n");
    }

    private static String getDebugSource(int source) {
        switch (source) {
        case org.lwjgl.opengl.GL43.GL_DEBUG_SOURCE_API:
            return "API";
        case org.lwjgl.opengl.GL43.GL_DEBUG_SOURCE_WINDOW_SYSTEM:
            return "WINDOW SYSTEM";
        case org.lwjgl.opengl.GL43.GL_DEBUG_SOURCE_SHADER_COMPILER:
            return "SHADER COMPILER";
        case org.lwjgl.opengl.GL43.GL_DEBUG_SOURCE_THIRD_PARTY:
            return "THIRD PARTY";
        case org.lwjgl.opengl.GL43.GL_DEBUG_SOURCE_APPLICATION:
            return "APPLICATION";
        case org.lwjgl.opengl.GL43.GL_DEBUG_SOURCE_OTHER:
            return "OTHER";
        default:
            return apiUnknownToken(source);
        }
    }

    private static String getDebugType(int type) {
        switch (type) {
        case org.lwjgl.opengl.GL43.GL_DEBUG_TYPE_ERROR:
            return "ERROR";
        case org.lwjgl.opengl.GL43.GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR:
            return "DEPRECATED BEHAVIOR";
        case org.lwjgl.opengl.GL43.GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR:
            return "UNDEFINED BEHAVIOR";
        case org.lwjgl.opengl.GL43.GL_DEBUG_TYPE_PORTABILITY:
            return "PORTABILITY";
        case org.lwjgl.opengl.GL43.GL_DEBUG_TYPE_PERFORMANCE:
            return "PERFORMANCE";
        case org.lwjgl.opengl.GL43.GL_DEBUG_TYPE_OTHER:
            return "OTHER";
        case org.lwjgl.opengl.GL43.GL_DEBUG_TYPE_MARKER:
            return "MARKER";
        default:
            return apiUnknownToken(type);
        }
    }

    private static String getDebugSeverity(int severity) {
        switch (severity) {
        case org.lwjgl.opengl.GL43.GL_DEBUG_SEVERITY_HIGH:
            return "HIGH";
        case org.lwjgl.opengl.GL43.GL_DEBUG_SEVERITY_MEDIUM:
            return "MEDIUM";
        case org.lwjgl.opengl.GL43.GL_DEBUG_SEVERITY_LOW:
            return "LOW";
        case org.lwjgl.opengl.GL43.GL_DEBUG_SEVERITY_NOTIFICATION:
            return "NOTIFICATION";
        default:
            return apiUnknownToken(severity);
        }
    }

    private static String getSourceARB(int source) {
        switch (source) {
        case GL_DEBUG_SOURCE_API_ARB:
            return "API";
        case GL_DEBUG_SOURCE_WINDOW_SYSTEM_ARB:
            return "WINDOW SYSTEM";
        case GL_DEBUG_SOURCE_SHADER_COMPILER_ARB:
            return "SHADER COMPILER";
        case GL_DEBUG_SOURCE_THIRD_PARTY_ARB:
            return "THIRD PARTY";
        case GL_DEBUG_SOURCE_APPLICATION_ARB:
            return "APPLICATION";
        case GL_DEBUG_SOURCE_OTHER_ARB:
            return "OTHER";
        default:
            return apiUnknownToken(source);
        }
    }

    private static String getTypeARB(int type) {
        switch (type) {
        case GL_DEBUG_TYPE_ERROR_ARB:
            return "ERROR";
        case GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR_ARB:
            return "DEPRECATED BEHAVIOR";
        case GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR_ARB:
            return "UNDEFINED BEHAVIOR";
        case GL_DEBUG_TYPE_PORTABILITY_ARB:
            return "PORTABILITY";
        case GL_DEBUG_TYPE_PERFORMANCE_ARB:
            return "PERFORMANCE";
        case GL_DEBUG_TYPE_OTHER_ARB:
            return "OTHER";
        default:
            return apiUnknownToken(type);
        }
    }

    private static String getSeverityARB(int severity) {
        switch (severity) {
        case GL_DEBUG_SEVERITY_HIGH_ARB:
            return "HIGH";
        case GL_DEBUG_SEVERITY_MEDIUM_ARB:
            return "MEDIUM";
        case GL_DEBUG_SEVERITY_LOW_ARB:
            return "LOW";
        default:
            return apiUnknownToken(severity);
        }
    }

    private static String getCategoryAMD(int category) {
        switch (category) {
        case GL_DEBUG_CATEGORY_API_ERROR_AMD:
            return "API ERROR";
        case GL_DEBUG_CATEGORY_WINDOW_SYSTEM_AMD:
            return "WINDOW SYSTEM";
        case GL_DEBUG_CATEGORY_DEPRECATION_AMD:
            return "DEPRECATION";
        case GL_DEBUG_CATEGORY_UNDEFINED_BEHAVIOR_AMD:
            return "UNDEFINED BEHAVIOR";
        case GL_DEBUG_CATEGORY_PERFORMANCE_AMD:
            return "PERFORMANCE";
        case GL_DEBUG_CATEGORY_SHADER_COMPILER_AMD:
            return "SHADER COMPILER";
        case GL_DEBUG_CATEGORY_APPLICATION_AMD:
            return "APPLICATION";
        case GL_DEBUG_CATEGORY_OTHER_AMD:
            return "OTHER";
        default:
            return apiUnknownToken(category);
        }
    }

    private static String getSeverityAMD(int severity) {
        switch (severity) {
        case GL_DEBUG_SEVERITY_HIGH_AMD:
            return "HIGH";
        case GL_DEBUG_SEVERITY_MEDIUM_AMD:
            return "MEDIUM";
        case GL_DEBUG_SEVERITY_LOW_AMD:
            return "LOW";
        default:
            return apiUnknownToken(severity);
        }
    }

}