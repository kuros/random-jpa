package com.kuro.random.jpa.mapper;

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
public class MetaModelProvider {
    private EntityManager entityManager;

    private MetaModelProvider(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public static MetaModelProvider newInstance(final EntityManager entityManager) {
        return new MetaModelProvider(entityManager);
    }

    public Map<String, EntityType<?>> getMetaModelRelations() {

        final EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
        final Set<EntityType<?>> entities = entityManagerFactory.getMetamodel().getEntities();

        Map<String, EntityType<?>> entityMap = new HashMap<String, EntityType<?>>(entities.size());
        for (EntityType<?> entity : entities) {
            final Class<?> javaType = entity.getJavaType();
            final String tableName = getTableName(javaType);

            entityMap.put(tableName, entity);
        }

        return entityMap;
    }

    private String getTableName(final Class<?> javaType) {
        final Table annotation = javaType.getAnnotation(Table.class);
        final String tableName = annotation.name();
        return tableName.isEmpty() ? javaType.getSimpleName() : tableName;
    }
}
