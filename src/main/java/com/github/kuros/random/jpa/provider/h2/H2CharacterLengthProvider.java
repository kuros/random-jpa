package com.github.kuros.random.jpa.provider.h2;

import com.github.kuros.random.jpa.annotation.VisibleForTesting;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.provider.base.AbstractCharacterLengthProvider;

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
public final class H2CharacterLengthProvider extends AbstractCharacterLengthProvider {

    private static final String QUERY = """
            SELECT isc.TABLE_NAME as table_name,
                        isc.COLUMN_NAME as column_name,
                        isc.CHARACTER_MAXIMUM_LENGTH as character_maximum_length,
                        isc.NUMERIC_PRECISION as numeric_precision,
                        isc.NUMERIC_SCALE as numeric_scale,
                        isc.DATA_TYPE as data_type
                        FROM INFORMATION_SCHEMA.COLUMNS isc
                        INNER JOIN information_schema.tables ist
                          ON isc.table_name = ist.table_name
                        WHERE isc.table_schema = 'PUBLIC'\
            """;

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
