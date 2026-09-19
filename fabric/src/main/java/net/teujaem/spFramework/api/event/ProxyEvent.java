package net.teujaem.spFramework.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.teujaem.spFramework.websoket.PluginMessage;

/**
 * 프록시(Velocity)로부터 메시지를 수신했을 때 발생하는 이벤트입니다.
 * Bukkit 사이드의 ProxyEvent와 동일한 역할이며, Fabric에는 이벤트 버스가 없으므로
 * Fabric API의 Event 방식으로 리스너를 등록합니다.
 *
 * <pre>
 * ProxyEvent.register(event -> {
 *     if (!event.getPluginName().equals("MyMod")) return;
 *     System.out.println(event.getEventName() + " = " + event.getValue());
 * });
 * </pre>
 */
public class ProxyEvent {

    public static final Event<Listener> EVENT = EventFactory.createArrayBacked(
            Listener.class,
            listeners -> event -> {
                for (Listener listener : listeners) {
                    listener.onProxyEvent(event);
                }
            }
    );

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

    public static void register(Listener listener) {
        EVENT.register(listener);
    }

    public static void fire(ProxyEvent event) {
        EVENT.invoker().onProxyEvent(event);
    }

    @FunctionalInterface
    public interface Listener {
        void onProxyEvent(ProxyEvent event);
    }
}
