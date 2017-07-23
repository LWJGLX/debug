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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;

public class MethodCall {

    private String source;
    private int line;
    private String name;

    private final List<String> params = new ArrayList<String>();
    private String returnValue;
    private boolean hasReturnValue;

    private String comment;

    public MethodCall(String source, int line, String name) {
        this.source = source;
        this.line = line;
        this.name = name;
    }

    public MethodCall param(int param) {
        params.add(Integer.toString(param));
        return this;
    }

    public MethodCall param(short param) {
        params.add(Short.toString(param));
        return this;
    }

    public MethodCall param(long param) {
        params.add(Long.toString(param) + "L");
        return this;
    }

    /**
     * See: https://stackoverflow.com/questions/17103660/print-string-with-escape-non-printable-characters
     */
    private static String removeUnicodeAndEscapeChars(String input) {
        StringBuilder buffer = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            if ((int) input.charAt(i) > 256) {
                buffer.append("\\u").append(Integer.toHexString((int) input.charAt(i)));
            } else {
                if (input.charAt(i) == '\n') {
                    buffer.append("\\n");
                } else if (input.charAt(i) == '\t') {
                    buffer.append("\\t");
                } else if (input.charAt(i) == '\r') {
                    buffer.append("\\r");
                } else if (input.charAt(i) == '\b') {
                    buffer.append("\\b");
                } else if (input.charAt(i) == '\f') {
                    buffer.append("\\f");
                } else if (input.charAt(i) == '\"') {
                    buffer.append("\\");
                } else if (input.charAt(i) == '\\') {
                    buffer.append("\\\\");
                } else {
                    buffer.append(input.charAt(i));
                }
            }
        }
        return buffer.toString();
    }

    private String printBuffer(Buffer buffer) {
        String type;
        if (buffer instanceof ByteBuffer) {
            type = "ByteBuffer";
        } else if (buffer instanceof ShortBuffer) {
            type = "ShortBuffer";
        } else if (buffer instanceof IntBuffer) {
            type = "IntBuffer";
        } else if (buffer instanceof LongBuffer) {
            type = "LongBuffer";
        } else if (buffer instanceof CharBuffer) {
            type = "CharBuffer";
        } else if (buffer instanceof FloatBuffer) {
            type = "FloatBuffer";
        } else if (buffer instanceof DoubleBuffer) {
            type = "DoubleBuffer";
        } else {
            type = "<UNKNOWN>";
        }
        if (buffer.isDirect()) {
            long address = MemoryUtil.memAddress0(buffer);
            int pos = buffer.position();
            int lim = buffer.limit();
            int cap = buffer.capacity();
            if (pos == 0 && lim == cap)
                return type + "[0x" + Long.toString(address, 16) + ", " + lim + "]";
            else
                return type + "[0x" + Long.toString(address, 16) + ", " + pos + ", " + lim + ", " + cap + "]";
        } else {
            int pos = buffer.position();
            int lim = buffer.limit();
            int cap = buffer.capacity();
            if (pos == 0 && lim == cap)
                return type + "[ND, " + lim + "]";
            else
                return type + "[ND, " + pos + ", " + lim + ", " + cap + "]";
        }
    }

    private String printBuffer(PointerBuffer buffer) {
        long address = MemoryUtil.memAddress0(buffer);
        int pos = buffer.position();
        int lim = buffer.limit();
        int cap = buffer.capacity();
        if (pos == 0 && lim == cap)
            return "PointerBuffer[0x" + Long.toString(address, 16) + ", " + lim + "]";
        else
            return "PointerBuffer[0x" + Long.toString(address, 16) + ", " + pos + ", " + lim + ", " + cap + "]";
    }

    public MethodCall param(Object param) {
        if (param instanceof String) {
            String string = (String) param;
            params.add("\"" + removeUnicodeAndEscapeChars(string) + "\"");
        } else if (param instanceof Buffer) {
            Buffer buffer = (Buffer) param;
            params.add(printBuffer(buffer));
        } else if (param instanceof PointerBuffer) {
            PointerBuffer buffer = (PointerBuffer) param;
            printBuffer(buffer);
        } else {
            params.add(String.valueOf(param));
        }
        return this;
    }

    public MethodCall paramEnum(String param) {
        params.add(String.valueOf(param));
        return this;
    }

    public MethodCall param(boolean param) {
        params.add(Boolean.toString(param));
        return this;
    }

    public MethodCall param(char param) {
        if (param == '\'') {
            params.add("'\\" + param + "'");
        } else {
            params.add("'" + param + "'");
        }
        return this;
    }

    public MethodCall param(float param) {
        params.add(Float.toString(param) + "f");
        return this;
    }

    public MethodCall param(double param) {
        params.add(Double.toString(param));
        return this;
    }

    public int returnValue(int val) {
        this.returnValue = Integer.toString(val);
        hasReturnValue = true;
        return val;
    }

    public short returnValue(short val) {
        this.returnValue = Short.toString(val);
        hasReturnValue = true;
        return val;
    }

    public long returnValue(long val) {
        this.returnValue = Long.toString(val) + "L";
        hasReturnValue = true;
        return val;
    }

    public boolean returnValue(boolean val) {
        this.returnValue = Boolean.toString(val);
        hasReturnValue = true;
        return val;
    }

    public char returnValue(char val) {
        if (val == '\'') {
            this.returnValue = "'\\" + val + "'";
        } else {
            this.returnValue = "'" + val + "'";
        }
        hasReturnValue = true;
        return val;
    }

    public Object returnValue(Object val) {
        if (val instanceof String) {
            String string = (String) val;
            this.returnValue = "\"" + removeUnicodeAndEscapeChars(string) + "\"";
        } else if (val instanceof Buffer) {
            Buffer buffer = (Buffer) val;
            this.returnValue = printBuffer(buffer);
        } else if (val instanceof PointerBuffer) {
            PointerBuffer buffer = (PointerBuffer) val;
            this.returnValue = printBuffer(buffer);
        } else {
            this.returnValue = String.valueOf(val);
        }
        hasReturnValue = true;
        return val;
    }

    public Object returnValueEnum(String val) {
        this.returnValue = val;
        hasReturnValue = true;
        return val;
    }

    public void comment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (source != null) {
            String sourceLine = "(" + source + ":" + line + ")";
            // expect at most 999 lines = hence 3 additional padding spaces
            int pad = Log.maxSourceLength;
            pad += 3; // <- account for parenthese and colon
            pad += Log.maxLineNumberLength;
            sourceLine = String.format("%1$-" + pad + "s", sourceLine);
            sb.append(sourceLine).append(" ");
        }
        sb.append(name).append("(");
        Iterator<String> it = params.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(")");
        if (hasReturnValue) {
            sb.append(" = ").append(String.valueOf(returnValue));
        }
        if (comment != null) {
            sb.append("  // ").append(comment);
        }
        return sb.toString();
    }

}
