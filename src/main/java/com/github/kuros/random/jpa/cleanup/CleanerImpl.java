package com.github.kuros.random.jpa.cleanup;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.definition.ChildGraph;
import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.log.LogFactory;
import com.github.kuros.random.jpa.log.Logger;
import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.metamodel.model.FieldWrapper;
import com.github.kuros.random.jpa.persistor.hepler.Finder;
import com.github.kuros.random.jpa.util.NumberUtil;
import com.github.kuros.random.jpa.util.Util;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public final class CleanerImpl implements Cleaner {

    private EntityManager entityManager;
    private ChildGraph childGraph;
    private Finder finder;

    private static final Logger LOGGER = LogFactory.getLogger(CleanerImpl.class);

    private CleanerImpl(final Cache cache, final ChildGraph childGraph) {
        this.entityManager = cache.getEntityManager();
        this.childGraph = childGraph;
        this.finder = new Finder(cache);
    }

    public static Cleaner newInstance(final Cache cache, final ChildGraph childGraph) {
        return new CleanerImpl(cache, childGraph);
    }

    public <T, V> void delete(final Class<T> type, final V... ids) {

        for (V id : ids) {
            final T byId = findById(type, id);
            if (byId == null) {
                throw new IllegalArgumentException("Element not found with id: " + id
                        + ", Class: " + type);
            }

            deleteChilds(byId);
        }
    }

    private <T> void deleteChilds(final T type) {

        final Set<Class<?>> childs = childGraph.getChilds(type.getClass());
        final Map<Class<?>, Set<Relation>> childRelationMap = getChildRelationMap(type.getClass());
        for (Class<?> child : childs) {

            final Map<String, Object> attributeValues = new HashMap<String, Object>();

            final Set<Relation> relations = childRelationMap.get(child);
            for (Relation relation : relations) {
                final FieldWrapper from = relation.getFrom();
                final Object value = getFieldValue(type, from);

                final FieldWrapper to = relation.getTo();
                attributeValues.put(to.getFieldName(), NumberUtil.castNumber(to.getField().getType(), value));
            }

            final List<?> byAttributes = finder.findByAttributes(child, attributeValues);
            for (Object byAttribute : byAttributes) {
                deleteChilds(byAttribute);
            }
        }

        delete(type);
    }

    private <T> Object getFieldValue(final T type, final FieldWrapper from) {
        final Field field = from.getField();
        field.setAccessible(true);
        try {
            return field.get(type);
        } catch (final IllegalAccessException e) {
            throw new RandomJPAException(e);
        }
    }

    public Map<Class<?>, Set<Relation>> getChildRelationMap(final Class<?> type) {

        final Map<Class<?>, Set<Relation>> childRelationMap = new HashMap<Class<?>, Set<Relation>>();
        final Set<Relation> childRelations = childGraph.getChildRelations(type);

        for (Relation childRelation : childRelations) {
            final Class childClass = childRelation.getTo().getInitializationClass();
            Set<Relation> relations = childRelationMap.get(childClass);
            if (relations == null) {
                relations = new HashSet<Relation>();
                childRelationMap.put(childClass, relations);
            }

            relations.add(childRelation);
        }

        return childRelationMap;
    }


    public <T> T findById(final Class<T> type, final Object value) {
        return entityManager.find(type, value);
    }

    public <T> void delete(final T t) {
        try {
            entityManager.remove(t);
            LOGGER.debug("Deleted Entity: " + t.getClass() + Util.printValues(t));
        } catch (final Exception e) {
            LOGGER.error("Unable to delete: " + t.getClass() + Util.printValues(t));
        }
    }

}
