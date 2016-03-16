package com.github.kuros.random.jpa.resolver;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.definition.HierarchyGraph;
import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.link.Preconditions;
import com.github.kuros.random.jpa.types.ClassDepth;
import com.github.kuros.random.jpa.types.CreationOrder;
import com.github.kuros.random.jpa.types.Entity;
import com.github.kuros.random.jpa.types.Plan;
import com.github.kuros.random.jpa.types.Version;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

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
public final class CreationOrderResolverImpl implements CreationOrderResolver {

    private final Cache cache;
    private final HierarchyGraph hierarchyGraph;
    private final Preconditions planLevelPreconditions;

    private CreationOrderResolverImpl(final Cache cache, final HierarchyGraph hierarchyGraph, final Preconditions planLevelPreconditions) {
        this.cache = cache;
        this.hierarchyGraph = hierarchyGraph;
        this.planLevelPreconditions = planLevelPreconditions;
    }

    @Deprecated
    public static CreationOrderResolver newInstance(final Cache cache, final HierarchyGraph hierarchyGraph, final Preconditions planLevelPreconditions) {
        return new CreationOrderResolverImpl(cache, hierarchyGraph, planLevelPreconditions);
    }

    public static CreationOrderResolver newInstance(final Cache cache, final HierarchyGraph hierarchyGraph) {
        return new CreationOrderResolverImpl(cache, hierarchyGraph, null);
    }

    public CreationOrder getCreationOrder(final Entity... entities) {
        final CreationOrder creationOrder = CreationOrder.newInstance();
        for (Entity entity : entities) {
            final Class type = entity.getType();
            addCreationCount(creationOrder, entity);
            try {
                generateCreationOrder(creationOrder, type);
            } catch (final ClassNotFoundException e) {
                throw new RandomJPAException("Class Not Found", e);
            }
        }

        if (cache.getVersion().isSupported(Version.V2)) {
            sortCreationOrderBasedOnDepth(creationOrder);
        }

        if (cache.getVersion().isSupported(Version.V1)) {
            applyPlanLevelPrecondition(creationOrder);
        }

        return creationOrder;
    }

    private void sortCreationOrderBasedOnDepth(final CreationOrder creationOrder) {
        Collections.sort(creationOrder.getOrder(), new Comparator<ClassDepth<?>>() {
            public int compare(final ClassDepth<?> o1, final ClassDepth<?> o2) {
                return -1 * Integer.valueOf(o1.getDepth()).compareTo(o2.getDepth());
            }
        });
    }

    private void applyPlanLevelPrecondition(final CreationOrder creationOrder) {
        final Set<Class<?>> identifiers = planLevelPreconditions.getIdentifiers();

        for (Class<?> identifier : identifiers) {
            if (!creationOrder.containsClass(identifier)) {
                continue;
            }

            final Plan preConditionPlan = planLevelPreconditions.getPlan(identifier);
            try {
                adjustEntityInCreationOrder(creationOrder, preConditionPlan);
            } catch (final ClassNotFoundException e) {
                throw new RandomJPAException(e);
            }
        }
    }

    private void adjustEntityInCreationOrder(final CreationOrder creationOrder, final Plan preConditionPlan) throws ClassNotFoundException {
        for (Entity entity : preConditionPlan.getEntities()) {

            final CreationOrder tempCreationOrder = CreationOrder.newInstance();
            generateCreationOrder(tempCreationOrder, entity.getType());
            final List<ClassDepth<?>> newOrder = tempCreationOrder.getOrder();
            final List<ClassDepth<?>> createdOrder = creationOrder.getOrder();
            final int minIndex = getMinIndex(createdOrder, newOrder);

            ClassDepth<?> location = null;
            int i = minIndex;
            while (i > 0) {
                final ClassDepth<?> aClass = createdOrder.get(--i);
                if (!newOrder.contains(aClass)) {
                    location = aClass;
                    break;
                }
            }

            createdOrder.removeAll(newOrder);

            i = minIndex;
            if (location != null) {
                i = createdOrder.indexOf(location);
                createdOrder.addAll(i + 1, newOrder);
            } else {
                createdOrder.addAll(i, newOrder);
            }

        }
    }

