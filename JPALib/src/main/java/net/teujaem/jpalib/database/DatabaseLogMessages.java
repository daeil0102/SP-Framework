package net.teujaem.jpalib.database;

import net.teujaem.lang.Lang;

public class DatabaseLogMessages {

    private static final String PREFIX = "database.";

    private final Lang lang;

    public DatabaseLogMessages(
            Lang lang
    ) {
        this.lang = lang;
    }

    private String get(
            String key,
            Object... args
    ) {
        return lang.get(
                PREFIX + key,
                args
        );
    }

    public String initialized() {
        return get("initialized");
    }

    public String connectionSuccess(
            String host,
            int port,
            String database
    ) {
        return get(
                "connection.success",
                host,
                port,
                database
        );
    }

    public String driverNotFound() {
        return get("driver.not_found");
    }

    public String jpaInitializeFailed() {
        return get("jpa.initialize_failed");
    }

    public String databaseAccessDenied(
            String username,
            String database
    ) {
        return get(
                "access_denied",
                username,
                database
        );
    }

    public String authenticationFailed(
            String username
    ) {
        return get(
                "authentication_failed",
                username
        );
    }

    public String databaseNotFound(
            String database
    ) {
        return get(
                "not_found",
                database
        );
    }

    public String connectionRefused(
            String host,
            int port
    ) {
        return get(
                "connection_refused",
                host,
                port
        );
    }

    public String connectionFailed(
            String message
    ) {
        return get(
                "connection_failed",
                message
        );
    }

    public String shutdown() {
        return get("shutdown");
    }
}