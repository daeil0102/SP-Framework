package net.teujaem.spFramework.api.event;

import net.teujaem.spFramework.websoket.PluginMessage;

public interface ProxyEvent {
    void onProxyEvent(PluginMessage pluginMessage);
}
