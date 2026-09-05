package net.teujaem.spFramework.config;

public class ConfigManager {

    private String language = "ko_kr";
    private boolean proxy = false;
    private WebsocketConfig websocket = new WebsocketConfig();
    private boolean debug = false;
    private DatabaseConfig database = new DatabaseConfig();

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isProxy() {
        return proxy;
    }

    public void setProxy(boolean proxy) {
        this.proxy = proxy;
    }

    public WebsocketConfig getWebsocket() {
        return websocket;
    }

    public void setWebsocket(WebsocketConfig websocket) {
        this.websocket = websocket;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public DatabaseConfig getDatabase() {
        return database;
    }

    public void setDatabase(DatabaseConfig database) {
        this.database = database;
    }

    public static class WebsocketConfig {

        private String host = "0.0.0.0";
        private int port = 8080;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    public static class DatabaseConfig {

        private String host = "localhost";
        private int port = 3306;
        private String name = "velocity_db";
        private String user = "root";
        private String password = "";

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}