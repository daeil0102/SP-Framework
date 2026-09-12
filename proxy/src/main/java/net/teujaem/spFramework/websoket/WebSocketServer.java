package net.teujaem.spFramework.websoket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.teujaem.spFramework.api.event.ProxyEvent;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class WebSocketServer extends org.java_websocket.server.WebSocketServer {

    private static Logger logger;
    private static final HashMap<String, WebSocket> SESSIONS_CLIENT_ID = new HashMap<>();
    private static final HashMap<String, WebSocket> SESSIONS_SERVER_ID = new HashMap<>();private final List<ProxyEvent> listeners = new CopyOnWriteArrayList<>();

    public WebSocketServer(String host, int port, Logger logger) {
        super(new InetSocketAddress(host, port));
        WebSocketServer.logger = logger;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        logger.info("open: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        if (SESSIONS_CLIENT_ID.containsValue(conn)) {
            String key = getKeyByValue(SESSIONS_CLIENT_ID, conn);
            if (key != null) {
                SESSIONS_CLIENT_ID.remove(key);
            }
        }
        if (SESSIONS_SERVER_ID.containsValue(conn)) {
            String key = getKeyByValue(SESSIONS_SERVER_ID, conn); // ← 올바른 맵을 탐색
            if (key != null) {
                SESSIONS_SERVER_ID.remove(key);
            }
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
        fireProxyEvent(msg);
        logger.info(msg.toString());

        if (msg.isFromClient()) {
            handleClientMessage(conn, msg);
        } else {
            handleServerMessage(conn, msg);
        }

        // 받은 메시지 원본 그대로 연결된 모든 클라이언트에게 전송
        broadcastAll(message);
    }

    public void broadcastAll(String message) {
        for (WebSocket ws : getConnections()) {
            if (ws != null && ws.isOpen()) {
                ws.send(message);
            }
        }
    }

    private void handleClientMessage(WebSocket conn, PluginMessage msg) {
        String user = String.valueOf(msg.user());
        String username = msg.username();

        if (user == null || username == null) {
            logger.info("client 메시지에 user/username 누락: " + msg);
            return;
        }

        if (msg.data() == null) {
            logger.info("client 메시지에 data 누락: " + msg);
            return;
        }

        Object eventNameObject = msg.data().get("eventname");
        if (!(eventNameObject instanceof String eventName)) {
            logger.info("client 메시지에 eventname 누락: " + msg);
            return;
        }

        if ("setSessionsId".equals(eventName)) {
            Object idObj = msg.data().get("id");
            if (idObj instanceof String id) {
                addSessionsIdUser(conn, id);
                logger.info("client 세션 등록: user={}, id={}", user, id);
            } else {
                logger.info("setSessionsId 메시지에 id 누락: " + msg);
            }
        }
    }

    private void handleServerMessage(WebSocket conn, PluginMessage msg) {
        if (msg.data() == null) {
            logger.info("server 메시지에 data 누락: " + msg);
            return;
        }

        Object eventNameObject = msg.data().get("eventname");
        if (!(eventNameObject instanceof String eventName)) {
            logger.info("server 메시지에 eventname 누락: " + msg);
            return;
        }

        if ("setSessionsId".equals(eventName)) {
            Object idObj = msg.data().get("id");
            if (idObj instanceof String id) {
                addSessionsIdServer(conn, id);
                logger.info("server 세션 등록: id={}", id);
            } else {
                logger.info("setSessionsId 메시지에 id 누락: " + msg);
            }
        }
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

    // 맵을 인자로 받아 재사용 가능하게 변경 (기존 getKeyUser 버그 수정)
    private String getKeyByValue(Map<String, WebSocket> map, WebSocket ws) {
        for (Map.Entry<String, WebSocket> entry : map.entrySet()) {
            if (entry.getValue().equals(ws)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public String getKeyUser(WebSocket ws) {
        return getKeyByValue(SESSIONS_CLIENT_ID, ws);
    }

    public WebSocket getWSUser(String name) {
        return SESSIONS_CLIENT_ID.get(name);
    }

    public void addProxyEventListener(ProxyEvent listener) {
        listeners.add(listener);
    }

    public void removeProxyEventListener(ProxyEvent listener) {
        listeners.remove(listener);
    }

    private void fireProxyEvent(PluginMessage msg) {
        for (ProxyEvent listener : listeners) {
            try {
                listener.onProxyEvent(msg);
            } catch (Exception e) {
                logger.error("ProxyEvent 리스너 처리 중 오류: " + e.getMessage());
            }
        }
    }
}