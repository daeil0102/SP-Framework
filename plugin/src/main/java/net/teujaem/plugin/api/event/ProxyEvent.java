package net.teujaem.plugin.api.event;

import net.teujaem.plugin.websoket.PluginMessage;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ProxyEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String pluginName;
    private final String eventName;
    private final Object value;
    private final PluginMessage rawMessage;

    public ProxyEvent(
            String pluginName,
            String eventName,
            Object value,
            PluginMessage rawMessage
    ) {
        this.pluginName = pluginName;
        this.eventName = eventName;
        this.value = value;
        this.rawMessage = rawMessage;
    }

    public String getPluginName() {
        return pluginName;
    }

    public String getEventName() {
        return eventName;
    }

    public Object getValue() {
        return value;
    }

    public PluginMessage getRawMessage() {
        return rawMessage;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}