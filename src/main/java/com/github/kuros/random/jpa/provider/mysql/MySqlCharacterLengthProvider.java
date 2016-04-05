package com.github.kuros.random.jpa.provider.mysql;

import com.github.kuros.random.jpa.annotation.VisibleForTesting;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.provider.base.AbstractCharacterLengthProvider;

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
public final class MySqlCharacterLengthProvider extends AbstractCharacterLengthProvider {

    private static final String QUERY = "select " +
            "c.TABLE_NAME,c.COLUMN_NAME, c.CHARACTER_MAXIMUM_LENGTH, c.NUMERIC_PRECISION, c.NUMERIC_SCALE, c.DATA_TYPE\n" +
            "from information_schema.COLUMNS c\n" +
            "WHERE c.TABLE_SCHEMA = DATABASE()\n" +
            "ORDER BY c.TABLE_NAME;";

    @VisibleForTesting
    MySqlCharacterLengthProvider(final EntityManager entityManager, final AttributeProvider attributeProvider) {
        super(attributeProvider, entityManager);
    }

    public static MySqlCharacterLengthProvider getInstance(final EntityManager entityManager, final AttributeProvider attributeProvider) {
        return new MySqlCharacterLengthProvider(entityManager, attributeProvider);
    }

    @Override
    public String getQuery() {
        return QUERY;
    }

}
