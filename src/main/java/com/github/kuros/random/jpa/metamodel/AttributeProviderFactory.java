package com.github.kuros.random.jpa.metamodel;

import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.log.LogFactory;
import com.github.kuros.random.jpa.log.Logger;
import com.github.kuros.random.jpa.metamodel.providers.EclipseLinkProvider;
import com.github.kuros.random.jpa.metamodel.providers.HibernateProvider;
import com.github.kuros.random.jpa.util.Util;

import javax.persistence.EntityManager;

public class AttributeProviderFactory {

    private static final Logger LOGGER = LogFactory.getLogger(AttributeProviderFactory.class);

    private AttributeProviderFactory() {
    }

    public static AttributeProvider getProvider(final EntityManager entityManager) {

        try {
            Class.forName("org.hibernate.Version");
            return new HibernateProvider(entityManager);
        } catch (final Exception e) {
            try {
                Class.forName("org.eclipse.persistence.sessions.DatabaseLogin");
                return new EclipseLinkProvider(entityManager);
            } catch (Exception e1) {
                LOGGER.error("Failed to use Hibernate as EntityProvider", Util.getDeepestRootCause(e));
                LOGGER.error("Failed to use EclipseLink as EntityProvider", Util.getDeepestRootCause(e1));
                throw new RandomJPAException("JPA Provider not supported");
            }
        }
    }
}
