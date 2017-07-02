package com.github.kuros.random.jpa.provider.factory;

import com.github.kuros.random.jpa.Database;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.provider.UniqueConstraintProvider;
import com.github.kuros.random.jpa.provider.mssql.MSSQLUniqueConstraintProvider;
import com.github.kuros.random.jpa.provider.mysql.MySqlUniqueConstraintProvider;
import com.github.kuros.random.jpa.provider.oracle.OracleUniqueConstraintProvider;
import com.github.kuros.random.jpa.provider.postgres.PostgresUniqueConstraintProvider;

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
public class UniqueConstraintProviderFactory {

    public static UniqueConstraintProvider getUniqueConstraintProvider(final Database database, final EntityManager entityManager, final AttributeProvider attributeProvider) {
        final UniqueConstraintProvider uniqueConstraintProvider;
        switch (database) {
            case MS_SQL_SERVER:
                uniqueConstraintProvider = MSSQLUniqueConstraintProvider.getInstance(entityManager, attributeProvider);
                break;
            case MY_SQL:
                uniqueConstraintProvider = MySqlUniqueConstraintProvider.getInstance(entityManager, attributeProvider);
                break;
            case ORACLE:
                uniqueConstraintProvider = OracleUniqueConstraintProvider.getInstance(entityManager, attributeProvider);
                break;
            case POSTGRES:
                uniqueConstraintProvider = PostgresUniqueConstraintProvider.getInstance(entityManager, attributeProvider);
                break;
            case NONE:
            default:
                uniqueConstraintProvider = new DefaultUniqueConstraintProvider();
        }

        return uniqueConstraintProvider;
    }



    static class DefaultUniqueConstraintProvider implements UniqueConstraintProvider {
        public List<String> getUniqueCombinationAttributes(final Class<?> entityName) {
            return new ArrayList<String>();
        }
    }
}
