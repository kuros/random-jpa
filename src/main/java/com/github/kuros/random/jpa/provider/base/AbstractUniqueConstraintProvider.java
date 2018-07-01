package com.github.kuros.random.jpa.provider.base;

import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.provider.UniqueConstraintProvider;

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
public abstract class AbstractUniqueConstraintProvider implements UniqueConstraintProvider {
    protected Map<Class<?>, List<String>> uniqueColumnCombinations;
    protected AttributeProvider attributeProvider;
    protected EntityManager entityManager;

    public AbstractUniqueConstraintProvider(final AttributeProvider attributeProvider, final EntityManager entityManager) {
        this.uniqueColumnCombinations = new HashMap<>();
        this.attributeProvider = attributeProvider;
        this.entityManager = entityManager;
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
                    addAttribute(entityTableMapping, (String) row[1]);
                }
            }
        }

        filter();
    }

    protected void filter() {
        final Set<Map.Entry<Class<?>, List<String>>> entries = uniqueColumnCombinations.entrySet();

        final List<Class<?>> singleColumnTables = new ArrayList<>();

        for (Map.Entry<Class<?>, List<String>> entry : entries) {
            if (entry.getValue().size() <= 1) {
                singleColumnTables.add(entry.getKey());
            }
        }

        for (Class<?> singleColumnTable : singleColumnTables) {
            uniqueColumnCombinations.remove(singleColumnTable);
        }

    }

    protected void addAttribute(final EntityTableMapping entityTableMapping, final String columnName) {
        final String attributeName = entityTableMapping.getAttributeName(columnName);
        if (attributeName != null) {
            List<String> attributeList = uniqueColumnCombinations.computeIfAbsent(entityTableMapping.getEntityClass(), k -> new ArrayList<>());
            attributeList.add(attributeName);
        }
    }

    public List<String> getUniqueCombinationAttributes(final Class<?> entityName) {
        return uniqueColumnCombinations.get(entityName);
    }

    protected abstract String getQuery();
}
