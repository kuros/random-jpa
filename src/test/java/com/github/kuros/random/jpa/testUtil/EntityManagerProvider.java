package com.github.kuros.random.jpa.testUtil;

import com.github.kuros.random.jpa.log.LogFactory;
import com.github.kuros.random.jpa.log.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class EntityManagerProvider {

    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY;
    private static final Logger LOGGER = LogFactory.getLogger(EntityManagerProvider.class);

    public static void init() {
        final EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        entityManager.close();
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

    static {
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("RandomJpaService");
    }
}
