package com.github.kuros.random.jpa.persistor.hepler;

import com.github.kuros.random.jpa.Database;
import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.exception.EmptyFieldException;
import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.exception.ResultNotFoundException;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.util.StringJoiner;
import com.github.kuros.random.jpa.util.Util;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.util.ArrayList;
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
public class Finder {

    public static final String SELECT_FROM = "SELECT * FROM ";
    private EntityManager entityManager;
    private AttributeProvider attributeProvider;
    private Database database;

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

    private <T> void populateParameters(final Query nativeQuery, final T typeObject, final List<String> attributes) {
        for (String attribute : attributes) {
            try {
                final Field declaredField = Util.getField(typeObject.getClass(), attribute);
                declaredField.setAccessible(true);
                final Object fieldValue = declaredField.get(typeObject);
                if (fieldValue == null) {
                    throw new EmptyFieldException();
                }

                nativeQuery.setParameter(attribute, fieldValue);
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
            builder.append(andJoiner.next()).append(columnName).append("=:").append(attribute);
        }
        return builder.toString();
    }

    private String getNoLockCondition() {
        final String val;
        switch (database) {
            case MS_SQL_SERVER:
                val = "WITH(NOLOCK)";
                break;
            case MY_SQL:
            case ORACLE:
            default:
                val = "";
        }
        return val;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> findByAttributes(final Class<T> type, final Map<String, Object> attributeValues) {

        if (isEmpty(type, attributeValues)) {
            return new ArrayList<T>();
        }

        final EntityTableMapping entityTableMapping = attributeProvider.get(type);

        final String query = SELECT_FROM + entityTableMapping.getTableName() + " " + getNoLockCondition() + getWhereClause(entityTableMapping, attributeValues.keySet());

        final Query nativeQuery = entityManager.createNativeQuery(query, type);
        for (String attribute : attributeValues.keySet()) {
            nativeQuery.setParameter(attribute, attributeValues.get(attribute));
        }
        return nativeQuery.getResultList();
    }

    private String getWhereClause(final EntityTableMapping entityTableMapping, final Set<String> attributeValues) {
        final StringJoiner andJoiner = new StringJoiner(" WHERE ", " and ");
        final StringBuilder builder = new StringBuilder();

        for (String attribute : attributeValues) {
            final String columnName = entityTableMapping.getColumnName(attribute);
            builder
                    .append(andJoiner.next())
                    .append(columnName)
                    .append("=:")
                    .append(attribute);

        }
        return builder.toString();
    }

    private <T> boolean isEmpty(final T typeObject, final List<String> attributes) {
        return typeObject == null || attributes == null || attributes.isEmpty();
    }

    private <T> boolean isEmpty(final Class<T> type, final Map<String, Object> attributeValues) {
        return type == null || attributeValues == null || attributeValues.isEmpty();
    }
}
