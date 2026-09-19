package net.teujaem.spFramework.api;

import net.minecraft.server.network.ServerPlayerEntity;
import net.teujaem.spFramework.SPFramework;
import net.teujaem.spFramework.websoket.PluginMessage;
import net.teujaem.spFramework.websoket.WebSocketClient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProxyData {

    public static void sendToServer(String pluginName, String eventName, String value) {
        WebSocketClient webSocketClient = SPFramework.getInstance().getWebSocketClient();

        Map<String, Object> data = new HashMap<>();
        data.put("eventname", eventName);
        data.put("value", value);

        PluginMessage message = new PluginMessage(
                "server",
                pluginName,
                null,
                null,
                data
        );

        webSocketClient.sendMessage(message);
    }

    public static void sendToServer(PluginMessage message) {
        WebSocketClient webSocketClient = SPFramework.getInstance().getWebSocketClient();

        webSocketClient.sendMessage(message);
    }

    public static void sendToPlayer(String pluginName, String eventName, String value, ServerPlayerEntity player) {
        sendToPlayer(
                pluginName,
                eventName,
                value,
                player.getUuid(),
                player.getName().getString()
        );
    }

    public static void sendToPlayer(
            String pluginName,
            String eventName,
            String value,
            UUID uuid,
            String name
    ) {
        WebSocketClient webSocketClient = SPFramework.getInstance().getWebSocketClient();

        Map<String, Object> data = new HashMap<>();
        data.put("eventname", eventName);
        data.put("value", value);

        PluginMessage message = new PluginMessage(
                "player",
                pluginName,
                uuid,
                name,
                data
        );

        webSocketClient.sendMessage(message);
    }
}
