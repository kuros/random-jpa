package com.github.kuros.random.jpa.metamodel;

import com.github.kuros.random.jpa.util.AttributeHelper;
import org.hibernate.id.IdentityGenerator;
import org.hibernate.id.SelectGenerator;
import org.hibernate.id.SequenceIdentityGenerator;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.SingleTableEntityPersister;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kumar Rohit on 5/30/15.
 */
public final class AttributeProvider {
    private Map<Class<?>, EntityTableMapping> entityTableMappingByClass;
    private Map<String, EntityTableMapping> entityTableMappingByTableName;
    private static AttributeProvider attributeProvider;

    public static AttributeProvider getInstance(final EntityManager entityManager) {
        if (attributeProvider == null) {
            attributeProvider = new AttributeProvider(entityManager);
        }

        return attributeProvider;
    }

    private AttributeProvider(final EntityManager entityManager) {
        init(entityManager);
    }

    private void init(final EntityManager entityManager) {
        entityTableMappingByClass = new HashMap<Class<?>, EntityTableMapping>();
        entityTableMappingByTableName = new HashMap<String, EntityTableMapping>();
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

                for (Attribute attribute : entity.getAttributes()) {
                    final String name = AttributeHelper.getName(attribute);
                    final String[] propertyColumnNames = singleTableEntityPersister.getPropertyColumnNames(name);
                    final String columnName = propertyColumnNames.length > 0 ? propertyColumnNames[0] : null;
                    if (columnName != null && attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC) {
                        entityTableMapping.addAttributeColumnMapping(name, columnName);
                    }
                }

                for (String id : entityTableMapping.getColumnIds()) {
                    entityTableMapping.addAttributeIds(entityTableMapping.getAttributeName(id));
                }

                entityTableMappingByTableName.put(getTableName(singleTableEntityPersister), entityTableMapping);
                entityTableMappingByClass.put(entity.getJavaType(), entityTableMapping);
            }

        }
    }

    private String getTableName(final SingleTableEntityPersister singleTableEntityPersister) {

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

    public EntityTableMapping get(final String tableName) {
        return entityTableMappingByTableName.get(tableName.toLowerCase());
    }

    public Set<Class<?>> getSupportedGeneratorType() {
        final Set<Class<?>> generators = new HashSet<Class<?>>();
        generators.add(IdentityGenerator.class);
        generators.add(SelectGenerator.class);
        generators.add(SequenceIdentityGenerator.class);

        return generators;
    }
}
