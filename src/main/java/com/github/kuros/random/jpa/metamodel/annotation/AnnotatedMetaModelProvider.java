package com.github.kuros.random.jpa.metamodel.annotation;

import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.EntityTableMapping;
import com.github.kuros.random.jpa.metamodel.FieldName;
import com.github.kuros.random.jpa.metamodel.MetaModelProvider;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
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
    private AttributeProvider attributeProvider;

    public AnnotatedMetaModelProvider(final EntityManager entityManager) {
        this.entityManager = entityManager;
        attributeProvider = AttributeProvider.getInstance(entityManager);
    }

    @Override
    public Map<String, List<FieldName>> getFieldsByTableName() {
        final Set<EntityType<?>> entities = getEntityTypes();

        final Map<String, List<FieldName>> entityMap = new HashMap<String, List<FieldName>>();
        for (EntityType<?> entity : entities) {
            final Class<?> javaType = entity.getJavaType();

            final EntityTableMapping entityTableMapping = attributeProvider.get(javaType);

            entityMap.put(entityTableMapping.getTableName(), getFields(javaType));
        }

        return entityMap;
    }

    private List<FieldName> getFields(final Class<?> type) {

        final EntityTableMapping entityTableMapping = attributeProvider.get(type);

        final List<FieldName> fields = new ArrayList<FieldName>();
        final Field[] declaredFields = type.getDeclaredFields();

        for (Field declaredField : declaredFields) {

            final String columnName = entityTableMapping.getColumnName(declaredField.getName());
            if (columnName != null) {
                fields.add(new FieldName(declaredField, columnName));
            }
        }

        return fields;
    }

    private Set<EntityType<?>> getEntityTypes() {
        final EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
        return entityManagerFactory.getMetamodel().getEntities();
    }
}
