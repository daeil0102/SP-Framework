package net.teujaem.jpalib.database;

import net.teujaem.lang.Lang;

import java.sql.SQLException;

public class DatabaseExceptionHandler {

    private final Lang lang;

    public DatabaseExceptionHandler(
            Lang lang
    ) {
        this.lang = lang;
    }

    public IllegalStateException handle(
            SQLException e,
            String username,
            String host,
            int port,
            String database
    ) {

        String message =
                e.getMessage();

        if (message == null) {
            message = "";
        }

        if (message.contains("Access denied")) {

            if (message.contains("to database")) {

                return new IllegalStateException(
                        lang.get(
                                "database.access_denied",
                                username,
                                database
                        ),
                        e
                );
            }

            return new IllegalStateException(
                    lang.get(
                            "database.authentication_failed",
                            username
                    ),
                    e
            );
        }

        if (message.contains("Unknown database")) {

            return new IllegalStateException(
                    lang.get(
                            "database.not_found",
                            database
                    ),
                    e
            );
        }

        if (message.contains("Connection refused")) {

            return new IllegalStateException(
                    lang.get(
                            "database.connection_refused",
                            host,
                            port
                    ),
                    e
            );
        }

        return new IllegalStateException(
                lang.get(
                        "database.connection_failed",
                        message
                ),
                e
        );
    }
}