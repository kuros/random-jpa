package com.github.kuros.random.jpa.resolver;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.definition.HierarchyGraph;
import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.log.LogFactory;
import com.github.kuros.random.jpa.log.Logger;
import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.metamodel.model.FieldWrapper;
import com.github.kuros.random.jpa.types.AttributeValue;
import com.github.kuros.random.jpa.types.Entity;
import com.github.kuros.random.jpa.types.Plan;
import com.github.kuros.random.jpa.util.ArrayListMultimap;
import com.github.kuros.random.jpa.util.AttributeHelper;
import com.github.kuros.random.jpa.util.Multimap;
import com.github.kuros.random.jpa.util.NumberUtil;
import com.github.kuros.random.jpa.util.Util;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.util.ArrayList;
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
public final class EntityResolverImpl implements EntityResolver {

    private static final Logger LOGGER = LogFactory.getLogger(EntityResolverImpl.class);
    private final HierarchyGraph hierarchyGraph;
    private final EntityManager entityManager;
    private final Plan entityList;
    private AttributeProvider attributeProvider;

    private EntityResolverImpl(final Cache cache, final HierarchyGraph hierarchyGraph, final Plan plan) {
        this.entityList = plan;
        this.entityManager = cache.getEntityManager();
        this.attributeProvider = cache.getAttributeProvider();
        this.hierarchyGraph = hierarchyGraph;
    }


    public static EntityResolverImpl newInstance(final Cache cache, final HierarchyGraph hierarchyGraph, final Plan plan) {
        return new EntityResolverImpl(cache, hierarchyGraph, plan);
    }

    @SuppressWarnings("unchecked")
    public Map<Field, Object> getFieldValueMap() {
        final Map<Field, Object> fieldValue = new HashMap<Field, Object>();
        final List<Entity> entities = this.entityList.getEntities();

        for (Entity entity : entities) {
            final List<AttributeValue> attributeValues = entity.getAttributeValues();
            for (AttributeValue attributeValue : attributeValues) {
                try {
                    final Field field = AttributeHelper.getField(attributeValue.getAttribute());
                    fieldValue.put(field, attributeValue.getValue());
                    addParentDetailsForField(fieldValue, field);
                } catch (final Exception e) {
                    //do nothing
                }
            }
        }

        return fieldValue;
    }

    private void addParentDetailsForField(final Map<Field, Object> fieldValueMap, final Field field) throws IllegalAccessException, NoSuchFieldException {
        final EntityTableMapping entityTableMapping = attributeProvider.get(field.getDeclaringClass());
        final Set<Relation> relations = hierarchyGraph.getAttributeRelations(field.getDeclaringClass());

        if ((entityTableMapping != null && !entityTableMapping.getAttributeIds().contains(field.getName()))
                || relations == null) {
            return;
        }

        final Object byId = findById(field.getDeclaringClass(), fieldValueMap.get(field));
        if (byId == null) {
            throw new IllegalArgumentException("Element not found with id: " + fieldValueMap.get(field)
                    + ", Class: " + field.getDeclaringClass());
        }

        generateIdForParent(fieldValueMap, field.getDeclaringClass(), byId);
    }

    private void generateIdForParent(final Map<Field, Object> fieldValueMap, final Class<?> type, final Object object) throws IllegalAccessException, NoSuchFieldException {

        final Set<Relation> relations = hierarchyGraph.getAttributeRelations(type);
        if (relations == null || relations.isEmpty()) {
            return;
        }

        final Multimap<Class, FieldValue> multimap = ArrayListMultimap.newArrayListMultimap();

        for (Relation relation : relations) {
            final FieldWrapper from = relation.getFrom();
            final FieldValue fieldValue = new FieldValue(relation.getTo().getField(), getFieldValue(object, from.getField()));
            multimap.put(relation.getTo().getInitializationClass(), fieldValue);
        }

        for (Class aClass : multimap.getKeySet()) {
            final Object row = findByQuery(aClass, new ArrayList<FieldValue>(multimap.get(aClass)));
            findId(fieldValueMap, aClass, row);
            generateIdForParent(fieldValueMap, aClass, row);
        }
    }

    private Object getFieldValue(final Object object, final Field field) throws IllegalAccessException {
        field.setAccessible(true);
        return field.get(object);
    }

    private void findId(final Map<Field, Object> fieldValueMap, final Class aClass, final Object row) throws NoSuchFieldException, IllegalAccessException {
        final EntityTableMapping entityTableMapping = attributeProvider.get(aClass);

        if (entityTableMapping == null || row == null) {
            return;
        }

        final List<String> attributeIds = entityTableMapping.getAttributeIds();

        for (String attributeId : attributeIds) {
            final Field declaredField = aClass.getDeclaredField(attributeId);
            fieldValueMap.put(declaredField, getFieldValue(row, declaredField));
        }
    }

    @SuppressWarnings("unchecked")
    private Object findByQuery(final Class<?> tableClass, final List<FieldValue> fieldValues) throws IllegalAccessException {
        filterEmptyFields(fieldValues);

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery q = criteriaBuilder.createQuery(tableClass);

        final Root<?> from = q.from(tableClass);

        q.select(from);

        final Predicate[] predicates = new Predicate[fieldValues.size()];

        for (int i = 0; i < fieldValues.size(); i++) {
            try {
                final FieldValue fieldValue = fieldValues.get(i);
                final Field key = fieldValue.getField();
                predicates[i] = criteriaBuilder.equal(from.get(key.getName()), NumberUtil.castNumber(key.getDeclaringClass(), fieldValue.getValue()));
            } catch (final Exception e) {
                throw new RandomJPAException(e);
            }
        }

        q.where(predicates);

        final TypedQuery typedQuery = entityManager.createQuery(q);
        final List resultList = typedQuery.getResultList();

        if (resultList.size() > 0) {
            LOGGER.debug("Reusing data for: " + tableClass + " " + Util.printValues(resultList.get(0)));
        }

        return resultList.size() == 0 ? null : resultList.get(0);
    }

    private void filterEmptyFields(final List<FieldValue> fieldValues) throws IllegalAccessException {
        final Set<FieldValue> emptyFields = new HashSet<FieldValue>();
        for (FieldValue keyValue : fieldValues) {
            if (keyValue.getValue() == null) {
                emptyFields.add(keyValue);
            }
        }

        fieldValues.removeAll(emptyFields);
    }


    public <T> T findById(final Class<T> type, final Object value) {
        return entityManager.find(type, value);
    }

    private class FieldValue {
        private Field field;
        private Object value;

        FieldValue(final Field field, final Object value) {
            this.field = field;
            this.value = value;
        }

        public Field getField() {
            return field;
        }

        public Object getValue() {
            return value;
        }
    }

}
