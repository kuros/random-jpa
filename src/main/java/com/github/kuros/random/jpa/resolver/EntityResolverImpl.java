package com.github.kuros.random.jpa.resolver;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.definition.HierarchyGraph;
import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.EntityTableMapping;
import com.github.kuros.random.jpa.types.AttributeValue;
import com.github.kuros.random.jpa.types.Entity;
import com.github.kuros.random.jpa.types.Plan;
import com.github.kuros.random.jpa.util.AttributeHelper;
import com.github.kuros.random.jpa.util.NumberUtil;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.util.HashMap;
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

    private final HierarchyGraph hierarchyGraph;
    private final EntityManager entityManager;
    private final Plan entityList;
    private AttributeProvider attributeProvider;

    private EntityResolverImpl(final HierarchyGraph hierarchyGraph, final Plan plan) {
        this.entityList = plan;
        this.entityManager = Cache.getInstance().getEntityManager();
        this.hierarchyGraph = hierarchyGraph;
        this.attributeProvider = AttributeProvider.getInstance();
    }


    public static EntityResolverImpl newInstance(final HierarchyGraph hierarchyGraph, final Plan plan) {
        return new EntityResolverImpl(hierarchyGraph, plan);
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
                    addParentDetailsForIdField(fieldValue, field);
                } catch (final Exception e) {
                    //do nothing
                }
            }
        }

        return fieldValue;
    }

    private void addParentDetailsForIdField(final Map<Field, Object> fieldValueMap, final Field field) throws IllegalAccessException {
        generateIdForParent(fieldValueMap, field);
    }

    private void generateIdForParent(final Map<Field, Object> fieldValueMap, final Field field) throws IllegalAccessException {

        final EntityTableMapping entityTableMapping = attributeProvider.get(field.getDeclaringClass());
        final Set<Relation> relations = hierarchyGraph.getAttributeRelations(field.getDeclaringClass());

        if (entityTableMapping.getAttributeIds().contains(field.getName()) || relations == null) {
            return;
        }

        final Object byId = findById(field.getDeclaringClass(), fieldValueMap.get(field));
        if (byId == null) {
            throw new IllegalArgumentException("Element not found with id: " + fieldValueMap.get(field)
                    + ", Class: " + field.getDeclaringClass());
        }

        for (Relation relation : relations) {
            final Field from = relation.getFrom();
            from.setAccessible(true);
            final Object value = from.get(byId);
            final Field to = relation.getTo();

            fieldValueMap.put(to, NumberUtil.castNumber(to.getType(), value));

            generateIdForParent(fieldValueMap, to);
        }
    }


    public <T> T findById(final Class<T> type, final Object value) {
        return entityManager.find(type, value);
    }

}
