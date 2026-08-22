package net.teujaem.jpalib.jpa.transaction;

import jakarta.persistence.EntityManager;

@FunctionalInterface
public interface JpaTransaction<T> {

    T execute(EntityManager entityManager) throws Exception;
}