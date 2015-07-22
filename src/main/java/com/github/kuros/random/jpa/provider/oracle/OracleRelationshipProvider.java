package com.github.kuros.random.jpa.provider.oracle;

import com.github.kuros.random.jpa.annotation.VisibleForTesting;
import com.github.kuros.random.jpa.provider.RelationshipProvider;
import com.github.kuros.random.jpa.provider.model.ForeignKeyRelation;

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
public final class OracleRelationshipProvider implements RelationshipProvider {

    private static final String QUERY = "SELECT\n" +
            "      c_src.TABLE_NAME as parent_table,\n" +
            "       c_src.COLUMN_NAME as parent_attribute,\n" +
            "       c_dest.TABLE_NAME as referenced_table,\n" +
            "       c_dest.COLUMN_NAME as referenced_attribute\n" +
            "FROM ALL_CONSTRAINTS c_list, ALL_CONS_COLUMNS c_src, ALL_CONS_COLUMNS c_dest\n" +
            "WHERE c_list.CONSTRAINT_NAME = c_src.CONSTRAINT_NAME\n" +
            "      AND c_list.R_CONSTRAINT_NAME = c_dest.CONSTRAINT_NAME\n" +
            "      AND c_list.CONSTRAINT_TYPE = 'R'\n" +
            "      And c_list.owner = (select user from dual)\n" +
            "order by c_src.TABLE_NAME, c_src.COLUMN_NAME;";

    private EntityManager entityManager;

    @VisibleForTesting
    OracleRelationshipProvider(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public static OracleRelationshipProvider newInstance(final EntityManager entityManager) {
        return new OracleRelationshipProvider(entityManager);
    }

    public List<ForeignKeyRelation> getForeignKeyRelations() {
        final List<ForeignKeyRelation> foreignKeyRelations = new ArrayList<ForeignKeyRelation>();

        final Query query = entityManager.createNativeQuery(QUERY);
        final List resultList = query.getResultList();
        for (Object o : resultList) {
            final Object[] row = (Object[]) o;

            final ForeignKeyRelation relation = ForeignKeyRelation.newInstance((String)row[0], (String)row[1], (String) row[2], (String)row[3]);
            foreignKeyRelations.add(relation);
        }

        return foreignKeyRelations;
    }

}
