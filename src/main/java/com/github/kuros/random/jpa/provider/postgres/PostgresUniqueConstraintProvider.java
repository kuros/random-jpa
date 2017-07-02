package com.github.kuros.random.jpa.provider.postgres;

import com.github.kuros.random.jpa.annotation.VisibleForTesting;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.provider.UniqueConstraintProvider;
import com.github.kuros.random.jpa.provider.base.AbstractUniqueConstraintProvider;

import javax.persistence.EntityManager;

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
public class PostgresUniqueConstraintProvider extends AbstractUniqueConstraintProvider {

    private static final String QUERY = "SELECT table_name, column_name\n" +
            "FROM information_schema.constraint_column_usage " +
            "WHERE constraint_schema not in ('pg_catalog', 'information_schema')";

    @VisibleForTesting
    PostgresUniqueConstraintProvider(final EntityManager entityManager, final AttributeProvider attributeProvider) {
        super(attributeProvider, entityManager);
    }

    public static UniqueConstraintProvider getInstance(final EntityManager entityManager, final AttributeProvider attributeProvider) {
        return new PostgresUniqueConstraintProvider(entityManager, attributeProvider);
    }

    @Override
    protected String getQuery() {
        return QUERY;
    }
}
