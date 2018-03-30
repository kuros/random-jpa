package com.github.kuros.random.jpa.metamodel;

import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.metamodel.providers.EclipseLinkProvider;
import com.github.kuros.random.jpa.metamodel.providers.HibernateProvider;

import javax.persistence.EntityManager;

public class AttributeProviderFactory {

    private AttributeProviderFactory() {
    }

    public static AttributeProvider getProvider(final EntityManager entityManager) {

        try {
            Class.forName("org.hibernate.Version");
            return new HibernateProvider(entityManager);
        } catch (final ClassNotFoundException e) {
            try {
                Class.forName("org.eclipse.persistence.sessions.DatabaseLogin");
                return new EclipseLinkProvider(entityManager);
            } catch (ClassNotFoundException e1) {
                throw new RandomJPAException("JPA Provider not supported");
            }
        }
    }
}
