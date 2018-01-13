package com.github.kuros.random.jpa.metamodel;

import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.metamodel.providers.EclipseLinkProvider;
import com.github.kuros.random.jpa.metamodel.providers.HibernateProvider;
import com.github.kuros.random.jpa.metamodel.providers.hibernate.v4.HibernateProviderV4;
import com.github.kuros.random.jpa.metamodel.providers.hibernate.v5.HibernateProviderV5;

import javax.persistence.EntityManager;
import java.lang.reflect.Method;

public class AttributeProviderFactory {
    public static AttributeProvider getProvider(final EntityManager entityManager) {

        try {
            final Class<?> aClass = Class.forName("org.hibernate.Version");
//            final Method getVersionString = aClass.getMethod("getVersionString");
//            final String hibernateVersion = (String) getVersionString.invoke(null);
//            if (hibernateVersion.startsWith("4")) {
//                return new HibernateProviderV4(entityManager);
//            } else if (hibernateVersion.startsWith("5")) {
//                return new HibernateProviderV5(entityManager);
//            } else {
//                throw new RandomJPAException(String.format("Hibernate version: %s not supported", hibernateVersion));
//            }
            return new HibernateProvider(entityManager);
        } catch (final Exception e) {
            try {
                Class.forName("org.eclipse.persistence.sessions.DatabaseLogin");
                return new EclipseLinkProvider(entityManager);
            } catch (ClassNotFoundException e1) {
                throw new RandomJPAException("JPA Provider not supported");
            }
        }
    }
}
