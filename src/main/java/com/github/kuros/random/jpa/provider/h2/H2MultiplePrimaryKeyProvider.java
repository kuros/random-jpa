package com.github.kuros.random.jpa.provider.h2;

import com.github.kuros.random.jpa.annotation.VisibleForTesting;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.provider.MultiplePrimaryKeyProvider;
import com.github.kuros.random.jpa.provider.base.AbstractMultiplePrimaryKeyProvider;

import jakarta.persistence.EntityManager;

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
public class H2MultiplePrimaryKeyProvider extends AbstractMultiplePrimaryKeyProvider {

    private static final String QUERY = """
            select TC.TABLE_NAME AS tab_name, CCU.COLUMN_NAME as col_name
            from INFORMATION_SCHEMA.TABLE_CONSTRAINTS TC
            JOIN INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE CCU ON TC.TABLE_NAME = CCU.TABLE_NAME AND TC.CONSTRAINT_NAME = CCU.CONSTRAINT_NAME
            where TC.CONSTRAINT_TYPE = 'PRIMARY KEY'
            AND TC.TABLE_SCHEMA = 'PUBLIC'\
            """;

    @VisibleForTesting
    H2MultiplePrimaryKeyProvider(final EntityManager entityManager, final AttributeProvider attributeProvider) {
        super(entityManager, attributeProvider);
    }

    public static MultiplePrimaryKeyProvider getInstance(final EntityManager entityManager, final AttributeProvider attributeProvider) {
        return new H2MultiplePrimaryKeyProvider(entityManager, attributeProvider);
    }


    @Override
    public String getQuery() {
        return QUERY;
    }


}
