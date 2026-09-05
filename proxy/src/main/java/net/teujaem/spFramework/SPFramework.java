package net.teujaem.spFramework;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.teujaem.jpalib.database.DatabaseManager;
import net.teujaem.lang.Lang;
import net.teujaem.spFramework.config.LoadConfig;
import net.teujaem.spFramework.config.ConfigManager;
import net.teujaem.spFramework.database.DatabaseController;
import net.teujaem.spFramework.websoket.WebSocketServerApplication;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "spframework",
        name = "SP-FrameworK",
        version = BuildConstants.VERSION
)
public class SPFramework {

    private static SPFramework instance;

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    private ConfigManager configManager;
    private Lang lang;

    private DatabaseManager databaseManager;

    private DatabaseController databaseController;

    private WebSocketServerApplication webSocketServerApplication;

    @Inject
    public SPFramework(
            ProxyServer server,
            Logger logger,
            @DataDirectory Path dataDirectory
    ) {

        instance = this;

        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialize(
            ProxyInitializeEvent event
    ) {
        
        configManager =
                LoadConfig.load(
                        dataDirectory,
                        logger
                );

        lang =
                new Lang(
                        configManager.getLanguage(),
                        logger
                );

        webSocketServerApplication = new WebSocketServerApplication(configManager.getWebsocket().getHost(), configManager.getWebsocket().getPort(), logger);

        databaseController = new DatabaseController();

        try {
            databaseManager = databaseController.initialize(configManager, logger, lang);

        } catch (Exception e) {

            logger.error(
                    lang.get(
                            "database.init_failed",
                            e.getMessage()
                    ),
                    e
            );

            databaseManager = null;
        }

        logger.info(
                lang.get(
                        "plugin.initialized"
                )
        );
    }

    @Subscribe
    public void onProxyShutdown(
            ProxyShutdownEvent event
    ) {

        if (databaseManager != null) {

            databaseManager.close();

            databaseManager = null;
        }
    }

    public ConfigManager getConfig() {
        return configManager;
    }

    public Lang getLang() {
        return lang;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public Logger getLogger() {
        return logger;
    }

    public ProxyServer getServer() {
        return server;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public static SPFramework getInstance() {
        return instance;
    }

    public WebSocketServerApplication getWebSocketServerApplication() {
        return webSocketServerApplication;
    }
}