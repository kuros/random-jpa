package com.github.kuros.random.jpa.testUtil.hierarchyGraph;

import com.github.kuros.random.jpa.definition.HierarchyGraph;
import com.github.kuros.random.jpa.mapper.Relation;

public class MockedHierarchyGraph {

    public static HierarchyGraph getHierarchyGraph() {
        final HierarchyGraph hierarchyGraph = HierarchyGraph.newInstance();

        for (Relation relation : DependencyHelper.getDependency()) {
            hierarchyGraph.addRelation(relation);
        }

        return hierarchyGraph;
    }
}
