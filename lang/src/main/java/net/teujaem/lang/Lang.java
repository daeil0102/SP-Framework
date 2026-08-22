package net.teujaem.lang;

import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Lang {

    private final Properties properties = new Properties();
    private final String locale;

    public Lang(
            String locale,
            Logger logger
    ) {
        this.locale = locale;

        load(
                "/lang/Plugin/" + locale + ".lang",
                logger
        );

        load(
                "/lang/JPALib/" + locale + ".lang",
                logger
        );

        logger.info(
                "Language loaded: {}",
                locale
        );
    }

    private void load(
            String path,
            Logger logger
    ) {

        try (
                InputStream input =
                        Lang.class.getResourceAsStream(path)
        ) {

            if (input == null) {

                logger.warn(
                        "언어 파일을 찾을 수 없습니다: {}",
                        path
                );

                return;
            }

            properties.load(
                    new InputStreamReader(
                            input,
                            StandardCharsets.UTF_8
                    )
            );

        } catch (IOException e) {

            throw new IllegalStateException(
                    "언어 파일을 불러오지 못했습니다: "
                            + path,
                    e
            );
        }
    }

    public String get(String key) {

        return properties.getProperty(
                key,
                key
        );
    }

    public String get(
            String key,
            Object... args
    ) {

        return String.format(
                get(key),
                args
        );
    }

    public String getLocale() {

        return locale;
    }
}