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
package org.lwjglx.debug.opengl;

import static org.lwjglx.debug.Log.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.lwjgl.opengl.ARBOcclusionQuery;
import org.lwjglx.debug.*;

public class Context implements Comparable<Context> {
    public static class ShareGroup {
        public Map<Integer, BufferObject> bufferObjects = new HashMap<>();
        public Map<Integer, TextureObject> textureObjects = new HashMap<>();
        public Set<Context> contexts = new ConcurrentSkipListSet<Context>();
    }

    public static class VAO {
        public boolean[] enabledVertexArrays;
        public boolean[] initializedVertexArrays;
        public boolean vertexArrayEnabled;
        public boolean vertexArrayInitialized;
        public boolean normalArrayEnabled;
        public boolean normalArrayInitialized;
        public boolean colorArrayEnabled;
        public boolean colorArrayInitialized;
        public boolean texCoordArrayEnabled;
        public boolean texCoordArrayInitialized;

        public VAO(int GL_MAX_VERTEX_ATTRIBS) {
            this.enabledVertexArrays = new boolean[GL_MAX_VERTEX_ATTRIBS];
            this.initializedVertexArrays = new boolean[GL_MAX_VERTEX_ATTRIBS];
        }
    }

    public static class FBO {
        public int handle;
        public FBO(int handle) {
            this.handle = handle;
        }
    }

    public static class ProgramPipeline {
    }

    public static class BufferObject {
        public long size;
    }

    public static class TextureLevel {
        public long size;
        public int internalformat;
        public int width;
        public int height;
    }

    public static class TextureLayer {
        public TextureLevel[] levels;
        public void ensureLevel(int level) {
            if (levels == null) {
                levels = new TextureLevel[level + 1];
            } else if (levels.length <= level) {
                TextureLevel[] newLevels = new TextureLevel[level + 1];
                System.arraycopy(levels, 0, newLevels, 0, levels.length);
                levels = newLevels;
            }
            for (int i = 0; i < levels.length; i++) {
                if (levels[i] == null)
                    levels[i] = new TextureLevel();
            }
        }
    }

    public static class TextureObject {
        public TextureLayer[] layers;
        public boolean generateMipmap;
    }

    public static class TimingQuery {
        public int before;
        public int after;
        public long time0;
        public long time1;
        public boolean used;
        public boolean drawTime;
    }

    public static class TimedCodeSection {
        public List<TimingQuery> queries = new ArrayList<>();
        public String name;
    }

    public static final ThreadLocal<Context> CURRENT_CONTEXT = new ThreadLocal<Context>();
    public static final Map<Long, Context> CONTEXTS = new ConcurrentHashMap<Long, Context>();
    public static final Map<Long, ShareGroup> SHARE_GROUPS = new ConcurrentHashMap<Long, ShareGroup>();
    private static final AtomicInteger CONTEXT_COUNTER = new AtomicInteger(1);

    public org.lwjgl.opengl.GLCapabilities caps;
    public boolean inited;
    public int GL_MAX_VERTEX_ATTRIBS;
    public long window;
    public int counter;
    public org.lwjgl.system.Callback debugCallback;
    public VAO defaultVao;
    public VAO currentVao;
    public FBO defaultFbo;
    public FBO currentFbo;
    public ProgramPipeline defaultProgramPipeline;
    public ProgramPipeline currentProgramPipeline;
    public Map<Integer, VAO> vaos = new HashMap<Integer, VAO>();
    public Map<Integer, FBO> fbos = new HashMap<Integer, FBO>();
    public Map<Integer, BufferObject> bufferObjectBindings = new HashMap<>();
    public Map<Integer, TextureObject> textureObjectBindings = new HashMap<>();
    public Map<Integer, ProgramPipeline> programPipelines = new HashMap<>();
    public ShareGroup shareGroup;
    public boolean inImmediateMode;
    public Thread currentInThread;
    /* per frame info */
    public boolean drawCallSeen;

    /* Profiling (per frame info) */
    public long frameStartTime;
    public long frameEndTime;
    public float drawCallTimeMs;
    public int glCallCount;
    public int immediateModeVertices;
    public int frame;
    public List<TimingQuery> timingQueries = new ArrayList<TimingQuery>(32);
    public TimingQuery currentTimingQuery;
    public boolean firstFrameSeen;
    public TimingQuery lastCodeSectionQuery;
    public int currentCodeSectionIndex = 0;
    public List<TimedCodeSection> codeSectionTimes = new ArrayList<>();

    public static Context currentContext() {
    	Context ctx = CURRENT_CONTEXT.get();
    	if (ctx == null) {
    		RT.throwISEOrLogError("No OpenGL context has been made current through recognized API methods (glfwMakeContextCurrent).");
    	}
    	return ctx;
    }

