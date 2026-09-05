package net.teujaem.spFramework.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class LoadConfig {

    public static ConfigManager load(Plugin plugin) {

        File file = new File(plugin.getDataFolder(), "config.yaml");
        if (!file.exists()) {
            plugin.saveResource("config.yaml", false);
        }

        FileConfiguration config =
                YamlConfiguration.loadConfiguration(file);

        ConfigManager configManager = new ConfigManager();

        configManager.setLanguage(
                config.getString("language", "ko_kr")
        );

        configManager.setProxy(
                config.getBoolean("proxy", false)
        );

        configManager.getWebsocket().setHost(
                config.getString("websocket.host", "0.0.0.0")
        );

        configManager.getWebsocket().setPort(
                config.getInt("websocket.port", 8080)
        );

        configManager.setDebug(
                config.getBoolean("debug", false)
        );

        configManager.getDatabase().setHost(
                config.getString("database.host", "localhost")
        );

        configManager.getDatabase().setPort(
                config.getInt("database.port", 3306)
        );

        configManager.getDatabase().setName(
                config.getString("database.name", "velocity_db")
        );

        configManager.getDatabase().setUser(
                config.getString("database.user", "root")
        );

        configManager.getDatabase().setPassword(
                config.getString("database.password", "")
        );

        return configManager;
    }
}