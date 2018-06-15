package com.github.kuros.random.jpa.provider.factory;

import com.github.kuros.random.jpa.Database;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.provider.MultiplePrimaryKeyProvider;
import com.github.kuros.random.jpa.provider.h2.H2MultiplePrimaryKeyProvider;
import com.github.kuros.random.jpa.provider.mssql.MSSQLMultiplePrimaryKeyProvider;
import com.github.kuros.random.jpa.provider.mysql.MySqlMultiplePrimaryKeyProvider;
import com.github.kuros.random.jpa.provider.oracle.OracleMultiplePrimaryKeyProvider;
import com.github.kuros.random.jpa.provider.postgres.PostgresMultiplePrimaryKeyProvider;

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
public class MultiplePrimaryKeyProviderFactory {

    public static MultiplePrimaryKeyProvider getMultiplePrimaryKeyProvider(final Database database, final EntityManager entityManager, final AttributeProvider attributeProvider) {
        final MultiplePrimaryKeyProvider multiplePrimaryKeyProvider;
        switch (database) {
            case MS_SQL_SERVER:
                multiplePrimaryKeyProvider = MSSQLMultiplePrimaryKeyProvider.getInstance(entityManager, attributeProvider);
                break;
            case MY_SQL:
                multiplePrimaryKeyProvider = MySqlMultiplePrimaryKeyProvider.getInstance(entityManager, attributeProvider);
                break;
            case ORACLE:
                multiplePrimaryKeyProvider = OracleMultiplePrimaryKeyProvider.getInstance(entityManager, attributeProvider);
                break;
            case POSTGRES:
                multiplePrimaryKeyProvider = PostgresMultiplePrimaryKeyProvider.getInstance(entityManager, attributeProvider);
                break;
            case H2:
                multiplePrimaryKeyProvider = H2MultiplePrimaryKeyProvider.getInstance(entityManager, attributeProvider);
                break;
            case NONE:
            default:
                multiplePrimaryKeyProvider = new DefaultMultiplePrimaryKeyProvider();
        }

        return multiplePrimaryKeyProvider;
    }

    static class DefaultMultiplePrimaryKeyProvider implements MultiplePrimaryKeyProvider {

        public List<String> getMultiplePrimaryKeyAttributes(final Class<?> entityName) {
            return new ArrayList<String>();
        }
    }
}
