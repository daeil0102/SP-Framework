package net.teujaem.jpalib.database;

import net.teujaem.jpalib.jpa.JpaConfig;
import net.teujaem.jpalib.jpa.scanner.JpaEntityScanner;
import net.teujaem.jpalib.jpa.JpaManager;
import net.teujaem.lang.Lang;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private final Logger logger;
    private final Lang lang;

    private final JpaManager jpaManager;

    public DatabaseManager(
            String host,
            int port,
            String database,
            String username,
            String password,
            Logger logger,
            Lang lang,
            Class<?> applicationClass
    ) {

        this.logger = logger;
        this.lang = lang;

        String jdbcUrl =
                "jdbc:mariadb://"
                        + host
                        + ":"
                        + port
                        + "/"
                        + database
                        + "?useSsl=false&serverTimezone=Asia/Seoul";

        try {

            Class.forName(
                    "org.mariadb.jdbc.Driver"
            );

        } catch (ClassNotFoundException e) {

            throw new IllegalStateException(
                    lang.get(
                            "database.driver.not_found"
                    ),
                    e
            );
        }

        testConnection(
                jdbcUrl,
                username,
                password,
                host,
                port,
                database
        );

        Class<?>[] entities =
                JpaEntityScanner.scan(
                        applicationClass
                );

        logger.info(
                lang.get(
                        "database.entity.scanned",
                        entities.length
                )
        );

        for (Class<?> entity : entities) {

            logger.info(
                    lang.get(
                            "database.entity.registered",
                            entity.getName()
                    )
            );
        }

        JpaConfig jpaConfig =
                new JpaConfig(
                        jdbcUrl,
                        username,
                        password
                )
                .ddlAuto("update")
                .showSql(false)
                .formatSql(false);

        this.jpaManager =
                new JpaManager(
                        jpaConfig,
                        entities
                );

        try {

            jpaManager.initialize();

        } catch (Exception e) {

            throw new IllegalStateException(
                    lang.get(
                            "database.jpa.initialize_failed"
                    )
                            + ": "
                            + e.getMessage(),
                    e
            );
        }

        logger.info(
                lang.get(
                        "database.jpa.initialized"
                )
        );
    }

    private void testConnection(
            String jdbcUrl,
            String username,
            String password,
            String host,
            int port,
            String database
    ) {

        try (
                Connection connection =
                        DriverManager.getConnection(
                                jdbcUrl,
                                username,
                                password
                        )
        ) {

            logger.info(lang.get("database.connection.success", host, port, database));

        } catch (SQLException e) {

            String message =
                    e.getMessage();

            if (message == null) {
                message = "";
            }

            if (message.contains(
                    "Access denied"
            )) {

                if (message.contains(
                        "to database"
                )) {

                    throw new IllegalStateException(
                            lang.get(
                                    "database.access_denied",
                                    username,
                                    database
                            ),
                            e
                    );

                } else {

                    throw new IllegalStateException(
                            lang.get(
                                    "database.authentication_failed",
                                    username
                            ),
                            e
                    );
                }
            }

            if (message.contains(
                    "Unknown database"
            )) {

                throw new IllegalStateException(
                        lang.get(
                                "database.not_found",
                                database
                        ),
                        e
                );
            }

            if (message.contains(
                    "Connection refused"
            )) {

                throw new IllegalStateException(
                        lang.get(
                                "database.connection_refused",
                                host,
                                port
                        ),
                        e
                );
            }

            throw new IllegalStateException(
                    lang.get(
                            "database.connection_failed",
                            message
                    ),
                    e
            );
        }
    }

    public JpaManager getJpa() {
        return jpaManager;
    }

    public void close() {

        if (jpaManager != null) {
            jpaManager.shutdown();
        }

        logger.info(
                lang.get(
                        "database.shutdown"
                )
        );
    }
}