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

    /**
     * 신규/기존 엔티티를 모두 안전하게 저장합니다.
     * <p>
     * 기존에는 em.persist(entity)만 호출하여, 이미 DB에 존재하는
     * PK를 가진 엔티티를 저장하려 할 때 INSERT 충돌
     * (Duplicate entry ... for key 'PRIMARY')이 발생했습니다.
     * <p>
     * em.contains(entity)로 현재 영속성 컨텍스트가 이미 관리 중인
     * 엔티티인지 확인하고, 그렇지 않다면(= 새로 생성됐거나 detached
     * 상태라면) em.merge(entity)를 사용합니다. merge()는 PK 기준으로
     * DB를 조회해 존재하면 UPDATE, 존재하지 않으면 INSERT를 자동으로
     * 수행하므로 이 문제가 해결됩니다.
     * <p>
     * 주의: merge()는 인자로 받은 detached 인스턴스를 그대로
     * 영속화하지 않고 새로운 managed 인스턴스를 반환합니다.
     * 따라서 반드시 반환값을 사용해야 하며, 호출부에서 원래
     * 넘겼던 entity 참조를 캐시 등에 계속 쓰고 있다면 반환값으로
     * 교체해주는 것이 안전합니다.
     */
    public <T> T save(T entity) {

        return execute(em -> {

            if (em.contains(entity)) {
                return entity;
            }

            @SuppressWarnings("unchecked")
            T managed = (T) em.merge(entity);

            return managed;
        });
    }

    public <T> CompletableFuture<T> saveAsync(
            T entity
    ) {

        return executeAsync(em -> {

            if (em.contains(entity)) {
                return entity;
            }

            @SuppressWarnings("unchecked")
            T managed = (T) em.merge(entity);

            return managed;
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