package net.teujaem.proxy.websoket;

import java.util.Map;

public record PluginMessage(
        String send,
        String name,
        String user,
        String username,
        Map<String, Object> data
)

{
    public boolean isFromClient() {
        return "client".equals(send);
    }
}