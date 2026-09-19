package net.teujaem.spFramework.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.teujaem.spFramework.SPFramework;

/**
 * 프레임워크 초기화(config/lang/DB/WebSocket 준비)가 끝났을 때 발행되는 이벤트입니다.
 * 프록시 사이드의 FrameworkInitializeEvent와 동일하게 초기화 타이밍 감지용으로 사용합니다.
 *
 * <pre>
 * FrameworkInitializeEvent.register(event ->
 *         new DataBase(MyEntity.class));
 * </pre>
 */
public class FrameworkInitializeEvent {

    public static final Event<Listener> EVENT = EventFactory.createArrayBacked(
            Listener.class,
            listeners -> event -> {
                for (Listener listener : listeners) {
                    listener.onFrameworkInitialize(event);
                }
            }
    );

    private final SPFramework framework;

    public FrameworkInitializeEvent(SPFramework framework) {
        this.framework = framework;
    }

    public SPFramework getFramework() {
        return framework;
    }

    public static void register(Listener listener) {
        EVENT.register(listener);
    }

    public static void fire(FrameworkInitializeEvent event) {
        EVENT.invoker().onFrameworkInitialize(event);
    }

    @FunctionalInterface
    public interface Listener {
        void onFrameworkInitialize(FrameworkInitializeEvent event);
    }
}