    public static void create(long window, long share) {
        Context ctx = new Context();
        ctx.window = window;
        ctx.counter = CONTEXT_COUNTER.getAndIncrement();
        if (share != 0L) {
            Context shareContext = CONTEXTS.get(share);
            ShareGroup shareGroup = SHARE_GROUPS.get(share);
            if (shareGroup == null) {
                shareGroup = new ShareGroup();
                shareGroup.contexts.add(shareContext);
                shareContext.shareGroup = shareGroup;
                SHARE_GROUPS.put(share, shareGroup);
            }
            ctx.shareGroup = shareGroup;
            SHARE_GROUPS.put(window, shareGroup);
            shareGroup.contexts.add(ctx);
        } else {
            ctx.shareGroup = new ShareGroup();
            SHARE_GROUPS.put(window, ctx.shareGroup);
            ctx.shareGroup.contexts.add(ctx);
        }
        CONTEXTS.put(window, ctx);
    }

    public String openglVersion() {
        String version = "1.1";
        if (caps.OpenGL12)
            version = "1.2";
        if (caps.OpenGL13)
            version = "1.3";
        if (caps.OpenGL14)
            version = "1.4";
        if (caps.OpenGL15)
            version = "1.5";
        if (caps.OpenGL20)
            version = "2.0";
        if (caps.OpenGL21)
            version = "2.1";
        if (caps.OpenGL30)
            version = "3.0";
        if (caps.OpenGL31)
            version = "3.1";
        if (caps.OpenGL32)
            version = "3.2";
        if (caps.OpenGL33)
            version = "3.3";
        if (caps.OpenGL40)
            version = "4.0";
        if (caps.OpenGL41)
            version = "4.1";
        if (caps.OpenGL42)
            version = "4.2";
        if (caps.OpenGL43)
            version = "4.3";
        if (caps.OpenGL44)
            version = "4.4";
        if (caps.OpenGL45)
            version = "4.5";
        return version;
    }

