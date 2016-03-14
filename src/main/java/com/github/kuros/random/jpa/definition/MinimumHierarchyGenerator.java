package com.github.kuros.random.jpa.definition;

import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.resolver.DependencyResolver;
import com.github.kuros.random.jpa.types.Entity;

import java.util.List;
import java.util.Set;

public class MinimumHierarchyGenerator {

    @SuppressWarnings("unchecked")
    public static HierarchyGraph generate(final HierarchyGraph parentGraph, final List<Entity> entities) {
        final HierarchyGraph hierarchyGraph = HierarchyGraph.newInstance();

        for (Entity entity : entities) {
            addParentToHierarchy(parentGraph, hierarchyGraph, entity.getType());

            final List<Relation> softRelations = DependencyResolver.generateRelations(entity.getSoftLinks());
            for (Relation relation : softRelations) {
                hierarchyGraph.addRelation(relation);
            }

            final List<Class<?>> beforeClasses = entity.getBeforeClasses();
            for (Class<?> parentClass : beforeClasses) {
                hierarchyGraph.addNode(entity.getType(), parentClass);
            }
        }

        new CyclicValidator(hierarchyGraph).validate();

        return hierarchyGraph;
    }

    private static void addParentToHierarchy(final HierarchyGraph parentGraph, final HierarchyGraph hierarchyGraph, final Class<?> type) {
        final TableNode tableNode = parentGraph.getTableNode(type);

        if (tableNode != null) {
            for (Relation relation : tableNode.getRelations()) {
                hierarchyGraph.addRelation(relation);
            }
            final Set<Class<?>> parentClasses = tableNode.getParentClasses();

            for (Class<?> parentClass : parentClasses) {
                hierarchyGraph.addNode(type, parentClass);
                addParentToHierarchy(parentGraph, hierarchyGraph, parentClass);
            }
        }
    }
}
