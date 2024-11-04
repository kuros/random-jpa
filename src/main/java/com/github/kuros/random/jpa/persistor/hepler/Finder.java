package com.github.kuros.random.jpa.persistor.hepler;

import com.github.kuros.random.jpa.Database;
import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.exception.EmptyFieldException;
import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.exception.ResultNotFoundException;
import com.github.kuros.random.jpa.log.LogFactory;
import com.github.kuros.random.jpa.log.Logger;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.util.NumberUtil;
import com.github.kuros.random.jpa.util.StringJoiner;
import com.github.kuros.random.jpa.util.Util;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
public class Finder {

    private static final Logger LOGGER = LogFactory.getLogger(Finder.class);
    public static final String SELECT_FROM = "SELECT * FROM ";
    private final EntityManager entityManager;
    private final AttributeProvider attributeProvider;
    private final Database database;

    public Finder(final Cache cache) {
        this.entityManager = cache.getEntityManager();
        this.attributeProvider = cache.getAttributeProvider();
        database = cache.getDatabase();
    }

    @SuppressWarnings("unchecked")
    public <T> T findByAttributes(final T typeObject, final List<String> attributes) {

        if (isEmpty(typeObject, attributes)) {
            throw new ResultNotFoundException("object/attributes are empty");
        }

        final Class<?> tableClass = typeObject.getClass();
        final EntityTableMapping entityTableMapping = attributeProvider.get(tableClass);

        final String tableName = entityTableMapping.getTableName();

        final String query = SELECT_FROM + tableName + " " + getNoLockCondition() + getWhereClause(entityTableMapping, attributes);

        final Query nativeQuery = entityManager.createNativeQuery(query, tableClass);

        populateParameters(nativeQuery, typeObject, attributes);

        try {
            final Object singleResult = nativeQuery.getSingleResult();
            return (T) singleResult;
        } catch (final NoResultException e) {
            throw new ResultNotFoundException(e);
        }
    }

    public <T> T findById(final Class<T> type, final Object id) {
        try {
            final var declaredFields = type.getDeclaredFields();
            final var field = Arrays.stream(declaredFields).filter(declaredField -> declaredField.getAnnotation(Id.class) != null)
                    .findFirst().orElseThrow(() -> new RandomJPAException("Unable to find @Id field, class: " + type));

            final var declaredConstructor = type.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            final var instance = declaredConstructor.newInstance();
            Util.setFieldValue(field, instance, id);

            final List<String> names = new ArrayList<>();
            names.add(field.getName());

            return findByAttributes(instance, names);
        } catch (final InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOGGER.error("Unable to create instance for entity: {} ", type);
            throw new RandomJPAException("Unable to create instance for entity: " + type, e);
        }
    }

    private <T> void populateParameters(final Query nativeQuery, final T typeObject, final List<String> attributes) {
        for (int i = 0; i < attributes.size(); i++) {
            final String attribute = attributes.get(i);
            try {
                final Field declaredField = Util.getField(typeObject.getClass(), attribute);
                declaredField.setAccessible(true);
                final Object fieldValue = declaredField.get(typeObject);
                if (fieldValue == null) {
                    throw new EmptyFieldException();
                }

                nativeQuery.setParameter(i + 1 , NumberUtil.castNumber(declaredField.getType(), fieldValue));
            } catch (final EmptyFieldException e) {
                throw new ResultNotFoundException(e);
            } catch (final Exception e) {
                throw new RandomJPAException(e);
            }
        }
    }

    private String getWhereClause(final EntityTableMapping entityTableMapping, final List<String> attributes) {
        final StringJoiner andJoiner = new StringJoiner(" WHERE ", " and ");
        final StringBuilder builder = new StringBuilder();
        for (final String attribute : attributes) {
            final String columnName = entityTableMapping.getColumnName(attribute);
            builder.append(andJoiner.next()).append(columnName).append("= ? ");
        }
        return builder.toString();
    }

    private String getNoLockCondition() {
        return switch (database) {
            case MS_SQL_SERVER -> "WITH(NOLOCK)";
            default -> "";
        };
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> findByAttributes(final Class<T> type, final Map<String, Object> attributeValues) {

        if (isEmpty(type, attributeValues)) {
            return new ArrayList<>();
        }

        LOGGER.debug("Loading entity attributes from {}, values: {}, {} ", type, attributeValues.keySet(), attributeValues.values());

        final EntityTableMapping entityTableMapping = attributeProvider.get(type);

        final List<String> attributeValueList = new ArrayList<>(attributeValues.keySet());
        final String query = SELECT_FROM + entityTableMapping.getTableName() + " " + getNoLockCondition() + getWhereClause(entityTableMapping, attributeValueList);

        final Query nativeQuery = entityManager.createNativeQuery(query, type);
        for (int i = 0; i < attributeValueList.size(); i++) {
            final String attribute = attributeValueList.get(i);
            nativeQuery.setParameter(i + 1, getAttributeValue(attributeValues, attribute));
        }
        return nativeQuery.getResultList();
    }

    private Object getAttributeValue(final Map<String, Object> attributeValues, final String attribute) {
        final var value = attributeValues.get(attribute);
        final var entityTableMapping = Optional.ofNullable(value).map(e -> attributeProvider.get(e.getClass()));
        if (entityTableMapping.isEmpty()) {
            return value;
        }

        final var field = Util.getField(entityTableMapping.get().getEntityClass(), attribute);
        final Class<?> fieldType = field.getType();
        final Object object = NumberUtil.castNumber(fieldType, Util.getFieldValue(value, attribute));
        LOGGER.debug("FieldType: {}, idType: {}", field, object.getClass());
        return object;
    }

    private <T> boolean isEmpty(final T typeObject, final List<String> attributes) {
        return typeObject == null || attributes == null || attributes.isEmpty();
    }

    private <T> boolean isEmpty(final Class<T> type, final Map<String, Object> attributeValues) {
        return type == null || attributeValues == null || attributeValues.isEmpty();
    }
}
