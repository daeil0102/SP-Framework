package net.teujaem.jpalib.database;

import net.teujaem.jpalib.jpa.JpaConfig;
import net.teujaem.jpalib.jpa.JpaManager;
import net.teujaem.lang.Lang;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DatabaseManager {

    private final Logger logger;
    private final Lang lang;

    private final String jdbcUrl;
    private final String username;
    private final String password;

    private final List<JpaManager> jpaManagers = new CopyOnWriteArrayList<>();

    public DatabaseManager(
            String host,
            int port,
            String database,
            String username,
            String password,
            Logger logger,
            Lang lang
    ) {

        this.logger = logger;
        this.lang = lang;
        this.username = username;
        this.password = password;

        this.jdbcUrl =
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
    }

    /**
     * 호출한 플러그인이 넘긴 엔티티 클래스들로 독립된 JpaManager를 생성합니다.
     * DataBase 인스턴스가 만들어질 때마다 호출됩니다.
     */
    public JpaManager createJpaManager(Class<?>... entities) {

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

        JpaManager jpaManager =
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

        jpaManagers.add(jpaManager);

        return jpaManager;
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

    public void close() {

        for (JpaManager jpaManager : jpaManagers) {
            jpaManager.shutdown();
        }

        jpaManagers.clear();

        logger.info(
                lang.get(
                        "database.shutdown"
                )
        );
    }
}