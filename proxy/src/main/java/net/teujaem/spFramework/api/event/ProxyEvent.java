package net.teujaem.spFramework.api.event;

import com.velocitypowered.api.event.annotation.AwaitingEvent;
import net.teujaem.spFramework.websoket.PluginMessage;

@AwaitingEvent
public class ProxyEvent {

    private final PluginMessage pluginMessage;

    public ProxyEvent(PluginMessage pluginMessage) {
        this.pluginMessage = pluginMessage;
    }

    public PluginMessage getPluginMessage() {
        return pluginMessage;
    }
}