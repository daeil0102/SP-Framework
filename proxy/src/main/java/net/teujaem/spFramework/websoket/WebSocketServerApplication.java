package net.teujaem.spFramework.websoket;

import org.java_websocket.WebSocket;
import org.slf4j.Logger;

public class WebSocketServerApplication {

    private final Logger logger;

    private WebSocketServer server;
    private volatile boolean started;

    public WebSocketServerApplication(String host, int port, Logger logger) {
        this.logger = logger;
        start(host, port);
    }

    private void start(String host, int port) {

        server = new WebSocketServer(
                host,
                port,
                logger
        );

        try {

            server.start();
            started = true;

            logger.info(
                    "WebSocket started: ws://{}:{}",
                    host,
                    port
            );

        } catch (Exception e) {

            started = false;

            logger.error(
                    "WebSocket start failed",
                    e
            );

            stop();
        }
    }

    public void broadcastServer(String message) {

        if (!started) {
            return;
        }

        server.broadcastServer(message);
    }

    public void sendUser(WebSocket ws, String message) {

        if (!started) {
            return;
        }

        server.sendUser(ws, message);
    }

    public void close(WebSocket ws) {

        if (!started) {
            return;
        }

        server.close(ws);
    }

    public boolean containSessionsIdUser(String id) {

        if (!started) {
            return false;
        }

        return server.containSessionsIdUser(id);
    }

    public void addSessionsIdUser(WebSocket ws, String id) {

        if (!started) {
            return;
        }

        server.addSessionsIdUser(ws, id);
    }

    public boolean containSessionsIdServer(String id) {

        if (!started) {
            return false;
        }

        return server.containSessionsIdServer(id);
    }

    public void addSessionsIdServer(WebSocket ws, String id) {

        if (!started) {
            return;
        }

        server.addSessionsIdServer(ws, id);
    }

    public String getKeyUser(WebSocket ws) {

        if (!started) {
            return null;
        }

        return server.getKeyUser(ws);
    }

    public WebSocket getWSUser(String name) {

        if (!started) {
            return null;
        }

        return server.getWSUser(name);
    }

    public void stop() {

        try {

            if (server != null) {
                server.stop(1000);
                logger.info("WebSocket stopped");
            }

        } catch (Exception e) {

            logger.error(
                    "WebSocket stop error",
                    e
            );

        } finally {
            started = false;
        }
    }

    public boolean isStarted() {
        return started;
    }
}