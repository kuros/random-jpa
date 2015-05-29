package com.github.kuros.random.jpa.metamodel;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kumar Rohit on 5/30/15.
 * MetaModel provider for Annotated entity classes.
 */
public class AnnotatedMetaModelProvider implements MetaModelProvider {
    private EntityManager entityManager;

    public AnnotatedMetaModelProvider(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Map<String, List<FieldName>> getFieldsByTableName() {
        final Set<EntityType<?>> entities = getEntityTypes();

        final Map<String, List<FieldName>> entityMap = new HashMap<String, List<FieldName>>();
        for (EntityType<?> entity : entities) {
            final Class<?> javaType = entity.getJavaType();

            if (isEntityTable(javaType)) {
                final String tableName = getTableName(javaType);
                entityMap.put(tableName, getFields(javaType));
            }
        }

        return entityMap;
    }

    private List<FieldName> getFields(final Class<?> type) {
        final List<FieldName> fields = new ArrayList<FieldName>();
        final Field[] declaredFields = type.getDeclaredFields();

        for (Field declaredField : declaredFields) {

            final Column column = declaredField.getAnnotation(Column.class);
            final AttributeOverrides attributeOverrides = declaredField.getAnnotation(AttributeOverrides.class);
            final AttributeOverride attributeOverride = declaredField.getAnnotation(AttributeOverride.class);

            if (column != null) {
                fields.add(new FieldName(declaredField, column.name()));
            } else if (attributeOverrides != null) {
                final AttributeOverride[] overrides = attributeOverrides.value();
                for (AttributeOverride override : overrides) {
                    if (override.column() != null) {
                        fields.add(new FieldName(declaredField, override.column().name()));
                        break;
                    }
                }
            } else if (attributeOverride != null && attributeOverride.column() != null) {
                fields.add(new FieldName(declaredField, attributeOverride.column().name()));
            }
        }

        return fields;
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
