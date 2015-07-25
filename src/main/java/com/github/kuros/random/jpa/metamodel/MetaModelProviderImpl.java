package com.github.kuros.random.jpa.metamodel;

import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.metamodel.model.FieldName;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import java.lang.reflect.Field;
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
public class MetaModelProviderImpl implements MetaModelProvider {
    private EntityManager entityManager;
    private AttributeProvider attributeProvider;

    public MetaModelProviderImpl(final EntityManager entityManager) {
        this.entityManager = entityManager;
        attributeProvider = AttributeProvider.getInstance();
    }

    public Map<String, List<FieldName>> getFieldsByTableName() {
        final Set<EntityType<?>> entities = getEntityTypes();

        final Map<String, List<FieldName>> entityMap = new HashMap<String, List<FieldName>>();
        for (EntityType<?> entity : entities) {
            final Class<?> javaType = entity.getJavaType();

            final EntityTableMapping entityTableMapping = attributeProvider.get(javaType);

            entityMap.put(entityTableMapping.getTableName().toLowerCase(), getFields(javaType));
        }

        return entityMap;
    }

    private List<FieldName> getFields(final Class<?> type) {
        final List<FieldName> fields = new ArrayList<FieldName>();
        final EntityTableMapping entityTableMapping = attributeProvider.get(type);

        getFieldsIncludingSuperClass(type, entityTableMapping, fields);

        return fields;
    }

    private void getFieldsIncludingSuperClass(final Class<?> type, final EntityTableMapping entityTableMapping, final List<FieldName> fields) {
        final Field[] declaredFields = type.getDeclaredFields();

        for (Field declaredField : declaredFields) {

            final String columnName = entityTableMapping.getColumnName(declaredField.getName());
            if (columnName != null) {
                fields.add(new FieldName(declaredField, columnName.toLowerCase()));
            }
        }

        // TODO - fix parent relationship
        if (type.getSuperclass() != Object.class) {
            getFieldsIncludingSuperClass(type.getSuperclass(), entityTableMapping, fields);
        }
    }

    private Set<EntityType<?>> getEntityTypes() {
        final EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
        return entityManagerFactory.getMetamodel().getEntities();
    }
}
