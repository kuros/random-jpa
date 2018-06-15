package com.github.kuros.random.jpa.provider.h2;

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
public final class H2CharacterLengthProvider extends AbstractCharacterLengthProvider {

    private static final String QUERY = "SELECT isc.TABLE_NAME as table_name,\n" +
            "            isc.COLUMN_NAME as column_name,\n" +
            "            isc.CHARACTER_MAXIMUM_LENGTH as character_maximum_length,\n" +
            "            isc.NUMERIC_PRECISION as numeric_precision,\n" +
            "            isc.NUMERIC_SCALE as numeric_scale,\n" +
            "            isc.TYPE_NAME as data_type\n" +
            "            FROM INFORMATION_SCHEMA.COLUMNS isc\n" +
            "            INNER JOIN information_schema.tables ist\n" +
            "              ON isc.table_name = ist.table_name\n" +
            "            WHERE isc.table_schema = 'PUBLIC'";

    @VisibleForTesting
    H2CharacterLengthProvider(final EntityManager entityManager, final AttributeProvider attributeProvider) {
        super(attributeProvider, entityManager);
    }

    public static H2CharacterLengthProvider getInstance(final EntityManager entityManager, final AttributeProvider attributeProvider) {
        return new H2CharacterLengthProvider(entityManager,
                attributeProvider);
    }

    @Override
    public String getQuery() {
        return QUERY;
    }

}
