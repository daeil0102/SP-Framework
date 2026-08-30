package net.teujaem.jpalib.jpa;

import java.util.LinkedHashSet;
import java.util.Set;

/** 각 플러그인이 자신의 JPA 엔티티 클래스를 등록하는 공용 레지스트리 */
public final class JpaEntityRegistry {

    private static final Set<Class<?>> ENTITIES = new LinkedHashSet<>();

    private JpaEntityRegistry() {}

    public static void register(Class<?> entityClass) {
        if (entityClass != null) {
            ENTITIES.add(entityClass);
        }
    }

    public static void registerAll(Class<?>... entityClasses) {
        for (Class<?> c : entityClasses) register(c);
    }

    public static Class<?>[] getAll() {
        return ENTITIES.toArray(new Class<?>[0]);
    }
}