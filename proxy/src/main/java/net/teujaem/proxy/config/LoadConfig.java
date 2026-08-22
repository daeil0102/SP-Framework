package net.teujaem.proxy.config;

import net.teujaem.proxy.SPFramework;
import org.slf4j.Logger;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class LoadConfig {

    public static ConfigManager load(Path dataDirectory, Logger logger) {
        try {
            if (!Files.exists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }

            Path configPath = dataDirectory.resolve("config.yaml");

            if (!Files.exists(configPath)) {
                try (InputStream in = SPFramework.class.getResourceAsStream("/config.yaml")) {
                    if (in == null) {
                        logger.error("기본 config.yaml 리소스를 찾을 수 없음");
                        return new ConfigManager();
                    }
                    Files.copy(in, configPath); // dataDirectory로 복사
                }
                logger.info("기본 config.yaml 생성됨: " + configPath);
            }

            LoaderOptions options = new LoaderOptions();
            Constructor constructor = new Constructor(ConfigManager.class, options);
            Yaml yaml = new Yaml(constructor);

            try (InputStream in = Files.newInputStream(configPath)) {
                ConfigManager configManager = yaml.load(in);
                return configManager != null ? configManager : new ConfigManager();
            }
        } catch (IOException e) {
            logger.error("설정 로드 실패, 기본값 사용", e);
            return new ConfigManager();
        }
    }
}