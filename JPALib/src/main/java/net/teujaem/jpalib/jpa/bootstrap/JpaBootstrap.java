package net.teujaem.jpalib.jpa.bootstrap;

import jakarta.persistence.EntityManagerFactory;
import net.teujaem.jpalib.jpa.JpaConfig;
import org.hibernate.jpa.HibernatePersistenceProvider;

import java.util.Properties;

public class JpaBootstrap {

    private JpaBootstrap() {
    }

    public static EntityManagerFactory create(
            JpaConfig config,
            Class<?>... entities
    ) {

        Properties properties =
                new Properties();

        properties.setProperty(
                "jakarta.persistence.jdbc.driver",
                "org.mariadb.jdbc.Driver"
        );

        properties.setProperty(
                "jakarta.persistence.jdbc.url",
                config.getJdbcUrl()
        );

        properties.setProperty(
                "jakarta.persistence.jdbc.user",
                config.getUsername()
        );

        properties.setProperty(
                "jakarta.persistence.jdbc.password",
                config.getPassword()
        );

        properties.setProperty(
                "hibernate.hbm2ddl.auto",
                config.getDdlAuto()
        );

        properties.setProperty(
                "hibernate.show_sql",
                String.valueOf(
                        config.isShowSql()
                )
        );

        properties.setProperty(
                "hibernate.format_sql",
                String.valueOf(
                        config.isFormatSql()
                )
        );

        JpaPersistenceUnitInfo persistenceUnitInfo =
                new JpaPersistenceUnitInfo(
                        "spframework",
                        entities,
                        properties,
                        Thread.currentThread()
                                .getContextClassLoader()
                );

        return new HibernatePersistenceProvider()
                .createContainerEntityManagerFactory(
                        persistenceUnitInfo,
                        properties
                );
    }
}