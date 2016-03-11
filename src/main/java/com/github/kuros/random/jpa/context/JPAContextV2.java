package com.github.kuros.random.jpa.context;

import com.github.kuros.random.jpa.JPAContext;
import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.definition.HierarchyGraph;
import com.github.kuros.random.jpa.definition.MinimumHierarchyGenerator;
import com.github.kuros.random.jpa.random.generator.Generator;
import com.github.kuros.random.jpa.resolver.CreationOrderResolver;
import com.github.kuros.random.jpa.resolver.CreationOrderResolverImpl;
import com.github.kuros.random.jpa.types.CreationOrder;
import com.github.kuros.random.jpa.types.CreationPlan;
import com.github.kuros.random.jpa.types.Entity;
import com.github.kuros.random.jpa.types.Plan;
import com.github.kuros.random.jpa.v1.resolver.CreationPlanResolver;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
public final class JPAContextV2 extends BaseContext {

    public static JPAContext newInstance(final Cache cache,
                                         final Generator generator) {
        return new JPAContextV2(cache, generator);
    }

    private JPAContextV2(final Cache cache, final Generator generator) {
        super(cache, generator);
    }

    public CreationPlan create(final Plan plan) {

        final List<Entity> entities = plan.getEntities();
        final HierarchyGraph hierarchyGraph = MinimumHierarchyGenerator.generate(getCache().getHierarchyGraph(), entities);

        final CreationOrderResolver creationOrderResolver = CreationOrderResolverImpl.newInstance(getCache(), hierarchyGraph, plan.getPreconditions());


        final Map<Class<?>, CreationOrder> creationOrderMap = new HashMap<Class<?>, CreationOrder>();

        for (Entity entity : entities) {
            final Class<?> key = findKey(creationOrderMap, entity.getType());
            if (key == null) {
                final CreationOrder creationOrder = creationOrderResolver.getCreationOrder(entity);

                if (!isFoundAndReplaced(creationOrderMap, creationOrder)) {
                    creationOrderMap.put(entity.getType(), creationOrder);
                }
            } else {
                final CreationOrder creationOrder = creationOrderMap.get(key);
                creationOrder.addCreationCount(entity.getType(), entity.getCount());
            }

        }

        final CreationPlanResolver creationPlanResolver = CreationPlanResolver.newInstance(
                getRandomizer(hierarchyGraph, plan), toArray(creationOrderMap.values()));
        return creationPlanResolver.with(hierarchyGraph).create();
    }

    private CreationOrder[] toArray(final Collection<CreationOrder> values) {
        final CreationOrder[] array = new CreationOrder[values.size()];
        return values.toArray(array);
    }

    private boolean isFoundAndReplaced(final Map<Class<?>, CreationOrder> creationOrderMap, final CreationOrder creationOrder) {
        for (Class<?> aClass : creationOrderMap.keySet()) {
            if (creationOrder.containsClass(aClass)) {
                final CreationOrder managedCreationOrder = creationOrderMap.get(aClass);
                managedCreationOrder.setOrder(creationOrder.getOrder());
                managedCreationOrder.addCreationCount(creationOrder.getCreationCount());
                return true;
            }
        }
        return false;
    }

    private Class<?> findKey(final Map<Class<?>, CreationOrder> creationOrderMap, final Class type) {

        for (Class<?> aClass : creationOrderMap.keySet()) {
            final CreationOrder creationOrder = creationOrderMap.get(aClass);
            if (creationOrder.containsClass(type)) {
                return aClass;
            }
        }

        return null;
    }

}
