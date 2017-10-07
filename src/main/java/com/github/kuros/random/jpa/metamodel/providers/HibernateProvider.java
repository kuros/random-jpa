package com.github.kuros.random.jpa.metamodel.providers;

import com.github.kuros.random.jpa.annotation.VisibleForTesting;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.util.AttributeHelper;
import org.hibernate.id.Assigned;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
import org.hibernate.tuple.IdentifierProperty;
import org.hibernate.tuple.NonIdentifierAttribute;
import org.hibernate.tuple.entity.EntityBasedAssociationAttribute;
import org.hibernate.tuple.entity.EntityMetamodel;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
public class HibernateProvider implements Provider {

    private Map<Class<?>, EntityTableMapping> entityTableMappingByClass;
    private Map<String, List<EntityTableMapping>> entityTableMappingByTableName;
    private EntityManager entityManager;

    public HibernateProvider(final EntityManager entityManager) {
        this.entityManager = entityManager;
        this.entityTableMappingByClass = new HashMap<Class<?>, EntityTableMapping>();
        this.entityTableMappingByTableName = new HashMap<String, List<EntityTableMapping>>();
        init();
    }

    private void init() {
        final HibernateEntityManagerFactory entityManagerFactory = (HibernateEntityManagerFactory) entityManager.getEntityManagerFactory();
        final Map<String, ClassMetadata> allClassMetadata = entityManagerFactory.getSessionFactory().getAllClassMetadata();

        final Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
        for (EntityType<?> entity : entities) {
            final ClassMetadata classMetadata = allClassMetadata.get(entity.getJavaType().getName());
            if (classMetadata instanceof SingleTableEntityPersister) {
                final EntityTableMapping entityTableMapping = new EntityTableMapping(entity.getJavaType());
                final SingleTableEntityPersister singleTableEntityPersister = (SingleTableEntityPersister) classMetadata;

                entityTableMapping.addColumnIds(singleTableEntityPersister.getKeyColumnNames());
                entityTableMapping.setTableName(getTableName(singleTableEntityPersister));
                entityTableMapping.setIdentifierGenerator(singleTableEntityPersister.getIdentifierGenerator().getClass());

                final List<String> attributeNames = getSupportedAttributeNames(singleTableEntityPersister);

                for (Attribute attribute : entity.getAttributes()) {
                    final String name = AttributeHelper.getName(attribute);
                    final String[] propertyColumnNames = singleTableEntityPersister.getPropertyColumnNames(name);
                    final String columnName = propertyColumnNames.length > 0 ? propertyColumnNames[0] : null;
                    if (columnName != null && attributeNames.contains(name)) {
                        entityTableMapping.addAttributeColumnMapping(name, columnName);
                    }
                }

                for (String id : entityTableMapping.getColumnIds()) {
                    entityTableMapping.addAttributeIds(entityTableMapping.getAttributeName(id));
                }

                putEntityTableMapping(singleTableEntityPersister, entityTableMapping);
                entityTableMappingByClass.put(entity.getJavaType(), entityTableMapping);
            }

        }
    }

    private void putEntityTableMapping(final SingleTableEntityPersister singleTableEntityPersister, final EntityTableMapping entityTableMapping) {
        final String tableName = getTableName(singleTableEntityPersister);
        List<EntityTableMapping> entityTableMappings = entityTableMappingByTableName.get(tableName);
        if (entityTableMappings == null) {
            entityTableMappings = new ArrayList<EntityTableMapping>();
            entityTableMappingByTableName.put(tableName, entityTableMappings);
        }
        entityTableMappings.add(entityTableMapping);
    }

    private List<String> getSupportedAttributeNames(final SingleTableEntityPersister singleTableEntityPersister) {
        final EntityMetamodel entityMetamodel = singleTableEntityPersister.getEntityMetamodel();
        final NonIdentifierAttribute[] properties = entityMetamodel.getProperties();
        final List<String> attributeNames = new ArrayList<String>();
        for (NonIdentifierAttribute property : properties) {
            if (property.isInsertable()) {
                if (property instanceof EntityBasedAssociationAttribute) {
                    final EntityBasedAssociationAttribute entityBasedAssociationAttribute = (EntityBasedAssociationAttribute)property;
                    if (!(entityBasedAssociationAttribute.getAssociationNature() == AssociationAttributeDefinition.AssociationNature.ENTITY)) {
                        continue;
                    }
                }

                attributeNames.add(property.getName());
            }
        }

        final IdentifierProperty identifierProperty = entityMetamodel.getIdentifierProperty();
        attributeNames.add(identifierProperty.getName());
        return attributeNames;
    }

    @VisibleForTesting
    String getTableName(final SingleTableEntityPersister singleTableEntityPersister) {

        String tableName = singleTableEntityPersister.getTableName();
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

    @VisibleForTesting
    Set<Class<?>> getUnSupportedGeneratorType() {
        final Set<Class<?>> generators = new HashSet<Class<?>>();
        generators.add(Assigned.class);
        return generators;
    }

}
