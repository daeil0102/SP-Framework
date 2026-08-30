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
     * PK를 가진 엔티티(예: UUID를 앱에서 직접 할당하는 PlayerEntity)를
     * 저장하려 할 때 INSERT 충돌(Duplicate entry ... for key 'PRIMARY')이
     * 발생했습니다.
     * <p>
     * 반대로 @GeneratedValue(strategy = IDENTITY) 등 DB가 PK를
     * 생성하는 엔티티(예: TestLogEntity)는 저장 전 ID가 null입니다.
     * 이 경우 persist()를 써야만 INSERT 직후 원본 객체(entity)에
     * 생성된 ID가 채워집니다. merge()를 쓰면 원본 객체는 건드리지
     * 않고 별도의 managed 복사본을 반환하므로, 호출부에서 원본
     * entity.getId()를 읽으면 계속 null이 나오는 문제가 생깁니다.
     * <p>
     * 따라서 엔티티의 PK가 이미 채워져 있는지 여부로 분기합니다.
     * - PK가 null (DB가 생성하는 신규 엔티티) → persist()
     *   → 원본 객체에 생성된 ID가 반영됨
     * - PK가 이미 존재 (앱이 직접 할당, DB에 있을 수도/없을 수도) → merge()
     *   → 있으면 UPDATE, 없으면 INSERT (upsert)
     * <p>
     * 주의: merge() 경로에서는 인자로 받은 detached 인스턴스를 그대로
     * 영속화하지 않고 새로운 managed 인스턴스를 반환합니다. 저장 이후
     * 그 엔티티를 계속 참조해서 쓸 계획이라면 반환값으로 교체하세요.
     */
    public <T> T save(T entity) {

        return execute(em -> {

            Object id = em.getEntityManagerFactory()
                    .getPersistenceUnitUtil()
                    .getIdentifier(entity);

            if (id == null) {
                // DB가 PK를 생성하는 신규 엔티티 (예: IDENTITY 전략)
                em.persist(entity);
                return entity;
            }

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

            Object id = em.getEntityManagerFactory()
                    .getPersistenceUnitUtil()
                    .getIdentifier(entity);

            if (id == null) {
                em.persist(entity);
                return entity;
            }

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