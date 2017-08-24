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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjglx.debug.Context;

public class DebugIT {

    private long window;
    private long window2;

    /*
     * "Polyfill" for JUnit 5's assertThrows.
     */
    public static void assertThrows(Class<? extends RuntimeException> exceptionClass, Runnable r) {
        assertThrows(exceptionClass, r, null);
    }

    public static void assertThrows(Class<? extends RuntimeException> exceptionClass, Runnable r, String message) {
        try {
            r.run();
            fail("Expected exception [" + exceptionClass + "] but none was thrown");
        } catch (RuntimeException e) {
            if (e.getClass() != exceptionClass)
                fail("Expected exception [" + exceptionClass + "] but got [" + e.getClass() + "]");
            if (message != null)
                assertEquals(message, e.getMessage());
        }
    }

    @BeforeClass
    public static void before() {
        glfwInit();
    }

    @AfterClass
    public static void after() {
        glfwTerminate();
    }

    @Before
    public void beforeEach() {
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
        assertThrows(IllegalStateException.class, () -> glBindFramebuffer(GL_FRAMEBUFFER, fbo), // <--- VAOs are NOT shared!
                "Trying to bind unknown FBO [1] from shared context [" + (Context.CURRENT_CONTEXT.get().counter - 1) + "]");
    }

    @Test
    public void testUniformWithoutBoundShader() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        assertThrows(IllegalStateException.class, () -> glUniform1f(1, 1.0f), "OpenGL function call raised an error (see stderr output)");
    }

}
