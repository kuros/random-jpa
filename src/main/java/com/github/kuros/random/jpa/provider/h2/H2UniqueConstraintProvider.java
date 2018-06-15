package com.github.kuros.random.jpa.provider.h2;

import com.github.kuros.random.jpa.annotation.VisibleForTesting;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.provider.UniqueConstraintProvider;
import com.github.kuros.random.jpa.provider.base.AbstractUniqueConstraintProvider;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

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
public class H2UniqueConstraintProvider extends AbstractUniqueConstraintProvider {

    private static final String QUERY = "select c.TABLE_NAME, c.COLUMN_LIST\n" +
            "  from INFORMATION_SCHEMA.CONSTRAINTS c\n" +
            "  where c.CONSTRAINT_TYPE = 'UNIQUE'";

    @VisibleForTesting
    H2UniqueConstraintProvider(final EntityManager entityManager, final AttributeProvider attributeProvider) {
        super(attributeProvider, entityManager);
    }

    public static UniqueConstraintProvider getInstance(final EntityManager entityManager, final AttributeProvider attributeProvider) {
        return new H2UniqueConstraintProvider(entityManager, attributeProvider);
    }

    @Override
    protected String getQuery() {
        return QUERY;
    }

    @Override
    protected void init() {
        final Query nativeQuery = entityManager.createNativeQuery(getQuery());
        final List resultList = nativeQuery.getResultList();
        for (Object result : resultList) {
            final Object[] row = (Object[]) result;
            final List<EntityTableMapping> entityTableMappings = attributeProvider.get((String) row[0]);
            if (entityTableMappings != null) {
                for (EntityTableMapping entityTableMapping : entityTableMappings) {
                    for (String columnName : ((String) row[1]).split(",")) {
                        addAttribute(entityTableMapping, columnName);
                    }
                }
            }
        }

        filter();
    }
}
