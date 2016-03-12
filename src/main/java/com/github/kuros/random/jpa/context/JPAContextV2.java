package com.github.kuros.random.jpa.context;

import com.github.kuros.random.jpa.JPAContext;
import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.definition.HierarchyGraph;
import com.github.kuros.random.jpa.definition.MinimumHierarchyGenerator;
import com.github.kuros.random.jpa.random.generator.Generator;
import com.github.kuros.random.jpa.resolver.CreationOrderResolver;
import com.github.kuros.random.jpa.resolver.CreationOrderResolverImpl;
import com.github.kuros.random.jpa.types.ClassDepth;
import com.github.kuros.random.jpa.types.CreationOrder;
import com.github.kuros.random.jpa.types.CreationPlan;
import com.github.kuros.random.jpa.types.Entity;
import com.github.kuros.random.jpa.types.Plan;
import com.github.kuros.random.jpa.v1.resolver.CreationPlanResolver;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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

        final Collection<CreationOrder> values = creationOrderMap.values();
        sort(values);
        final CreationPlanResolver creationPlanResolver = CreationPlanResolver.newInstance(
                getRandomizer(hierarchyGraph, plan), toArray(values));
        return creationPlanResolver.with(hierarchyGraph).create();
    }

    private void sort(final Collection<CreationOrder> values) {
        for (CreationOrder value : values) {
            Collections.sort(value.getOrder(), new Comparator<ClassDepth<?>>() {
                public int compare(final ClassDepth<?> o1, final ClassDepth<?> o2) {
                    return -1 * Integer.valueOf(o1.getDepth()).compareTo(o2.getDepth());
                }
            });
        }
    }

    private CreationOrder[] toArray(final Collection<CreationOrder> values) {
        final CreationOrder[] array = new CreationOrder[values.size()];
        return values.toArray(array);
    }

    private boolean isFoundAndReplaced(final Map<Class<?>, CreationOrder> creationOrderMap, final CreationOrder creationOrder) {

        final Set<ClassDepth<?>> topLevelNodes = getTopLevelNodes(creationOrder);

        for (CreationOrder order : creationOrderMap.values()) {
            final Set<ClassDepth<?>> topLevelNodes2 = getTopLevelNodes(order);
            if (validateLeftContainsRight(creationOrder.getOrder(), topLevelNodes2)
                    || validateLeftContainsRight(order.getOrder(), topLevelNodes)) {
                mergeRightToLeft(order, creationOrder);
                return true;
            }
        }

//        for (Class<?> aClass : creationOrderMap.keySet()) {
//            if (creationOrder.containsClass(aClass)) {
//                final CreationOrder managedCreationOrder = creationOrderMap.get(aClass);
//                managedCreationOrder.setOrder(creationOrder.getOrder());
//                managedCreationOrder.addCreationCount(creationOrder.getCreationCount());
//                return true;
//            }
//        }
        return false;
    }

    private void mergeRightToLeft(final CreationOrder left, final CreationOrder right) {
        final List<ClassDepth<?>> leftOrder = left.getOrder();
        for (ClassDepth<?> rightOrderClass : right.getOrder()) {
            if (!leftOrder.contains(rightOrderClass)) {
                leftOrder.add(rightOrderClass);
            } else {
                final int index = leftOrder.indexOf(rightOrderClass);
                final ClassDepth<?> leftClassDepth = leftOrder.get(index);
                if (leftClassDepth.getDepth() < rightOrderClass.getDepth()) {
                    leftClassDepth.setDepth(rightOrderClass.getDepth());
                }
            }
        }

        left.addCreationCount(right.getCreationCount());
    }

    private boolean validateLeftContainsRight(final Collection<ClassDepth<?>> left, final Collection<ClassDepth<?>> right) {
        for (ClassDepth<?> classDepth : right) {
            if (left.contains(classDepth)) {
                return true;
            }
        }
        return false;
    }

    private Set<ClassDepth<?>> getTopLevelNodes(final CreationOrder creationOrder) {
        final List<ClassDepth<?>> order = creationOrder.getOrder();
        Set<ClassDepth<?>> classDepths = new HashSet<ClassDepth<?>>();
        if (!order.isEmpty()) {
            final int depth = order.get(0).getDepth();

            for (ClassDepth<?> classDepth : order) {
                if (classDepth.getDepth() == depth) {
                    classDepths.add(classDepth);
                }
            }
        }

        return classDepths;
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
