package net.teujaem.jpalib.database;

import net.teujaem.lang.Lang;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private final Logger logger;
    private final Lang lang;

    public DatabaseConnection(
            Logger logger,
            Lang lang
    ) {
        this.logger = logger;
        this.lang = lang;
    }

    public Connection connect(
            String jdbcUrl,
            String username,
            String password,
            String host,
            int port,
            String database
    ) {

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

        try {

            Connection connection =
                    DriverManager.getConnection(
                            jdbcUrl,
                            username,
                            password
                    );

            logger.info(
                    lang.get(
                            "database.connection.success",
                            host,
                            port,
                            database
                    )
            );

            return connection;

        } catch (SQLException e) {

            throw new DatabaseExceptionHandler(
                    lang
            ).handle(
                    e,
                    username,
                    host,
                    port,
                    database
            );
        }
    }
}