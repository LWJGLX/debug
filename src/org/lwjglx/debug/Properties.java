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

public class Properties {

    public static boolean DEBUG = getBooleanProperty("org.lwjglx.DEBUG", false);
    public static boolean TRACE = getBooleanProperty("org.lwjglx.TRACE", false);
    public static boolean PROFILE = getBooleanProperty("org.lwjglx.PROFILE", false);
    public static boolean NO_THROW_ON_ERROR = getBooleanProperty("org.lwjglx.NO_THROW", false);
    public static String OUTPUT = System.getProperty("org.lwjglx.OUTPUT", null);
    public static long SLEEP = getLongProperty("org.lwjglx.SLEEP", 0L);

    private static boolean getBooleanProperty(String prop, boolean def) {
        String value = System.getProperty(prop);
        if (value != null)
            return value.equals("") || Boolean.valueOf(value);
        return def;
    }

    private static long getLongProperty(String prop, long def) {
        String value = System.getProperty(prop);
        try {
            if (value != null)
                return Long.valueOf(value);
            return def;
        } catch (NumberFormatException e) {
            throw new AssertionError("System property [" + prop + "] is not an integer: " + value);
        }
    }

}
