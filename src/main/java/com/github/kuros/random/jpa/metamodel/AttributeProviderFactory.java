package com.github.kuros.random.jpa.metamodel;

import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.metamodel.providers.EclipseLinkProvider;
import com.github.kuros.random.jpa.metamodel.providers.Provider;
import com.github.kuros.random.jpa.metamodel.providers.hibernate.v4.HibernateProviderV4;
import com.github.kuros.random.jpa.metamodel.providers.hibernate.v5.HibernateProviderV5;

import javax.persistence.EntityManager;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class AttributeProviderFactory {

    private static final Map<String, Class<? extends Provider>> PROVIDER_MAP = new HashMap<String, Class<? extends Provider>>();

    static {
        PROVIDER_MAP.put("org.eclipse.persistence.internal.jpa.EntityManagerFactoryDelegate", EclipseLinkProvider.class);
        PROVIDER_MAP.put("org.hibernate.jpa.internal.EntityManagerFactoryImpl", HibernateProviderV4.class);
        PROVIDER_MAP.put("org.hibernate.internal.SessionFactoryImpl", HibernateProviderV5.class);
    }

    public static Provider getProvider(final EntityManager entityManager) {
        try {
            final Class<? extends Provider> providerClass = PROVIDER_MAP.get(entityManager.getEntityManagerFactory().getClass().getName());
            final Constructor<? extends Provider> constructor = providerClass.getConstructor(EntityManager.class);
            return constructor.newInstance(entityManager);
        } catch (final Exception e) {
            throw new RandomJPAException("JPA Provider Not Supported");
        }
    }
}
