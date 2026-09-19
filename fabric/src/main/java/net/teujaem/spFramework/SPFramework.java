package net.teujaem.spFramework;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.teujaem.spFramework.api.event.FrameworkInitializeEvent;
import net.teujaem.spFramework.config.ConfigManager;
import net.teujaem.spFramework.config.LoadConfig;
import net.teujaem.spFramework.database.DatabaseController;
import net.teujaem.spFramework.websoket.WebSocketClient;
import net.teujaem.lang.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SPFramework implements ModInitializer {

    public static final String MOD_ID = "spframework";

    private static SPFramework instance;

    private ConfigManager configManager;
    private WebSocketClient webSocketClient;
    private Logger logger;
    private DatabaseController databaseController;
    private Lang lang;

    private MinecraftServer server;

    @Override
    public void onInitialize() {
        instance = this;

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            this.server = server;

            try {
                reload();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            FrameworkInitializeEvent.fire(new FrameworkInitializeEvent(this));
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> shutdown());
    }

    public void reload() throws Exception {

        logger = LoggerFactory.getLogger("SP-Framework");

        configManager = LoadConfig.load(logger);

        if (configManager.isProxy())
            webSocketClient = new WebSocketClient(configManager.getWebsocket().getHost(), configManager.getWebsocket().getPort(), logger, this);

        lang = new Lang(configManager.getLanguage(), logger);

        databaseController = new DatabaseController();
        databaseController.initialize(configManager, logger, lang);

        logger.info(lang.get("plugin.initialized"));
    }

    private void shutdown() {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.close();
        }

        if (databaseController != null && databaseController.getDatabaseManager() != null) {
            databaseController.getDatabaseManager().close();
        }

        server = null;
    }

    public static SPFramework getInstance() {
        return instance;
    }

    public WebSocketClient getWebSocketClient() {
        return webSocketClient;
    }

    public DatabaseController getDatabaseController() {
        return databaseController;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Lang getLang() {
        return lang;
    }

    public Logger getLogger() {
        return logger;
    }

    public MinecraftServer getServer() {
        return server;
    }
}
