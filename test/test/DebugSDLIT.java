package test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lwjgl.sdl.SDLError;
import org.lwjgl.sdl.SDLHints;
import org.lwjgl.sdl.SDLInit;
import org.lwjgl.sdl.SDLStdinc;
import org.lwjgl.sdl.SDLTimer;
import org.lwjglx.debug.Properties;
import org.lwjglx.debug.org.lwjgl.opengl.Context;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL.setCapabilities;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY_POINTER;
import static org.lwjgl.opengl.GL11.glEnable;
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
import static org.lwjgl.sdl.SDLVideo.SDL_WINDOW_HIDDEN;
import static org.lwjgl.sdl.SDLVideo.SDL_WINDOW_OPENGL;

public class DebugSDLIT {

    private long window;
    private long window2;
    private long context;
    private long context2;
    private boolean isMac;

    static {
        Properties.STRICT.enable();
    }

    public static void assertThrowsWithMessage(Class<? extends RuntimeException> exceptionClass, Runnable r, Object message) {
        RuntimeException e = assertThrows(exceptionClass, () -> r.run());
        if (message != null) {
            if (message instanceof String)
                assertEquals((String) message, e.getMessage());
            else if (message instanceof Pattern)
                assertTrue(((Pattern)message).matcher(e.getMessage()).matches(), "Expect Regex [" + message.toString() + "] to match [" + e.getMessage() + "]");
        }
    }

