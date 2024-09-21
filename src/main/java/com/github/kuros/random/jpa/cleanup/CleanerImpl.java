package com.github.kuros.random.jpa.cleanup;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.definition.ChildGraph;
import com.github.kuros.random.jpa.definition.HierarchyGraph;
import com.github.kuros.random.jpa.log.LogFactory;
import com.github.kuros.random.jpa.log.Logger;
import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.metamodel.model.FieldWrapper;
import com.github.kuros.random.jpa.persistor.hepler.Finder;
import com.github.kuros.random.jpa.types.DeletionOrder;
import com.github.kuros.random.jpa.types.DeletionOrderImpl;
import com.github.kuros.random.jpa.util.ArrayListMultimap;
import com.github.kuros.random.jpa.util.Multimap;
import com.github.kuros.random.jpa.util.NumberUtil;
import com.github.kuros.random.jpa.util.Util;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
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

    private static final String DELETE_FROM = "DELETE FROM ";
    private final EntityManager entityManager;
    private final HierarchyGraph hierarchyGraph;
    private final ChildGraph childGraph;
    private final Finder finder;
    private final Set<Class<?>> skipTruncation;
    private final Set<Class<?>> allClassesToSkip;
    private final Cache cache;

    private static final Logger LOGGER = LogFactory.getLogger(CleanerImpl.class);

    private CleanerImpl(final Cache cache) {
        this.entityManager = cache.getEntityManager();
        this.childGraph = ChildGraph.newInstance(cache.getHierarchyGraph());
        this.finder = new Finder(cache);
        this.skipTruncation = cache.getSkipTruncation();
        this.hierarchyGraph = cache.getHierarchyGraph();
        this.cache = cache;
        this.allClassesToSkip = getSkippedClasses();
    }


    public static Cleaner newInstance(final Cache cache) {
        return new CleanerImpl(cache);
    }

    @SafeVarargs
    @Override
    public final <T, V> void delete(final Class<T> type, final V... ids) {
        delete(getDeletionOrder(type, ids));
    }

    @Override
    public void truncateAll() {
        final Set<Class<?>> skip = new HashSet<>(allClassesToSkip);

        final Set<Class<?>> classes = childGraph.keySet();
        for (Class<?> aClass : classes) {
            truncate(skip , aClass);
        }
    }

    @SafeVarargs
    @Override
    public final <T, V> DeletionOrder getDeletionOrder(final Class<T> type, final V... ids) {
        final List<Object> deletionOrder = new ArrayList<>();
        for (V id : ids) {
            final T byId = findById(type, id);
            if (byId == null) {
                throw new IllegalArgumentException("Element not found with id: " + id
                        + ", Class: " + type);
            }

            getDeleteOrder(deletionOrder, byId);
        }

        return DeletionOrderImpl.create(deletionOrder);
    }

    @Override
    public void delete(final DeletionOrder deletionOrder) {
        final List<Object> deletionOrders = deletionOrder.getDeletionOrders();
        final AttributeProvider attributeProvider = cache.getAttributeProvider();

        final Set<Class<?>> uniqueClassesInOrder = new LinkedHashSet<>();
        Multimap<Class<?>, Object> classEntityMultimap = ArrayListMultimap.newArrayListMultimap();
        for (Object entity : deletionOrders) {
            classEntityMultimap.put(entity.getClass(), entity);
            uniqueClassesInOrder.add(entity.getClass());
        }

        for (Class<?> type : uniqueClassesInOrder) {
            final EntityTableMapping entityTableMapping = attributeProvider.get(type);
            final List<String> columnIds = entityTableMapping.getColumnIds();

            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append(DELETE_FROM)
                    .append(entityTableMapping.getTableName())
                    .append(" WHERE ");
            final Map<String, List<Object>> allParams = new HashMap<>();
            for (int i = 0; i < columnIds.size(); i++) {
                if (i != 0) {
                    queryBuilder.append(" AND ");
                }

                final String column = columnIds.get(i);
                queryBuilder.append(column)
                        .append(" IN (:")
                        .append(column)
                        .append(")");

                final List<Object> params = new ArrayList<>();

                for (Object entity : classEntityMultimap.get(type)) {

                    final String attributeName = entityTableMapping.getAttributeName(column);
                    final Field declaredField = Util.getField(type, attributeName);
                    final Object value = Util.getFieldValue(entity, declaredField);
                    params.add(value);
                }
                allParams.put(column, params);
            }

            final Query query = entityManager.createNativeQuery(queryBuilder.toString());
            for (String paramKey : allParams.keySet()) {
                query.setParameter(paramKey, allParams.get(paramKey));
            }

            query.executeUpdate();

        }

        entityManager.flush();
        entityManager.clear();

    }

    @Override
    public void truncate(final Class<?> type) {
        final Set<Class<?>> skippedClasses = new HashSet<>(allClassesToSkip);
        truncate(skippedClasses, type);
    }


    private void truncate(final Set<Class<?>> skip, final Class<?> type) {

        final Set<Class<?>> childs = childGraph.getChilds(type);
        for (Class<?> child : childs) {
            truncate(skip, child);
        }

        if (!skip.contains(type)) {
            final int rowsDeleted = entityManager.createQuery(DELETE_FROM + type.getSimpleName()).executeUpdate();
            skip.add(type);
            LOGGER.debug("Class: " + type + " No. of rows deleted: " + rowsDeleted);
        }

    }

    private Set<Class<?>> getSkippedClasses() {
        final Set<Class<?>> skip = new HashSet<>();
        for (Class<?> aClass : skipTruncation) {
            addAllParents(skip, aClass);
        }

        skip.addAll(skipTruncation);
        return skip;
    }

    private void addAllParents(final Set<Class<?>> holder, final Class<?> type) {
        final Set<Class<?>> parents = hierarchyGraph.getParents(type);
        holder.addAll(parents);
        for (Class<?> parent : parents) {
            addAllParents(holder, parent);
        }
    }

    private <T> void getDeleteOrder(final List<Object> deletionOrder, final T type) {
        final Set<Class<?>> childs = childGraph.getChilds(type.getClass());
        final Map<Class<?>, Set<Relation>> childRelationMap = getChildRelationMap(type.getClass());
        for (Class<?> child : childs) {

            final List<?> byAttributes = getChilds(childRelationMap, type, child);
            for (Object byAttribute : byAttributes) {
                getDeleteOrder(deletionOrder, byAttribute);
            }
        }

        if (!allClassesToSkip.contains(type.getClass())) {
            deletionOrder.add(type);
        }
    }

    private <T> List<?> getChilds(final Map<Class<?>, Set<Relation>> childRelationMap, final T type, final Class<?> child) {
        final Map<String, Object> attributeValues = new HashMap<>();

        final Set<Relation> relations = childRelationMap.get(child);
        for (Relation relation : relations) {
            final FieldWrapper from = relation.getFrom();
            final Object value = getFieldValue(type, from);

            final FieldWrapper to = relation.getTo();
            attributeValues.put(to.getFieldName(), NumberUtil.castNumber(to.getField().getType(), value));
        }

        return finder.findByAttributes(child, attributeValues);
    }

    private <T> Object getFieldValue(final T type, final FieldWrapper from) {
        final Field field = from.getField();
        return Util.getFieldValue(type, field);
    }

    private Map<Class<?>, Set<Relation>> getChildRelationMap(final Class<?> type) {

        final Map<Class<?>, Set<Relation>> childRelationMap = new HashMap<>();
        final Set<Relation> childRelations = childGraph.getChildRelations(type);

        for (Relation childRelation : childRelations) {
            final Class<?> childClass = childRelation.getTo().getInitializationClass();
            Set<Relation> relations = childRelationMap.computeIfAbsent(childClass, k -> new HashSet<>());

            relations.add(childRelation);
        }

        return childRelationMap;
    }


    private  <T> T findById(final Class<T> type, final Object value) {
        return entityManager.find(type, value);
    }

}
