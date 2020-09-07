package com.github.kuros.random.jpa.resolver;

import com.github.kuros.random.jpa.definition.HierarchyGraph;
import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.types.ClassDepth;
import com.github.kuros.random.jpa.types.CreationOrder;
import com.github.kuros.random.jpa.types.Entity;
import com.github.kuros.random.jpa.types.EntityHelper;

import java.util.*;

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

    private final HierarchyGraph hierarchyGraph;

    private CreationOrderResolverImpl(final HierarchyGraph hierarchyGraph) {
        this.hierarchyGraph = hierarchyGraph;
    }

    public static CreationOrderResolver newInstance(final HierarchyGraph hierarchyGraph) {
        return new CreationOrderResolverImpl(hierarchyGraph);
    }

    public CreationOrder getCreationOrder(final Entity... entities) {
        final CreationOrder creationOrder = CreationOrder.newInstance();
        for (Entity entity : entities) {
            final Class type = EntityHelper.getType(entity);
            addCreationCount(creationOrder, entity);
            try {
                generateCreationOrder(creationOrder, type);
            } catch (final Exception e) {
                throw new RandomJPAException("Class Not Found", e);
            }
        }

        sortCreationOrderBasedOnDepth(creationOrder);

        return creationOrder;
    }

    private void sortCreationOrderBasedOnDepth(final CreationOrder creationOrder) {
        creationOrder.getOrder().sort((o1, o2) -> -1 * Integer.compare(o1.getDepth(), o2.getDepth()));
    }

    private void addCreationCount(final CreationOrder creationOrder, final Entity entity) {
        creationOrder.addCreationCount(EntityHelper.getType(entity), EntityHelper.getCount(entity));
    }

    private void generateCreationOrder(final CreationOrder creationOrder, final Class<?> type) {

        final ClassDepth classDepth = ClassDepth.newInstance(type, 0);
        final Queue<ClassDepth<?>> queue = new PriorityQueue<>(11, Comparator.comparingInt(ClassDepth::getDepth));

        queue.offer(classDepth);

        final Stack<ClassDepth<?>> stack = new Stack<>();
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
                    setParentDepth(creationOrder.getOrder(), pop.getDepth(), obj.getType());
                }
            }
        }
    }

    private void setDepthIfApplicable(final Collection<ClassDepth<?>> collection, final int depth, final Class<?> parent) {
        for (ClassDepth<?> classDepth : collection) {
            if (classDepth.getType() == parent && classDepth.getDepth() <= depth) {
                classDepth.setDepth(depth + 1);
            }
        }

        setParentDepth(collection, depth, parent);
    }

    private void setParentDepth(final Collection<ClassDepth<?>> collection, final int depth, final Class<?> parent) {
        final Set<Class<?>> parents = hierarchyGraph.getParents(parent);
        for (Class<?> aClass : parents) {
            if(parent.equals(aClass))
                continue;
            setDepthIfApplicable(collection, depth + 1, aClass);
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
}
