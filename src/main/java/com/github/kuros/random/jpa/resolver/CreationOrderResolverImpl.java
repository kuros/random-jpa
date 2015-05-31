package com.github.kuros.random.jpa.resolver;

import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.mapper.HierarchyGraph;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.EntityTableMapping;
import com.github.kuros.random.jpa.types.AttributeValue;
import com.github.kuros.random.jpa.types.CreationOrder;
import com.github.kuros.random.jpa.types.Entity;
import com.github.kuros.random.jpa.types.Plan;

import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

/**
 * Created by Kumar Rohit on 5/17/15.
 */
public final class CreationOrderResolverImpl implements CreationOrderResolver {

    private AttributeProvider attributeProvider;
    private HierarchyGraph hierarchyGraph;
    private Plan plan;


    private CreationOrderResolverImpl(final AttributeProvider attributeProvider, final HierarchyGraph hierarchyGraph, final Plan plan) {
        this.attributeProvider = attributeProvider;
        this.hierarchyGraph = hierarchyGraph;
        this.plan = plan;
    }

    public static CreationOrderResolver newInstance(final AttributeProvider attributeProvider, final HierarchyGraph hierarchyGraph, final Plan plan) {
        return new CreationOrderResolverImpl(attributeProvider, hierarchyGraph, plan);
    }

    @Override
    public CreationOrder getCreationOrder() {
        final CreationOrder creationOrder = CreationOrder.newInstance(hierarchyGraph);
        final List<Entity> entities = plan.getEntities();
        for (Entity entity : entities) {
            final Class type = entity.getType();
            addCreationCount(creationOrder, entity);
            try {
                generateCreationOrder(creationOrder, type);
            } catch (final ClassNotFoundException e) {
                throw new RandomJPAException("Class Not Found", e);
            }
        }

        filterAlreadyGenerated(creationOrder, plan.getEntities());

        return creationOrder;
    }

    private void addCreationCount(final CreationOrder creationOrder, final Entity entity) {
        creationOrder.addCreationCount(entity.getType(), entity.getCount());
    }

    private void filterAlreadyGenerated(final CreationOrder creationOrder, final List<Entity> entities) {

        final Set<Class<?>> itemsWithId = new HashSet<Class<?>>();
        final Set<Class<?>> itemsWithoutId = new HashSet<Class<?>>();

        for (Entity entity : entities) {
            if (isFilterRequired(entity)) {
                itemsWithId.addAll(hierarchyGraph.getParents(entity.getType()));
            } else {
                itemsWithoutId.addAll(hierarchyGraph.getParents(entity.getType()));
            }
        }

        itemsWithId.removeAll(itemsWithoutId);

        creationOrder.getOrder().removeAll(itemsWithId);
    }

    @SuppressWarnings("unchecked")
    private boolean isFilterRequired(final Entity entity) {
        final List<AttributeValue> attributeValues = entity.getAttributeValues();

        final EntityTableMapping entityTableMapping = attributeProvider.get(entity.getType());

        for (AttributeValue attributeValue : attributeValues) {
            final String attributeName = attributeValue.getAttribute().getName();
            if (entityTableMapping.getAttributeIds().contains(attributeName)) {
                return true;
            }
        }

        return false;
    }

    private void generateCreationOrder(final CreationOrder creationOrder, final Class<?> type) throws ClassNotFoundException {

        final Queue<String> queue = new PriorityQueue<String>();
        queue.offer(type.getName());
        final Stack<Class<?>> stack = new Stack<Class<?>>();
        stack.push(type);

        while (!queue.isEmpty()) {
            final Set<Class<?>> parents = hierarchyGraph.getParents(Class.forName(queue.poll()));
            for (Class<?> parent : parents) {
                if (!stack.contains(parent) && !creationOrder.contains(parent)) {
                    queue.offer(parent.getName());
                    stack.push(parent);
                }
            }
        }

        while (!stack.isEmpty()) {
            final Class<?> pop = stack.pop();
                creationOrder.add(pop);
        }
    }

}
