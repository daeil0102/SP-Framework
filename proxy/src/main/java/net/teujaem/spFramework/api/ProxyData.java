package net.teujaem.spFramework.api;

import net.teujaem.spFramework.SPFramework;
import net.teujaem.spFramework.websoket.PluginMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProxyData {

    public static void sendToServer(String pluginName, String eventName, String value) {
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

        SPFramework.getInstance()
                .getWebSocketServerApplication()
                .sendMessage(message);
    }

    public static void sendToServer(PluginMessage message) {
        SPFramework.getInstance()
                .getWebSocketServerApplication()
                .sendMessage(message);
    }

    public static void sendToPlayer(
            String pluginName,
            String eventName,
            String value,
            UUID uuid,
            String name
    ) {
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

        SPFramework.getInstance()
                .getWebSocketServerApplication()
                .sendMessage(message);
    }
}