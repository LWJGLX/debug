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

import static org.lwjglx.debug.Context.*;
import static org.lwjglx.debug.Properties.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.function.Supplier;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Log {

    public static int maxSourceLength = 1;
    public static int maxLineNumberLength = 1;

    public static final PrintStream OUTPUT;
    static {
        PrintStream outStream = System.err;
        if (Properties.OUTPUT != null && !Properties.OUTPUT.trim().isEmpty()) {
            File out = new File(Properties.OUTPUT).getAbsoluteFile();
            if (!out.getParentFile().exists()) {
                throw new AssertionError("Directory to create log output file in does not exist: " + out.getParentFile().getAbsolutePath());
            } else {
                try {
                    OutputStream os = new FileOutputStream(out);
                    if (out.getName().toLowerCase().endsWith(".zip")) {
                        ZipOutputStream zos = new ZipOutputStream(os);
                        zos.setLevel(Deflater.BEST_COMPRESSION);
                        zos.putNextEntry(new ZipEntry(out.getName().substring(0, out.getName().length() - 4)));
                        os = zos;
                        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                            public void run() {
                                try {
                                    zos.closeEntry();
                                    zos.finish();
                                    zos.flush();
                                    zos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }));
                    } else if (out.getName().toLowerCase().endsWith(".gz")) {
                        GZIPOutputStream zos = new GZIPOutputStream(os);
                        os = zos;
                        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                            public void run() {
                                try {
                                    zos.finish();
                                    zos.flush();
                                    zos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }));
                    }
                    outStream = new PrintStream(os);
                } catch (Exception e) {
                    throw new AssertionError("Failed to create log output file: " + out.getAbsolutePath());
                }
            }
        }
        OUTPUT = outStream;
    }

    public static class DebugStreamFactory implements java.util.function.Supplier<java.io.PrintStream> {
        public PrintStream get() {
            return Log.OUTPUT;
        }
    }

    public static class LineBreakingStringBuilder {
        private final int indent = 4;
        private final int maxLength = 80;
        private final StringBuilder sb = new StringBuilder();
        private int currentLength = 0;

        public void append(String str) {
            if (currentLength + str.length() > maxLength) {
                sb.append("\n");
                for (int i = 0; i < indent; i++) {
                    sb.append(" ");
                }
                currentLength = 0;
            }
            sb.append(str);
            currentLength += str.length();
        }

        @Override
        public String toString() {
            return sb.toString();
        }
    }

    private static void log(String level, String message) {
        log(level, message, 2);
    }

    private static void log(String level, String message, int multiline) {
        log(level, message, multiline, null);
    }

    private static void log(String level, String message, Throwable t) {
        log(level, message, 2, t);
    }

    private static void log(String level, String message, int multiline, Throwable t) {
        Context context = CURRENT_CONTEXT.get();
        String prefix;
        if (context != null) {
            prefix = "[" + level + "][" + context.counter + "] ";
        } else {
            prefix = "[" + level + "] ";
        }
        int indent = prefix.length();
        StringBuilder msg = new StringBuilder();
        String[] lines = message.split("(\r)?\n");
        int numLines = lines.length;
        int maxLineNumberLength = Integer.toString(numLines).length();
        indent -= maxLineNumberLength + 2;
        int lineNumber = 1;
        for (String line : lines) {
            for (int i = 0; lineNumber >= multiline && i < indent; i++)
                msg.append(" ");
            if (lineNumber >= multiline) {
                msg.append(String.format("%1$" + maxLineNumberLength + "d", lineNumber - multiline + 1)).append("  ");
            }
            msg.append(line).append("\n");
            lineNumber++;
        }
        indent = 2;
        if (t != null) {
            for (int i = 0; i < indent; i++)
                msg.append(" ");
            indent += "Stacktrace: ".length();
            msg.append("Stacktrace: ");
            StackTraceElement[] st = t.getStackTrace();
            for (int i = 0; i < st.length; i++) {
                if (i > 0) {
                    for (int ii = 0; ii < indent; ii++)
                        msg.append(" ");
                }
                msg.append(st[i].toString()).append("\n");
            }
        }
        OUTPUT.print(prefix + msg.toString());
    }

    public static void info(String message) {
        log("info ", message, 2);
    }

    public static void info(String message, int multiline) {
        log("info ", message, multiline);
    }

    public static void debug(String message) {
        if (DEBUG)
            log("debug", message);
    }

    public static void error(String message) {
        error(message, null);
    }

    public static void error(String message, Throwable t) {
        log("error", message, t);
    }

    public static void trace(String message) {
        log("trace", message);
    }

}
