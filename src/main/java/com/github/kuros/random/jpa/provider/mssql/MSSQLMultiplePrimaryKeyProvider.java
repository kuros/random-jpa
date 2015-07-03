package com.github.kuros.random.jpa.provider.mssql;

import com.github.kuros.random.jpa.annotation.VisibleForTesting;
import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.provider.MultiplePrimaryKeyProvider;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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
public class MSSQLMultiplePrimaryKeyProvider implements MultiplePrimaryKeyProvider {

    private Map<Class<?>, List<String>> multiplePrimaryKeyCombinations;
    private AttributeProvider attributeProvider;
    private EntityManager entityManager;
    private static MultiplePrimaryKeyProvider multiplePrimaryKeyProvider;
    private static final String QUERY = "select t.name as TABLE_NAME , c.name as COLUMN_NAME" +
            " from sys.indexes i, sys.tables t, sys.index_columns ic, sys.columns c " +
            " where i.object_id = t.object_id " +
            " and i.type_desc = 'CLUSTERED' " +
            " and i.object_id = ic.object_id " +
            " and i.index_id = ic.index_id " +
            " and i.is_unique = 1 " +
            " and c.object_id = ic.object_id " +
            " and ic.column_id = c.column_id " +
            " order by t.name ";


    private MSSQLMultiplePrimaryKeyProvider() {
        this(Cache.getInstance().getEntityManager(), AttributeProvider.getInstance());
    }

    @VisibleForTesting
    MSSQLMultiplePrimaryKeyProvider(final EntityManager entityManager, final AttributeProvider attributeProvider) {
        this.attributeProvider = attributeProvider;
        this.entityManager = entityManager;
        this.multiplePrimaryKeyCombinations = new HashMap<Class<?>, List<String>>();
        init();
    }

    public static MultiplePrimaryKeyProvider getInstance() {
        if (multiplePrimaryKeyProvider == null) {
            multiplePrimaryKeyProvider = new MSSQLMultiplePrimaryKeyProvider();
        }

        return multiplePrimaryKeyProvider;
    }

    private void init() {
        final Query nativeQuery = entityManager.createNativeQuery(QUERY);
        final List resultList = nativeQuery.getResultList();
        for (Object result : resultList) {
            final Object[] row = (Object[]) result;
            final EntityTableMapping entityTableMapping = attributeProvider.get((String) row[0]);
            if (entityTableMapping != null) {
                final String attributeName = entityTableMapping.getAttributeName((String) row[1]);
                if (attributeName != null) {
                    List<String> attributeList = multiplePrimaryKeyCombinations.get(entityTableMapping.getEntityClass());
                    if (attributeList == null) {
                        attributeList = new ArrayList<String>();
                        multiplePrimaryKeyCombinations.put(entityTableMapping.getEntityClass(), attributeList);
                    }
                    attributeList.add(attributeName);
                }
            }
        }

        filter();
    }

    private void filter() {
        final Set<Map.Entry<Class<?>, List<String>>> entries = multiplePrimaryKeyCombinations.entrySet();

        final List<Class<?>> singleColumnTables = new ArrayList<Class<?>>();

        for (Map.Entry<Class<?>, List<String>> entry : entries) {
            if (entry.getValue().size() <= 1) {
                singleColumnTables.add(entry.getKey());
            }
        }

        for (Class<?> singleColumnTable : singleColumnTables) {
            multiplePrimaryKeyCombinations.remove(singleColumnTable);
        }

    }

    public List<String> getMultiplePrimaryKeyAttributes(final Class<?> entityName) {
        return multiplePrimaryKeyCombinations.get(entityName);
    }
}
