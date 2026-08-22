package net.teujaem.plugin.database;

import net.teujaem.jpalib.database.DatabaseManager;
import net.teujaem.lang.Lang;
import net.teujaem.plugin.config.ConfigManager;
import org.slf4j.Logger;

public class DatabaseController {

    private DatabaseManager databaseManager;

    public DatabaseManager initialize(ConfigManager configManager, Logger logger, Lang lang, Class<?> applicationClass) {
        return databaseManager = new DatabaseManager(
                configManager.getDatabase().getHost(),
                configManager.getDatabase().getPort(),
                configManager.getDatabase().getName(),
                configManager.getDatabase().getUser(),
                configManager.getDatabase().getPassword(),
                logger,
                lang,
                applicationClass
        );
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
