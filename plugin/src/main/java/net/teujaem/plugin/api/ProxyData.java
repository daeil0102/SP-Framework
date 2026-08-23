package net.teujaem.plugin.api;

import net.teujaem.plugin.SPFramework;
import net.teujaem.plugin.websoket.PluginMessage;
import net.teujaem.plugin.websoket.WebSocketClient;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

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

    public static void sendToPlayer(String pluginName, String eventName, String value, Player player) {
        WebSocketClient webSocketClient = SPFramework.getInstance().getWebSocketClient();

        Map<String, Object> data = new HashMap<>();
        data.put("eventname", eventName);
        data.put("value", value);

        PluginMessage message = new PluginMessage(
                "player",
                pluginName,
                player.getUniqueId(),
                player.getName(),
                data
        );

        webSocketClient.sendMessage(message);
    }

}
