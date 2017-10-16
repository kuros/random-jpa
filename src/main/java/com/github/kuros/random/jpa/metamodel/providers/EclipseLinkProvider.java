package com.github.kuros.random.jpa.metamodel.providers;

import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.metamodel.EntityTypeImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        this.entityTableMappingByClass = new HashMap<Class<?>, EntityTableMapping>();
        this.entityTableMappingByTableName = new HashMap<String, List<EntityTableMapping>>();
        init();
    }

    public EntityTableMapping get(final Class<?> type) {
        return entityTableMappingByClass.get(type);
    }

    public List<EntityTableMapping> get(final String tableName) {
        return entityTableMappingByTableName.get(tableName.toLowerCase());
    }

    private void init() {
        final Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();

        for (EntityType<?> entity : entities) {
            final EntityTableMapping entityTableMapping = new EntityTableMapping(entity.getJavaType());

            final EntityTypeImpl entityType = (EntityTypeImpl) entity;
            final ClassDescriptor descriptor = entityType.getDescriptor();

            entityTableMapping.setTableName(descriptor.getTableName().toLowerCase());

            for (DatabaseMapping databaseMapping : descriptor.getMappings()) {
                if (databaseMapping.isReadOnly()) {
                    continue;
                }
                if (databaseMapping.isPrimaryKeyMapping()) {
                    entityTableMapping.addColumnIds(databaseMapping.getField().getName());
                    entityTableMapping.addAttributeIds(databaseMapping.getAttributeName());
                }

                if (databaseMapping.getFields().size() > 0) {
                    entityTableMapping.addAttributeColumnMapping(databaseMapping.getAttributeName(), databaseMapping.getFields().get(0).getName());
                }
            }

            putEntityTableMapping(descriptor.getTableName(), entityTableMapping);
            entityTableMappingByClass.put(entity.getJavaType(), entityTableMapping);
        }
    }

    private void putEntityTableMapping(final String tableName, final EntityTableMapping entityTableMapping) {
        List<EntityTableMapping> entityTableMappings = entityTableMappingByTableName.get(tableName);
        if (entityTableMappings == null) {
            entityTableMappings = new ArrayList<EntityTableMapping>();
            entityTableMappingByTableName.put(tableName.toLowerCase(), entityTableMappings);
        }
        entityTableMappings.add(entityTableMapping);
    }
}
