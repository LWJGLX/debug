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
package org.lwjglx.debug.org.lwjgl.opengl;

import org.lwjglx.debug.Properties;
import org.lwjglx.debug.RT;
import org.lwjglx.debug.ResourceState;
import org.lwjglx.debug.ResourceTracker;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjglx.debug.Log.LineBreakingStringBuilder;
import static org.lwjglx.debug.Log.info;

public class Context implements Comparable<Context> {
    public static class ShareGroup {
        public final ResourceTracker<BufferObject> bufferObjects = new ResourceTracker<>("Buffer");
        public final ResourceTracker<TextureObject> textureObjects = new ResourceTracker<>("Texture");
        public final ResourceTracker<RenderbufferObject> renderbufferObjects = new ResourceTracker<>("Renderbuffer");
        public final ResourceTracker<ShaderObject> shaderObjects = new ResourceTracker<>("Shader");
        public final ResourceTracker<ProgramObject> programObjects = new ResourceTracker<>("Program");
        public final Set<Context> contexts = new ConcurrentSkipListSet<Context>();
    }

    public static class RenderbufferObject {
    }

    public static class ShaderObject {
        public final int type;
        public ShaderObject(int type) {
            this.type = type;
        }
    }

    public static class ProgramObject {
        public final Set<Integer> attachedShaders = ConcurrentHashMap.newKeySet();
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
        public final Map<Integer, Integer> attachedTextures = new ConcurrentHashMap<Integer, Integer>();
        public final Map<Integer, Integer> attachedRenderbuffers = new ConcurrentHashMap<Integer, Integer>();
        public FBO(int handle) {
            this.handle = handle;
        }

        public void attachTexture(int attachment, int texture) {
            if (texture != 0) {
                attachedTextures.put(attachment, texture);
                attachedRenderbuffers.remove(attachment);
            } else {
                attachedTextures.remove(attachment);
                attachedRenderbuffers.remove(attachment);
            }
        }

