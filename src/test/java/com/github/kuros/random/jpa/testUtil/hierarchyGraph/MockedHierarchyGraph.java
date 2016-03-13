package com.github.kuros.random.jpa.testUtil.hierarchyGraph;

import com.github.kuros.random.jpa.definition.HierarchyGraph;
import com.github.kuros.random.jpa.link.Dependencies;
import com.github.kuros.random.jpa.link.Link;
import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.resolver.DependencyResolver;

import javax.persistence.metamodel.Attribute;
import java.util.List;

public class MockedHierarchyGraph {

    public static HierarchyGraph getHierarchyGraph() {
        final HierarchyGraph hierarchyGraph = HierarchyGraph.newInstance();

        for (Relation relation : DependecyHelper.getDependency()) {
            hierarchyGraph.addRelation(relation);
        }

        return hierarchyGraph;
    }
}
