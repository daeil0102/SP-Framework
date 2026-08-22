package net.teujaem.plugin;

import net.teujaem.plugin.config.ConfigManager;
import net.teujaem.plugin.config.LoadConfig;
import net.teujaem.plugin.websoket.WebSocketClient;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SPFramework extends JavaPlugin {

    private static SPFramework instance;

    private ConfigManager configManager;
    private WebSocketClient webSocketClient;
    private Logger logger;

    @Override
    public void onEnable() {
        instance = this;
        try {
            reload();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void reload() throws Exception {

        logger = LoggerFactory.getLogger("SP-Framework");

        configManager = LoadConfig.load(this);

        if (configManager.isProxy())
            webSocketClient = new WebSocketClient(configManager.getWebsocket().getHost(), configManager.getWebsocket().getPort(), logger);
    }

}
