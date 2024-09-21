package com.github.kuros.random.jpa.testUtil;

import com.github.kuros.random.jpa.log.LogFactory;
import com.github.kuros.random.jpa.log.Logger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
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
public class EntityManagerProvider {

    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY;
    private static final EntityManagerFactory ECLIPSELINK_FACTORY;
    private static final Logger LOGGER = LogFactory.getLogger(EntityManagerProvider.class);

    public static void init() {
        final EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        entityManager.close();

        ECLIPSELINK_FACTORY.createEntityManager().close();
    }

    public static <T> T persist(final T t) {
        final EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();

        entityManager.getTransaction().begin();
        entityManager.persist(t);
        entityManager.flush();
        entityManager.getTransaction().commit();
        entityManager.close();
        return t;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> find(final String query) {
        final EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();

        return entityManager.createQuery(query).getResultList();
    }

    public static EntityManager getEntityManager() {
        return ENTITY_MANAGER_FACTORY.createEntityManager();
    }

    public static EntityManager getEclipseLinkEntityManager() {
        return ECLIPSELINK_FACTORY.createEntityManager();
    }

    static {
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("RandomJpaService");
        ECLIPSELINK_FACTORY = Persistence.createEntityManagerFactory("eclipse-link");
    }
}
