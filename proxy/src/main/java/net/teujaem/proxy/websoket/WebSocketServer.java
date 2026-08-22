package net.teujaem.proxy.websoket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class WebSocketServer extends org.java_websocket.server.WebSocketServer {

    private static Logger logger;
    private static final HashMap<String, WebSocket> SESSIONS_CLIENT_ID = new HashMap<>();
    private static final HashMap<String, WebSocket> SESSIONS_SERVER_ID = new HashMap<>();

    public WebSocketServer(String host, int port, Logger logger) {
        super(new InetSocketAddress(host, port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        logger.info("open: " + conn.getRemoteSocketAddress());
        conn.send("getSessionsId");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        if (SESSIONS_CLIENT_ID.containsValue(conn)) {
            String key = getKeyUser(conn);
            SESSIONS_CLIENT_ID.remove(key);
        }
        if (SESSIONS_SERVER_ID.containsValue(conn)) {
            String key = getKeyUser(conn);
            SESSIONS_SERVER_ID.remove(key);
        }
        logger.info("close: " + reason);
    }

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onMessage(WebSocket conn, String message) {
        if (message == null || message.isEmpty()) return;

        PluginMessage msg;
        try {
            msg = mapper.readValue(message, PluginMessage.class);
        } catch (JsonProcessingException e) {
            logger.info("잘못된 메시지 형식: " + message);
            return;
        }

        logger.info(msg.toString());

        if (msg.isFromClient()) {
            handleClientMessage(conn, msg);
        } else {
            handleServerMessage(conn, msg);
        }
    }

    private void handleClientMessage(WebSocket conn, PluginMessage msg) {
        String user = msg.user();
        String username = msg.username();

        if (user == null || username == null) {
            logger.info("client 메시지에 user/username 누락: " + msg);
            return;
        }

        String eventName = (String) msg.data().get("eventname");
    }

    private void handleServerMessage(WebSocket conn, PluginMessage msg) {
        String eventName = (String) msg.data().get("eventname");
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        logger.error("error: " + ex.getMessage());
    }

    @Override
    public void onStart() {
        logger.info("WebSocket server started");
    }

    public void broadcastServer(String message) {
        for (WebSocket ws : SESSIONS_SERVER_ID.values()) {
            if (ws != null && ws.isOpen()) {
                ws.send(message);
            }
        }
    }

    public void sendUser(WebSocket ws, String message) {
        ws.send(message);
    }

    public void close(WebSocket ws) {
        ws.close();
    }

    public boolean containSessionsIdUser(String id) {
        return SESSIONS_CLIENT_ID.containsKey(id);
    }

    public void addSessionsIdUser(WebSocket ws, String id) {
        SESSIONS_CLIENT_ID.put(id, ws);
    }

    public boolean containSessionsIdServer(String id) {
        return SESSIONS_SERVER_ID.containsKey(id);
    }

    public void addSessionsIdServer(WebSocket ws, String id) {
        SESSIONS_SERVER_ID.put(id, ws);
    }

    public String getKeyUser(WebSocket ws) {
        for (Map.Entry<String, WebSocket> entry : SESSIONS_CLIENT_ID.entrySet()) {
            if (entry.getValue().equals(ws)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public WebSocket getWSUser(String name) {
        return SESSIONS_CLIENT_ID.get(name);
    }

}
