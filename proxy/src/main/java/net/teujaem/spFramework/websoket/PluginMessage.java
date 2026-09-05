package net.teujaem.spFramework.websoket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PluginMessage(
        String send,
        String name,
        String user,
        String username,
        Map<String, Object> data
) {
    public boolean isFromClient() {
        return "client".equals(send);
    }
}