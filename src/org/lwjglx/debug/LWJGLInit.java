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

class LWJGLInit {

    static {
        /* Set properties (possibly overriding -D command line arguments) */
        System.setProperty("org.lwjgl.util.Debug", "true");
        System.setProperty("org.lwjgl.util.NoChecks", "false");
        System.setProperty("org.lwjgl.util.DebugLoader", "true");
        System.setProperty("org.lwjgl.util.DebugAllocator", "true");
        System.setProperty("org.lwjgl.util.DebugStack", "true");
        /* And ensure that user code cannot change them via setProperty */
        org.lwjgl.system.Configuration.DEBUG.hashCode();
        /* Set the debug stream */
        org.lwjgl.system.Configuration.DEBUG_STREAM.set("org.lwjglx.debug.Log$DebugStreamFactory");
    }

    static void touch() {}

}
