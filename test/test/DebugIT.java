package test;

import static org.junit.Assert.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.lwjglx.debug.Context;
import org.lwjglx.debug.Properties;

public class DebugIT {

    private long window;
    private long window2;

    static {
        /*
         * Also perform validations that are OS-dependent.
         */
        Properties.STRICT.enable();
    }

    /*
     * "Polyfill" for JUnit 5's assertThrows.
     */
    public static void assertThrows(Class<? extends RuntimeException> exceptionClass, Runnable r) {
        assertThrows(exceptionClass, r, null);
    }

    public static void assertThrows(Class<? extends RuntimeException> exceptionClass, Runnable r, Object message) {
        try {
            r.run();
            fail("Expected exception [" + exceptionClass + "] but none was thrown");
        } catch (RuntimeException e) {
            if (e.getClass() != exceptionClass)
                fail("Expected exception [" + exceptionClass + "] but got [" + e.getClass() + "]");
            if (message != null) {
                if (message instanceof String)
                    assertEquals((String) message, e.getMessage());
                else if (message instanceof Pattern)
                    assertTrue("Expect Regex [" + message.toString() + "] to match [" + e.getMessage() + "]", ((Pattern)message).matcher(e.getMessage()).matches());
            }
        }
    }

    private boolean alreadyTerminated;

    @Before
    public void beforeEach() {
        glfwInit();
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    }

    @After
    public void afterEach() {
        if (window != 0L)
            glfwDestroyWindow(window);
        if (window2 != 0L)
            glfwDestroyWindow(window2);
        setCapabilities(null);
        GLFWErrorCallback callback = glfwSetErrorCallback(null);
        if (callback != null)
            callback.free();
        if (!alreadyTerminated)
            glfwTerminate();
    }

    @Test
    public void freeErrorCallbackAndTerminate() {
        assertNull(glfwSetErrorCallback(null));
        GLFWErrorCallback callback = GLFWErrorCallback.createPrint(System.err).set();
        assertNotNull(glfwSetErrorCallback(callback));
        glfwSetErrorCallback(null).free();
        alreadyTerminated = true;
        glfwTerminate();
        assertNull(glfwSetErrorCallback(null));
    }

    @Test
    public void terminateAndFreeErrorCallback() {
        assertNull(glfwSetErrorCallback(null));
        GLFWErrorCallback callback = GLFWErrorCallback.createPrint(System.err).set();
        assertNotNull(glfwSetErrorCallback(callback));
        alreadyTerminated = true;
        glfwTerminate();
        assertNotNull(glfwSetErrorCallback(callback));
        glfwSetErrorCallback(null).free();
        assertNull(glfwSetErrorCallback(null));
    }

    @Test
    public void testWrongMonitorArgument() {
        assertThrows(IllegalArgumentException.class, () -> window = glfwCreateWindow(800, 600, "", 1L, 0L));
    }

    @Test
    public void testWrongShareArgument() {
        assertThrows(IllegalArgumentException.class, () -> window = glfwCreateWindow(800, 600, "", 0L, 1L));
    }

