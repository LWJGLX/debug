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
    public void testNoGLCapabilities() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        try {
            glEnable(GL_DEPTH_TEST); // <- should throw
            fail("glEnable should have thrown");
        } catch (IllegalStateException e) {
            // expected!
        }
    }

    @Test
    public void testUnsafeMethod() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        try {
            nglBufferData(GL_ARRAY_BUFFER, 0L, 0L, GL_STATIC_DRAW); // <- should throw
            fail("glEnable should have thrown");
        } catch (IllegalStateException e) {
            // expected!
        }
    }

    @Test
    public void testUnsupportedGLFunction() {
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        try {
            glVertexPointer(2, GL_FLOAT, 0, 0L);
            fail("glVertexPointer should have thrown");
        } catch (IllegalStateException e) {
            assertEquals("glVertexPointer is not supported in the current profile", e.getMessage());
        }
    }

    @Test
    public void testGLError() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        try {
            glEnable(GL_VERTEX_ARRAY_POINTER);
            fail("glEnable should have thrown");
        } catch (IllegalStateException e) {
            assertEquals("OpenGL function call raised an error (see stderr output)", e.getMessage());
        }
    }

    @Test
    public void testNoVertexAttribPointerDefaultVAO() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        try {
            glEnableVertexAttribArray(0);
            // glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, fb); // <--- FORGOT THIS!
            glDrawArrays(GL_POINTS, 0, 1);
            fail("glDrawArrays should have thrown");
        } catch (IllegalStateException e) {
            assertEquals("Vertex array [0] enabled but not initialized", e.getMessage());
        }
    }

    @Test
    public void testNoVertexAttribPointerInCustomVAO() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        try {
            int vao = glGenVertexArrays();
            glBindVertexArray(vao);
            glEnableVertexAttribArray(3);
            // glVertexAttribPointer(3, 2, GL_FLOAT, false, 0, fb); // <--- FORGOT THIS!
            glDrawArrays(GL_POINTS, 0, 1);
            fail("glDrawArrays should have thrown");
        } catch (IllegalStateException e) {
            assertEquals("Vertex array [3] enabled but not initialized", e.getMessage());
        }
    }

    @Test
    public void testNoVertexAttribPointerInCustomVAOWithIndicesBuffer() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        try {
            int vao = glGenVertexArrays();
            glBindVertexArray(vao);
            glEnableVertexAttribArray(3);
            // glVertexAttribPointer(3, 2, GL_FLOAT, false, 0, fb); // <--- FORGOT THIS!
            glDrawElements(GL_POINTS, BufferUtils.createIntBuffer(4));
            fail("glDrawElements should have thrown");
        } catch (IllegalStateException e) {
            assertEquals("Vertex array [3] enabled but not initialized", e.getMessage());
        }
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
        try {
            glDrawElements(GL_POINTS, 1, GL_UNSIGNED_INT, 0L);
            fail("glDrawElements should have thrown");
        } catch (IllegalStateException e) {
            assertEquals("glDrawElements called with index offset but no ELEMENT_ARRAY_BUFFER bound", e.getMessage());
        }
    }

    @Test
    public void testBufferDataWithNoDirectBuffer() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        try {
            int vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, ByteBuffer.wrap(new byte[] { 1, 2, 3, 4 }), GL_STATIC_DRAW);
            fail("glBufferData should have thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("buffer is not direct. Buffers created via ByteBuffer.allocate() or ByteBuffer.wrap() are not supported. Use BufferUtils.createByteBuffer() instead.", e.getMessage());
        }
    }

    @Test
    public void testUniformMatrixWithNoDirectBuffer() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        try {
            glUniformMatrix4fv(0, false, ByteBuffer.allocate(16 * 4).asFloatBuffer());
            fail("glUniformMatrix4fv should have thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("buffer is not direct. Buffers created via FloatBuffer.allocate() or FloatBuffer.wrap() are not supported. Use BufferUtils.createFloatBuffer() instead.", e.getMessage());
        }
    }

    @Test
    public void testWriteFloatBufferWithWrongByteOrder() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        FloatBuffer bb = ByteBuffer.allocateDirect(16 * 4).asFloatBuffer();
        bb.put(0, 1.0f);
        try {
            glLoadMatrixf(bb);
            fail("glLoadMatrixf should have thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("buffer contains values written using non-native endianness.", e.getMessage());
        }
    }

    @Test
    public void testUseFloatBufferViewOfByteBufferWithWrongByteOrder() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        ByteBuffer bb = ByteBuffer.allocateDirect(16 * 4);
        bb.putFloat(0, 1.0f);
        FloatBuffer fb = bb.asFloatBuffer();
        try {
            glLoadMatrixf(fb);
            fail("glLoadMatrixf should have thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("buffer contains values written using non-native endianness.", e.getMessage());
        }
    }

    @Test
    public void testUseSliceOfByteBufferWithWrongByteOrder() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        ByteBuffer bb = ByteBuffer.allocateDirect(16 * 4);
        bb.putFloat(0, 1.0f);
        FloatBuffer fb = bb.slice().asFloatBuffer();
        try {
            glLoadMatrixf(fb);
            fail("glLoadMatrixf should have thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("buffer contains values written using non-native endianness.", e.getMessage());
        }
    }

    @Test
    public void testWriteSliceOfByteBufferWithWrongByteOrder() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        ByteBuffer bb = ByteBuffer.allocateDirect(16 * 4);
        FloatBuffer fb = bb.asFloatBuffer().slice();
        fb.put(0, 1.0f);
        try {
            glLoadMatrixf(fb);
            fail("glLoadMatrixf should have thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("buffer contains values written using non-native endianness.", e.getMessage());
        }
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
        try {
            FloatBuffer fb = BufferUtils.createFloatBuffer(16);
            for (int i = 0; i < 16; i++) {
                fb.put(i);
            }
            glUniformMatrix4fv(0, false, fb);
            fail("glUniformMatrix4fv should have thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("buffer has no remaining elements. Did you forget to flip()/rewind() it?", e.getMessage());
        }
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
        try {
            int vao = glGenVertexArrays();
            glfwMakeContextCurrent(window2);
            glBindVertexArray(vao); // <--- VAOs are NOT shared!
            fail("glBindVertexArray should have thrown");
        } catch (IllegalStateException e) {
            assertEquals("Trying to bind unknown VAO [1] from shared context [" + (Context.CURRENT_CONTEXT.get().counter - 1) + "]", e.getMessage());
        }
    }

    @Test
    public void testBindFBOFromSharedContext() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        window2 = glfwCreateWindow(800, 600, "", 0L, window);
        glfwMakeContextCurrent(window);
        createCapabilities();
        try {
            int fbo = glGenFramebuffers();
            glfwMakeContextCurrent(window2);
            glBindFramebuffer(GL_FRAMEBUFFER, fbo); // <--- VAOs are NOT shared!
            fail("glBindFramebuffer should have thrown");
        } catch (IllegalStateException e) {
            assertEquals("Trying to bind unknown FBO [1] from shared context [" + (Context.CURRENT_CONTEXT.get().counter - 1) + "]", e.getMessage());
        }
    }

    @Test
    public void testUniformWithoutBoundShader() {
        window = glfwCreateWindow(800, 600, "", 0L, 0L);
        glfwMakeContextCurrent(window);
        createCapabilities();
        try {
            glUniform1f(1, 1.0f);
            fail("glUniform1f should have thrown");
        } catch (IllegalStateException e) {
            assertEquals("OpenGL function call raised an error (see stderr output)", e.getMessage());
        }
    }

}
