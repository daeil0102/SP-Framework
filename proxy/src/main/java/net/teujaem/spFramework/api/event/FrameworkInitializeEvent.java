package net.teujaem.spFramework.api.event;

import com.velocitypowered.api.event.annotation.AwaitingEvent;
import net.teujaem.spFramework.SPFramework;

@AwaitingEvent
public class FrameworkInitializeEvent {

    private final SPFramework framework;

    public FrameworkInitializeEvent(SPFramework framework) {
        this.framework = framework;
    }

    public SPFramework getFramework() {
        return framework;
    }

}