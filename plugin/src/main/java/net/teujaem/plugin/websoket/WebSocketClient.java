package net.teujaem.plugin.websoket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.teujaem.plugin.SPFramework;
import net.teujaem.plugin.api.event.ProxyEvent;
import org.bukkit.Bukkit;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WebSocketClient extends org.java_websocket.client.WebSocketClient {

    private final Logger logger;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String sessionId;

    public WebSocketClient(String host, int port, Logger logger) throws Exception {
        super(new URI("ws://" + host + ":" + port + "/ws"));

        this.logger = logger;

        this.sessionId = UUID.randomUUID()
                .toString()
                .replace("-", "");

        connect();
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        logger.info("WebSocket client connected: {}", getURI());

        Map<String, Object> data = new HashMap<>();
        data.put("eventname", "setSessionsId");
        data.put("id", sessionId);

        PluginMessage message = new PluginMessage(
                "server",
                "SP-Framework",
                null,
                null,
                data
        );

        sendMessage(message);
    }

    @Override
    public void onMessage(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }

        logger.info("WebSocket message: {}", message);

        try {
            PluginMessage msg = mapper.readValue(
                    message,
                    PluginMessage.class
            );

            if (msg.isFromClient()) {
                handleClientMessage(msg);
            } else {
                handleServerMessage(msg);
            }

        } catch (JsonProcessingException e) {
            logger.warn("잘못된 WebSocket 메시지 형식: {}", message);
        }
    }

    private void handleClientMessage(PluginMessage msg) {
        UUID user = msg.user();
        String username = msg.username();

        if (user == null || username == null) {
            logger.warn("client 메시지에 user/username 누락: {}", msg);
            return;
        }

        if (msg.data() == null) {
            logger.warn("client 메시지에 data 누락: {}", msg);
            return;
        }

        Object eventNameObject = msg.data().get("eventname");

        if (!(eventNameObject instanceof String eventName)) {
            logger.warn("client 메시지에 eventname 누락: {}", msg);
            return;
        }

        logger.info(
                "Client Event - user={}, username={}, event={}",
                user,
                username,
                eventName
        );
    }

    private void handleServerMessage(PluginMessage msg) {
        if (msg.data() == null) {
            logger.warn("server 메시지에 data 누락: {}", msg);
            return;
        }

        Object eventNameObject = msg.data().get("eventname");

        if (!(eventNameObject instanceof String eventName)) {
            logger.warn("server 메시지에 eventname 누락: {}", msg);
            return;
        }

        Object value = msg.data().get("value");

        logger.info("Server Event - event={}", eventName);

        Bukkit.getScheduler().runTask(
                SPFramework.getInstance(),
                () -> Bukkit.getPluginManager().callEvent(
                        new ProxyEvent(
                                msg.name(),
                                eventName,
                                value,
                                msg
                        )
                )
        );
    }

    public void sendMessage(PluginMessage message) {
        try {
            super.send(mapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            logger.error("WebSocket 메시지 변환 실패", e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info(
                "WebSocket client disconnected: {} ({}) remote={}",
                reason,
                code,
                remote
        );
    }

    @Override
    public void onError(Exception ex) {
        logger.error("WebSocket client error", ex);
    }

    public String getSessionId() {
        return sessionId;
    }
}