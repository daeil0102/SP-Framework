package net.teujaem.spFramework;

import net.teujaem.lang.Lang;
import net.teujaem.spFramework.config.ConfigManager;
import net.teujaem.spFramework.config.LoadConfig;
import net.teujaem.spFramework.database.DatabaseController;
import net.teujaem.spFramework.websoket.WebSocketClient;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SPFramework extends JavaPlugin {

    private static SPFramework instance;

    private ConfigManager configManager;
    private WebSocketClient webSocketClient;
    private Logger logger;
    private DatabaseController databaseController;
    private Lang lang;

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
        if (databaseController != null && databaseController.getDatabaseManager() != null) {
            databaseController.getDatabaseManager().close();
        }
    }

    public void reload() throws Exception {

        logger = LoggerFactory.getLogger("SP-Framework");

        configManager = LoadConfig.load(this);

        if (configManager.isProxy())
            webSocketClient = new WebSocketClient(configManager.getWebsocket().getHost(), configManager.getWebsocket().getPort(), logger);

        lang = new Lang(configManager.getLanguage(), logger);

        databaseController = new DatabaseController();
        databaseController.initialize(configManager, logger, lang);
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
}