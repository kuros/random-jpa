package com.github.kuros.random.jpa.definition;

import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.resolver.DependencyResolver;
import com.github.kuros.random.jpa.types.Entity;
import com.github.kuros.random.jpa.types.EntityHelper;

import java.util.List;
import java.util.Set;

public class MinimumHierarchyGenerator {

    @SuppressWarnings("unchecked")
    public static HierarchyGraph generate(final HierarchyGraph parentGraph, final List<Entity> entities) {
        final HierarchyGraph hierarchyGraph = HierarchyGraph.newInstance();

        for (Entity<?> entity : entities) {
            addParentToHierarchy(parentGraph, hierarchyGraph, EntityHelper.getType(entity));

            final Set<Relation> softRelations = DependencyResolver.generateRelations(EntityHelper.getSoftLinks(entity));
            for (Relation relation : softRelations) {
                hierarchyGraph.addRelation(relation);
            }

            final List<Class<?>> afterClasses = EntityHelper.getAfterClasses(entity);
            for (Class<?> parentClass : afterClasses) {
                hierarchyGraph.addNode(EntityHelper.getType(entity), parentClass);
            }

            final List<Class<?>> beforeClasses = EntityHelper.getBeforeClasses(entity);
            for (Class<?> beforeClass : beforeClasses) {
                hierarchyGraph.addNode(beforeClass, EntityHelper.getType(entity));
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
