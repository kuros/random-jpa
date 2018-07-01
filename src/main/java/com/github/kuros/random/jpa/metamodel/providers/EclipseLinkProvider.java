package com.github.kuros.random.jpa.metamodel.providers;

import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.ColumnNameType;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;

import javax.persistence.EntityManager;
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
public class EclipseLinkProvider implements AttributeProvider {

    private Map<Class<?>, EntityTableMapping> entityTableMappingByClass;
    private Map<String, List<EntityTableMapping>> entityTableMappingByTableName;
    private EntityManager entityManager;

    public EclipseLinkProvider(final EntityManager entityManager) {
        this.entityManager = entityManager;
        this.entityTableMappingByClass = new HashMap<>();
        this.entityTableMappingByTableName = new HashMap<>();
        init();
    }

    public EntityTableMapping get(final Class<?> type) {
        return entityTableMappingByClass.get(type);
    }

    public List<EntityTableMapping> get(final String tableName) {
        return entityTableMappingByTableName.get(tableName.toLowerCase());
    }

    private void init() {

        try {
            final Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
            for (EntityType<?> entity : entities) {
                final EntityTableMapping entityTableMapping = new EntityTableMapping(entity.getJavaType());
                final Object descriptor = invokeMethod(entity, "getDescriptor");
                final String tableName = (String) invokeMethod(descriptor, "getTableName");
                entityTableMapping.setTableName(tableName.toLowerCase());

                addAttributeMapping(entityTableMapping, descriptor);

                putEntityTableMapping((String) invokeMethod(descriptor, "getTableName"), entityTableMapping);
                entityTableMappingByClass.put(entity.getJavaType(), entityTableMapping);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

    }

    private void addAttributeMapping(final EntityTableMapping entityTableMapping, final Object descriptor) {
        final List<Object> mappings = (List<Object>) invokeMethod(descriptor, "getMappings");
        for (Object databaseMapping : mappings) {
            if ((Boolean) invokeMethod(databaseMapping, "isReadOnly")) {
                continue;
            }
            if ((Boolean) invokeMethod(databaseMapping, "isPrimaryKeyMapping")) {
                final Object field = invokeMethod(databaseMapping, "getField");
                entityTableMapping.addColumnIds((String) invokeMethod(field, "getName"));
                entityTableMapping.addAttributeIds((String) invokeMethod(databaseMapping, "getAttributeName"));
            }

            final List<Object> fields = (List<Object>) invokeMethod(databaseMapping, "getFields");
            if (fields.size() > 0) {
                final boolean isForeignReferenceMapping = (Boolean) invokeMethod(databaseMapping, "isForeignReferenceMapping");

                if (isForeignReferenceMapping) {
                    entityTableMapping.addAttributeColumnMapping((String)invokeMethod(databaseMapping, "getAttributeName"), new ColumnNameType((String)invokeMethod(fields.get(0), "getName"), ColumnNameType.Type.MAPPED));
                } else {
                    entityTableMapping.addAttributeColumnMapping((String)invokeMethod(databaseMapping, "getAttributeName"), new ColumnNameType((String)invokeMethod(fields.get(0), "getName"), ColumnNameType.Type.BASIC));
                }
            }
        }
    }

    private void putEntityTableMapping(final String tableName, final EntityTableMapping entityTableMapping) {
        List<EntityTableMapping> entityTableMappings = entityTableMappingByTableName.get(tableName);
        if (entityTableMappings == null) {
            entityTableMappings = new ArrayList<>();
            entityTableMappingByTableName.put(tableName.toLowerCase(), entityTableMappings);
        }
        entityTableMappings.add(entityTableMapping);
    }
}