    public void init(int GL_MAX_VERTEX_ATTRIBS) {
        this.inited = true;
        this.GL_MAX_VERTEX_ATTRIBS = GL_MAX_VERTEX_ATTRIBS;
        this.defaultVao = new VAO(GL_MAX_VERTEX_ATTRIBS);
        this.currentVao = defaultVao;
        this.vaos.put(0, defaultVao);
        this.defaultFbo = new FBO(0);
        this.currentFbo = defaultFbo;
        this.fbos.put(0, defaultFbo);
        this.defaultProgramPipeline = new ProgramPipeline();
        this.currentProgramPipeline = defaultProgramPipeline;
        this.programPipelines.put(0, defaultProgramPipeline);
        StringBuilder sb = new StringBuilder();
        sb.append("Initialized OpenGL context for window[").append(this.counter).append("]\n");
        sb.append("  Effective OpenGL version: ").append(openglVersion()).append("\n");
        sb.append("  OpenGL version string   : ").append(org.lwjgl.opengl.GL11.glGetString(org.lwjgl.opengl.GL11.GL_VERSION)).append("\n");
        sb.append("  OpenGL vendor           : ").append(org.lwjgl.opengl.GL11.glGetString(org.lwjgl.opengl.GL11.GL_VENDOR)).append("\n");
        sb.append("  OpenGL renderer         : ").append(org.lwjgl.opengl.GL11.glGetString(org.lwjgl.opengl.GL11.GL_RENDERER)).append("\n");
        sb.append("  GL_MAX_VERTEX_ATTRIBS   : ").append(GL_MAX_VERTEX_ATTRIBS).append("\n");
        /* Print capabilities */
        LineBreakingStringBuilder extensions = new LineBreakingStringBuilder();
        for (Field field : org.lwjgl.opengl.GLCapabilities.class.getDeclaredFields()) {
            boolean isPublic = Modifier.isPublic(field.getModifiers());
            boolean isFinal = Modifier.isFinal(field.getModifiers());
            boolean isBoolean = field.getType() == boolean.class;
            boolean isOpenGL = field.getName().startsWith("OpenGL");
            if (!isPublic || !isFinal || !isBoolean || isOpenGL)
                continue;
            try {
                boolean supported = field.getBoolean(caps);
                if (supported) {
                    extensions.append(field.getName() + " ");
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }
        }
        sb.append("  Capabilities:\n    ").append(extensions.toString());
        info(sb.toString(), 8);
    }

    public void destroy() {
        info("Destroying OpenGL context for window[" + this.counter + "]");
        if (shareGroup != null) {
            shareGroup.contexts.remove(this);
            SHARE_GROUPS.remove(window);
            shareGroup = null;
        }
        if (debugCallback != null) {
            /* Can happen when we never actually called GL.createCapabilities() */
            debugCallback.free();
        }
    }

    @Override
    public int compareTo(Context o) {
        if (this.window < o.window)
            return -1;
        else if (this.window > o.window)
            return +1;
        return 0;
    }

    public TimingQuery nextTimerQuery() {
        for (int i = 0; i < timingQueries.size(); i++) {
            TimingQuery qo = timingQueries.get(i);
            if (!qo.used) {
                qo.used = true;
                qo.drawTime = false;
                return qo;
            }
        }
        int before = ARBOcclusionQuery.glGenQueriesARB();
        int after = ARBOcclusionQuery.glGenQueriesARB();
        TimingQuery q = new TimingQuery();
        q.before = before;
        q.after = after;
        q.used = true;
        timingQueries.add(q);
        return q;
    }

    public static void deleteVertexArray(int index) {
        if (index == 0)
            return;
        Context context = currentContext();
        VAO vao = context.vaos.get(index);
        if (vao != null && vao == context.currentVao) {
            context.currentVao = context.defaultVao;
        }
        context.vaos.remove(index);
    }

    public static void deleteVertexArrays(IntBuffer indices) {
        Context context = currentContext();
        int pos = indices.position();
        for (int i = 0; i < indices.remaining(); i++) {
            int index = indices.get(pos + i);
            if (index == 0)
                continue;
            VAO vao = context.vaos.get(index);
            if (vao != null && vao == context.currentVao) {
                context.currentVao = context.defaultVao;
            }
            context.vaos.remove(index);
        }
    }

    public static void deletePipeline(int index) {
        if (index == 0)
            return;
        Context context = currentContext();
        ProgramPipeline pp = context.programPipelines.get(index);
        if (pp != null && pp == context.currentProgramPipeline) {
            context.currentProgramPipeline = context.defaultProgramPipeline;
        }
        context.programPipelines.remove(index);
    }

    public static void deletePipelines(IntBuffer pipelines) {
        Context context = currentContext();
        int pos = pipelines.position();
        for (int i = 0; i < pipelines.remaining(); i++) {
            int index = pipelines.get(pos + i);
            if (index == 0)
                continue;
            ProgramPipeline pp = context.programPipelines.get(index);
            if (pp != null && pp == context.currentProgramPipeline) {
                context.currentProgramPipeline = context.defaultProgramPipeline;
            }
            context.programPipelines.remove(index);
        }
    }

    public static void deletePipelines(int[] pipelines) {
        Context context = currentContext();
        for (int i = 0; i < pipelines.length; i++) {
            int index = pipelines[i];
            if (index == 0)
                continue;
            ProgramPipeline pp = context.programPipelines.get(index);
            if (pp != null && pp == context.currentProgramPipeline) {
                context.currentProgramPipeline = context.defaultProgramPipeline;
            }
            context.programPipelines.remove(index);
        }
    }

    public static void deleteVertexArrays(int[] indices) {
        Context context = currentContext();
        for (int i = 0; i < indices.length; i++) {
            int index = indices[i];
            if (index == 0)
                continue;
            VAO vao = context.vaos.get(index);
            if (vao != null && vao == context.currentVao) {
                context.currentVao = context.defaultVao;
            }
            context.vaos.remove(index);
        }
    }

    public static void checkBeforeDrawCall() {
        checkFramebufferCompleteness();
        checkVertexAttributes();
    }

    public static void checkVertexAttributes() {
        Context context = currentContext();
        VAO vao = context.currentVao;
        for (int i = 0; i < vao.enabledVertexArrays.length; i++) {
            if (vao.enabledVertexArrays[i] && !vao.initializedVertexArrays[i]) {
                RT.throwISEOrLogError("Vertex array [" + i + "] enabled but not initialized");
            }
        }
        if (vao.vertexArrayEnabled && !vao.vertexArrayInitialized) {
            RT.throwISEOrLogError("GL_VERTEX_ARRAY enabled but not initialized");
        }
        if (vao.normalArrayEnabled && !vao.normalArrayInitialized) {
            RT.throwISEOrLogError("GL_NORMAL_ARRAY enabled but not initialized");
        }
        if (vao.colorArrayEnabled && !vao.colorArrayInitialized) {
            RT.throwISEOrLogError("GL_COLOR_ARRAY enabled but not initialized");
        }
        if (vao.texCoordArrayEnabled && !vao.texCoordArrayInitialized) {
            RT.throwISEOrLogError("GL_TEXTURE_COORD_ARRAY enabled but not initialized");
        }
    }

    public static void checkFramebufferCompleteness() {
        if (Properties.VALIDATE.enabled) {
            Context context = currentContext();
            if (context.currentFbo != null) {
                /* Check framebuffer status */
                int status = org.lwjgl.opengl.GL30.glCheckFramebufferStatus(org.lwjgl.opengl.GL30.GL_FRAMEBUFFER);
                if (status != org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE) {
                    RT.throwISEOrLogError("Framebuffer [" + context.currentFbo.handle + "] is not complete: " + status);
                }
            }
        }
    }

}
