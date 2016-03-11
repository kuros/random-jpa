package com.github.kuros.random.jpa.definition;

import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.resolver.DependencyResolver;
import com.github.kuros.random.jpa.types.Entity;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class MinimumHierarchyGenerator {

    @SuppressWarnings("unchecked")
    public static HierarchyGraph generate(final HierarchyGraph parentGraph, final List<Entity> entities) {
        HierarchyGraph hierarchyGraph = HierarchyGraph.newInstance();

        for (Entity entity : entities) {
            addParentToHierarchy(parentGraph, hierarchyGraph, entity.getType());

            final List<Relation> softRelations = DependencyResolver.generateRelations(entity.getSoftLinks());
            for (Relation relation : softRelations) {
                hierarchyGraph.addRelation(relation);
            }
        }

        return hierarchyGraph;
    }

    private static void addParentToHierarchy(final HierarchyGraph parentGraph, final HierarchyGraph hierarchyGraph, final Class<?> type) {
        final TableNode tableNode = parentGraph.getTableNode(type);

        for (Relation relation : tableNode.getRelations()) {
            hierarchyGraph.addRelation(relation);
        }
        final Set<Class<?>> parentClasses = tableNode.getParentClasses();

        for (Class<?> parentClass : parentClasses) {
            addParentToHierarchy(parentGraph, hierarchyGraph, parentClass);
        }
    }


    private static boolean notEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }
}
