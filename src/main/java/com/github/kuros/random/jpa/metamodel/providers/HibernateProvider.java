package com.github.kuros.random.jpa.metamodel.providers;

import com.github.kuros.random.jpa.annotation.VisibleForTesting;
import com.github.kuros.random.jpa.exception.MetaModelGenerationException;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.ColumnNameType;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.util.AttributeHelper;
import com.github.kuros.random.jpa.util.Util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.kuros.random.jpa.util.Util.invokeMethod;

public class HibernateProvider implements AttributeProvider {

    private Map<Class<?>, EntityTableMapping> entityTableMappingByClass;
    private Map<String, List<EntityTableMapping>> entityTableMappingByTableName;
    private EntityManager entityManager;

    public HibernateProvider(final EntityManager entityManager) {
        this.entityManager = entityManager;
        this.entityTableMappingByClass = new HashMap<>();
        this.entityTableMappingByTableName = new HashMap<>();
        init();
    }

    private void init() {
        final EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();

        try {
            final Map<String, Object> allClassMetadata = getMetaDataMap(entityManagerFactory);

            final Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
            for (EntityType<?> entity : entities) {
                final Object classMetadata = allClassMetadata.get(entity.getJavaType().getName());
                if (instanceOfSinglePersistable(classMetadata)) {
                    final EntityTableMapping entityTableMapping = new EntityTableMapping(entity.getJavaType());

                    entityTableMapping.addColumnIds((String[]) invokeMethod(classMetadata, "getKeyColumnNames"));
                    entityTableMapping.setTableName(getTableName((String) invokeMethod(classMetadata, "getTableName")));

                    addAttributeColumnMapping(entity, classMetadata, entityTableMapping);

                    for (String id : entityTableMapping.getColumnIds()) {
                        entityTableMapping.addAttributeIds(entityTableMapping.getAttributeName(id));
                    }

                    putEntityTableMapping(getTableName((String) invokeMethod(classMetadata, "getTableName")), entityTableMapping);
                    entityTableMappingByClass.put(entity.getJavaType(), entityTableMapping);
                }

            }
        } catch (Exception e) {
            throw new MetaModelGenerationException("Failed to generate metamodel\n", e);
        }
    }

    private void addAttributeColumnMapping(final EntityType<?> entity, final Object classMetadata, final EntityTableMapping entityTableMapping) throws ClassNotFoundException {
        final Map<String, ColumnNameType.Type> supportedAttributeNames = getSupportedAttributeNames(classMetadata);

        for (Attribute attribute : entity.getAttributes()) {
            final String name = AttributeHelper.getName(attribute);
            final String[] propertyColumnNames = (String[]) invokeMethod(classMetadata, "getPropertyColumnNames", name);
            final String columnName = propertyColumnNames.length > 0 ? propertyColumnNames[0] : null;
            if (columnName != null && supportedAttributeNames.keySet().contains(name)) {
                entityTableMapping.addAttributeColumnMapping(name, new ColumnNameType(columnName, supportedAttributeNames.get(name)));
            }
        }
    }

    private void putEntityTableMapping(final String tableName, final EntityTableMapping entityTableMapping) {
        List<EntityTableMapping> entityTableMappings = entityTableMappingByTableName.computeIfAbsent(tableName, k -> new ArrayList<>());
        entityTableMappings.add(entityTableMapping);
    }

    private Map<String, ColumnNameType.Type> getSupportedAttributeNames(final Object classMetaData) throws ClassNotFoundException {

        Map<String, ColumnNameType.Type> attributeTypeMap = new HashMap<>();
        final boolean[] insertables = (boolean[]) invokeMethod(classMetaData, "getPropertyInsertability");
        final String[] propertyNames = (String[]) invokeMethod(classMetaData, "getPropertyNames");
        final Object[] propertyTypes = (Object[]) invokeMethod(classMetaData, "getPropertyTypes");
        for (int i = 0; i < propertyNames.length; i++) {
            try {
                if (insertables[i]) {
                    if (instanceOfBasicType(propertyTypes[i])) {
                        attributeTypeMap.put(propertyNames[i], ColumnNameType.Type.BASIC);
                    } else {
                        attributeTypeMap.put(propertyNames[i], ColumnNameType.Type.MAPPED);
                    }
                }
            } catch (final IndexOutOfBoundsException e) {
                //Skip
            }
        }

        final Object entityMetamodel = invokeMethod(classMetaData, "getEntityMetamodel");
        final Object identifierProperty = invokeMethod(entityMetamodel, "getIdentifierProperty");
        attributeTypeMap.put((String) invokeMethod(identifierProperty, "getName"), ColumnNameType.Type.BASIC);
        return attributeTypeMap;
    }

    @VisibleForTesting
    String getTableName(final String name) {
        String tableName = name;
        if (tableName.contains(".")) {
            final int lastIndexOf = tableName.lastIndexOf('.');
            if (lastIndexOf + 1 < tableName.length()) {
                tableName = tableName.substring(lastIndexOf + 1);
            }
        }
        return tableName.toLowerCase();
    }

    public EntityTableMapping get(final Class<?> type) {
        return entityTableMappingByClass.get(type);
    }

    public List<EntityTableMapping> get(final String tableName) {
        return entityTableMappingByTableName.get(tableName.toLowerCase());
    }

    private boolean instanceOfSinglePersistable(final Object obj) throws ClassNotFoundException {
        final String singleTableEntityPersister = "org.hibernate.persister.entity.SingleTableEntityPersister";
        final Class<?> singleTableClass = Class.forName(singleTableEntityPersister);

        return singleTableClass.isInstance(obj);
    }

    private boolean instanceOfBasicType(final Object obj) throws ClassNotFoundException {
        final Class<?> basicType = Class.forName("org.hibernate.type.BasicType");
        return basicType.isInstance(obj);
    }

    private Map<String, Object> getMetaDataMap(final EntityManagerFactory entityManagerFactory) {
        try {
            final Metamodel metamodel = entityManagerFactory.getMetamodel();
            return (Map<String, Object>) Util.invokeMethod(metamodel, "entityPersisters");
        } catch (final Exception e) {
            final Object sessionFactory = invokeMethod(entityManagerFactory, "getSessionFactory");
            return (Map<String, Object>) invokeMethod(sessionFactory, "getAllClassMetadata");
        }
    }
}
