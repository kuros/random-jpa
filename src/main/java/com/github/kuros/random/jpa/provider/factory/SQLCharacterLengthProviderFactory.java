package com.github.kuros.random.jpa.provider.factory;

import com.github.kuros.random.jpa.Database;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.provider.SQLCharacterLengthProvider;
import com.github.kuros.random.jpa.provider.mssql.MSSQLCharacterLengthProvider;
import com.github.kuros.random.jpa.provider.mysql.MySqlCharacterLengthProvider;
import com.github.kuros.random.jpa.provider.oracle.OracleCharacterLengthProvider;
import com.github.kuros.random.jpa.provider.postgres.PostgresCharacterLengthProvider;

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
public class SQLCharacterLengthProviderFactory {

    public static SQLCharacterLengthProvider getSqlCharacterLengthProvider(final Database database, final EntityManager entityManager, final AttributeProvider attributeProvider) {
        final SQLCharacterLengthProvider sqlCharacterLengthProvider;
        switch (database) {
            case MS_SQL_SERVER:
                sqlCharacterLengthProvider = MSSQLCharacterLengthProvider.getInstance(entityManager, attributeProvider);
                break;
            case MY_SQL:
                sqlCharacterLengthProvider = MySqlCharacterLengthProvider.getInstance(entityManager, attributeProvider);
                break;
            case ORACLE:
                sqlCharacterLengthProvider = OracleCharacterLengthProvider.getInstance(entityManager, attributeProvider);
                break;
            case POSTGRES:
                sqlCharacterLengthProvider = PostgresCharacterLengthProvider.getInstance(entityManager, attributeProvider);
                break;
            case NONE:
            default:
                sqlCharacterLengthProvider = new DefaultSQLCharacterLengthProvider();
                break;
        }

        return sqlCharacterLengthProvider;
    }



    static class DefaultSQLCharacterLengthProvider implements SQLCharacterLengthProvider {
        public Integer getMaxLength(final String entityName, final String attributeName) {
            return 10;
        }

        public Object applyLengthConstraint(final String entityName, final String attributeName, final Object value) {
            return value;
        }
    }
}