    private boolean alreadyTerminated;

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        isMac = System.getProperty("os.name").toLowerCase().contains("mac");
        if (!SDL_Init(SDL_INIT_VIDEO)) {
            throw new IllegalStateException("Unable to initialize SDL: " + SDL_GetError());
        }
        SDL_GL_SetAttribute(SDL_GL_CONTEXT_MAJOR_VERSION, 3);
        SDL_GL_SetAttribute(SDL_GL_CONTEXT_MINOR_VERSION, 2);
        SDL_GL_SetAttribute(SDL_GL_CONTEXT_PROFILE_MASK, SDL_GL_CONTEXT_PROFILE_CORE);
    }

    @AfterEach
    public void afterEach() {
        if (context != 0L) {
            SDL_GL_DestroyContext(context);
            context = 0L;
        }
        if (context2 != 0L) {
            SDL_GL_DestroyContext(context2);
            context2 = 0L;
        }
        if (window != 0L) {
            SDL_DestroyWindow(window);
            window = 0L;
        }
        if (window2 != 0L) {
            SDL_DestroyWindow(window2);
            window2 = 0L;
        }
        setCapabilities(null);
        if (!alreadyTerminated) {
            SDL_Quit();
        }
    }

    @Test
    public void testQuitAndInit() {
        SDL_Quit();
        alreadyTerminated = true;
        assertTrue(SDL_Init(SDL_INIT_VIDEO));
        alreadyTerminated = false;
    }

    @Test
    public void testWrongWindowArgumentForContext() {
        assertThrows(IllegalArgumentException.class, () -> context = SDL_GL_CreateContext(12345L));
    }

    @Test
    public void testWrongWindowArgumentForMakeCurrent() {
        window = SDL_CreateWindow("Test", 300, 300, SDL_WINDOW_OPENGL | SDL_WINDOW_HIDDEN);
        assertNotNull(window);
        context = SDL_GL_CreateContext(window);
        assertNotNull(context);
        assertThrows(IllegalArgumentException.class, () -> SDL_GL_MakeCurrent(12345L, context));
    }

    @Test
    public void testWrongContextArgumentForMakeCurrent() {
        window = SDL_CreateWindow("Test", 300, 300, SDL_WINDOW_OPENGL | SDL_WINDOW_HIDDEN);
        assertNotNull(window);
        assertThrows(IllegalArgumentException.class, () -> SDL_GL_MakeCurrent(window, 12345L));
    }

    @Test
    public void testNoGLCapabilities() {
        window = SDL_CreateWindow("Test", 300, 300, SDL_WINDOW_OPENGL | SDL_WINDOW_HIDDEN);
        context = SDL_GL_CreateContext(window);
        assertTrue(SDL_GL_MakeCurrent(window, context));
        assertThrows(IllegalStateException.class, () -> glEnable(GL_DEPTH_TEST));
    }

    @Test
    public void testNoCurrentContext() {
        assertThrowsWithMessage(IllegalStateException.class, () -> Context.currentContext(),
                "No OpenGL context has been made current through recognized API methods (glfwMakeContextCurrent or SDL_GL_MakeCurrent).");
    }

    @Test
    public void testCurrentInAnotherThread() throws Exception {
        CountDownLatch l1 = new CountDownLatch(1);
        CountDownLatch l2 = new CountDownLatch(1);
        window = SDL_CreateWindow("Test", 300, 300, SDL_WINDOW_OPENGL | SDL_WINDOW_HIDDEN);
        context = SDL_GL_CreateContext(window);
        
        Thread t = new Thread() {
            public void run() {
                SDL_GL_MakeCurrent(window, context);
                l1.countDown();
                try {
                    l2.await();
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();
        l1.await();
        
        assertThrowsWithMessage(IllegalStateException.class, () -> SDL_GL_MakeCurrent(window, context),
                Pattern.compile("Context of context\\[\\d+\\] is current in another thread \\[.*\\]"));
        l2.countDown();
        t.join();
    }

    @Test
    public void testGLError() {
        window = SDL_CreateWindow("Test", 300, 300, SDL_WINDOW_OPENGL | SDL_WINDOW_HIDDEN);
        context = SDL_GL_CreateContext(window);
        assertTrue(SDL_GL_MakeCurrent(window, context));
        createCapabilities();
        assertThrowsWithMessage(IllegalStateException.class, () -> glEnable(GL_VERTEX_ARRAY_POINTER), Pattern.compile("glEnable produced error: 1280 \\(GL_INVALID_ENUM\\)"));
    }

    @Test
    public void testPreInitAllowed() {
        SDL_Quit();
        alreadyTerminated = true;
        assertTrue(SDLHints.SDL_SetHint("SDL_VIDEO_DRIVER", "dummy"));
        assertEquals("dummy", SDLHints.SDL_GetHint("SDL_VIDEO_DRIVER"));
        SDLError.SDL_GetError();
        ByteBuffer buf = SDLStdinc.SDL_malloc(16);
        if (buf != null) {
            SDLStdinc.SDL_free(buf);
        }
        assertTrue(SDL_Init(SDL_INIT_VIDEO));
        alreadyTerminated = false;
        window = SDL_CreateWindow("Test PreInit Allowed", 320, 240, SDL_WINDOW_OPENGL | SDL_WINDOW_HIDDEN);
        assertNotNull(window);
    }

    @Test
    public void testSubsystemRefcounting() {
        SDL_Quit();
        alreadyTerminated = true;
        assertTrue(SDL_Init(SDL_INIT_VIDEO));
        assertTrue(SDL_Init(SDL_INIT_VIDEO));
        alreadyTerminated = false;
        window = SDL_CreateWindow("Test Refcount 1", 320, 240, SDL_WINDOW_OPENGL | SDL_WINDOW_HIDDEN);
        assertNotNull(window);
        SDLInit.SDL_QuitSubSystem(SDL_INIT_VIDEO);
        window2 = SDL_CreateWindow("Test Refcount 2", 320, 240, SDL_WINDOW_OPENGL | SDL_WINDOW_HIDDEN);
        assertNotNull(window2);
        SDL_DestroyWindow(window);
        window = 0L;
        SDL_DestroyWindow(window2);
        window2 = 0L;
        SDLInit.SDL_QuitSubSystem(SDL_INIT_VIDEO);
        alreadyTerminated = true;
        assertThrows(IllegalStateException.class, () -> SDL_CreateWindow("Should Fail", 320, 240, SDL_WINDOW_OPENGL | SDL_WINDOW_HIDDEN));
    }

    @Test
    public void testInitZero() {
        SDL_Quit();
        alreadyTerminated = true;
        assertTrue(SDL_Init(0));
        // core/timer method should succeed
        long ticks = SDLTimer.SDL_GetTicks();
        assertTrue(ticks >= 0L);
        // video method should throw IllegalStateException because video subsystem is not initialized
        assertThrows(IllegalStateException.class, () -> SDL_CreateWindow("Should Fail", 320, 240, SDL_WINDOW_OPENGL | SDL_WINDOW_HIDDEN));
    }
}
