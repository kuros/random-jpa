package com.github.kuros.random.jpa.provider.oracle;

import com.github.kuros.random.jpa.annotation.VisibleForTesting;
import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.provider.MultiplePrimaryKeyProvider;
import com.github.kuros.random.jpa.provider.base.AbstractMultiplePrimaryKeyProvider;

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
public class OracleMultiplePrimaryKeyProvider extends AbstractMultiplePrimaryKeyProvider {

    private static MultiplePrimaryKeyProvider multiplePrimaryKeyProvider;
    private static final String QUERY = "select ac.TABLE_NAME, acc.COLUMN_NAME" +
            " from ALL_CONSTRAINTS ac, ALL_CONS_COLUMNS acc" +
            " WHERE ac.CONSTRAINT_NAME=acc.CONSTRAINT_NAME" +
            "   and ac.CONSTRAINT_TYPE = 'P'" +
            "   and ac.owner = (select user from dual)";


    private OracleMultiplePrimaryKeyProvider() {
        this(Cache.getInstance().getEntityManager(), AttributeProvider.getInstance());
    }

    @VisibleForTesting
    OracleMultiplePrimaryKeyProvider(final EntityManager entityManager, final AttributeProvider attributeProvider) {
        super(entityManager, attributeProvider);
    }

    public static MultiplePrimaryKeyProvider getInstance() {
        if (multiplePrimaryKeyProvider == null) {
            multiplePrimaryKeyProvider = new OracleMultiplePrimaryKeyProvider();
        }

        return multiplePrimaryKeyProvider;
    }


    @Override
    public String getQuery() {
        return QUERY;
    }
}
