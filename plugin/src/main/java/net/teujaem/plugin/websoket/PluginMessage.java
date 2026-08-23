package net.teujaem.plugin.websoket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PluginMessage(
        String send,
        String name,
        UUID user,
        String username,
        Map<String, Object> data
) {
    public boolean isFromClient() {
        return "client".equals(send);
    }
}