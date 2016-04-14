package com.github.kuros.random.jpa.provider.base;

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
public abstract class AbstractMultiplePrimaryKeyProvider implements MultiplePrimaryKeyProvider {
    private Map<Class<?>, List<String>> multiplePrimaryKeyCombinations;
    private AttributeProvider attributeProvider;
    private EntityManager entityManager;

    public AbstractMultiplePrimaryKeyProvider(final EntityManager entityManager, final AttributeProvider attributeProvider) {
        this.multiplePrimaryKeyCombinations = new HashMap<Class<?>, List<String>>();
        this.entityManager = entityManager;
        this.attributeProvider = attributeProvider;
        init();
    }

    protected void init() {
        final Query nativeQuery = entityManager.createNativeQuery(getQuery());
        final List resultList = nativeQuery.getResultList();
        for (Object result : resultList) {
            final Object[] row = (Object[]) result;
            final List<EntityTableMapping> entityTableMappings = attributeProvider.get((String) row[0]);
            if (entityTableMappings != null) {
                for (EntityTableMapping entityTableMapping : entityTableMappings) {
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

    protected abstract String getQuery();
}
