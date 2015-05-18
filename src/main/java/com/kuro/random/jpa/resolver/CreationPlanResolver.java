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
import java.util.Map;
import java.util.Set;

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
            generateCreationOrder(creationPlan, hierarchyGraph.getParentRelations(), type);
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

    private void generateCreationOrder(final CreationPlan creationPlan, final Map<Class<?>, TableNode> parentRelations, final Class<?> type) {
        final TableNode tableNode = parentRelations.get(type);
        for (Class<?> parent : tableNode.getParentClasses()) {
            if (!creationPlan.contains(parent)) {
                generateCreationOrder(creationPlan, parentRelations, parent);
            }
        }
        creationPlan.add(type);
    }


}
