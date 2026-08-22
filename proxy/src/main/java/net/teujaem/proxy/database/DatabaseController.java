package net.teujaem.proxy.database;

import net.teujaem.jpalib.database.DatabaseManager;
import net.teujaem.lang.Lang;
import net.teujaem.proxy.config.PluginConfig;
import org.slf4j.Logger;

public class DatabaseController {

    private DatabaseManager databaseManager;

    public DatabaseManager initialize(PluginConfig pluginConfig, Logger logger, Lang lang, Class<?> applicationClass) {
        return databaseManager = new DatabaseManager(
                pluginConfig.getDatabase().getHost(),
                pluginConfig.getDatabase().getPort(),
                pluginConfig.getDatabase().getName(),
                pluginConfig.getDatabase().getUser(),
                pluginConfig.getDatabase().getPassword(),
                logger,
                lang,
                applicationClass
        );
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