    private void addCreationCount(final CreationOrder creationOrder, final Entity entity) {
        creationOrder.addCreationCount(entity.getType(), entity.getCount());
    }

    private void generateCreationOrder(final CreationOrder creationOrder, final Class<?> type) throws ClassNotFoundException {

        final ClassDepth classDepth = ClassDepth.newInstance(type, 0);
        final Queue<ClassDepth<?>> queue = new PriorityQueue<ClassDepth<?>>(new Comparator<ClassDepth<?>>() {
            public int compare(final ClassDepth<?> o1, final ClassDepth<?> o2) {
                return Integer.valueOf(o1.getDepth()).compareTo(o2.getDepth());
            }
        });
        queue.offer(classDepth);

        final Stack<ClassDepth<?>> stack = new Stack<ClassDepth<?>>();
        stack.push(classDepth);

        while (!queue.isEmpty()) {
            final ClassDepth<?> polledClass = queue.poll();
            final Set<Class<?>> parents = hierarchyGraph.getParents(polledClass.getType());
            Integer index = null;
            for (Class<?> parent : parents) {
                if (notContains(stack, parent)) {
                    queue.offer(ClassDepth.newInstance(parent, polledClass.getDepth() + 1));
                } else {
                    final int stackIndex = getIndex(stack, parent);
                    if (index == null || index > stackIndex) {
                        index = stackIndex;
                    }
                }

                setDepthIfApplicable(queue, polledClass.getDepth(), parent);
                setDepthIfApplicable(stack, polledClass.getDepth(), parent);
            }


            if (index != null) {
                stack.add(index, polledClass);
            } else if (!stack.contains(polledClass)) {
                stack.push(polledClass);
            }
        }

        while (!stack.isEmpty()) {
            final ClassDepth<?> pop = stack.pop();
            if (!creationOrder.contains(pop)) {
                creationOrder.add(pop);
            } else {
                final List<ClassDepth<?>> order = creationOrder.getOrder();

                final int indexOf = order.indexOf(pop);
                final ClassDepth<?> obj = order.get(indexOf);
                if (obj.getDepth() < pop.getDepth()) {
                    obj.setDepth(pop.getDepth());
                }
            }
        }
    }

    private void setDepthIfApplicable(final Collection<ClassDepth<?>> collection, final int depth, final Class<?> parent) {
        for (ClassDepth<?> classDepth : collection) {
            if (classDepth.getType() == parent && classDepth.getDepth() <= depth) {
                classDepth.setDepth(depth + 1);
                final Set<Class<?>> parents = hierarchyGraph.getParents(parent);
                for (Class<?> aClass : parents) {
                    setDepthIfApplicable(collection, depth + 1, aClass);
                }
            }
        }
    }

    private int getIndex(final Stack<ClassDepth<?>> stack, final Class<?> parent) {
        for (int i = 0; i < stack.size(); i++) {
            final ClassDepth<?> classDepth = stack.get(i);
            if (classDepth.getType() == parent) {
                return i;
            }
        }
        return 0;
    }

    private boolean notContains(final Stack<ClassDepth<?>> stack, final Class<?> parent) {
        for (ClassDepth<?> classDepth : stack) {
            if (classDepth.getType() == parent) {
                return false;
            }
        }
        return true;
    }

    private int getMinIndex(final List<ClassDepth<?>> from, final List<ClassDepth<?>> order) {
        int index = from.size();

        for (ClassDepth<?> type : order) {
            final int i = from.indexOf(type);
            if (index > i && i != -1) {
                index = i;
            }
        }

        return index;
    }
}
