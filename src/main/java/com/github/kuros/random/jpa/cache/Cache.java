package com.github.kuros.random.jpa.cache;

import com.github.kuros.random.jpa.Database;
import com.github.kuros.random.jpa.exception.RandomJPAException;

import javax.persistence.EntityManager;

/**
 * Created by Kumar Rohit on 6/3/15.
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
