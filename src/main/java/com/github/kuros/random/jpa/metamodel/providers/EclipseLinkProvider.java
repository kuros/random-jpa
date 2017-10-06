package com.github.kuros.random.jpa.metamodel.providers;

import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryDelegate;
import org.eclipse.persistence.internal.jpa.metamodel.EntityTypeImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EclipseLinkProvider implements Provider {

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
            final EntityTypeImpl entityType = (EntityTypeImpl) entity;
            final EntityTableMapping entityTableMapping = new EntityTableMapping(entity.getJavaType());

            final ClassDescriptor descriptor = entityType.getDescriptor();

            entityTableMapping.setTableName(descriptor.getTableName());

            for (DatabaseMapping databaseMapping : descriptor.getMappings()) {
                if (databaseMapping instanceof DirectToFieldMapping) {
                    if (databaseMapping.isReadOnly()) {
                        continue;
                    }
                    if (databaseMapping.isPrimaryKeyMapping()) {
                        entityTableMapping.addColumnIds(databaseMapping.getField().getName());
                        entityTableMapping.addAttributeIds(databaseMapping.getAttributeName());
                    }
                    entityTableMapping.addAttributeColumnMapping(databaseMapping.getAttributeName(), databaseMapping.getField().getName());
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
            entityTableMappingByTableName.put(tableName, entityTableMappings);
        }
        entityTableMappings.add(entityTableMapping);
    }
}
