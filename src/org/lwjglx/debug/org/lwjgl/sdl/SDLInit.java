/*
 * (C) Copyright 2026 Kai Burjack
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lwjglx.debug.org.lwjgl.sdl;

import org.lwjglx.debug.Properties;
import org.lwjglx.debug.RT;

import static org.lwjglx.debug.Log.error;

public class SDLInit {

    public static boolean SDL_Init(int flags) {
        boolean ret = org.lwjgl.sdl.SDLInit.SDL_Init(flags);
        if (ret) {
            RT.sdlInitialized = true;
        } else {
            if (Properties.VALIDATE.enabled) {
                error("SDL_Init returned false");
            }
        }
        return ret;
    }

    public static boolean SDL_InitSubSystem(int flags) {
        boolean ret = org.lwjgl.sdl.SDLInit.SDL_InitSubSystem(flags);
        if (ret) {
            RT.sdlInitialized = true;
        } else {
            if (Properties.VALIDATE.enabled) {
                error("SDL_InitSubSystem returned false");
            }
        }
        return ret;
    }

    public static void SDL_Quit() {
        org.lwjgl.sdl.SDLInit.SDL_Quit();
        RT.sdlInitialized = false;
    }

    public static void SDL_QuitSubSystem(int flags) {
        org.lwjgl.sdl.SDLInit.SDL_QuitSubSystem(flags);
    }
}
