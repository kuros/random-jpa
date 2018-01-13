package com.github.kuros.random.jpa.metamodel.providers;

import com.github.kuros.random.jpa.metamodel.providers.hibernate.HibernateProviderBase;
import com.github.kuros.random.jpa.util.Util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.Metamodel;
import java.util.Map;

import static com.github.kuros.random.jpa.util.Util.invokeMethod;

public class HibernateProvider extends HibernateProviderBase {

    public HibernateProvider(final EntityManager entityManager) {
        super(entityManager);
        init();
    }

    protected Map<String, Object> getMetaDataMap(final EntityManagerFactory entityManagerFactory) {
        try {
            final Metamodel metamodel = entityManagerFactory.getMetamodel();
            return (Map<String, Object>) Util.invokeMethod(metamodel, "entityPersisters");
        } catch (final Exception e) {
            final Object sessionFactory = invokeMethod(entityManagerFactory, "getSessionFactory");
            return (Map<String, Object>) invokeMethod(sessionFactory, "getAllClassMetadata");
        }
    }
}
