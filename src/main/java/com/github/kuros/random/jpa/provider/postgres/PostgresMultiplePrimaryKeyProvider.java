package com.github.kuros.random.jpa.provider.postgres;

import com.github.kuros.random.jpa.annotation.VisibleForTesting;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.provider.MultiplePrimaryKeyProvider;
import com.github.kuros.random.jpa.provider.base.AbstractMultiplePrimaryKeyProvider;

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
public class PostgresMultiplePrimaryKeyProvider extends AbstractMultiplePrimaryKeyProvider {

    private static final String QUERY = "SELECT t.table_name,\n" +
            "  kcu.column_name\n" +
            "FROM    INFORMATION_SCHEMA.TABLES t\n" +
            "  LEFT JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc\n" +
            "    ON tc.table_catalog = t.table_catalog\n" +
            "       AND tc.table_schema = t.table_schema\n" +
            "       AND tc.table_name = t.table_name\n" +
            "       AND tc.constraint_type = 'PRIMARY KEY'\n" +
            "  LEFT JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu\n" +
            "    ON kcu.table_catalog = tc.table_catalog\n" +
            "       AND kcu.table_schema = tc.table_schema\n" +
            "       AND kcu.table_name = tc.table_name\n" +
            "       AND kcu.constraint_name = tc.constraint_name\n" +
            "WHERE   t.table_schema NOT IN ('pg_catalog', 'information_schema')\n" +
            "ORDER BY t.table_catalog,\n" +
            "  t.table_schema,\n" +
            "  t.table_name,\n" +
            "  kcu.constraint_name,\n" +
            "  kcu.ordinal_position";

    @VisibleForTesting
    PostgresMultiplePrimaryKeyProvider(final EntityManager entityManager, final AttributeProvider attributeProvider) {
        super(entityManager, attributeProvider);
    }

    @Override
    public String getQuery() {
        return QUERY;
    }

    public static MultiplePrimaryKeyProvider getInstance(final EntityManager entityManager, final AttributeProvider attributeProvider) {
        return new PostgresMultiplePrimaryKeyProvider(entityManager, attributeProvider);
    }
}
