package net.teujaem.spFramework.websoket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.teujaem.spFramework.api.event.ProxyEvent;
import org.java_websocket.WebSocket;
import org.slf4j.Logger;

public class WebSocketServerApplication {

    private final Logger logger;

    private WebSocketServer server;
    private volatile boolean started;
    private final ObjectMapper mapper = new ObjectMapper();

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

    public void addProxyEventListener(ProxyEvent listener) {
        server.addProxyEventListener(listener);
    }

    public void removeProxyEventListener(ProxyEvent listener) {
        server.removeProxyEventListener(listener);
    }

    public void broadcastServer(String message) {

        if (!started) {
            return;
        }

        server.broadcastServer(message);
    }

    public void sendMessage(PluginMessage message) {
        try {
            broadcastServer(mapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            logger.error("WebSocket 메시지 변환 실패", e);
        }
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