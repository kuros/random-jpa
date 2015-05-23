package com.kuro.random.jpa.resolver;

import com.kuro.random.jpa.mapper.HierarchyGraph;
import com.kuro.random.jpa.mapper.TableNode;
import com.kuro.random.jpa.types.AttributeValue;
import com.kuro.random.jpa.types.CreationPlan;
import com.kuro.random.jpa.types.Entity;
import com.kuro.random.jpa.types.Plan;
import com.kuro.random.jpa.util.AttributeHelper;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

/**
 * Created by Kumar Rohit on 5/17/15.
 */
public class CreationPlanResolver {

    private HierarchyGraph hierarchyGraph;
    private Plan plan;

    private CreationPlanResolver(final HierarchyGraph hierarchyGraph, final Plan plan) {
        this.hierarchyGraph = hierarchyGraph;
        this.plan = plan;
    }

    public static CreationPlanResolver newInstance(final HierarchyGraph hierarchyGraph, final Plan plan) {
        return new CreationPlanResolver(hierarchyGraph, plan);
    }

    public CreationPlan getCreationPlan() {
        final CreationPlan creationPlan = CreationPlan.newInstance(hierarchyGraph);
        final List<Entity> entities = plan.getEntities();
        for (Entity entity : entities) {
            final Class type = entity.getType();

            try {
                generateCreationOrder(creationPlan, type);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        filterAlreadyGenerated(creationPlan, plan.getEntities());

        return creationPlan;
    }

    private void filterAlreadyGenerated(final CreationPlan creationPlan, final List<Entity> entities) {

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

        creationPlan.getCreationPlan().removeAll(itemsWithId);
    }

    @SuppressWarnings("unchecked")
    private boolean isFilterRequired(final Entity entity) {
        final List<AttributeValue> attributeValues = entity.getAttributeValues();

        for (AttributeValue attributeValue : attributeValues) {
            try {
                final Field field = AttributeHelper.getField(attributeValue.getAttribute());
                final Id annotation = field.getAnnotation(Id.class);
                if (annotation != null) {
                    return true;
                }
            } catch (final NoSuchFieldException e) {
                return false;
            }
        }

        return false;
    }

    private void generateCreationOrder(final CreationPlan creationPlan, final Class<?> type) throws ClassNotFoundException {

        final Queue<String> queue = new PriorityQueue<String>();
        queue.offer(type.getName());
        final Stack<Class<?>> stack = new Stack<Class<?>>();
        stack.push(type);

        while(!queue.isEmpty()) {
            final Set<Class<?>> parents = hierarchyGraph.getParents(Class.forName(queue.poll()));
            for (Class<?> parent : parents) {
                if (!stack.contains(parent) && !creationPlan.contains(parent)) {
                    queue.offer(parent.getName());
                    stack.push(parent);
                }
            }
        }

        while(!stack.isEmpty()) {
            final Class<?> pop = stack.pop();
                creationPlan.add(pop);
        }
    }


}
