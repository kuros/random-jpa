package com.github.kuros.random.jpa.persistor.functions;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.log.LogFactory;
import com.github.kuros.random.jpa.log.Logger;

import jakarta.persistence.EntityManager;

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
class PersistFunction<T> implements Function<T> {

    private static final Logger LOGGER = LogFactory.getLogger(PersistFunction.class);
    private final EntityManager entityManager;

    PersistFunction(final Cache cache) {
        entityManager = cache.getEntityManager();
    }

    public T apply(final T object) {
        entityManager.persist(object);
        entityManager.flush();
        LOGGER.debug("Persisted values for table: " + object.getClass().getName());
        return object;
    }
}
