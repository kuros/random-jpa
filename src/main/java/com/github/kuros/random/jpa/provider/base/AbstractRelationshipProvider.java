package com.github.kuros.random.jpa.provider.base;

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
public abstract class AbstractRelationshipProvider implements RelationshipProvider {
    private EntityManager entityManager;

    public AbstractRelationshipProvider(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<ForeignKeyRelation> getForeignKeyRelations() {
        final List<ForeignKeyRelation> foreignKeyRelations = new ArrayList<>();

        final Query query = entityManager.createNativeQuery(getQuery());
        final List resultList = query.getResultList();
        for (Object o : resultList) {
            final Object[] row = (Object[]) o;

            final ForeignKeyRelation relation = ForeignKeyRelation.newInstance(String.valueOf(row[0]).toLowerCase(), String.valueOf(row[1]).toLowerCase(), String.valueOf(row[2]).toLowerCase(), String.valueOf(row[3]).toLowerCase());
            foreignKeyRelations.add(relation);
        }

        return foreignKeyRelations;
    }

    protected abstract String getQuery();
}
