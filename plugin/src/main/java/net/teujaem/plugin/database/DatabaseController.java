package net.teujaem.plugin.database;

import net.teujaem.jpalib.database.DatabaseManager;
import net.teujaem.jpalib.jpa.JpaManager;
import net.teujaem.lang.Lang;
import net.teujaem.plugin.config.ConfigManager;
import org.slf4j.Logger;

public class DatabaseController {

    private DatabaseManager databaseManager;

    public DatabaseManager initialize(ConfigManager configManager, Logger logger, Lang lang) {
        return databaseManager = new DatabaseManager(
                configManager.getDatabase().getHost(),
                configManager.getDatabase().getPort(),
                configManager.getDatabase().getName(),
                configManager.getDatabase().getUser(),
                configManager.getDatabase().getPassword(),
                logger,
                lang
        );
    }

    public JpaManager createJpaManager(Class<?>... entities) {
        return databaseManager.createJpaManager(entities);
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}