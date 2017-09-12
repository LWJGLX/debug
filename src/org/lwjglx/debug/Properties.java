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

    public static class BooleanProperty {
        public boolean enabled;
        public boolean byDefault = true;

        public void enable() {
            this.enabled = true;
            this.byDefault = false;
        }

        public void disableIfByDefault() {
            if (this.byDefault) {
                this.enabled = false;
                this.byDefault = false;
            }
        }
    }

    public static final BooleanProperty VALIDATE = getBooleanProperty("org.lwjglx.VALIDATE", true);
    public static final BooleanProperty STRICT = getBooleanProperty("org.lwjglx.STRICT", false);
    public static final BooleanProperty DEBUG = getBooleanProperty("org.lwjglx.DEBUG", false);
    public static final BooleanProperty TRACE = getBooleanProperty("org.lwjglx.TRACE", false);
    public static final BooleanProperty PROFILE = getBooleanProperty("org.lwjglx.PROFILE", false);
    public static final BooleanProperty PROFILE_SUSPEND = new BooleanProperty();
    public static final BooleanProperty NO_THROW_ON_ERROR = getBooleanProperty("org.lwjglx.NO_THROW", false);
    public static String OUTPUT = System.getProperty("org.lwjglx.OUTPUT", null);
    public static long SLEEP = getLongProperty("org.lwjglx.SLEEP", 0L);

    private static BooleanProperty getBooleanProperty(String prop, boolean def) {
        String value = System.getProperty(prop);
        BooleanProperty p = new BooleanProperty();
        if (value != null) {
            p.byDefault = false;
            p.enabled = value.equals("") || Boolean.valueOf(value);
        } else {
            p.byDefault = true;
            p.enabled = def;
        }
        return p;
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
