package com.github.kuros.random.jpa.cache;

import com.github.kuros.random.jpa.Database;
import com.github.kuros.random.jpa.exception.RandomJPAException;

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

    private static Cache cache;
    private EntityManager entityManager;
    private Database database;

    private Cache(final Database database, final EntityManager entityManager) {
        this.database = database;
        this.entityManager = entityManager;
    }

    public static void init(final Database database, final EntityManager entityManager) {
        if (cache == null) {
            cache = new Cache(database, entityManager);
        }
    }

    public static Cache getInstance() {
        if (cache == null) {
            throw new RandomJPAException("Cache should be initialized");
        }

        return cache;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public Database getDatabase() {
        return database;
    }
}
