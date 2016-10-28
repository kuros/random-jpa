package com.github.kuros.random.jpa.persistor.functions;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.cache.TriggerCache;
import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.link.Link;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.persistor.hepler.Finder;
import com.github.kuros.random.jpa.types.Trigger;
import com.github.kuros.random.jpa.util.AttributeHelper;
import com.github.kuros.random.jpa.util.Util;

import javax.persistence.EntityManager;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
class TriggerFunction<T> implements Function<T> {

    private final Cache cache;
    private final EntityManager entityManager;
    private Finder finder;

    TriggerFunction(final Cache cache) {
        this.cache = cache;
        this.finder = new Finder(cache);
        this.entityManager = cache.getEntityManager();
    }

    public T apply(final T object) {
        final Class<?> type = object.getClass();
        final TriggerCache triggerCache = cache.getTriggerCache();

        if (triggerCache.contains(type)) {
            final Trigger<?> trigger = triggerCache.getTrigger(type);
            final List<String> attributeNames = getAttributeNames(trigger);

            final T found = finder.findByAttributes(object, attributeNames);
            if (found == null) {
                throw new RandomJPAException("Entity should have been created by trigger: "
                        + type.getName()
                        + Util.printValues(object) + " by values: " + attributeNames);
            }

            copyValues(object, found);

            entityManager.merge(found);
            return found;
        }

        return null;
    }

    private void copyValues(final T from, final T to) {

        final EntityTableMapping entityTableMapping = cache.getAttributeProvider().get(from.getClass());
        final List<String> attributeIds = entityTableMapping.getAttributeIds();
        try {
            final BeanInfo beanInfo = Introspector.getBeanInfo(from.getClass(), Object.class);
            final PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor pd : props) {

                if (!attributeIds.contains(pd.getName())) {
                    final Method getter = pd.getReadMethod();

                    final Object value = getter.invoke(from);

                    pd.getWriteMethod().invoke(to, value);
                }
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

    }

    private List<String> getAttributeNames(final Trigger<?> trigger) {
        final List<Link> links = trigger.getLinks();

        final List<String> attributeNames = new ArrayList<String>();
        for (Link link : links) {
            final String name = AttributeHelper.getName(link.getFrom());
            attributeNames.add(name);
        }
        return attributeNames;
    }

    void setFinder(final Finder finder) {
        this.finder = finder;
    }
}
