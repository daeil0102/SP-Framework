package net.teujaem.jpalib.jpa;

public class JpaConfig {

    private final String jdbcUrl;
    private final String username;
    private final String password;

    private String ddlAuto = "update";
    private boolean showSql = false;
    private boolean formatSql = false;

    public JpaConfig(
            String jdbcUrl,
            String username,
            String password
    ) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    public JpaConfig ddlAuto(String ddlAuto) {
        this.ddlAuto = ddlAuto;
        return this;
    }

    public JpaConfig showSql(boolean showSql) {
        this.showSql = showSql;
        return this;
    }

    public JpaConfig formatSql(boolean formatSql) {
        this.formatSql = formatSql;
        return this;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDdlAuto() {
        return ddlAuto;
    }

    public boolean isShowSql() {
        return showSql;
    }

    public boolean isFormatSql() {
        return formatSql;
    }
}