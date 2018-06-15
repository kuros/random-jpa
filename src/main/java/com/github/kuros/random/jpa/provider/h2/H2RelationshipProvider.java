package com.github.kuros.random.jpa.provider.h2;

import com.github.kuros.random.jpa.annotation.VisibleForTesting;
import com.github.kuros.random.jpa.provider.base.AbstractRelationshipProvider;

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
public final class H2RelationshipProvider extends AbstractRelationshipProvider {

    private static final String QUERY = "select DISTINCT cf.FKTABLE_NAME, cf.FKCOLUMN_NAME, cf.PKTABLE_NAME, cf.PKCOLUMN_NAME\n" +
            "from information_schema.CROSS_REFERENCES cf\n" +
            "where cf.FKTABLE_SCHEMA = 'PUBLIC'";


    @VisibleForTesting
    H2RelationshipProvider(final EntityManager entityManager) {
        super(entityManager);
    }

    public static H2RelationshipProvider newInstance(final EntityManager entityManager) {
        return new H2RelationshipProvider(entityManager);
    }

    @Override
    protected String getQuery() {
        return QUERY;
    }

}
