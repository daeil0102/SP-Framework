package net.teujaem.jpalib.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import net.teujaem.jpalib.jpa.bootstrap.JpaBootstrap;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class JpaManager {

    private final JpaConfig config;
    private final Class<?>[] entities;

    private EntityManagerFactory entityManagerFactory;

    private final ExecutorService executor =
            Executors.newFixedThreadPool(4);

    public JpaManager(
            JpaConfig config,
            Class<?>... entities
    ) {
        this.config = config;
        this.entities = entities;
    }

    public void initialize() {

        if (entityManagerFactory != null) {
            return;
        }

        entityManagerFactory =
                JpaBootstrap.create(
                        config,
                        entities
                );
    }

    public EntityManager createEntityManager() {

        if (entityManagerFactory == null) {
            throw new IllegalStateException(
                    "JPA가 초기화되지 않았습니다."
            );
        }

        return entityManagerFactory
                .createEntityManager();
    }

    public <T> T execute(
            Function<EntityManager, T> function
    ) {

        EntityManager em =
                createEntityManager();

        EntityTransaction transaction =
                em.getTransaction();

        try {

            transaction.begin();

            T result =
                    function.apply(em);

            transaction.commit();

            return result;

        } catch (Exception e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;

        } finally {

            em.close();
        }
    }

    public <T> CompletableFuture<T> executeAsync(
            Function<EntityManager, T> function
    ) {

        return CompletableFuture.supplyAsync(
                () -> execute(function),
                executor
        );
    }

    public <T> T save(T entity) {

        return execute(em -> {

            em.persist(entity);

            return entity;
        });
    }

    public <T> CompletableFuture<T> saveAsync(
            T entity
    ) {

        return executeAsync(em -> {

            em.persist(entity);

            return entity;
        });
    }

    public <T> T find(
            Class<T> entityClass,
            Object id
    ) {

        return execute(
                em -> em.find(
                        entityClass,
                        id
                )
        );
    }

    public <T> CompletableFuture<T> findAsync(
            Class<T> entityClass,
            Object id
    ) {

        return executeAsync(
                em -> em.find(
                        entityClass,
                        id
                )
        );
    }

    public void delete(Object entity) {

        execute(em -> {

            Object managedEntity = entity;

            if (!em.contains(entity)) {

                managedEntity =
                        em.merge(entity);
            }

            em.remove(managedEntity);

            return null;
        });
    }

    public CompletableFuture<Void> deleteAsync(
            Object entity
    ) {

        return executeAsync(em -> {

            Object managedEntity = entity;

            if (!em.contains(entity)) {

                managedEntity =
                        em.merge(entity);
            }

            em.remove(managedEntity);

            return null;
        });
    }

    public void shutdown() {

        executor.shutdown();

        if (entityManagerFactory != null) {

            entityManagerFactory.close();

            entityManagerFactory = null;
        }
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }
}