        public void attachRenderbuffer(int attachment, int renderbuffer) {
            if (renderbuffer != 0) {
                attachedRenderbuffers.put(attachment, renderbuffer);
                attachedTextures.remove(attachment);
            } else {
                attachedTextures.remove(attachment);
                attachedRenderbuffers.remove(attachment);
            }
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
    public FBO currentDrawFbo;
    public FBO currentReadFbo;
    public ProgramPipeline defaultProgramPipeline;
    public ProgramPipeline currentProgramPipeline;
    public final ResourceTracker<VAO> vaoTracker = new ResourceTracker<>("VAO");
    public final ResourceTracker<FBO> fboTracker = new ResourceTracker<>("FBO");
    public final ResourceTracker<ProgramPipeline> pipelineTracker = new ResourceTracker<>("ProgramPipeline");
    public int currentProgram;
    public Map<Integer, VAO> vaos = new ConcurrentHashMap<Integer, VAO>();
    public Map<Integer, FBO> fbos = new ConcurrentHashMap<Integer, FBO>();
    public Map<Integer, BufferObject> bufferObjectBindings = new ConcurrentHashMap<>();
    public Map<Integer, TextureObject> textureObjectBindings = new ConcurrentHashMap<>();
    public Map<Integer, ProgramPipeline> programPipelines = new ConcurrentHashMap<>();
    public ShareGroup shareGroup;
    public boolean inImmediateMode;
    public Thread currentInThread;
    /* per frame info */
    public boolean drawCallSeen;

    public static Context currentContext() {
    	Context ctx = CURRENT_CONTEXT.get();
    	if (ctx == null) {
    		RT.throwISEOrLogError("No OpenGL context has been made current through recognized API methods (glfwMakeContextCurrent or SDL_GL_MakeCurrent).");
    	}
    	return ctx;
    }

    public static Context createCurrent() {
        org.lwjglx.debug.Log.warn("No OpenGL context has been made current through recognized API methods. Created a fallback context.", new Throwable(), 3);
        Context ctx = new Context();
        ctx.counter = CONTEXT_COUNTER.getAndIncrement();
        ctx.shareGroup = new ShareGroup();
        ctx.shareGroup.contexts.add(ctx);
        CURRENT_CONTEXT.set(ctx);
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
        this.vaoTracker.create(0, defaultVao);
        this.defaultFbo = new FBO(0);
        this.currentFbo = defaultFbo;
        this.currentDrawFbo = defaultFbo;
        this.currentReadFbo = defaultFbo;
        this.fbos.put(0, defaultFbo);
        this.fboTracker.create(0, defaultFbo);
        this.defaultProgramPipeline = new ProgramPipeline();
        this.currentProgramPipeline = defaultProgramPipeline;
        this.programPipelines.put(0, defaultProgramPipeline);
        this.pipelineTracker.create(0, defaultProgramPipeline);
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
        try {
            auditLeaks();
        } finally {
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
    }

    private void auditLeaks() {
        if (!Properties.VALIDATE.enabled) {
            return;
        }
        List<String> leakMessages = new ArrayList<>();
        int liveVaos = vaoTracker.liveCountExcludingDefault();
        if (liveVaos > 0) {
            leakMessages.add("OpenGL context for window[" + this.counter + "] destroyed with " + liveVaos + " un-deleted VAO(s)"
                    + leakDetails(vaoTracker.liveEntriesExcludingDefault()));
        }
        int liveFbos = fboTracker.liveCountExcludingDefault();
        if (liveFbos > 0) {
            leakMessages.add("OpenGL context for window[" + this.counter + "] destroyed with " + liveFbos + " un-deleted FBO(s)"
                    + leakDetails(fboTracker.liveEntriesExcludingDefault()));
        }
        int livePipelines = pipelineTracker.liveCountExcludingDefault();
        if (livePipelines > 0) {
            leakMessages.add("OpenGL context for window[" + this.counter + "] destroyed with " + livePipelines + " un-deleted ProgramPipeline(s)"
                    + leakDetails(pipelineTracker.liveEntriesExcludingDefault()));
        }
        if (shareGroup != null && shareGroup.contexts.size() <= 1) {
            int liveBuffers = shareGroup.bufferObjects.liveCountExcludingDefault();
            if (liveBuffers > 0) {
                leakMessages.add("ShareGroup destroyed with " + liveBuffers + " un-deleted Buffer(s)"
                        + leakDetails(shareGroup.bufferObjects.liveEntriesExcludingDefault()));
            }
            int liveTextures = shareGroup.textureObjects.liveCountExcludingDefault();
            if (liveTextures > 0) {
                leakMessages.add("ShareGroup destroyed with " + liveTextures + " un-deleted Texture(s)"
                        + leakDetails(shareGroup.textureObjects.liveEntriesExcludingDefault()));
            }
            int liveRenderbuffers = shareGroup.renderbufferObjects.liveCountExcludingDefault();
            if (liveRenderbuffers > 0) {
                leakMessages.add("ShareGroup destroyed with " + liveRenderbuffers + " un-deleted Renderbuffer(s)"
                        + leakDetails(shareGroup.renderbufferObjects.liveEntriesExcludingDefault()));
            }
            int liveShaders = shareGroup.shaderObjects.liveCountExcludingDefault();
            if (liveShaders > 0) {
                leakMessages.add("ShareGroup destroyed with " + liveShaders + " un-deleted Shader(s)"
                        + leakDetails(shareGroup.shaderObjects.liveEntriesExcludingDefault()));
            }
            int livePrograms = shareGroup.programObjects.liveCountExcludingDefault();
            if (livePrograms > 0) {
                leakMessages.add("ShareGroup destroyed with " + livePrograms + " un-deleted Program(s)"
                        + leakDetails(shareGroup.programObjects.liveEntriesExcludingDefault()));
            }
        }
        for (String msg : leakMessages) {
            if (Properties.FAIL_ON_LEAKS.enabled) {
                RT.throwISEOrLogError(msg);
            } else {
                org.lwjglx.debug.Log.warn(msg);
            }
        }
    }

    private static <T> String leakDetails(List<ResourceTracker.Entry<T>> entries) {
        if (!Properties.STRICT.enabled || entries == null || entries.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (ResourceTracker.Entry<T> e : entries) {
            sb.append("\n  Handle [").append(e.handle).append("]");
            if (e.creationSite != null) {
                sb.append(" created at: ").append(ResourceTracker.formatTrace(e.creationSite));
            }
        }
        return sb.toString();
    }

    @Override
    public int compareTo(Context o) {
        if (this.window < o.window)
            return -1;
        else if (this.window > o.window)
            return +1;
        return 0;
    }

    public static void deleteVertexArray(int index) {
        if (index == 0)
            return;
        Context context = currentContext();
        context.vaoTracker.delete(index, "glDeleteVertexArrays");
        VAO vao = context.vaos.get(index);
        if (vao != null && vao == context.currentVao) {
            context.currentVao = context.defaultVao;
        }
    }

    public static void deleteVertexArrays(IntBuffer indices) {
        int pos = indices.position();
        for (int i = 0; i < indices.remaining(); i++) {
            deleteVertexArray(indices.get(pos + i));
        }
    }

    public static void deleteVertexArrays(int[] indices) {
        for (int i = 0; i < indices.length; i++) {
            deleteVertexArray(indices[i]);
        }
    }

    public static void deletePipeline(int index) {
        if (index == 0)
            return;
        Context context = currentContext();
        context.pipelineTracker.delete(index, "glDeleteProgramPipelines");
        ProgramPipeline pp = context.programPipelines.get(index);
        if (pp != null && pp == context.currentProgramPipeline) {
            context.currentProgramPipeline = context.defaultProgramPipeline;
        }
    }

    public static void deletePipelines(IntBuffer pipelines) {
        int pos = pipelines.position();
        for (int i = 0; i < pipelines.remaining(); i++) {
            deletePipeline(pipelines.get(pos + i));
        }
    }

    public static void deletePipelines(int[] pipelines) {
        for (int i = 0; i < pipelines.length; i++) {
            deletePipeline(pipelines[i]);
        }
    }

    public static void deleteFramebuffer(int index) {
        if (index == 0)
            return;
        Context context = currentContext();
        context.fboTracker.delete(index, "glDeleteFramebuffers");
        FBO fbo = context.fbos.remove(index);
        if (fbo != null) {
            fbo.attachedTextures.clear();
            fbo.attachedRenderbuffers.clear();
            if (fbo == context.currentDrawFbo) {
                context.currentDrawFbo = context.defaultFbo;
            }
            if (fbo == context.currentReadFbo) {
                context.currentReadFbo = context.defaultFbo;
            }
            context.currentFbo = context.currentDrawFbo;
        }
    }

    public static void deleteFramebuffers(IntBuffer framebuffers) {
        int pos = framebuffers.position();
        for (int i = 0; i < framebuffers.remaining(); i++) {
            deleteFramebuffer(framebuffers.get(pos + i));
        }
    }

    public static void deleteFramebuffers(int[] framebuffers) {
        for (int i = 0; i < framebuffers.length; i++) {
            deleteFramebuffer(framebuffers[i]);
        }
    }

    public static void deleteRenderbuffer(int index) {
        if (index == 0)
            return;
        Context context = currentContext();
        context.shareGroup.renderbufferObjects.delete(index, "glDeleteRenderbuffers");
        Set<Context> contexts = context.shareGroup != null ? context.shareGroup.contexts : Collections.singleton(context);
        for (Context c : contexts) {
            for (FBO fbo : c.fbos.values()) {
                if (fbo.attachedRenderbuffers.values().removeIf(r -> r == index)) {
                    org.lwjglx.debug.Log.warn("Renderbuffer [" + index + "] deleted while still attached to FBO [" + fbo.handle + "]");
                }
            }
        }
    }

    public static void deleteRenderbuffers(IntBuffer indices) {
        int pos = indices.position();
        for (int i = 0; i < indices.remaining(); i++) {
            deleteRenderbuffer(indices.get(pos + i));
        }
    }

    public static void deleteRenderbuffers(int[] indices) {
        for (int i = 0; i < indices.length; i++) {
            deleteRenderbuffer(indices[i]);
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
            FBO fbo = context.currentDrawFbo;
            if (fbo != null) {
                for (int tex : fbo.attachedTextures.values()) {
                    context.shareGroup.textureObjects.checkAlive(tex, "glCheckFramebufferStatus");
                }
                for (int rb : fbo.attachedRenderbuffers.values()) {
                    context.shareGroup.renderbufferObjects.checkAlive(rb, "glCheckFramebufferStatus");
                }
                /* Check framebuffer status */
                int status = org.lwjgl.opengl.GL30.glCheckFramebufferStatus(org.lwjgl.opengl.GL30.GL_FRAMEBUFFER);
                if (status != org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE) {
                    RT.throwISEOrLogError("Framebuffer [" + fbo.handle + "] is not complete: " + status);
                }
            }
        }
    }

}
