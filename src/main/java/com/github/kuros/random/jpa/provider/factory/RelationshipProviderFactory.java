package com.github.kuros.random.jpa.provider.factory;

import com.github.kuros.random.jpa.Database;
import com.github.kuros.random.jpa.provider.RelationshipProvider;
import com.github.kuros.random.jpa.provider.model.ForeignKeyRelation;
import com.github.kuros.random.jpa.provider.mssql.MSSQLRelationshipProvider;
import com.github.kuros.random.jpa.provider.mysql.MySqlRelationshipProvider;
import com.github.kuros.random.jpa.provider.oracle.OracleRelationshipProvider;
import com.github.kuros.random.jpa.provider.postgres.PostgresRelationshipProvider;

import javax.persistence.EntityManager;
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
public class RelationshipProviderFactory {

    public static RelationshipProvider getRelationshipProvider(final Database database, final EntityManager entityManager) {
        final RelationshipProvider relationshipProvider;
        switch (database) {
            case MS_SQL_SERVER:
                relationshipProvider = MSSQLRelationshipProvider.newInstance(entityManager);
                break;
            case MY_SQL:
                relationshipProvider = MySqlRelationshipProvider.newInstance(entityManager);
                break;
            case ORACLE:
                relationshipProvider = OracleRelationshipProvider.newInstance(entityManager);
                break;
            case POSTGRES:
                relationshipProvider = PostgresRelationshipProvider.newInstance(entityManager);
                break;
            case NONE:
            default:
                relationshipProvider = new EmptyRelationshipProvider();
        }

        return relationshipProvider;
    }

    static class EmptyRelationshipProvider implements RelationshipProvider {

        public List<ForeignKeyRelation> getForeignKeyRelations() {
            return new ArrayList<ForeignKeyRelation>();
        }
    }
}

