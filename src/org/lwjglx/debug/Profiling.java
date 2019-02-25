package org.lwjglx.debug;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import javax.servlet.ServletException;

import org.eclipse.jetty.http.pathmap.ServletPathSpec;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeFilter;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.lwjgl.system.MemoryUtil;
import org.lwjglx.debug.Context.BufferObject;
import org.lwjglx.debug.Context.TextureLayer;
import org.lwjglx.debug.Context.TextureLevel;
import org.lwjglx.debug.Context.TextureObject;
import org.lwjglx.debug.Context.TimedCodeSection;
import org.lwjglx.debug.Context.TimingQuery;

@SuppressWarnings("serial")
class EchoSocketServlet extends WebSocketServlet {
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(ProfilingConnection.class);
    }
}

class ProfilingConnection implements WebSocketListener {
    public static final List<ProfilingConnection> connections = new ArrayList<>();
    Session outbound;
    ByteBuffer buffer = ByteBuffer.allocateDirect(1024).order(ByteOrder.BIG_ENDIAN);
    long bufferAddr = MemoryUtil.memAddress0(buffer);
    Future<Void> lastSend = null;

    public void send(long addr, int size) {
        if (lastSend != null && !lastSend.isDone()) {
            /* We have to wait for the last send to complete until we can reuse the buffer */
            try {
                lastSend.get();
            } catch (Exception e) {
            }
        }
        MemoryUtil.memCopy(addr, bufferAddr, size);
        buffer.rewind();
        buffer.limit(size);
        lastSend = outbound.getRemote().sendBytesByFuture(buffer);
    }

    public void onWebSocketClose(int statusCode, String reason) {
        this.outbound = null;
        synchronized (connections) {
            connections.remove(this);
        }
    }

    public void onWebSocketConnect(Session session) {
        this.outbound = session;
        synchronized (connections) {
            connections.add(this);
        }
        Profiling.frontendConnected.countDown();
    }

    public void onWebSocketError(Throwable cause) {
    }

    public void onWebSocketText(String message) {
    }

    public void onWebSocketBinary(byte[] arg0, int arg1, int arg2) {
    }
}

class ProfilingConnectionCreator implements WebSocketCreator {
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        return new ProfilingConnection();
    }
}

class Profiling {

    public static final long sendIntervalMs = 500L;

    public static Server server;
    public static long lastSent;
    public static final CountDownLatch frontendConnected = new CountDownLatch(1);

    public static void startServer() throws ServletException {
        QueuedThreadPool threadPool = new QueuedThreadPool(10);
        threadPool.setDaemon(true);
        threadPool.setMaxThreads(10);
        Server server = new Server(threadPool);
        server.addBean(new ScheduledExecutorScheduler("JettyScheduler", true), true);
        ServerConnector http = new ServerConnector(server, new HttpConnectionFactory());
        http.setPort(2992);
        server.addConnector(http);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setBaseResource(Resource.newClassPathResource("/org/lwjglx/debug/static"));
        context.setWelcomeFiles(new String[] { "index.html" });
        server.setHandler(context);

        WebSocketUpgradeFilter wsfilter = WebSocketUpgradeFilter.configureContext(context);
        // wsfilter.getFactory().getPolicy().setIdleTimeout(5000);
        wsfilter.addMapping(new ServletPathSpec("/ws"), new ProfilingConnectionCreator());

        ServletHolder holderDefault = new ServletHolder("default", DefaultServlet.class);
        holderDefault.setInitParameter("dirAllowed", "true");
        context.addServlet(holderDefault, "/");

        try {
            server.start();
        } catch (Exception e) {
            throw new AssertionError("Could not start profiling server", e);
        }
    }

    public static void frame(Context ctx) {
        if (!ctx.firstFrameSeen) {
            /* Do not count the first frame end, since we have no idea when the frame actually started. */
            ctx.firstFrameSeen = true;
            return;
        }
        if ((ctx.frameEndTime - lastSent) / 1E6 < sendIntervalMs) {
            return;
        }
        lastSent = ctx.frameEndTime;
        ByteBuffer buf = MemoryUtil.memAlloc(1024).order(ByteOrder.BIG_ENDIAN);
        long addr = MemoryUtil.memAddress0(buf);
        float frameTimeCpu = (float) (ctx.frameEndTime - ctx.frameStartTime) / 1E6f;
        float drawCallTimeGpu = ctx.drawCallTimeMs;
        buf.putInt(ctx.counter);
        buf.putInt(ctx.frame);
        buf.putFloat(frameTimeCpu);
        buf.putFloat(drawCallTimeGpu);
        buf.putInt(ctx.glCallCount);
        buf.putInt(ctx.verticesCount);
        long boMemory = 0L;
        for (BufferObject bo : ctx.shareGroup.bufferObjects.values()) {
            boMemory += bo.size;
        }
        buf.putDouble(boMemory / 1024);
        long toMemory = 0L;
        for (TextureObject to : ctx.shareGroup.textureObjects.values()) {
            if (to.layers != null) {
                for (TextureLayer layer : to.layers) {
                    if (layer.levels != null) {
                        for (TextureLevel level : layer.levels) {
                            toMemory += level.size;
                        }
                    }
                }
            }
        }
        buf.putDouble(toMemory / 1024L);

        /* Write code section timings */
        buf.putShort((short) ctx.codeSectionTimes.size());
        for (TimedCodeSection section : ctx.codeSectionTimes) {
            String name = section.name;
            byte[] nameBytes = name.getBytes();
            buf.putShort((short) nameBytes.length);
            for (int i = 0; i < nameBytes.length; i++) {
                buf.put(nameBytes[i]);
            }
            float totalTime = 0.0f; // <- ms
            for (TimingQuery q : section.queries) {
                totalTime += (q.time1 - q.time0) / 1E6f;
            }
            buf.putFloat(totalTime);
        }

        synchronized (ProfilingConnection.connections) {
            for (ProfilingConnection c : ProfilingConnection.connections) {
                c.send(addr, buf.position());
            }
        }

        MemoryUtil.memFree(buf);
    }

}
