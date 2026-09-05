package net.teujaem.spFramework.api;

import net.teujaem.jpalib.jpa.JpaManager;
import net.teujaem.spFramework.SPFramework;

import java.util.concurrent.CompletableFuture;

public class DataBase {

    private final JpaManager jpaManager;

    public DataBase(Class<?>... entityClasses) {
        this.jpaManager = SPFramework.getInstance()
                .getDatabaseController()
                .createJpaManager(entityClasses);
    }

    public CompletableFuture<Boolean> save(Object entity) {
        return jpaManager
                .saveAsync(entity)
                .thenApply(saved -> true)
                .exceptionally(error -> {
                    error.printStackTrace();
                    return false;
                });
    }

    public <T> CompletableFuture<T> find(Class<T> entityClass, Object id) {
        return jpaManager
                .findAsync(entityClass, id)
                .exceptionally(error -> {
                    error.printStackTrace();
                    return null;
                });
    }

    public CompletableFuture<Boolean> delete(Object entity) {
        return jpaManager
                .deleteAsync(entity)
                .thenApply(v -> true)
                .exceptionally(error -> {
                    error.printStackTrace();
                    return false;
                });
    }
}