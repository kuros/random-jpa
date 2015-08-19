package com.github.kuros.random.jpa.cache;

import com.github.kuros.random.jpa.Database;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.provider.MultiplePrimaryKeyProvider;
import com.github.kuros.random.jpa.provider.RelationshipProvider;
import com.github.kuros.random.jpa.provider.SQLCharacterLengthProvider;
import com.github.kuros.random.jpa.provider.UniqueConstraintProvider;
import com.github.kuros.random.jpa.provider.factory.MultiplePrimaryKeyProviderFactory;
import com.github.kuros.random.jpa.provider.factory.RelationshipProviderFactory;
import com.github.kuros.random.jpa.provider.factory.SQLCharacterLengthProviderFactory;
import com.github.kuros.random.jpa.provider.factory.UniqueConstraintProviderFactory;

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
public final class Cache {

    private EntityManager entityManager;
    private Database database;
    private AttributeProvider attributeProvider;
    private MultiplePrimaryKeyProvider multiplePrimaryKeyProvider;
    private RelationshipProvider relationshipProvider;
    private SQLCharacterLengthProvider sqlCharacterLengthProvider;
    private UniqueConstraintProvider uniqueConstraintProvider;

    private Cache(final Database database, final EntityManager entityManager) {
        this.database = database;
        this.entityManager = entityManager;
        this.attributeProvider = initAttributeProvider();
        this.multiplePrimaryKeyProvider = initMultiplePrimaryKeyProvider();
        this.relationshipProvider = initRelationshipProvider();
        this.sqlCharacterLengthProvider = initSqlCharacterLengthProvider();
        this.uniqueConstraintProvider = initUniqueConstraintProvider();
    }

    private UniqueConstraintProvider initUniqueConstraintProvider() {
        return UniqueConstraintProviderFactory.getUniqueConstraintProvider(database, entityManager, attributeProvider);
    }

    private SQLCharacterLengthProvider initSqlCharacterLengthProvider() {
        return SQLCharacterLengthProviderFactory.getSqlCharacterLengthProvider(database, entityManager, attributeProvider);
    }

    private RelationshipProvider initRelationshipProvider() {
        return RelationshipProviderFactory.getRelationshipProvider(database, entityManager);
    }

    private MultiplePrimaryKeyProvider initMultiplePrimaryKeyProvider() {
        return MultiplePrimaryKeyProviderFactory.getMultiplePrimaryKeyProvider(database, entityManager, attributeProvider);
    }

    private AttributeProvider initAttributeProvider() {
        return AttributeProvider.getInstance(entityManager);
    }

    public static Cache create(final Database database, final EntityManager entityManager) {
        return new Cache(database, entityManager);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public Database getDatabase() {
        return database;
    }

    public AttributeProvider getAttributeProvider() {
        return attributeProvider;
    }

    public MultiplePrimaryKeyProvider getMultiplePrimaryKeyProvider() {
        return multiplePrimaryKeyProvider;
    }

    public RelationshipProvider getRelationshipProvider() {
        return relationshipProvider;
    }

    public SQLCharacterLengthProvider getSqlCharacterLengthProvider() {
        return sqlCharacterLengthProvider;
    }

    public UniqueConstraintProvider getUniqueConstraintProvider() {
        return uniqueConstraintProvider;
    }
}
