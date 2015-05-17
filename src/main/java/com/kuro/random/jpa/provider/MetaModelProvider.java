package com.kuro.random.jpa.provider;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kumar Rohit on 4/25/15.
 */
public final class MetaModelProvider {
    private EntityManager entityManager;

    private MetaModelProvider(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public static MetaModelProvider newInstance(final EntityManager entityManager) {
        return new MetaModelProvider(entityManager);
    }

    public Map<String, EntityType<?>> getMetaModelRelations() {

        final Set<EntityType<?>> entities = getEntityTypes();

        final Map<String, EntityType<?>> entityMap = new HashMap<String, EntityType<?>>(entities.size());
        for (EntityType<?> entity : entities) {
            final Class<?> javaType = entity.getJavaType();

            if (isEntityTable(javaType)) {
                final String tableName = getTableName(javaType);
                entityMap.put(tableName, entity);
            }
        }

        return entityMap;
    }

    public Map<Class<?>, EntityType<?>> getMetaModelClassMapping() {

        final Set<EntityType<?>> entities = getEntityTypes();

        final Map<Class<?>, EntityType<?>> entityMap = new HashMap<Class<?>, EntityType<?>>(entities.size());
        for (EntityType<?> entity : entities) {
            final Class<?> javaType = entity.getJavaType();

            if (isEntityTable(javaType)) {
                entityMap.put(javaType, entity);
            }
        }

        return entityMap;
    }

    private Set<EntityType<?>> getEntityTypes() {
        final EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
        return entityManagerFactory.getMetamodel().getEntities();
    }

    private String getTableName(final Class<?> javaType) {
        final Table annotation = javaType.getAnnotation(Table.class);
        final String tableName = annotation.name();
        return tableName.isEmpty() ? javaType.getSimpleName() : tableName;
    }

    private boolean isEntityTable(final Class<?> javaType) {
        return javaType.getAnnotation(Table.class) != null;
    }
}
