package com.github.kuros.random.jpa.metamodel.providers.hibernate.v4;

import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.metamodel.providers.hibernate.HibernateProviderBase;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import static com.github.kuros.random.jpa.util.Util.invokeMethod;

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
public class HibernateProviderV4 extends HibernateProviderBase {

    private Map<Class<?>, EntityTableMapping> entityTableMappingByClass;
    private Map<String, List<EntityTableMapping>> entityTableMappingByTableName;
    private EntityManager entityManager;

    public HibernateProviderV4(final EntityManager entityManager) {
        super(entityManager);
        init();
    }

    protected Map<String, Object> getMetaDataMap(final EntityManagerFactory entityManagerFactory) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final Object sessionFactory = invokeMethod(entityManagerFactory, "getSessionFactory");
        return (Map<String, Object>) invokeMethod(sessionFactory, "getAllClassMetadata");
    }


}
