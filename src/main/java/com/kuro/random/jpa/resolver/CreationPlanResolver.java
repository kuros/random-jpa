package com.kuro.random.jpa.resolver;

import com.kuro.random.jpa.mapper.HierarchyGraph;
import com.kuro.random.jpa.mapper.TableNode;
import com.kuro.random.jpa.types.CreationPlan;
import com.kuro.random.jpa.types.Entity;
import com.kuro.random.jpa.types.Plan;

import java.util.List;
import java.util.Map;

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

        return creationPlan;
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
