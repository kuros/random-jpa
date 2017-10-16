package com.github.kuros.random.jpa.metamodel.providers.hibernate.v5;

import com.github.kuros.random.jpa.metamodel.providers.hibernate.HibernateProviderBase;
import com.github.kuros.random.jpa.util.Util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.Metamodel;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

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
public class HibernateProviderV5 extends HibernateProviderBase {

    public HibernateProviderV5(final EntityManager entityManager) {
        super(entityManager);
        init();
    }

    protected Map<String, Object> getMetaDataMap(final EntityManagerFactory entityManagerFactory) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final Metamodel metamodel = entityManagerFactory.getMetamodel();
        return (Map<String, Object>) Util.invokeMethod(metamodel, "entityPersisters");
    }

}
