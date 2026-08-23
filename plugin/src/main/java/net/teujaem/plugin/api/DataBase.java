package net.teujaem.plugin.api;

import net.teujaem.plugin.SPFramework;

import java.util.concurrent.CompletableFuture;

public class DataBase {

    public static CompletableFuture<Boolean> save(Object entity) {
        return SPFramework.getInstance()
                .getDatabaseController()
                .getDatabaseManager()
                .getJpa()
                .saveAsync(entity)
                .thenApply(saved -> true)
                .exceptionally(error -> {
                    error.printStackTrace();
                    return false;
                });
    }

    public static <T> CompletableFuture<T> find(Class<T> entityClass, Object id) {
        return SPFramework.getInstance()
                .getDatabaseController()
                .getDatabaseManager()
                .getJpa()
                .findAsync(entityClass, id)
                .exceptionally(error -> {
                    error.printStackTrace();
                    return null;
                });
    }

    public static CompletableFuture<Boolean> delete(Object entity) {
        return SPFramework.getInstance()
                .getDatabaseController()
                .getDatabaseManager()
                .getJpa()
                .deleteAsync(entity)
                .thenApply(v -> true)
                .exceptionally(error -> {
                    error.printStackTrace();
                    return false;
                });
    }

}
