package com.github.kuros.random.jpa.persistor.functions;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.log.LogFactory;
import com.github.kuros.random.jpa.log.Logger;

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
public class PersistFunction<T> implements Function<T> {

    private static final Logger LOGGER = LogFactory.getLogger(PersistFunction.class);
    private EntityManager entityManager;
    private Function<T> findById;

    public PersistFunction() {
        entityManager = Cache.getInstance().getEntityManager();
        findById = new FindById<T>();
    }

    public T apply(final T object) {
        final Class<?> tableClass = object.getClass();
        try {
            entityManager.persist(object);
            LOGGER.debug("Persisted values for table: " + tableClass.getName());
        } catch (final Exception e) {
            LOGGER.error("Failed to persist: " + tableClass.getName());
            throw new RandomJPAException(e);
        }
        return findById.apply(object);
    }
}
