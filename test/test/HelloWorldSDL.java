package test;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.sdl.SDLError.SDL_GetError;
import static org.lwjgl.sdl.SDLInit.SDL_INIT_VIDEO;
import static org.lwjgl.sdl.SDLInit.SDL_Init;
import static org.lwjgl.sdl.SDLInit.SDL_Quit;
import static org.lwjgl.sdl.SDLVideo.SDL_CreateWindow;
import static org.lwjgl.sdl.SDLVideo.SDL_DestroyWindow;
import static org.lwjgl.sdl.SDLVideo.SDL_GL_CONTEXT_MAJOR_VERSION;
import static org.lwjgl.sdl.SDLVideo.SDL_GL_CONTEXT_MINOR_VERSION;
import static org.lwjgl.sdl.SDLVideo.SDL_GL_CONTEXT_PROFILE_CORE;
import static org.lwjgl.sdl.SDLVideo.SDL_GL_CONTEXT_PROFILE_MASK;
import static org.lwjgl.sdl.SDLVideo.SDL_GL_CreateContext;
import static org.lwjgl.sdl.SDLVideo.SDL_GL_DestroyContext;
import static org.lwjgl.sdl.SDLVideo.SDL_GL_MakeCurrent;
import static org.lwjgl.sdl.SDLVideo.SDL_GL_SetAttribute;
import static org.lwjgl.sdl.SDLVideo.SDL_GL_SwapWindow;
import static org.lwjgl.sdl.SDLVideo.SDL_WINDOW_OPENGL;
import static org.lwjgl.system.MemoryUtil.NULL;

public class HelloWorldSDL {

    public void run() {
        System.out.println("Hello LWJGL SDL3 " + Version.getVersion() + "!");

        if (!SDL_Init(SDL_INIT_VIDEO)) {
            throw new IllegalStateException("Unable to initialize SDL: " + SDL_GetError());
        }

        // Configure SDL GL attributes
        SDL_GL_SetAttribute(SDL_GL_CONTEXT_MAJOR_VERSION, 3);
        SDL_GL_SetAttribute(SDL_GL_CONTEXT_MINOR_VERSION, 2);
        SDL_GL_SetAttribute(SDL_GL_CONTEXT_PROFILE_MASK, SDL_GL_CONTEXT_PROFILE_CORE);

        long window = SDL_CreateWindow("Hello SDL3!", 300, 300, SDL_WINDOW_OPENGL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the SDL window: " + SDL_GetError());
        }

        long context = SDL_GL_CreateContext(window);
        if (context == NULL) {
            throw new RuntimeException("Failed to create the OpenGL context: " + SDL_GetError());
        }

        if (!SDL_GL_MakeCurrent(window, context)) {
            throw new RuntimeException("Failed to make OpenGL context current: " + SDL_GetError());
        }

        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 1.0f, 0.0f, 0.0f); // Green for SDL!

        // Draw a few frames and exit
        for (int i = 0; i < 60; i++) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            SDL_GL_SwapWindow(window);
        }

        SDL_GL_DestroyContext(context);
        SDL_DestroyWindow(window);
        SDL_Quit();
    }

    public static void main(String[] args) {
        new HelloWorldSDL().run();
    }
}
