package com.github.kuros.random.jpa.types;

import com.github.kuros.random.jpa.definition.ChildGraph;
import com.github.kuros.random.jpa.definition.ChildNode;
import com.github.kuros.random.jpa.definition.HierarchyGraph;
import com.github.kuros.random.jpa.mapper.Relation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CreationGraph {

    private final HierarchyGraph hierarchyGraph;
    private final ChildGraph childGraph;
    private final Set<Class<?>> managedClasses;
    private final Set<Class<?>> parentNodes;
    private final Map<Class<?>, Integer> creationCount;

    private CreationGraph(final HierarchyGraph hierarchyGraph, final Plan plan) {
        this.hierarchyGraph = hierarchyGraph;
        this.childGraph = ChildGraph.newInstance();
        this.managedClasses = new HashSet<Class<?>>();
        this.parentNodes = new LinkedHashSet<Class<?>>();
        this.creationCount = new HashMap<Class<?>, Integer>();
        initialize(plan);
    }

    public static CreationGraph newInstance(final HierarchyGraph hierarchyGraph, final Plan plan) {
        return new CreationGraph(hierarchyGraph, plan);
    }

    public void initialize(final Plan plan) {
        Integer level = 0;

        final List<Entity> entities = plan.getEntities();
        for (Entity entity : entities) {
            final Class<?> type = entity.getType();
            creationCount.put(type, entity.getCount());

            generateParentHierarchy(type, level);
        }
    }

    private void generateParentHierarchy(final Class<?> type, final Integer level) {
        managedClasses.add(type);
        final Set<Class<?>> parents = hierarchyGraph.getParents(type);
        if (parents.isEmpty()) {
            parentNodes.add(type);
        }

        final Set<Relation> attributeRelations = hierarchyGraph.getAttributeRelations(type);
        for (Class<?> parent : parents) {
            final ChildNode childNode = childGraph.getChildNode(parent, level + 1);
            childGraph.addRelation(childNode, type, attributeRelations);
            generateParentHierarchy(parent, level);
        }
    }

    public Set<Class<?>> getParentNodes() {
        return parentNodes;
    }

    public Set<Class<?>> getManagedClasses() {
        return managedClasses;
    }
}