    @Test
    public void testCorrectShareArgument() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        window2 = glfwCreateWindow(800, 600, "", 0L, window); // <- MUST NOT THROW
    }

    @Test
    public void testNoGLCapabilities() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        assertThrows(IllegalStateException.class, () -> glEnable(GL_DEPTH_TEST));
    }

    @Test
    public void testShareCurrentInAnotherThread() throws Exception {
        CountDownLatch l1 = new CountDownLatch(1);
        CountDownLatch l2 = new CountDownLatch(1);
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        Thread t = new Thread() {
            public void run() {
                glfwMakeContextCurrent(window);
                l1.countDown();
                try {
                    l2.await();
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();
        l1.await();
        assertThrows(IllegalStateException.class, () -> window2 = glfwCreateWindow(800, 600, "", 0L, window), 
                Pattern.compile("Context of share window\\[\\d+\\] is current in another thread \\[.*\\]"));
        l2.countDown();
        t.join();
    }

    @Test
    public void testCurrentInAnotherThread() throws Exception {
        CountDownLatch l1 = new CountDownLatch(1);
        CountDownLatch l2 = new CountDownLatch(1);
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        Thread t = new Thread() {
            public void run() {
                glfwMakeContextCurrent(window);
                l1.countDown();
                try {
                    l2.await();
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();
        l1.await();
        assertThrows(IllegalStateException.class, () -> glfwMakeContextCurrent(window), 
                Pattern.compile("Context of window\\[\\d+\\] is current in another thread \\[.*\\]"));
        l2.countDown();
        t.join();
    }

    @Test
    public void testUnsafeMethod() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        assertThrows(IllegalStateException.class, () -> nglBufferData(GL_ARRAY_BUFFER, 0L, 0L, GL_STATIC_DRAW));
    }

    @Test
    public void testUnsupportedGLFunction() {
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        assertThrows(IllegalStateException.class, () -> glVertexPointer(2, GL_FLOAT, 0, 0L), "glVertexPointer is not supported in the current profile");
    }

    @Test
    public void testGLError() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        assertThrows(IllegalStateException.class, () -> glEnable(GL_VERTEX_ARRAY_POINTER), "OpenGL function call raised an error (see stderr output)");
    }

    @Test
    public void testNoVertexAttribPointerDefaultVAO() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        glEnableVertexAttribArray(0);
        assertThrows(IllegalStateException.class, () -> glDrawArrays(GL_POINTS, 0, 1), "Vertex array [0] enabled but not initialized");
    }

    @Test
    public void testNoVertexAttribPointerDefaultVAOwithGL11C() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        glEnableVertexAttribArray(0);
        assertThrows(IllegalStateException.class, () -> GL11C.glDrawArrays(GL_POINTS, 0, 1), "Vertex array [0] enabled but not initialized");
    }

    @Test
    public void testNoVertexAttribPointerInCustomVAO() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        int vao = glGenVertexArrays();
        glBindVertexArray(vao);
        glEnableVertexAttribArray(3);
        assertThrows(IllegalStateException.class, () -> glDrawArrays(GL_POINTS, 0, 1), "Vertex array [3] enabled but not initialized");
    }

    @Test
    public void testNoVertexAttribPointerInCustomVAOWithIndicesBuffer() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        int vao = glGenVertexArrays();
        glBindVertexArray(vao);
        glEnableVertexAttribArray(3);
        assertThrows(IllegalStateException.class, () -> glDrawElements(GL_POINTS, BufferUtils.createIntBuffer(4)), "Vertex array [3] enabled but not initialized");
    }

    @Test
    public void testVertexArrayEnabledAndInitializedDefaultVAO() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        glEnableClientState(GL_VERTEX_ARRAY);
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, 2 * 4, GL_STATIC_DRAW);
        glVertexPointer(2, GL_FLOAT, 0, 0L);
        glDrawArrays(GL_POINTS, 0, 1);
    }

    @Test
    public void testVertexArrayEnabledButNotInitializedDefaultVAO() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        glEnableClientState(GL_VERTEX_ARRAY);
        assertThrows(IllegalStateException.class, () -> glDrawArrays(GL_POINTS, 0, 1), "GL_VERTEX_ARRAY enabled but not initialized");
    }

    @Test
    public void testNormalArrayEnabledButNotInitializedDefaultVAO() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        glEnableClientState(GL_NORMAL_ARRAY);
        assertThrows(IllegalStateException.class, () -> glDrawArrays(GL_POINTS, 0, 1), "GL_NORMAL_ARRAY enabled but not initialized");
    }

    @Test
    public void testTexCoordArrayEnabledAndInitializedDefaultVAO() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, 2 * 4, GL_STATIC_DRAW);
        glTexCoordPointer(2, GL_FLOAT, 0, 0L);
        glDrawArrays(GL_POINTS, 0, 1);
    }

    @Test
    public void testTexCoordArrayEnabledButNotInitializedDefaultVAO() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        assertThrows(IllegalStateException.class, () -> glDrawArrays(GL_POINTS, 0, 1), "GL_TEXTURE_COORD_ARRAY enabled but not initialized");
    }

    @Test
    public void testCorrectVertexAttribPointerViaVBO() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        glEnableVertexAttribArray(0);
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, 2 * 4, GL_STATIC_DRAW);
        /* The following should not throw because the vertex array was correctly initialized via VBO */
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0L);
        glDrawArrays(GL_POINTS, 0, 1);
    }

    @Test
    public void testNoVertexAttribPointerInOtherVAO() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        glEnableVertexAttribArray(0);
        int vao = glGenVertexArrays();
        glBindVertexArray(vao);
        glDrawArrays(GL_POINTS, 0, 1); // <--- MUST NOT THROW
        glDeleteVertexArrays(new int[] { vao });
    }

    @Test
    public void testNoVertexAttribPointerInOtherVAOViaExtension() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        ARBVertexShader.glEnableVertexAttribArrayARB(0);
        int vao = ARBVertexArrayObject.glGenVertexArrays();
        ARBVertexArrayObject.glBindVertexArray(vao);
        glDrawArrays(GL_POINTS, 0, 1); // <--- MUST NOT THROW
        ARBVertexArrayObject.glDeleteVertexArrays(new int[] { vao });
    }

    @Test
    public void testEnabledAndDisabledVertexAttribute() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        glEnableVertexAttribArray(0);
        glDisableVertexAttribArray(0);
        glDrawArrays(GL_POINTS, 0, 1); // <--- MUST NOT THROW
    }

    @Test
    public void testDrawElementsWithOffsetWithoutBuffer() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        assertThrows(IllegalStateException.class, () -> glDrawElements(GL_POINTS, 1, GL_UNSIGNED_INT, 0L), "glDrawElements called with index offset but no ELEMENT_ARRAY_BUFFER bound");
    }

    @Test
    public void testBufferDataWithNoDirectBuffer() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        assertThrows(IllegalArgumentException.class, () -> glBufferData(GL_ARRAY_BUFFER, ByteBuffer.wrap(new byte[] { 1, 2, 3, 4 }), GL_STATIC_DRAW),
                "buffer is not direct. Buffers created via ByteBuffer.allocate() or ByteBuffer.wrap() are not supported. Use BufferUtils.createByteBuffer() instead.");
    }

    @Test
    public void testUniformMatrixWithNoDirectBuffer() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        assertThrows(IllegalArgumentException.class, () -> glUniformMatrix4fv(0, false, ByteBuffer.allocate(16 * 4).asFloatBuffer()),
                "buffer is not direct. Buffers created via FloatBuffer.allocate() or FloatBuffer.wrap() are not supported. Use BufferUtils.createFloatBuffer() instead.");
    }

    @Test
    public void testWriteFloatBufferWithWrongByteOrder() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        FloatBuffer bb = ByteBuffer.allocateDirect(16 * 4).asFloatBuffer();
        bb.put(0, 1.0f);
        assertThrows(IllegalArgumentException.class, () -> glLoadMatrixf(bb), "buffer contains values written using non-native endianness.");
    }

    @Test
    public void testUseFloatBufferViewOfByteBufferWithWrongByteOrder() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        ByteBuffer bb = ByteBuffer.allocateDirect(16 * 4);
        bb.putFloat(0, 1.0f);
        FloatBuffer fb = bb.asFloatBuffer();
        assertThrows(IllegalArgumentException.class, () -> glLoadMatrixf(fb), "buffer contains values written using non-native endianness.");
    }

    @Test
    public void testUseSliceOfByteBufferWithWrongByteOrder() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        ByteBuffer bb = ByteBuffer.allocateDirect(16 * 4);
        bb.putFloat(0, 1.0f);
        FloatBuffer fb = bb.slice().asFloatBuffer();
        assertThrows(IllegalArgumentException.class, () -> glLoadMatrixf(fb), "buffer contains values written using non-native endianness.");
    }

    @Test
    public void testWriteSliceOfByteBufferWithWrongByteOrder() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        ByteBuffer bb = ByteBuffer.allocateDirect(16 * 4);
        FloatBuffer fb = bb.asFloatBuffer().slice();
        fb.put(0, 1.0f);
        assertThrows(IllegalArgumentException.class, () -> glLoadMatrixf(fb), "buffer contains values written using non-native endianness.");
    }

    @Test
    public void testFloatBufferWithWrongByteOrderNotWrittenTo() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        FloatBuffer bb = ByteBuffer.allocateDirect(16 * 4).asFloatBuffer();
        glLoadMatrixf(bb); // <- should NOT throw
    }

    @Test
    public void testFloatBufferViewOfByteBufferSlice() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        ByteBuffer bb = ByteBuffer.allocateDirect(16 * 4);
        ByteBuffer bb2 = bb.slice().order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb2.asFloatBuffer().slice();
        fb.put(0, 1.0f);
        glLoadMatrixf(fb); // <- should NOT throw
    }

    @Test
    public void testBufferNoRemaining() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        for (int i = 0; i < 16; i++) {
            fb.put(i);
        }
        assertThrows(IllegalArgumentException.class, () -> glUniformMatrix4fv(0, false, fb), "buffer has no remaining elements. Did you forget to flip()/rewind() it?");
    }

    @Test
    public void testBufferZeroCapacity() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        FloatBuffer fb = BufferUtils.createFloatBuffer(0);
        assertThrows(IllegalArgumentException.class, () -> glBufferData(GL_VERTEX_ARRAY, fb, GL_STATIC_DRAW), 
                "buffer has zero capacity. If you want to clear an OpenGL buffer object, use GL15.glBufferData(target, size=0, usage) instead.");
    }

    @Test
    public void testPopClientAttrib() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        glPushClientAttrib(GL_CLIENT_VERTEX_ARRAY_BIT);
        glEnableVertexAttribArray(0);
        glPopClientAttrib();
        glDrawArrays(GL_POINTS, 0, 1); // <--- MUST NOT THROW
    }

    @Test
    public void testBindTextureFromSharedContext() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        window2 = glfwCreateWindow(800, 600, "", 0L, window);
        glfwMakeContextCurrent(window);
        createCapabilities();
        int tex = glGenTextures();
        glfwMakeContextCurrent(window2);
        glBindTexture(GL_TEXTURE_2D, tex);
    }

    @Test
    public void testBindProgramFromSharedContext() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        window2 = glfwCreateWindow(800, 600, "", 0L, window);
        glfwMakeContextCurrent(window);
        createCapabilities();
        int prog = glCreateProgram();
        int vs = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vs, "#version 110\nvoid main(void) {gl_Position=vec4(1.0);}");
        glCompileShader(vs);
        glAttachShader(prog, vs);
        glLinkProgram(prog);
        glfwMakeContextCurrent(window2);
        glUseProgram(prog);
    }

    @Test
    public void testBindVAOFromSharedContext() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        window2 = glfwCreateWindow(800, 600, "", 0L, window);
        glfwMakeContextCurrent(window);
        createCapabilities();
        int vao = glGenVertexArrays();
        glfwMakeContextCurrent(window2);
        assertThrows(IllegalStateException.class, () -> glBindVertexArray(vao), // <--- VAOs are NOT shared!,
                "Trying to bind unknown VAO [1] from shared context [" + (Context.CURRENT_CONTEXT.get().counter - 1) + "]");
    }

    @Test
    public void testBindFBOFromSharedContext() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        window2 = glfwCreateWindow(800, 600, "", 0L, window);
        glfwMakeContextCurrent(window);
        createCapabilities();
        int fbo = glGenFramebuffers();
        glfwMakeContextCurrent(window2);
        assertThrows(IllegalStateException.class, () -> glBindFramebuffer(GL_FRAMEBUFFER, fbo), // <--- FBOs are NOT shared!
                        "Trying to bind unknown FBO [1] from shared context [" + (Context.CURRENT_CONTEXT.get().counter - 1) + "]");
    }

    @Test
    public void testUniformWithoutBoundShader() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        assertThrows(IllegalStateException.class, () -> glUniform1f(1, 1.0f), "OpenGL function call raised an error (see stderr output)");
    }

    @Test
    public void testNullInNonNullParameter() {
        assertThrows(IllegalArgumentException.class, () -> glfwCreateWindow(800, 600, (String) null, 0L, 0L),
                "Argument for 3. parameter 'title' must not be null");
    }

    @Test
    public void testNullInNullableParameter() {
        GL.setCapabilities(null);
    }

    @Test
    public void testFlipOnBufferPosition0() {
        ByteBuffer bb = ByteBuffer.allocateDirect(16 * 4);
        assertThrows(IllegalStateException.class, () -> bb.flip());
    }

    @Test
    public void testMemReallocAllowedOnNoRemainingBuffer() {
        ByteBuffer bb = MemoryUtil.memAlloc(1);
        bb.put((byte) 0);
        bb = MemoryUtil.memRealloc(bb, 2);
        MemoryUtil.memFree(bb);
    }

    @Test
    public void testMemFreeAllowedOnNoRemainingBuffer() {
        ByteBuffer bb = MemoryUtil.memAlloc(1);
        bb.put((byte) 0);
        MemoryUtil.memFree(bb);
    }

    @Test
    public void testJvmArgumentAsCommandLineArgument() {
        assertThrows(IllegalStateException.class, () -> main(new String[] {"-XstartOnFirstThread"}),
                        "'-XstartOnFirstThread' was provided as command line argument instead of JVM parameter. "
                        + "Make sure to specify '-XstartOnFirstThread' before any '-jar' argument");
        assertThrows(IllegalStateException.class, () -> main(new String[] {"-Djava.library.path=./some/path"}),
                        "'-Djava.library.path=./some/path' was provided as command line argument instead of JVM parameter. "
                        + "Make sure to specify '-Djava.library.path=./some/path' before any '-jar' argument");
    }

    @Test
    public void testGlErrorInMainMethod() {
        assertThrows(IllegalStateException.class, () -> main(new String[0]), "OpenGL function call raised an error (see stderr output)");
    }

    public static void main(String[] args) {
        // Will be instrumented for checking JVM argument as command line argument

        // Also check some GL errors
        long window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        glEnable(GL_VERTEX_ARRAY_POINTER);
    }

}
