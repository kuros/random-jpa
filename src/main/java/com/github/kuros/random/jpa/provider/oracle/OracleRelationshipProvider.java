package com.github.kuros.random.jpa.provider.oracle;

import com.github.kuros.random.jpa.annotation.VisibleForTesting;
import com.github.kuros.random.jpa.provider.base.AbstractRelationshipProvider;

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
public final class OracleRelationshipProvider extends AbstractRelationshipProvider {

    private static final String QUERY = """
            SELECT
                  c_src.TABLE_NAME as parent_table,
                   c_src.COLUMN_NAME as parent_attribute,
                   c_dest.TABLE_NAME as referenced_table,
                   c_dest.COLUMN_NAME as referenced_attribute
            FROM ALL_CONSTRAINTS c_list, ALL_CONS_COLUMNS c_src, ALL_CONS_COLUMNS c_dest
            WHERE c_list.CONSTRAINT_NAME = c_src.CONSTRAINT_NAME
                  AND c_list.R_CONSTRAINT_NAME = c_dest.CONSTRAINT_NAME
                  AND c_list.CONSTRAINT_TYPE = 'R'
                  And c_list.owner = (select user from dual)
            order by c_src.TABLE_NAME, c_src.COLUMN_NAME\
            """;


    @VisibleForTesting
    OracleRelationshipProvider(final EntityManager entityManager) {
        super(entityManager);
    }

    public static OracleRelationshipProvider newInstance(final EntityManager entityManager) {
        return new OracleRelationshipProvider(entityManager);
    }

    @Override
    protected String getQuery() {
        return QUERY;
    }

}
