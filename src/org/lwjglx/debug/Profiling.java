package org.lwjglx.debug;

import static org.lwjgl.system.MemoryStack.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.eclipse.jetty.http.pathmap.ServletPathSpec;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeFilter;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.lwjgl.system.MemoryStack;
import org.lwjglx.debug.Context.BufferObject;

@SuppressWarnings("serial")
class EchoSocketServlet extends WebSocketServlet {
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(ProfilingConnection.class);
    }
}

class ProfilingConnection implements WebSocketListener {
    public static final List<ProfilingConnection> connections = new ArrayList<>();

    static final Logger LOG = Log.getLogger(ProfilingConnection.class);
    Session outbound;

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
    }

    public void onWebSocketError(Throwable cause) {
    }

    public void onWebSocketText(String message) {
        if ((outbound != null) && (outbound.isOpen())) {
            outbound.getRemote().sendString(message, null);
        }
    }

    @Override
    public void onWebSocketBinary(byte[] arg0, int arg1, int arg2) {
        /* ignore */
    }
}

class Profiling {

    public static final long sendIntervalMs = 500L;

    public static Server server;
    public static long lastSent;
    public static int frame;

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
        frame++;
        if ((ctx.frameEndTime - lastSent) / 1E6 < sendIntervalMs) {
            return;
        }
        lastSent = ctx.frameEndTime;
        try (MemoryStack stack = stackPush()) {
            ByteBuffer buf = stack.malloc(28).order(ByteOrder.BIG_ENDIAN);
            float ms = (float) (ctx.frameEndTime - ctx.frameStartTime) / 1E6f;
            buf.putInt(0, ctx.counter);
            buf.putInt(4, frame);
            buf.putFloat(8, ms);
            buf.putInt(12, ctx.glCallCount);
            buf.putInt(16, ctx.verticesCount);
            long boMemory = 0L;
            for (BufferObject bo : ctx.bufferObjects.values()) {
                boMemory += bo.size;
            }
            buf.putDouble(20, boMemory);
            synchronized (ProfilingConnection.connections) {
                for (ProfilingConnection c : ProfilingConnection.connections) {
                    c.outbound.getRemote().sendBytesByFuture(buf);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Profiling.startServer();
        Thread.sleep(20000000000L);
    }

}
