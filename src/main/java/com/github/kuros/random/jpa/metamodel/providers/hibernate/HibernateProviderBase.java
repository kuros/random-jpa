package com.github.kuros.random.jpa.metamodel.providers.hibernate;

import com.github.kuros.random.jpa.annotation.VisibleForTesting;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.ColumnNameType;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.util.AttributeHelper;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.kuros.random.jpa.util.Util.invokeMethod;

/*
 * Copyright (c) 2015 Kumar Rohit
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License or any
 *    later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public abstract class HibernateProviderBase implements AttributeProvider {
    protected Map<Class<?>, EntityTableMapping> entityTableMappingByClass;
    protected Map<String, List<EntityTableMapping>> entityTableMappingByTableName;
    protected EntityManager entityManager;

    public HibernateProviderBase(final EntityManager entityManager) {
        this.entityManager = entityManager;
        this.entityTableMappingByClass = new HashMap<Class<?>, EntityTableMapping>();
        this.entityTableMappingByTableName = new HashMap<String, List<EntityTableMapping>>();
    }

    protected void init() {
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
            // do nothing
        }
    }

    private void addAttributeColumnMapping(final EntityType<?> entity, final Object classMetadata, final EntityTableMapping entityTableMapping) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
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
        List<EntityTableMapping> entityTableMappings = entityTableMappingByTableName.get(tableName);
        if (entityTableMappings == null) {
            entityTableMappings = new ArrayList<EntityTableMapping>();
            entityTableMappingByTableName.put(tableName, entityTableMappings);
        }
        entityTableMappings.add(entityTableMapping);
    }

    private Map<String, ColumnNameType.Type> getSupportedAttributeNames(final Object classMetaData) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {

        Map<String, ColumnNameType.Type> attributeTypeMap = new HashMap<String, ColumnNameType.Type>();
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

    protected abstract Map<String, Object> getMetaDataMap(final EntityManagerFactory entityManagerFactory) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;
}
